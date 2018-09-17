package com.cheche365.cheche.core.tools

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.Memoized
import org.reflections.Reflections
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata

import javax.persistence.Entity
import java.lang.reflect.Method

import static java.lang.reflect.Modifier.isStatic

/**
 * Created by zhengwei on 30/10/2017.
 */
class SystemTool {

    static final REFLECTIONS = new Reflections("com.cheche365.cheche")


    @Memoized
    static findModel(String model){
        REFLECTIONS
            .getTypesAnnotatedWith(Entity)
            .find {it.simpleName.equalsIgnoreCase(model)}
    }

    @Memoized
    static findRepoClass(String model){
        findModel(model)
            .with{ Class modelClass ->
                REFLECTIONS
                    .getSubTypesOf(CrudRepository)
                    .find { modelClass == new DefaultRepositoryMetadata(it).domainType }
        }
    }

    @Memoized
    static findRepoBean(Class repoClass){
        ApplicationContextHolder
            .applicationContext
            .getBeansOfType(repoClass)
            .values()
            .first()
    }

    @Memoized
    static findRepoBean(String model){
        findRepoBean(findRepoClass(model))
    }

    static findRepoBean(String model, action){

        findRepoClass(model)
        .with{
            findRepoBean(it)
        }
        .with {
            action.call(it)
        }
    }

    static callRepoFindBy(String model, Map conditions){
        Class repoClass = findRepoClass(model)
        conditions = conditions.collectEntries {[it.key.toLowerCase(), it.value]}
        Method targetMethod = matchFindBy(repoClass.declaredMethods, conditions.keySet())
        def typedArgs = handleArgs(targetMethod, conditions)

        findRepoBean(repoClass)
        .with {
            it."${targetMethod.name}"(*typedArgs)
        }
    }

    static matchFindBy(Method[] methods, Set conditionNames){
        methods
            .findAll {it.name.startsWith('findBy')  || it.name.startsWith('findFirstBy') }
            .find { method ->
                def methodNameParts = (parseFindByMethod(method.name) as Set).collect {it.toLowerCase()}
                methodNameParts.sort() == conditionNames.sort()
            }
    }


    static handleArgs(Method method, Map conditions){
        def typedArgs = []

        parseFindByMethod(method.name)
            .eachWithIndex{ String model, int i ->
                Class argClass = method.parameterTypes[i]
                if(Entity in argClass.declaredAnnotations*.annotationType()){
                    typedArgs << findRepoBean(argClass.simpleName){repo -> repo.findOne(conditions."${model.toLowerCase()}" as Long)}
                } else {
                    typedArgs << conditions."${model.toLowerCase()}".asType(method.parameterTypes[i])
                }

            }

        return typedArgs

    }

    static parseFindByMethod(String methodName){
        (methodName -'findBy' - 'findFirstBy')
            .split('OrderBy')[0]
            .split('And')
            .findAll {!it.toLowerCase().endsWith('true')}
    }


    static initialCase(String original, caseUpdater){
        original.toLowerCase()
        caseUpdater(original[0]) + original[1..-1]
    }

    static asMap(pojo){

        def jsonIgnoreFields = findFieldByAnnotation(pojo, JsonIgnore)
        pojo.class.declaredFields.findAll { !it.synthetic && !jsonIgnoreFields.contains(it.name) && !isStatic(it.modifiers)}.collectEntries {
            [ (it.name): pojo."$it.name" ]
        }
    }

    static findFieldByAnnotation(pojo, Class annotation){
        pojo
            .getClass()
            .declaredFields
            .findAll { annotation in it.declaredAnnotations*.annotationType() }
            .collect { it.name }
    }

    @Memoized
    static newInstancesByClass(Class classType) {
        REFLECTIONS.getSubTypesOf(classType).collect {
            it.newInstance()
        }
    }


}
