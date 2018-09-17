package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.Feedback;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.service.FeedbackService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 反馈相关资源
 */
@RestController
@RequestMapping("/"+ ContextResource.VERSION_NO+"/feedbacks")
@VersionedResource(from = "1.0")
public class FeedbackResource extends ContextResource {

    @Autowired
    private FeedbackService feedbackService;

    @RequestMapping(value="", method= RequestMethod.POST)
    public HttpEntity addFeedback(@RequestBody(required = true) Feedback feedback, HttpServletRequest request) {
        User user = safeGetCurrentUser();
        if(user != null){
            feedback.setUser_id(user.getId());
            feedback.setMobile(user.getMobile());
        }
        feedback.setChannel(getChannel());
        feedback.setCreate_time(new Date());
        feedbackService.addFeedback(feedback);
        return getResponseEntity(null);
    }
}
