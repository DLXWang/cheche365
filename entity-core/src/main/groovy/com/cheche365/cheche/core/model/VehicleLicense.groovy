package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical
import groovy.transform.ToString
import org.springframework.beans.BeanUtils

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import java.beans.PropertyDescriptor

/**
 * 行驶证
 */
@Canonical
@ToString(ignoreNulls = true, includeNames = true, excludes = ['id'])
@Entity
@JsonIgnoreProperties(value = ["id"],ignoreUnknown = true)
class VehicleLicense {

    public static PropertyDescriptor[] PROPERTIES = BeanUtils.getPropertyDescriptors(VehicleLicense.class)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(45)")
    String licensePlateNo //车牌号

    @Column(columnDefinition = "VARCHAR(45)")
    String engineNo //发动机号

    @Column(columnDefinition = "VARCHAR(45)")
    String owner //车主姓名

    @Column(columnDefinition = "VARCHAR(45)")
    String vinNo //车架号

    @Column(columnDefinition = "DATE")
    Date enrollDate //车辆注册日期

    @Column(columnDefinition = "DATE")
    Date issueDate //发证日期

    @Column(columnDefinition = "VARCHAR(45)")
    String identity //身份证号

    @Column(columnDefinition = "VARCHAR(45)")
    String brandCode //品牌型号

    @ManyToOne
    @JoinColumn(name = "vehicle_type", foreignKey=@ForeignKey(name="FK_VEHICLE_LICENSE_REF_VEHICLE_TYPE", foreignKeyDefinition="FOREIGN KEY (vehicle_type) REFERENCES vehicle_type(id)"))
    VehicleType vehicleType //车辆类型

    @ManyToOne
    @JoinColumn(name = "identityType", foreignKey = @ForeignKey(name = "FK_VL_REF_IDENTITY_TYPE", foreignKeyDefinition = "FOREIGN KEY (identity_type) REFERENCES identity_type(id)"))
    IdentityType identityType//证件类型

    @ManyToOne
    @JoinColumn(name = "fuel_type", foreignKey = @ForeignKey(name = "FK_VL_REF_FUEL_TYPE", foreignKeyDefinition = "FOREIGN KEY (`fuel_type`) REFERENCES `fuel_type` (`id`)"))
    FuelType fuelType//燃料类型

    @ManyToOne
    @JoinColumn(name = "use_character", foreignKey=@ForeignKey(name="FK_VEHICLE_LICENSE_REF_USE_CHARACTER", foreignKeyDefinition="FOREIGN KEY (use_character) REFERENCES use_character(id)"))
    UseCharacter useCharacter //使用性质

    @ManyToOne
    @JoinColumn(name = "data_source")
    VehicleDataSource dataSource //数据来源

    @Column(columnDefinition = "INT(11)")
    Integer seats //座位数

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double newPrice //新车购置价格

    @Column(columnDefinition = "INT(11)")
    Integer licenseColorCode //车牌颜色

    @Column(columnDefinition = "INT(11)")
    Integer isPublic // 0:续保失败,无法获取该属性 1:公车 2:私车

    static VehicleLicense merge(newVehicleLicense, oldVehicleLicense) {
        PROPERTIES.inject oldVehicleLicense, { m, prop ->
            if (!["class", "id"].contains(prop.name) && newVehicleLicense[prop.name]) {
                m[prop.name] = newVehicleLicense[prop.name]
            }
            m
        }
    }

    static VehicleLicense createVLByAuto(Auto auto) {
        new VehicleLicense(
            licensePlateNo: auto.licensePlateNo,
            owner: auto.owner,
            engineNo: auto.engineNo,
            vinNo: auto.vinNo,
            enrollDate: auto.enrollDate,
            identity: auto.identity,
            brandCode: auto.autoType?.code,
            fuelType: auto.fuelType,
            useCharacter: auto.useCharacter,
            identityType: auto.identityType
        )
    }

    static VehicleLicense createVLByQuotePhoto(QuotePhoto quotePhoto) {
        new VehicleLicense(
            licensePlateNo: quotePhoto.licensePlateNo,
            owner: quotePhoto.owner,
            engineNo: quotePhoto.engineNo,
            vinNo: quotePhoto.vinNo,
            enrollDate: quotePhoto.enrollDate,
            brandCode: quotePhoto.code,
            identity: quotePhoto.identity,
            issueDate: quotePhoto.transferDate
        )
    }
}
