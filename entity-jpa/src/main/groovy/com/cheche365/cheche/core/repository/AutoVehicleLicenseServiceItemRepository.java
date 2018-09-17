package com.cheche365.cheche.core.repository;


import com.cheche365.cheche.core.model.AutoServiceType;
import com.cheche365.cheche.core.model.AutoVehicleLicenseServiceItem;
import com.cheche365.cheche.core.model.Channel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Shanxf on 2016/10/8.
 */
@Repository
public interface AutoVehicleLicenseServiceItemRepository extends PagingAndSortingRepository<AutoVehicleLicenseServiceItem, Long> {
    @Query(value = "select * from auto_vehicle_license_service_item where disable = 0 and id in (select auto_vehicle_license_service_item from auto_vehicle_license_service_item_channel where channel=?1 and auto_service_type=?2) order by priority asc", nativeQuery = true)
    List<AutoVehicleLicenseServiceItem> getAutoVehicleLicenseServiceItemByChannelAndType(Channel channel, AutoServiceType autoServiceType);
}
