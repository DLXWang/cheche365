import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.wechat.MessageSender
import com.cheche365.cheche.wechat.UserManager
import com.cheche365.cheche.wechat.app.config.WechatConfig
import com.cheche365.cheche.core.model.WechatUserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Created by michael on 11/24/16.
 */
@WebAppConfiguration
@ContextConfiguration( classes = [CoreConfig,WechatConfig] )
class SpringMessageConvertFT extends Specification {

    @Autowired
    UserManager manager

    @Autowired
    MessageSender sender

    def "string response test"(){
        given:
        def openId = 'oDYirs-VsGnjTUjRpIWz15dlVAzE'
        def accessToken = 'MZH9FGyKOXauT0NXlbhoPiM6AW2e-XroZslzLXg6ZUvqExgqXnlh1Zg5BRknY9dHzqeP8hjbnJyym4oOJJ4Wp7o1e2x_M4rO-aKNAXRp26kmU9UcN3Ps2isUZqvzkakpJVYbAAAYKN'
        def apiPath = '/cgi-bin/user/info'
        def restTemplate = sender.createRestTemplate();
        def parameters = [
            "openid" :  openId,
            "lang" : "zh_CN",
            "access_token" : accessToken
        ]

        when:

        URI url = sender.buildURL(apiPath, parameters, false, false);
        Map infoMap = restTemplate.getForEntity(url, Map.class).body

        WechatUserInfo userInfo = new WechatUserInfo()
        ['city', 'province', 'nickname', 'headimgurl', 'language', 'unionid', 'sex'].each {
            userInfo[it] = infoMap[it]
        }
//        userInfo.city = infoMap.get('city')
//        userInfo.province = infoMap.get('province')
//        userInfo.setCountry(infoMap.get("country") == null ? null : infoMap.get("country").toString());
//        userInfo.setNickname(infoMap.get("nickname") == null ? null : infoMap.get("nickname").toString());
//        userInfo.setHeadimgurl(infoMap.get("headimgurl") == null ? null : infoMap.get("headimgurl").toString());
//        userInfo.setLanguage(infoMap.get("language") == null ? null : infoMap.get("language").toString());
//        userInfo.setUnionid(infoMap.get("unionid") == null ? null : infoMap.get("unionid").toString());
//        userInfo.setSex(infoMap.get("sex"));
        then:
        true
    }
}
