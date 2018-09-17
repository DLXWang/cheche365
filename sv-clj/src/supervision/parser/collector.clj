(ns supervision.parser.collector
  "收集器组件，用于收集所有来自于监视器的事件，然后交由交互操作组件负责通知其他进程"
  (:gen-class)
  (:require
    [taoensso.timbre :as log]))

(defn collector-state-fn
  "返回根据传入配置返回收集器组件状态的函数"
  [conf]
  (fn [put-fn]
    (do
      (log/info "收集器开始初始化")
      {:state (atom {})})))

(defn update-parser-state
  "更新保险公司服务状态"
  [{:keys [cmp-state put-fn msg-payload]}]
  (log/info "收集到保险公司服务消息" msg-payload)
  (put-fn [:parser/availability msg-payload]))

(defn cmp-map
  "创建状态收集器组件"
  [cmp-id conf]
  {:cmp-id      cmp-id
   :state-fn    (collector-state-fn conf)
   :handler-map {:monitor/availability update-parser-state}})
