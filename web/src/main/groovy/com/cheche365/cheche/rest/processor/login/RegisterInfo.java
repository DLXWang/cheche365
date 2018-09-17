package com.cheche365.cheche.rest.processor.login;

import com.cheche365.cheche.common.util.ContactUtils;
import com.cheche365.cheche.core.model.Gender;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.Ethnic;
import com.cheche365.cheche.core.model.agent.ShopType;
import org.apache.commons.lang3.StringUtils;

public class RegisterInfo extends LoginInfo{

    private String inviteCode;

    private String name;

    private String identity;

    private ShopType shopType; // 代理人所在渠道，1:个人、2:洗车店、3:维修中心

    private String shop; //门店名称，当用户选择渠道为“个人”时，此字段隐藏，无需填写，当用户选择洗车店或汽车维修中心时，此为必填字段

    private String email;

    /**
     * 用户二维码注册时候，回传注册渠道Id
     */
    private Long channelId;

    private Ethnic ethnic;

    private Boolean useDefaultEmail;

    public Ethnic getEthnic() {
        return ethnic;
    }

    public void setEthnic(Ethnic ethnic) {
        this.ethnic = ethnic;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getUseDefaultEmail() {
        return useDefaultEmail;
    }

    public void setUseDefaultEmail(Boolean useDefaultEmail) {
        this.useDefaultEmail = useDefaultEmail;
    }

    public User getUserInfo(){
        User user = new User();
        user.setName(name);
        user.setMobile(getMobile());
        user.setIdentity(identity);
        user.setUseDefaultEmail(useDefaultEmail);
        if (StringUtils.isNotBlank(identity)){
            Gender gender = ((Integer) ContactUtils.getGenderByIdentity(identity)) == 1 ? Gender.Enum.MALE : Gender.Enum.FEMALE;
            user.setGender(gender);
        }
        user.setEmail(email);
        return user;
    }
    public ChannelAgent getChannelAgentInfo(){
        ChannelAgent channelAgent = new ChannelAgent();
        channelAgent.setShopType(shopType);
        channelAgent.setShop(shop);
        channelAgent.setEthnic(ethnic);
        return channelAgent;
    }
}
