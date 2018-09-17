package com.cheche365.cheche.piccuk.tob.pages

import geb.Page


/**
 * ie接续确认页面
 * Created by suyaqiang on 2017/12/12.
 */
class ContinuePage extends Page {

    static url = 'https://10.134.136.48:8888/casserver/login?service=http%3A%2F%2F10.134.136.48%3A80%2Fportal%2Findex.jsp'

    static content = {
        continueButton(to: LoginPage, wait: true) { $('#overridelink') }
    }

    static at = {
        title == '证书错误: 导航已阻止'
    }

}
