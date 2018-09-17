package com.cheche365.cheche.core.util

import groovy.xml.MarkupBuilder

/**
 * Created by Administrator on 2017/12/22.
 */
class FormUtil {

    static Map buildForm(String url, Map params,String method){
        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer).with {
            it.setEscapeAttributes(false)
            it
        }
        markup.html{
            form (id:formId(), action:url, method:method){
                params?.each {
                    input(type: 'hidden', name: it.key, id: it.key, value: it.value)
                }


            }
            script "document.getElementById('${formId()}').submit()"
        }
        [form:writer.toString()]
    }

    static formId(){
        "${this.getClass().getSimpleName()}Form"
    }
}
