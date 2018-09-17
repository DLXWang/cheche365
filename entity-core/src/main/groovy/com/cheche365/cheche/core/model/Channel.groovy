package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.*
import java.lang.reflect.Modifier
import java.util.regex.Matcher
import java.util.regex.Pattern

import static com.cheche365.cheche.core.constants.TagConstants.CHANNEL_TAGS

@Entity
@JsonIgnoreProperties(ignoreUnknown = true, value = ["parent", "partner"])
@Canonical
class Channel implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(45)")
    String name

    @ManyToOne
    Channel parent

    @ManyToOne
    ApiPartner apiPartner

    @ManyToOne
    Partner partner

    @Column(columnDefinition = "VARCHAR(200)")
    String icon

    @Column(columnDefinition = "BIGINT(20)")
    Long tag

    @Column(columnDefinition = "VARCHAR(2000)")
    String description

    @Column(columnDefinition = "DATETIME")
    Date createTime

    @Column(columnDefinition = "DATETIME")
    Date updateTime

    @PrePersist
    void onCreate() {
        createTime = new Date()
    }

    @PreUpdate
    void onUpdate() {
        updateTime = new Date()
    }

    static List<Channel> allChannels() {
        ApplicationContextHolder.getApplicationContext().getBean('channelRepository').findAll()
    }

    static List<Channel> allActiveChannels() {
        allChannels().findAll { !it.disable() }
    }

    static List<Channel> findActiveChannel() {
        (allPartners() + self()).unique()
    }

    static List<Channel> self() {
        findByTag(CHANNEL_TAGS.SELF.mask)
    }

    static List<Channel> selfApp() {
        findByTag(CHANNEL_TAGS.SELF_APP.mask)
    }

    static List<Channel> nonAuto() {
        findByTag(CHANNEL_TAGS.NON_AUTO.mask)
    }

    static List<Channel> nativeWechatApp() {
        findByTag(CHANNEL_TAGS.WECHAT_APP.mask)
    }

    static List<Channel> allPartners() {
        findByTag(CHANNEL_TAGS.PARTNER_H5.mask)
    }

    static List<Channel> thirdPartnerChannels() {//包含第三方出单中心
        allChannels().findAll { it.apiPartner }
    }

    static List<Channel> orderCenterChannels() {
        findByTag(CHANNEL_TAGS.ORDER_CENTER.mask)
    }

    static List<Channel> orderCenterChannelsAndSelf() {
        (self() + orderCenterChannels()).unique()
    }

    static List<Channel> agents() {
        findByTag(CHANNEL_TAGS.AGENT.mask)
    }

    static List<Channel> standardAgents() {
        findByTag(CHANNEL_TAGS.STANDARD_AGENT.mask)
    }

    static List<Channel> levelAgents() {
        findByTag(CHANNEL_TAGS.LEVEL_AGENT.mask)
    }

    static List<Channel> partnerAPIChannels() {
        findByTag(CHANNEL_TAGS.PARTNER_API.mask)
    }

    static List<Channel> disables() {
        findByTag(CHANNEL_TAGS.DISABLED_CHANNEL.mask)
    }

    static List<Channel> rebateToWallets() {
        findByTag(CHANNEL_TAGS.REBATE_INTO_WALLET.mask)
    }

    static List<Channel> unAllowQuote() {
        findByTag(CHANNEL_TAGS.QUOTE_FORBID.mask)
    }

    static List<Channel> unAllowPay() {
        findByTag(CHANNEL_TAGS.PAY_FORBID.mask)
    }

    static findByTag(mask) {
        allChannels().findAll { it.tag && (it.tag & mask) }
    }

    static List<Channel> normalLoginPartner() {
        findByTag(CHANNEL_TAGS.NORMAL_LOGIN_PARTNER.mask)
    }

    static List<Channel> partnerLoginChannel() {
        allPartners() - normalLoginPartner()
    }

    static List<Channel> agentLevelChannels() {
        self().intersect(levelAgents())
    }

    static Channel toChannel(Long id) {
        allChannels().find { it.id == id }
    }

    static Channel findByApiPartner(ApiPartner apiPartner) {
        allChannels().find { apiPartner && it.apiPartner == apiPartner && it.name.startsWith('PARTNER_') } //剔除出单中心相关渠道
    }

    static Channel findAgentChannel(Channel channel) {
        if (channel != null && agentLevelChannels().contains(channel)) {
            return Enum.PARTNER_CHEBAOYI_67
        }
        return channel
    }

    static boolean isInGroup(Long id, Channel... parentChannel) {
        parentChannel.any { channel ->
            allChannels().any {
                it.id == id && it.parent == channel.parent
            }
        }
    }

    static explainTag(Long tag) {
        CHANNEL_TAGS.collectEntries {
            [it.value.desc, (tag & it.value.mask) as boolean]
        }
    }

    @JsonIgnore
    Boolean isSelf() {
        return self().contains(this)
    }

    @JsonIgnore
    Boolean isPartnerChannel() {
        allPartners().contains(this)
    }

    @JsonIgnore
    Boolean isThirdPartnerChannel() {
        thirdPartnerChannels().any { it.id == this.id }
    }

    @JsonIgnore
    Boolean isOrderCenterChannel() {
        orderCenterChannels().any { it.id == this.id }
    }

    @JsonIgnore
    Boolean isAgentChannel() {
        return agents().contains(this)
    }

    @JsonIgnore
    Boolean isStandardAgent() {
        return standardAgents().contains(this)
    }

    @JsonIgnore
    Boolean isLevelAgent() {
        return levelAgents().contains(this)
    }

    @JsonIgnore
    Boolean isPartnerAPIChannel() {
        return partnerAPIChannels().contains(this)
    }

    @JsonIgnore
    Boolean disable() {
        this.tag && (this.tag & CHANNEL_TAGS.DISABLED_CHANNEL.mask)
    }

    static Channel findByDescription(String description) {
        allChannels().find { it.description == description }
    }

    static Channel toOrderCenterChannel(Long id) {
        Channel channel = toChannel(id)
        if (channel.isThirdPartnerChannel()) {
            def name = 'ORDER_CENTER_' + channel.name - 'PARTNER_'
            return orderCenterChannels().find { it.name == name }
        } else if (channel.isSelf()) {
            return Enum.ORDER_CENTER_11
        }
        return channel
    }

    static List<Long> getDataSourceChannel(String[] channelIds) {
        if (!channelIds) return null

        def result = []
        channelIds.each {
            Long channelId = Long.valueOf(it)
            if (channelId == 0) {
                result += [Enum.WE_CHAT_3.id, Enum.IOS_4.id, Enum.WEB_5.id, Enum.ANDROID_6.id, Enum.WAP_8.id, Enum.WE_CHAT_APP_39.id]
            } else {
                result += allChannels().findAll { (it.parent.id == channelId) }.collect { it.id }
            }
        }
        result
    }

    static List<Channel> findChannels(Channel channel) {
        List<Channel> channels = agentLevelChannels().contains(channel) ? agentLevelChannels()
            : thirdPartnerChannels().contains(channel) ? allChannels().findAll { channel.id == it.parent.id }
            : allChannels().findAll { !it.parent.isStandardAgent() }
        channels
    }

    static class Enum {

        public static Channel WE_CHAT_3
        public static Channel IOS_4
        public static Channel WEB_5
        public static Channel ANDROID_6
        public static Channel WAP_8
        public static Channel ORDER_CENTER_11
        public static Channel ALIPAY_21
        public static Channel WE_CHAT_APP_39

        public static Channel CLAIM_APP_214

        public static Channel PARTNER_BAIDU_15
        public static Channel PARTNER_NCI_25
        public static Channel PARTNER_RRYP_40
        public static Channel PARTNER_TUHUAPI_46
        public static Channel PARTNER_JINGSUANSHI_58
        public static Channel PARTNER_ANBANG_61
        public static Channel PARTNER_HUIBAO_75
        public static Channel PARTNER_CEBBANK_94
        public static Channel PARTNER_TUHU_203
        public static Channel PARTNER_BDINSUR_215

        public static Channel ORDER_CENTER_BAIDU_16
        public static Channel ORDER_CENTER_TUHU_18
        public static Channel ORDER_CENTER_NCI_26
        public static Channel ORDER_CENTER_JINGSUANSHI_59
        public static Channel ORDER_CENTER_TUHUAPI_60

        public static Channel PARTNER_CHEBAOYI_67
        public static Channel IOS_CHEBAOYI_221
        public static Channel ANDROID_CHEBAOYI_222
        public static Channel PARTNER_JD

        static {
            loadEnum('channelRepository', Channel, Enum)
        }

        def static loadEnum(repo, Class outerClass, Class enumClass) {
            def repoInstance = ApplicationContextHolder.getApplicationContext()?.getBean(repo)
            def allList = repoInstance.findAll()

            enumClass.declaredFields.findAll {
                Modifier.isStatic(it.modifiers) && it.type == outerClass
            }.with { enumList ->
                enumList.each { enumItem ->
                    def fieldName = enumItem.name
                    Pattern p = Pattern.compile('(.+)_(\\d+)\$');
                    Matcher m = p.matcher(fieldName);
                    if (m.find()) {
                        def id = m.group(2) as Long
                        def dbItem = allList.find { it.id == id }
                        if (dbItem) {
                            enumItem.set(enumClass.newInstance(), dbItem)
                        } else {
                            def name = m.group(1)
                            enumItem.set(enumClass.newInstance(), new Channel(id: id, name: name))
                        }
                        dbItem && enumItem.set(enumClass.newInstance(), dbItem)
                    } else {
                        def name = enumItem.name
                        def dbItem = allList.find { it.name == name }
                        if (dbItem) {
                            dbItem && enumItem.set(enumClass.newInstance(), dbItem)
                        } else {
                            enumItem.set(enumClass.newInstance(), new Channel(name: name))
                        }
                        dbItem && enumItem.set(enumClass.newInstance(), dbItem)
                    }
                }
            }
        }
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (o == null || !getClass().is(o.getClass())) return false
        Channel channel = (Channel) o

        (id == channel.id)

    }

    @Override
    int hashCode() {
        id.hashCode()
    }


}
