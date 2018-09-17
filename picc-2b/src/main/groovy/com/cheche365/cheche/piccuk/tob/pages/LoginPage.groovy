package com.cheche365.cheche.piccuk.tob.pages

import geb.Page

/**
 * 登录页
 * Created by suyaqiang on 2017/12/12.
 */
class LoginPage extends Page {

    static content = {
        userName { $('#username1') }
        passWord { $('#password1') }
        loginButton(to : PortalPage, wait : true) { $('#button') }
    }

    static at = {
        title == 'PICC-核心业务系统-登录中心'
    }


}
