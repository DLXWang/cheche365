package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.OrderTransmissionStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangfei on 2015/6/11.
 */
@Component
public class OrderTransmissionStatusResource {

    public List<OrderTransmissionStatus> listAll() {
        return OrderTransmissionStatus.Enum.ALLSTATUS;
    }
}
