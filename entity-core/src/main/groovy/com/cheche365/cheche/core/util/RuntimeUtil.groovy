package com.cheche365.cheche.core.util

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException

import static java.lang.reflect.Modifier.isStatic

/**
 * Created by zhengwei on 11/9/15.
 */
class RuntimeUtil {

    static String getEvnProfile() {
        return System.getProperty("spring.profiles.active");
    }

    static boolean isProductionEnv(){
        return "production" == getEvnProfile();
    }

    static boolean isDevEnv() {
        return "dev" == getEvnProfile() || getEvnProfile().contains("intranet");
    }

    def static loadEnum(repo, Class outerClass, Class enumClass, createAdditionalProperties = { field -> [:] }){

        def repoInstance = ApplicationContextHolder.getApplicationContext()?.getBean(repo)
        def ALL = []

        enumClass.declaredFields
            .findAll{isStatic(it.modifiers) && it.type == outerClass && it.name.contains('_') && it.name.split('_').last().isInteger()}
            .collect {
                [
                    name: it.name,
                    id: (it.name.split('_').last() as Long)
                ] + createAdditionalProperties(it)
            }
            .with{consInfo ->
                if(repoInstance) {
                    repoInstance.findAll().each{ dbItem ->
                        def cons = consInfo.find{it.id == dbItem.id}
                        if(cons && cons.name){
                            enumClass[cons.name] = dbItem
                            ALL << enumClass[cons.name]
                        }
                    }
                } else if(isDevEnv()){
                    consInfo.each{ cons ->
                        enumClass[cons.name] = outerClass.newInstance().with { enumInstance ->
                            cons.each { propName, value ->
                                if ('name' != propName) {
                                    enumInstance[propName] = value
                                }
                            }
                            enumInstance
                        }
                        ALL << enumClass[cons.name]
                    }
                } else {
                    throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "${outerClass.simpleName}初始化失败");
                }
            }
        return ALL
    }

    def static loadEnum(Class outerClass, Class enumClass, Closure init){

        def staticFields = enumClass.declaredFields.findAll{isStatic(it.modifiers) && it.type == outerClass}.collect {it.name}
        staticFields.each{
            init(it)
        }
    }

    static boolean isNonAuto(String orderNo){
        orderNo.startsWith('A') || orderNo.startsWith('N')
    }

}
