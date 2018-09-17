package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.Area

import javax.persistence.*

/**
 * 潮汐系统: 合约-支持投保城市关联表
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class TideContractSupportArea {

    Long id
    TideContract tideContract   //合约
    Area supportArea    //支持投保城市
    Boolean disable = false // 禁用/启用


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "tide_contract", foreignKey = @ForeignKey(name = "FK_TIDE_CONTRACT_SUPPORT_AREA_REF_TIDE_CONTRACT_ID", foreignKeyDefinition = "FOREIGN KEY (tide_contract) REFERENCES tide_contract(id)"))
    TideContract getTideContract() {
        return tideContract
    }

    @ManyToOne
    @JoinColumn(name = "support_area", foreignKey = @ForeignKey(name = "FK_TIDE_CONTRACT_SUPPORT_AREA_REF_AREA_ID", foreignKeyDefinition = "FOREIGN KEY (support_area) REFERENCES area(id)"))
    Area getSupportArea() {
        return supportArea
    }

    @Column(columnDefinition = 'tinyint(1)')
    Boolean getDisable() {
        return disable
    }
}
