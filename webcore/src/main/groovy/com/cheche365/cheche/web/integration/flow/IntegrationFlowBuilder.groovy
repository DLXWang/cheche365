package com.cheche365.cheche.web.integration.flow

import com.cheche365.cheche.common.flow.step.Identity
import groovy.transform.TupleConstructor

import static com.cheche365.cheche.common.flow.Constants._CHECKER_EQUAL
import static com.cheche365.cheche.common.flow.Constants._STEP_ROUTER_CONDITIONAL
import static com.cheche365.cheche.common.flow.Constants._STEP_ROUTER_SIMPLE

/**
 * Created by liheng on 2018/5/16 0016.
 */
class IntegrationFlowBuilder {

    private static final _CREATE_ROUTER_SIMPLE = { nextStepsWithRouter ->
        _STEP_ROUTER_SIMPLE.curry nextStepsWithRouter
    }

    private static final _CREATE_ROUTER_CONDITIONAL = { nextStepsWithRouter ->
        _STEP_ROUTER_CONDITIONAL.curry nextStepsWithRouter
    }

    private static final _ATTACH_META_INFO_TO_STEP = { id, stepInstance ->
        stepInstance
    }

    private Map<Object, Class> nameClazzMappings
    private Map<Object, IntegrationFlow> nameFlowMappings
    private Map<Object, Closure> nameTemplateMappings
    private final STEPS = [] as LinkedList


    def call(Closure closure) {
        call0 closure
        def stepsWithRouter = build0 STEPS.clone()
        build stepsWithRouter
    }

    def propertyMissing(step) {
        if (nameClazzMappings[step]) {
            appendSimple step
        } else if (nameFlowMappings?.get(step)) {
            appendSubFlow step
        } else {
            throw new MissingPropertyException(step, this.class)
        }
    }

    /**
     * 路由
     * @param subFlows 子流程
     * @return
     */
    def route(Map<Object, Closure> subFlows) {
        def meta = subFlows.collectEntries { key, subFlow ->
            [(key): new IntegrationFlowBuilder(
                nameClazzMappings: nameClazzMappings,
                nameFlowMappings: nameFlowMappings,
                nameTemplateMappings: nameTemplateMappings
            )(subFlow)]
        }
        STEPS.last.first()().subFlows = meta
        new StepDef(meta.values())
    }

    /**
     * 对首步骤为fork时的特殊处理
     * @param fork
     * @return StepDef
     */
    def fork(Map fork) {
        def meta = [
            {
                new Identity().with _ATTACH_META_INFO_TO_STEP.curry('同前')
            },
            _CREATE_ROUTER_SIMPLE
        ]
        STEPS << meta
        appendFork fork
    }

    /**
     * 用模板生成流程
     * @param templateName 模板名称
     * @param bindings 绑定值
     * @return StepDef
     */
    def make(String templateName, Map bindings) {
        def phClazzMappings = substitute nameClazzMappings, bindings
        def phFlowMappings = substitute nameFlowMappings, bindings
        def nestedFlowMappings = bindings.findAll { _placeHolder, value ->
            value instanceof List
        }.collectEntries { placeHolder, templateWithBindings ->
            def (subTemplate, subTemplateBindings) = templateWithBindings
            def builder = new IntegrationFlowBuilder(
                nameClazzMappings: nameClazzMappings,
                nameFlowMappings: nameFlowMappings,
                nameTemplateMappings: nameTemplateMappings
            )
            def subFlow = builder {
                make subTemplate, subTemplateBindings
            }
            [(placeHolder): subFlow]
        }
        def builder = new IntegrationFlowBuilder(
            nameClazzMappings: nameClazzMappings + phClazzMappings,
            nameFlowMappings: (nameFlowMappings ?: [:]) + phFlowMappings + nestedFlowMappings,
            nameTemplateMappings: nameTemplateMappings
        )
        def flow = builder nameTemplateMappings[templateName]
        appendSubFlow flow
    }


    private call0(Closure closure) {
        def cloned = closure.rehydrate(this, closure.owner, closure.thisObject)
        cloned.resolveStrategy = Closure.DELEGATE_FIRST
        cloned()
    }

    private static build(stepsWithRouter) {
        new IntegrationFlow(stepsWithRouter: stepsWithRouter)
    }

    private build0(steps) {
        // 构造step
        def step = steps.removeFirst()[0]()

        // 构造router
        def (createRouter, nextStepsWithRouter) = steps ? [steps.first()[1], build0(steps)] : [_CREATE_ROUTER_SIMPLE, null]
        def router = createRouter nextStepsWithRouter

        // 返回step－router pair
        new Tuple2(step, router)
    }

    /**
     * 将模板参数中的占位符对应的值替换为原映射中的相应值
     * @param originalMappings 原映射
     * @param bindings 绑定值
     * @return
     */
    private static substitute(originalMappings, bindings) {
        originalMappings?.subMap(bindings.values())?.with { effectiveMappings ->
            bindings.collectEntries { placeHolder, name ->
                [(placeHolder): effectiveMappings[name]]
            }
        } ?: [:]
    }


    private appendSimple(step) {
        def meta = [
            {
                def clazzOrStep = nameClazzMappings[step]
                def attachToStep = _ATTACH_META_INFO_TO_STEP.curry step
                if (clazzOrStep instanceof Class) {
                    nameClazzMappings[step].newInstance().with attachToStep
                } else if (clazzOrStep instanceof TIntegrationStep) {
                    clazzOrStep.with attachToStep
                } else {
                    throw new IllegalArgumentException('错误的参数类型')
                }
            },
            _CREATE_ROUTER_SIMPLE
        ]
        STEPS << meta
        new StepDef(meta)
    }

    private appendFork(Map fork) {
        def meta = [
            {
                fork.collectEntries { condition, subFlow ->
                    def builder = new IntegrationFlowBuilder(
                        nameClazzMappings: nameClazzMappings,
                        nameFlowMappings: nameFlowMappings,
                        nameTemplateMappings: nameTemplateMappings
                    )
                    subFlow = builder(subFlow)
                    [(_CHECKER_EQUAL.curry(condition)): subFlow]
                }
            },
            _CREATE_ROUTER_CONDITIONAL
        ]
        STEPS << meta
        new StepDef(meta)
    }

    private appendSubFlow(IntegrationFlow subFlow) {
        def meta = [
            {
                subFlow
            },
            _CREATE_ROUTER_SIMPLE
        ]
        STEPS << meta
        new StepDef(meta)
    }

    private appendSubFlow(step) {
        appendSubFlow nameFlowMappings[step]
    }


    @TupleConstructor
    private class StepDef {

        def meta

        def rightShift(StepDef stepDef) {
            stepDef
        }

        def rightShift(Map fork) {
            appendFork fork
        }

        def rightShift(IntegrationFlow subFlow) {
            appendSubFlow subFlow
        }

    }

}
