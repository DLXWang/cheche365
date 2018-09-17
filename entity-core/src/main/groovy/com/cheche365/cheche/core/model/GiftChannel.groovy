package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.repository.GiftChannelRepository;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.*;

/**
 * Created by mahong on 2016/1/25.
 * 配置优惠券/兑换码支持的渠道
 * 缺省值：默认支持所有的渠道
 */
@Entity
public class GiftChannel {
    private Long id;
    private Channel channel;
    private SourceType sourceType;
    private Long source;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_GIFT_CHANNEL_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "source_type", foreignKey = @ForeignKey(name = "FK_GIFT_CHANNEL_REF_SOURCE_TYPE", foreignKeyDefinition = "FOREIGN KEY (source_type) REFERENCES source_type(id)"))
    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public static class Enum {
        public static Map GIFT_CHANNEL_MAP

        static {

            GIFT_CHANNEL_MAP = ApplicationContextHolder.getApplicationContext().getBean(GiftChannelRepository)
                .findAll()
                .groupBy {cacheKey(it.sourceType, it.source)}
                .collectEntries {[(it.key): it.value.channel.id]}
        }


        public static boolean containsChannel(SourceType sourceType=SourceType.Enum.WECHATRED_2, Long source, Channel channel){
            def channels = GIFT_CHANNEL_MAP.get(cacheKey(sourceType, source))
            !channels || channels.contains(channel.id)
        }

    }

    public static cacheKey(SourceType sourceType, Long source){
        (sourceType && source) ?  "$sourceType.id:$source" : null
    }
}
