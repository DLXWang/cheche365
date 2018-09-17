(ns supervision.parser.checkers
  "各个保险公司的服务检查器"
  (:gen-class)
  (:require
    [clj-http.client :as http]
    [net.cgrand.enlive-html :as eh]
    [taoensso.timbre :as log]
    [cheshire.core :as json]))

(defn check-picc
  "检查PICC网站"
  []
  (log/info "开始检查PICC服务状态")
  (let [options
        {:form-params {:areaCode   11000000
                       :cityCode   11000000
                       :licenseNo  "京NCT589"
                       :licenseFlag 1}}
        result (http/post "http://www.epicc.com.cn/newecar/proposal/normalProposal" options)
        status (:status result)
        html (eh/html-snippet (:body result))
        uniqueId (->
                   (eh/select html [(eh/attr= :id "uniqueID")])
                   (#((comp :value :attrs first) %)))]
    (log/info "PICC，uniqueId" uniqueId)
    (and (= 200 status) uniqueId)))

(defn check-pingan
  "检查PINGAN网站"
  []
  (log/info "开始检查PINGAN服务状态")
  (let [options
        {:query-params {:department.cityCode  110100
                        :vehicle.licenseNo    "京N11XZ1"
                        :partner.mediaSources "SC03-Direct-00001"
                        :partner.partnerName  "chexie-mobile"}}
        result (http/get "http://u.pingan.com/autox/do/api/renewal-check" options)
        status (:status result)
        json (json/parse-string (:body result) true)
        flowId (:flowId json)
        resultCode (:resultCode json)]
    (log/info "PINGAN，flowId" flowId "resultCode" resultCode)
    (and (= 200 status) flowId (= "C0000" resultCode))))

(defn check-chinalife
  "检查CHINALIFE网站"
  []
  (log/info "开始检查CHINALIFE服务状态")
  (let [options
        {:form-params {:temporary.geProposalArea.deptID               "3110000"
                       :temporary.geProposalArea.parentid             "3110000"
                       :temporary.quoteMain.areaCode                  "3110000"
                       "temporary.quoteMain.geQuoteCars[0].licenseNo" "京NCT589"
                       "temporary.quoteMain.geQuoteCars[0].engineNo"  "940407A"
                       "temporary.quoteMain.geQuoteCars[0].frameNo"   "LGBM2DE45AY032341"
                       "temporary.quoteMain.geQuoteCars[0].carOwner"  "田子凡"}
         :form-param-encoding "GBK"}
        result (http/post "http://www.chinalife.com.cn/online/saleNewCar/carProposalfindCarInfo.do" options)
        status (:status result)
        json (json/parse-string (:body result) true)
        car-list (:list json)]
    (log/info "CHINALIFE，(empty? car-list)" (empty? car-list))
    (and (= 200 status) (not-empty car-list))))

(defn check-cpic
  "检查CPIC网站"
  []
  (log/info "开始检查CPIC服务状态")
  (let [options
        {:form-params {:provinceCode 110000
                       :cityCode     110100
                       :otherSource  ""
                       :customType   ""}}
        result (http/post "http://www.ecpic.com.cn/cpiccar/salesNew/businessCollect/loadCityBranchCode" options)
        status (:status result)
        json (json/parse-string (:body result) true)
        branch-code (:branchCode json)]
    (log/info "CPIC，branchCode" branch-code)
    (and (= 200 status) branch-code)))

(defn check-sinosig
  "检查SINOSIG网站"
  []
  (log/info "SINOSIG does nothing")
  true)

(defn check-cic
  "检查CIC网站"
  []
  (log/info "CIC does nothing")
  true)

(defn check-zhongan
  "检查ZHONGAN网站"
  []
  (log/info "ZHONGAN does nothing")
  true)
