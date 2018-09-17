;; Project
(defproject supervision "1.1.0"
  :description "监督服务，用于检查服务或服务所依赖环境的可用性，然后实施相应策略"
  :url "http://www.cheche365.com"


  ;; plugins
  :plugins [[lein-ancient "0.6.8" :exclusions [org.clojure/clojure]]
            [lein-environ "1.0.2"]
            [jonase/eastwood "0.2.3" :exclusions [org.clojure/clojure]]
            [lein-codox "0.9.4"]
            [lein-try "0.4.3"]]


  ;; dependencies
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;[aero "0.2.0"]
                 [cheshire "5.6.1"]
                 [clj-http "3.1.0"]
                 [clj-pid "0.1.2"]
                 [com.taoensso/carmine "2.12.2"]
                 [com.taoensso/timbre "4.3.1" :exclusions [io.aviso/pretty]]
                 [com.taoensso/encore "2.53.0" :scope "runtime"]
                 [enlive "1.1.6"]
                 [luminus/config "0.8"]
                 [matthiasn/systems-toolbox "0.5.18" :exclusions [org.clojure/clojurescript]]

                 ;; runtime
                 [org.clojure/tools.logging "0.3.1" :scope "runtime"]
                 [org.clojure/tools.reader "1.0.0-beta1" :scope "runtime"]
                 [com.fzakaria/slf4j-timbre "0.3.2" :scope "runtime" :exclusions [com.taoensso/encore com.taoensso/timbre com.taoensso/truss]]]

                ;[matthiasn/systems-toolbox-kafka "0.5.1"]
                ;[matthiasn/systems-toolbox-ui "0.5.6"]
                ;[matthiasn/systems-toolbox-sente "0.5.12"]
                ;[org.clojure/tools.cli "0.3.3"]
                ;[environ "1.0.2"]
                ;[http-kit "2.1.19"]
                ;[criterium "0.4.4"]
                ;[clojurewerkz/quartzite "2.0.0"]
                ;[co.paralleluniverse/pulsar "0.7.4"]



  ;; lein & jvm
  :min-lein-version   "2.5.0"
  ;: repl-options       {:timeout 120000}
  :javac-options      ["-source" "1.8" "-target" "1.8"]
  :jvm-opts ^:replace ["-server"
                       "-Xms2g"
                       "-Xmx2g"
                       "-XX:+UseG1GC"
                       "-Dclojure.compiler.disable-locals-clearing=true"]
  ;:java-agents        [[co.paralleluniverse/quasar-core "0.7.4" :options "m" :classifier "jdk8"]]
  :main               supervision.core
  :target-path        "target/%s/"

  ;; profiles
  :profiles
  {:uberjar       {:aot :all}
   :itg           {:resource-paths ["conf/itg"]}
   :qa            {:resource-paths ["conf/qa"]}
   :prod          {:env {:production true}
                   :resource-paths ["conf/prod"]}
   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]
   :project/dev   {:env {:dev true}}
   :project/test  {:env {:test true}}
   :profiles/dev  {:resource-paths ["conf/dev"]}
   :profiles/test {:resource-paths ["conf/dev"]}})
