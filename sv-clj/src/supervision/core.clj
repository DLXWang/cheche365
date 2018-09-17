(ns supervision.core
  (:gen-class)
  (:require
    [clj-pid.core :as pid]
    [clojure.java.io :as io]
    [config.core :refer [env]]
    [io.aviso.logging :as pretty]
    [matthiasn.systems-toolbox.switchboard :as sb]
    [matthiasn.systems-toolbox.scheduler :as sched]
    [supervision.parser.collector :as collector]
    [supervision.interop.interop :as iop]
    [supervision.parser.monitor :as monitor]
    [supervision.parser.checkers :as checkers]
    [taoensso.timbre :as log]
    [taoensso.timbre.appenders.3rd-party.rolling :as rolling])
  (:import (java.net InetAddress)))


(pretty/install-pretty-logging)
(pretty/install-uncaught-exception-handler)

(def conf env)

(defonce ^:private switchboard (sb/component :switchboard))

(defonce ^:private company-codes '("picc" "pingan" "cpic" "sinosig" "cic" "zhongan" "chinalife"))

(defn- configure-log
  "配置日志"
  []
  (when-not (or (:dev conf) (:test conf))
    (let [hostname (.getHostName (InetAddress/getLocalHost))
          get-log-file-path
          (fn [hostname]
            (if (:production conf)
              (str "/data/nfs0/logs/sv/" hostname)
              (if (.exists (io/file "/data/nfs2"))
                (str "/data/nfs2/logs/sv/" hostname)
                "logs")))
          log-file-path-prefix
          (if (:production conf)
            (get-log-file-path hostname)
            (if (.exists (io/file "target"))
              "target/logs"
              (get-log-file-path hostname)))
          log-file-path (str log-file-path-prefix "/supervision.log")]
      (when-not (.exists (io/file log-file-path))
        (.mkdirs (.getParentFile (io/file log-file-path))))
      (log/merge-config!
        {:appenders {:rolling (rolling/rolling-appender {:path (str log-file-path-prefix "/supervision.log") })}}))))


(defn restart!
  "启动（或重启）系统，配电板会按照给定的蓝图构造系统"
  []
  (let [init-comp-cmds
        [:cmd/init-comp
         [(sched/cmp-map :scheduler-cmp)           ;调度器组件，用于定时任务的编排
          (monitor/cmp-map :monitor-cmp-picc conf switchboard "picc" checkers/check-picc)                ;监视器组件
          (monitor/cmp-map :monitor-cmp-pingan conf switchboard "pingan" checkers/check-pingan)          ;监视器组件
          (monitor/cmp-map :monitor-cmp-cpic conf switchboard "cpic" checkers/check-cpic)                ;监视器组件
          (monitor/cmp-map :monitor-cmp-sinosig conf switchboard "sinosig" checkers/check-sinosig)       ;监视器组件
          (monitor/cmp-map :monitor-cmp-cic conf switchboard "cic" checkers/check-cic)                   ;监视器组件
          (monitor/cmp-map :monitor-cmp-zhongan conf switchboard "zhongan" checkers/check-zhongan)       ;监视器组件
          (monitor/cmp-map :monitor-cmp-chinalife conf switchboard "chinalife" checkers/check-chinalife) ;监视器组件
          (collector/cmp-map :collector-cmp conf)  ;收集器组件
          (iop/cmp-map :interop-cmp conf)]]        ;进程间交互组件
        route-cmds (map #(vector :cmd/route {:from :scheduler-cmp :to (keyword (str "monitor-cmp-" %))}) company-codes)
        route-all-cmds (map #(vector :cmd/route-all {:from (keyword (str "monitor-cmp-" %)) :to :collector-cmp}) company-codes)
        start-new-schedule-cmds
        (map
          #(vector :cmd/send
                   {:to   :scheduler-cmp
                    :msg  [:cmd/schedule-new
                           {:timeout  (:interval-normal conf)
                            :id       (keyword (str "schedule/check-heartbeat-" %))
                            :message  [(keyword (str "scheduler/check-heartbeat-" %))]}]})
          company-codes)
        conj-fn (partial apply conj)
        all-cmds (->
                   [init-comp-cmds]
                   (conj-fn route-cmds)
                   (conj-fn route-all-cmds)
                   (conj [:cmd/route {:from :collector-cmp :to :interop-cmp}])
                   (conj-fn start-new-schedule-cmds))]
    (sb/send-mult-cmd switchboard all-cmds)))

(defn init
  "程序初始化，记录当前PID，设置日志参数等"
  []
  (let [pidfile (:pidfile-name conf)]
    (pid/save pidfile)
    (pid/delete-on-shutdown! pidfile))
  (configure-log))

(defn -main
  "从命令行启动应用程序的主入口，
  由于restart!不会阻塞当前线程，
  所以需要等到当前线程结束后才能退出此函数"
  [& args]
  (init)
  (restart!)
  (.join (Thread/currentThread)))
