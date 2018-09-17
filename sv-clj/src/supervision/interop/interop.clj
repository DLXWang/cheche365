(ns supervision.interop.interop
  "交互操作组件，负责将本进程的消息通知给其他进程，当前实现是通过Redis"
  (:gen-class)
  (:require
    [taoensso.carmine :as redis]
    [taoensso.timbre :as log]))

(defonce ^:private redis-key-prefix "parser-availability-")

(defn- publish
  "发布消息到指定的key"
  [current-state msg-payload]
  (let [{:keys [conn conf]} current-state
        {:keys [company-code service-state]} msg-payload
        key (str redis-key-prefix company-code)
        ttl-secs (+ 10                                      ;; add 10 seconds
                    (/ (case service-state
                         :normal        (:interval-normal conf)
                         :early-warning (:interval-early-warning conf)
                         :unavailable   (:interval-unavailable conf))
                       1000))]
    (log/info "向Redis更新键值：" key service-state "超时设置为" ttl-secs "秒")
    (redis/wcar conn (redis/setex key ttl-secs service-state))))

(defn iop-state-fn
  "返回根据传入配置返回进程间交互组件状态的函数"
  [conf]
  (fn [put-fn]
    (let [{:keys [redis-host redis-port]} conf
          conn {:pool {}
                :spec {:host redis-host
                       :port redis-port}}]
      (log/info "连接Redis到" redis-host redis-port)
      {:state (atom {:conf conf
                     :conn conn})})))

(defn publish-parser-state
  "发布parser状态到Redis上"
  [{:keys [current-state msg-payload]}]
  (publish current-state msg-payload))

(defn cmp-map
  "创建基于Redis的进程间交互组件"
  [cmp-id conf]
  {:cmp-id      cmp-id
   :state-fn    (iop-state-fn conf)
   :handler-map {:parser/availability publish-parser-state}})
