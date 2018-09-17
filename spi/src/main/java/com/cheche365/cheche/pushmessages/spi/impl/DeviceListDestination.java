package com.cheche365.cheche.pushmessages.spi.impl;


import com.cheche365.cheche.pushmessages.model.Platform;
import com.cheche365.cheche.pushmessages.spi.Destination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by liqiang on 7/20/15.
 */
public class DeviceListDestination implements Destination {

    private Map<Platform, List<String>> devices = new HashMap<Platform, List<String>>();

    @Override
    public List<String> getDestinationDevices(Platform platform) {
        if (Platform.ALL == platform) {
            List<String> allDevice = new ArrayList<>();
            devices.values().forEach(devicesInPlatform -> {
                allDevice.addAll(devicesInPlatform);
            });
            return allDevice;
        }
        return devices.get(platform);
    }

    public void setDevices(Platform platform, List<String> destinationDevices){
        devices.put(platform,destinationDevices);
    }

    @Override
    public boolean isSupportedPlatform(Platform platform) {
        return devices.containsKey(platform);
    }

}
