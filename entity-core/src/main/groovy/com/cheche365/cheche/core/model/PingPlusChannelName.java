package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.HashMap;

/**
 * @Author shanxf
 * @Date 2018/1/16  17:21
 */
@Entity
public class PingPlusChannelName {

    private Long id;
    private PaymentChannel paymentChannel;
    private Channel channel;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public PaymentChannel getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(PaymentChannel paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    @ManyToOne
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
