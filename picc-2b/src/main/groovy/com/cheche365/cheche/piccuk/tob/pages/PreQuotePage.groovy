package com.cheche365.cheche.piccuk.tob.pages

import geb.Page

/**
 * 登录页
 * Created by suyaqiang on 2017/12/12.
 */
class PreQuotePage extends Page {

    static url = 'http://10.134.136.48:8000/prpall/index.jsp?calogin'

    static content = {
        toQuoteButton(wait: true) { $('input', name: 'quick_proposal') }

    }

//    static at = {
//        title == 'PICC承保系统(应用程序版本:V2.0.5.3_P_5_0 数据库版本:V2.0.5.3_P_5_0 )'
//    }

}
