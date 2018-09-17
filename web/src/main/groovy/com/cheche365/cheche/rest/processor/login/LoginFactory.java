package com.cheche365.cheche.rest.processor.login;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by zhaozhong on 2015/12/17.
 */
@Component
public class LoginFactory {

    @Autowired
    private List<LoginProcessor> loginProcessorMap;
    @Autowired
    private HttpServletRequest request;

    @Autowired
    NormalLoginProcessor normalLoginProcessor;

    public LoginProcessor getUserLoginProcessor() {
        final Channel clientType = ClientTypeUtil.getChannel(request);
        Optional<LoginProcessor> loginProcessorOptional=loginProcessorMap.stream().filter(p -> p.getSupportClientType().contains(clientType)).findFirst();
        if(loginProcessorOptional.isPresent()) {
            return loginProcessorOptional.get();
        }
        return normalLoginProcessor;
    }
}
