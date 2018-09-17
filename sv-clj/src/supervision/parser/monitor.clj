(ns supervision.parser.monitor
  "监视组件，以FSM实现，分为“正常”、“预警”和“不可用”三种状态"
  (:gen-class)
  (:require [matthiasn.systems-toolbox.switchboard :as sb]
            [slingshot.slingshot :refer [try+]]
            [taoensso.timbre :as log]))


;; 检查后处理
(defn- post-check-state
  "重新调度并更新组件状态"
  [cmp-state new-state timeout company-code]
  (sb/send-cmd (:switchboard @cmp-state)
               [:cmd/send {:to  :scheduler-cmp
                           :msg [:cmd/schedule-new
                                 {:timeout  timeout
                                  :id       (keyword (str "schedule/check-heartbeat-" company-code))
                                  :message  [(keyword (str "scheduler/check-heartbeat-" company-code))]}]}])
  (swap! cmp-state assoc :service-state new-state))


;; 状态检查及更新函数
(defmulti check-state
          "检查服务状态"
          (fn [cmp-state] (:service-state @cmp-state)))

;; 正常状态下的检查函数
(defmethod check-state
  :normal
  [cmp-state]
  (log/info (:company-code @cmp-state) "处于normal状态")
  (let [check-fn (:check-fn @cmp-state)
        alive? (check-fn)
        new-state (if alive? :normal :early-warning)
        conf (:conf @cmp-state)
        timeout (if (= :normal new-state)
                  (:interval-normal conf)
                  (:interval-early-warning conf))
        company-code (:company-code @cmp-state)]
    (post-check-state cmp-state new-state timeout company-code)))

;; 预警状态下的检查函数
(defmethod check-state
  :early-warning
  [cmp-state]
  (log/info (:company-code @cmp-state) "处于early-warning状态")
  (let [check-fn (:check-fn @cmp-state)
        alive? (check-fn)
        new-state (if alive?
                    :normal
                    (if (<= (:early-warning-count @cmp-state) 10)
                      :early-warning
                      :unavailable))
        conf (:conf @cmp-state)
        timeout (if (= :normal new-state)
                  (:interval-normal conf)
                  (if (= :early-warning new-state)
                    (:interval-early-warning conf)
                    (:interval-unavailable conf)))
        company-code (:company-code @cmp-state)]
    (if-not (= :early-warning new-state)
      (swap! cmp-state assoc :early-warning-count 0)
      (swap! cmp-state update :early-warning-count inc))
    (post-check-state cmp-state new-state timeout company-code)))

;; 不可用状态下的检查函数
(defmethod check-state
  :unavailable
  [cmp-state]
  (log/info (:company-code @cmp-state) "处于unavailable状态")
  (let [check-fn (:check-fn @cmp-state)
        alive? (check-fn)
        new-state (if alive? :normal :unavailable)
        conf (:conf @cmp-state)
        timeout (if (= :normal new-state)
                  (:interval-normal conf)
                  (:interval-unavailable conf))
        company-code (:company-code @cmp-state)]
    (post-check-state cmp-state new-state timeout company-code)))

(defn monitor-state-fn
  "返回根据传入配置返回监控组件状态的函数"
  [conf switchboard company-code check-fn]
  (fn [put-fn]
    {:state (atom {:company-code        company-code
                   :check-fn            (fn [] (try+
                                                 (check-fn)
                                                 (catch Object ex
                                                   (log/error company-code "抛出异常：" ex)
                                                   false)))
                   :service-state       :normal
                   :early-warning-count 0
                   :switchboard         switchboard
                   :conf                conf})}))


(defn check-heartbeat
  "检查保险公司服务的心跳"
  [{:keys [cmp-state put-fn]}]
  (log/info (:company-code @cmp-state) "监控组件开始检查服务心跳")
  (check-state cmp-state)
  (put-fn [:monitor/availability (select-keys @cmp-state [:company-code :service-state])]))

(defn cmp-map
  "创建保险公司服务监控组件"
  [cmp-id conf switchboard company-code check-fn]
  {:cmp-id      cmp-id
   :state-fn    (monitor-state-fn conf switchboard company-code check-fn)
   :handler-map {(keyword (str "scheduler/check-heartbeat-" company-code)) check-heartbeat}})
