#已经没用了！！！

# 保费
## 交强险
   * com.cheche365.cheche.misc.util.BusinessUtils._AUTO_TAX_VALID_PRICES
## 商业险
   * 通过com.cheche365.cheche.misc.controller.FakeDataExporter._FAKE_DATA_DECISION_TREE
    控制保额，从而控制保费
   * 通过无限循环，控制保费在合适的范围内
    
# 数据
  * user 电话随机，已避开随机到库中已有数据
  * auto
  * user_auto
  * insurance_package  先查询
  * quote_record
  * purchase_order
  * order_operation_info
  * payment
  * insurance
  * compulsory_insurance
    
    
# 数据导出
## 数据在保险公司的比例
        com.cheche365.cheche.misc.util.BusinessUtils.getQuoteRecord
        比例在程序中配置，不在导入参数中体现

## 导出配置
    当前城市、当前渠道、当前月份、需要数据的总数 以及提供的车辆从哪个位置开始读入
   * -fdexp 
   * -alf   D:/temp/vl_beijing.csv  csv 格式的车辆数，在程序中进行排序 
   * -alof  0                // 从车辆csv中读入车辆的其实位置
   * -rf    D:/temp/01-03-alipay-beijing-rf.csv       导出报表
   * -df    D:/temp/01-03-alipay-beijing-data.json    导出json
   * -ctsd  2016-03-02   数据开始时间（从2号开始，有bug）
   * -cted  2016-03-30   数据结束时间
   * -ecul  523          导入的数据总数
   * -posc  ALIPAY       导入数据的当前渠道（purchase_order_source_channel）
                         【 ALIPAY PARTNER_BAIDU PARTNER_AUTOHOME PARTNER_TUHU    
                          IOS  ANDROID  微信 PC  WAP】	       
   * -city 110000       城市
    
# 数据导入
## 导入配置
   * -fdimp 
   * -df D:/temp/01-03-alipay-beijing-data.json    数据导出中的json文件 
   * -rf D:/temp/report01/01-03-alipay-beijing-imp-rf.csv 数据导入后的payment*id
    
# OrderOperationInfo
   * com.cheche365.cheche.core.service.OrderOperationInfoService.createOperationInfo
    增加：
    Date date = purchaseOrder.getCreateTime(); // 日期加一天
   
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
    Date datePlus1day = calendar.getTime();
   
    operationInfo.setCreateTime(datePlus1day);
    operationInfo.setUpdateTime(datePlus1day);
    operationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER);
   
    return operationInfo;
