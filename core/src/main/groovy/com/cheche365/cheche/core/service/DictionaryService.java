package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.Ethnic;
import com.cheche365.cheche.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WF on 2015/7/16.
 */

@Service
@Transactional
public class DictionaryService {


    @Autowired
    GiftStatusRepository giftStatusRepository;

    @Autowired
    GiftTypeRepository giftTypeRepository;

    @Autowired
    AreaTypeRepository areaTypeRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    GlassTypeRepository glassTypeRepository;

    @Autowired
    IdentityTypeRepository identityTypeRepository;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired
    OrderTypeRepository orderTypeRepository;

    @Autowired
    PaymentChannelRepository paymentChannelRepository;

    @Autowired
    PaymentStatusRepository paymentStatusRepository;

    @Autowired
    private OrderCancelReasonTypeRepository orderCancelReasonTypeRepository;

    @Autowired
    private EthnicRepository ethnicRepository;

    public Iterable<GiftStatus> getAll(){
        return this.giftStatusRepository.findAll();
    }

    public Iterable<GiftType> getgiftype() {
        return this.giftTypeRepository.findAll();
    }

    public Iterable<AreaType> getareatype() {
        return this.areaTypeRepository.findAll();
    }

    public Iterable<Channel> getchannel() {
        return this.channelRepository.findAll();
    }

    public Iterable<Gender> getgender() {
        return this.genderRepository.findAll();
    }

    public Iterable<GlassType> getglasstype() {
        return this.glassTypeRepository.findAll();
    }

    public Iterable<IdentityType> getidentitytype() {
        return this.identityTypeRepository.findAll();
    }

    public Iterable<OrderStatus> getorderstatus() {
        return this.orderStatusRepository.findAll();
    }

    public Iterable<OrderType> getordertype() {
        return this.orderTypeRepository.findAll();
    }

    public Iterable<PaymentChannel> getpaymentchannel() {
        return this.paymentChannelRepository.findAll();
    }

    public Iterable<PaymentStatus> getpaymentstatusl() {
        return this.paymentStatusRepository.findAll();
    }

    public List<OrderCancelReasonType> listOrderCancelReasonType() {
        List<OrderCancelReasonType> list = new ArrayList<>();
        orderCancelReasonTypeRepository.findAll(new Sort(new Sort.Order("order"))).forEach(t -> list.add(t));
        return list;
    }

    public Iterable<Ethnic> getEthnics() {
        return ethnicRepository.findAll();
    }
}
