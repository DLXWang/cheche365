package com.cheche365.cheche.manage.common.model;

import javax.persistence.*;

@Entity
public class TelMarketingCenterAssignBatchData {

    private Long id;//主键
    private TelMarketingCenterAssignBatch batch;
    private TelMarketingCenter telMarketingCenter;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "batch", foreignKey=@ForeignKey(name="FK_TMCHABD_REF_TEL_MARKETING_CENTER_ASSIG_BATCH", foreignKeyDefinition="FOREIGN KEY (batch) REFERENCES tel_marketing_center_assign_batch (id)"))
    public TelMarketingCenterAssignBatch getBatch() {
        return batch;
    }

    public void setBatch(TelMarketingCenterAssignBatch batch) {
        this.batch = batch;
    }

    @ManyToOne
    @JoinColumn(name = "tel_marketing_center", foreignKey=@ForeignKey(name="FK_TMCHABD_REF_TEL_MARKETING_CENTER", foreignKeyDefinition="FOREIGN KEY (tel_marketing_center) REFERENCES tel_marketing_center (id)"))
    public TelMarketingCenter getTelMarketingCenter() {
        return telMarketingCenter;
    }

    public void setTelMarketingCenter(TelMarketingCenter telMarketingCenter) {
        this.telMarketingCenter = telMarketingCenter;
    }
}
