package com.cheche365.cheche.pushmessages.spi;

import com.cheche365.cheche.pushmessages.model.Platform;

import java.util.List;


/**
 * Created by liqiang on 7/17/15.
 */
public interface Destination {

    List<String> getDestinationDevices(Platform platform);
    boolean isSupportedPlatform(Platform platform);

}

