package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by Shanxf on 2016/10/8.
 */
@Entity
public class AutoVehicleLicenseServiceItemChannel {
    private Long id;
    private Channel channel;
    private AutoVehicleLicenseServiceItem autoVehicleLicenseServiceItem;
    private  AutoServiceType autoServiceType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey=@ForeignKey(name="FK_CHANNEL_TYPE_REF_CHANNEL", foreignKeyDefinition="FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "auto_vehicle_license_service", foreignKey=@ForeignKey(name="FK_ATUO_VEHICLE_LICENSE_SERVICE_TYPE_REF_ATUO_VEHICLE_LICENSE_SERVICE", foreignKeyDefinition="FOREIGN KEY (auto_vehicle_license_service) REFERENCES auto_vehicle_license_service(id)"))
    public AutoVehicleLicenseServiceItem getAutoVehicleLicenseServiceItem() {
        return autoVehicleLicenseServiceItem;
    }

    public void setAutoVehicleLicenseServiceItem(AutoVehicleLicenseServiceItem autoVehicleLicenseServiceItem) {
        this.autoVehicleLicenseServiceItem = autoVehicleLicenseServiceItem;
    }
    @ManyToOne
    @JoinColumn(name = "auto_service_type", foreignKey=@ForeignKey(name="FK_ATUO_SERVICE_TYPE_REF_ATUO__SERVICE", foreignKeyDefinition="FOREIGN KEY (auto_service_type) REFERENCES auto_service_type(id)"))
    public AutoServiceType getAutoServiceType() {
        return autoServiceType;
    }

    public void setAutoServiceType(AutoServiceType autoServiceType) {
        this.autoServiceType = autoServiceType;
    }
}

