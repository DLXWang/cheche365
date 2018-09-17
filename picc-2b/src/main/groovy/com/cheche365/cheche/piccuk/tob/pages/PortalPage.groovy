package com.cheche365.cheche.piccuk.tob.pages

import geb.Page

/**
 * 登录页
 * Created by suyaqiang on 2017/12/12.
 */
class PortalPage extends Page {

    static url = 'http://10.134.136.48/portal/index.jsp'

    static content = {
        toInsuranceButton(wait: true) { $('a', text: '北京车险承保系统') }
    }

    static at = {
        title == 'PICC第三代核心业务系统' || title == 'PICC-核心业务系统-登录中心'
    }

}
