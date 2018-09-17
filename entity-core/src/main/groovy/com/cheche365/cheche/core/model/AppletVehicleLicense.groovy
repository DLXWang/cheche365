package com.cheche365.cheche.core.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class AppletVehicleLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id

    @Column(columnDefinition = "VARCHAR(45)")
    String unionid //微信unionid

    @Column(columnDefinition = "VARCHAR(45)")
    String contactName

    @Column(columnDefinition = "VARCHAR(45)")
    String mobile //电话

    @Column(columnDefinition = "VARCHAR(100)")
    String relativePath //图片保存地址

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
    @JoinColumn(name = "use_character", foreignKey=@ForeignKey(name="FK_VEHICLE_LICENSE_REF_USE_CHARACTER", foreignKeyDefinition="FOREIGN KEY (use_character) REFERENCES use_character(id)"))
    UseCharacter useCharacter //使用性质

    @Column(columnDefinition = "DATETIME")
    Date createTime

    @Column(columnDefinition = "DATETIME")
    Date updateTime
}
