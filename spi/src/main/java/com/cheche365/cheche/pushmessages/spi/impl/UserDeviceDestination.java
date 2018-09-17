package com.cheche365.cheche.pushmessages.spi.impl;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.Device;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.DeviceRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.pushmessages.model.Platform;
import com.cheche365.cheche.pushmessages.spi.Destination;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liqiang on 7/20/15.
 */
public class UserDeviceDestination implements Destination {

    private Long userId;
    private Platform supportedPlatform;
    private Map<String, Device> deviceMap = new HashMap<String,Device>();
    public Map<String, Device> getDeviceMap() {
        return deviceMap;
    }

    @Override
    public List<String> getDestinationDevices(Platform platform) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        DeviceRepository deviceRepository = applicationContext.getBean(DeviceRepository.class);
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        User user = userRepository.findOne(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        List<Device> devices = deviceRepository.findByUserAndDeviceType(user, 4L);
        return devices.stream().map(device -> {
            deviceMap.put(device.getDeviceUniqueId(), device);
            return device.getDeviceUniqueId();
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isSupportedPlatform(Platform platform) {
        if (Platform.ALL == supportedPlatform){
            return true;
        }
        return supportedPlatform == platform;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public void setSupportedPlatform(Platform platform){
        this.supportedPlatform = platform;
    }

}
