package com.cheche365.cheche.ordercenter.aop;

import com.cheche365.cheche.ordercenter.third.clink.ClinkService;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by yellow on 2017/9/26.
 * 登进登出的相关切点操作
 */
@Component
@Aspect
public class LoginAspect {

    @Autowired
    private ClinkService clinkService;

    @Pointcut("@annotation(com.cheche365.cheche.ordercenter.annotation.LoginIn)")
    public void loginInAfter() {
    }

    //@Pointcut("@annotation(com.cheche365.cheche.ordercenter.annotation.LoginOut)")
    @Pointcut("execution(* org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler.logout(..))")
    public void loginOutBefore() {
    }

    @After(value="loginInAfter()")
    public void loginInAfterHandler(){
        clinkService.onLine();
    }

    @Before(value="loginOutBefore()")
    public void loginOutBeforeHandler(){
        clinkService.offLine();
    }
}
