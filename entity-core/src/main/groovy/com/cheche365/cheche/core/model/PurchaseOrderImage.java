package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.*;

/**
 * Created by xu.yelong on 2016/8/22.
 */
@Entity
public class PurchaseOrderImage {
    private Long id;
    private String url;
    private Integer status=0;
    private InternalUser operator;
    private Integer source;
    private Date createTime;
    private Date updateTime;
    private Date expireDate;
    private PurchaseOrderImageType imageType;
    private Date uploadTime;
    private Date auditTime;
    private Channel channel;
    private PurchaseOrderImageScene imageScene;
    private String hint;
    private Long objId;//对象Id：如果为安心核保场景，为quote_record_id,其他场景目前均为purchase_order_id

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(columnDefinition = "SMALLINT(2)")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_IMAGE_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "DATE")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @ManyToOne
    @JoinColumn(name = "image_type", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_IMAGE_REF_PURCHASE_ORDER_IMAGE_TYPE", foreignKeyDefinition="FOREIGN KEY (image_type) REFERENCES purchase_order_image_type(id)"))
    public PurchaseOrderImageType getImageType() {
        return imageType;
    }

    public void setImageType(PurchaseOrderImageType imageType) {
        this.imageType = imageType;
    }

    @ManyToOne
    @JoinColumn(name = "image_scene", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_IMAGE_REF_PURCHASE_ORDER_IMAGE_SCENE", foreignKeyDefinition="FOREIGN KEY (image_scene) REFERENCES purchase_order_image_scene(id)"))
    public PurchaseOrderImageScene getImageScene() {
        return imageScene;
    }

    public void setImageScene(PurchaseOrderImageScene imageScene) {
        this.imageScene = imageScene;
    }

    @Column
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_IMAGE_REF_CHANNEL", foreignKeyDefinition="FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public static class SOURCE{
        public static final Integer WEB=1;
        public static final Integer ORDER_CENTER=2;
    }

    public static class STATUS{
        //未上传
        public static final Integer UPLOAD=0;
        //待审核
        public static final Integer AUDIT=1;
        //审核通过
        public static final Integer PASS=2;
        //审核未通过
        public static final Integer NOT_PASS=3;

        public static Map<Integer, String> STATUS_MAP = new HashMap<Integer, String>() {
            {
                put(UPLOAD, "未上传");
                put(AUDIT, "待审核");
                put(PASS, "审核通过");
                put(NOT_PASS, "审核未通过");
            }
        };
        public static List<Map<String,String>> STATUS_AUDIT_LIST = new ArrayList<Map<String,String>>() {
            {
                Map<String,String> NO_OK_PASS_ADD= new HashMap<>();
                Map<String,String> OK_PASS= new HashMap<>();
                Map<String,String> NO_CHECK= new HashMap<>();
                NO_OK_PASS_ADD.put("title","未通过审核与新增照片");
                NO_OK_PASS_ADD.put("status","3");
                NO_OK_PASS_ADD.put("desc", "");
                OK_PASS.put("title", "已通过审核照片");
                OK_PASS.put("status", "2");
                OK_PASS.put("desc", "");
                NO_CHECK.put("title", "正在审核中照片");
                NO_CHECK.put("status", "1");
                NO_CHECK.put("desc", "");
                add(NO_OK_PASS_ADD);
                add(OK_PASS);
                add( NO_CHECK);
            }
        };
    }
}
