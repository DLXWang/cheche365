package com.cheche365.cheche.core.service.spi

import javax.servlet.http.HttpSession

/**
 * Created by zhengwei on 21/11/2017.
 */
interface IHTTPContext {

    //同一线程下使用，直接获取当前session对象
    HttpSession currentSession()

    //跨线程情况使用，返回包括session所有属性的map
    Map currentSession(id)

    void copySession()

    void removeSession(id)

    int sessionSize()
}
