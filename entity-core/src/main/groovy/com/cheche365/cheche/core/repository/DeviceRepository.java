package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Device;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mahong on 2015/7/20.
 */
@Repository
public interface DeviceRepository extends PagingAndSortingRepository<Device, Long> {

    Device findFirstByDeviceUniqueId(String deviceUniqueId);

    //List<Device> findByUser(User user);

    List<Device> findByUserAndDeviceType(User user, Long deviceType);

    List<Device> findByUser(User user);
}
