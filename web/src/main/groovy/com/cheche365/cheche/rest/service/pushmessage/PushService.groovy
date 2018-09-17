package com.cheche365.cheche.rest.service.pushmessage

import com.cheche365.cheche.core.model.Device
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.DeviceRepository
import com.cheche365.pushmessage.api.model.PushBody
import com.cheche365.pushmessage.api.service.IPushmessageService
import groovy.util.logging.Slf4j
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class PushService implements IPushService{

    @Autowired
    private DeviceRepository deviceRepository
    @Autowired
    private IPushmessageService pushmessageService
    @Override
    String simplePush(User user, PushBusinessType businessType) {

        String result = null

        log.info("push message start with user:${user} and businessType:${businessType}")

        try{
            PushBody pushBody = new PushBody()

            List<Device> deviceList = deviceRepository.findByUser(user)
            if(CollectionUtils.isNotEmpty(deviceList)){
                Map<String,String> extras = new HashMap<String,String>()
                Set<String> deviceIds = deviceList.collect {it.deviceUniqueId}
                if(CollectionUtils.isEmpty(deviceIds)){
                    throw new Exception("no deviceIds to push!");
                }
                pushBody.setDeviceIds(deviceIds)
                pushBody.setAlert(PushMessageConstant.TYPE_MAPPING.get(businessType).alert)
                pushBody.setTitle(PushMessageConstant.TYPE_MAPPING.get(businessType).title)
                pushBody.setExtras(extras)
                result = pushmessageService.push(pushBody)

                log.info("push message reponse:${result}")

            }else{
                log.warn("push message with none device token found.")
            }
            result
        }catch(Exception e){
            log.error("push message error!",e)
            e.printStackTrace()
        }

    }
}
