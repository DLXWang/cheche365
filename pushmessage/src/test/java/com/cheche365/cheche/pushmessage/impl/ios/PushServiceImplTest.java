package com.cheche365.cheche.pushmessage.impl.ios;

import com.cheche365.cheche.core.model.PushMessage;
import com.cheche365.cheche.pushmessage.app.config.TestAppConfig;
import com.cheche365.cheche.pushmessages.model.Platform;
import com.cheche365.cheche.pushmessages.spi.PushService;
import com.cheche365.cheche.pushmessages.spi.impl.DeviceListDestination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 7/20/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestAppConfig.class})
public class PushServiceImplTest {


    @Autowired
    @Qualifier("ios")
    private PushService pushService;

    @Before
    public void setup() {

    }

    @Test
    public void testPush(){
        DeviceListDestination destination = new DeviceListDestination();
        List<String> deviceList = new ArrayList<>();
        deviceList.add("f4545754 f4d8d75a ca083e06 3320a725 5e5aad6d 073f4b8b 74f0841d bcb38e9b");
        destination.setDevices(Platform.IOS, deviceList);
        pushService.push(1, destination, null, null, null);
    }
}
