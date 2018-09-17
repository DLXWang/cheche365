package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.model.mongo.MongoUser
import org.apache.commons.lang3.StringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "moApplicationLog")
class MoApplicationLog implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    private String id;
    /**
     * 由于user关联的属性太多，在批量保存到Mongodb时，会出现内存泄露的问题，
     * 因此，自定义一个用户对象，只保存主要的一些信息
     * **/
    private MongoUser user;//用户

    private String logId;//日志的ID，标示是那种日志
    private int logLevel;//日志的级别 1debug  2info 3warn 4error
    private Object logMessage;//日志信息
    private LogType logType;

    private String objId;//对象id，例如，对报价相关的log，obj_id就是quote表的id
    private String objTable;//对象表名

    private Long opeartor;//操作员
    private String instanceNo;//log是在哪个application instance 上生成的
    private Date createTime;//创建时间


    boolean isMobileEmpty() {
        def mobileObj = this.logMessage["mobile"]//可能为 JsonNull
        def isQuoteMobileEmpty = mobileObj == null || "null".equals(mobileObj) || StringUtils.isEmpty(mobileObj as String)
        def isUserMobileEmpty = (user == null || StringUtils.isEmpty(user.mobile))
        if (isQuoteMobileEmpty && isUserMobileEmpty) {
            return true
        }
        if (!isUserMobileEmpty) {
            this.logMessage.mobile = user.mobile
        }
        return false
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

    MongoUser getUser() {
        return user
    }

    void setUser(MongoUser user) {
        this.user = user
    }

    String getLogId() {
        return logId;
    }

    void setLogId(String logId) {
        this.logId = logId;
    }

    int getLogLevel() {
        return logLevel;
    }

    void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    LogType getLogType() {
        return logType;
    }

    MoApplicationLog setLogType(LogType logType) {
        this.logType = logType;
        return this;
    }

    Object getLogMessage() {
        return logMessage;
    }

    void setLogMessage(Object logMessage) {
        this.logMessage = logMessage;
    }

    String getObjId() {
        return objId;
    }

    void setObjId(String objId) {
        this.objId = objId;
    }

    String getObjTable() {
        return objTable;
    }

    void setObjTable(String objTable) {
        this.objTable = objTable;
    }

    Long getOpeartor() {
        return opeartor;
    }

    void setOpeartor(Long opeartor) {
        this.opeartor = opeartor;
    }

    String getInstanceNo() {
        return instanceNo;
    }

    void setInstanceNo(String instanceNo) {
        this.instanceNo = instanceNo;
    }

    Date getCreateTime() {
        return createTime;
    }

    void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    static MoApplicationLog applicationLogByPurchaseOrder(PurchaseOrder purchaseOrder, LogType logType = null) {
        new MoApplicationLog(
            createTime: Calendar.getInstance().getTime(),
            instanceNo: purchaseOrder?.getOrderNo(),
            objId: purchaseOrder?.getId()?.toString(),
            objTable: 'purchase_order',
            user: MongoUser.toMongoUser(purchaseOrder?.getApplicant()),
            logType: logType ?: LogType.Enum.ORDER_RELATED_3
        )
    }

}
