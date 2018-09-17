package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PingPlusChannelName;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PingPlusCnRepository extends CrudRepository<PingPlusChannelName, Long> {

    PingPlusChannelName findByPaymentChannelAndChannel(PaymentChannel paymentChannel, Channel channel);

}
