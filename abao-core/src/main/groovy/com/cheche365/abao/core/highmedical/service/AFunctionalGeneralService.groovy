package com.cheche365.abao.core.highmedical.service

import com.cheche365.cheche.core.service.spi.IHandlerService
import com.cheche365.cheche.insurance.core.service.TBusinessControllableService
import com.cheche365.flow.core.service.TFunctionalService

import static com.cheche365.cheche.insurance.core.util.FlowUtils.getInsuringFlow
import static com.cheche365.cheche.insurance.core.util.FlowUtils.getOrderingFlow
import static com.cheche365.cheche.insurance.core.util.FlowUtils.getQuotingFlow

/**
 * 服务方法有返回值的报价服务基类
 * Created by suyq on 2016/12/28.
 */
abstract class AFunctionalGeneralService<QO, QR, IO, IR, OO, OR>
    implements IHandlerService<QO, QR, IO, IR, OO, OR>,
               TFunctionalService,
               TBusinessControllableService {


    private static _DEFAULT_GET_RESULT_OBJECT = { context, businessObjects ->
        context.resultObject
    }

    @Override
    QR quote(QO quoteObject) {
        if (quotingFlowEnabled) {
            def context = createQuoteContext(quoteObject) + createContext(quoteObject)
            def flow = getQuotingFlow context
            service null, flow, context
        }
    }

    @Override
    IR insure(IO insureObject) {
        if (insuringFlowEnabled) {
            def context = createInsureContext(insureObject) + createContext(insureObject)
            def flow = getInsuringFlow context
            service null, flow, context
        }
    }

    @Override
    OR order(OO orderObject) {
        if (orderingFlowEnabled) {
            def context = createOrderContext(orderObject) + createContext(orderObject)
            def flow = getOrderingFlow context
            service null, flow, context
        }
    }

    /**
     * 创建报价通用context
     */
    protected createQuoteContext(quoteObject) {
        [quoteObject: quoteObject] + createCommonContext(quoteObject, quoteResultObject)
    }

    /**
     * 创建核保通用context
     */
    protected createInsureContext(insureObject) {
        [insureObject: insureObject] + createCommonContext(insureObject, insureResultObject)
    }

    /**
     * 创建晨报通用context
     */
    protected createOrderContext(orderObject) {
        [orderObject: orderObject] + createCommonContext(orderObject, orderResultObject)
    }

    /**
     * 获取报价服务方法返回值的闭包
     * @return 闭包调用的返回值，将作为服务方法的返回值
     */
    protected getQuoteResultObject() {
        _DEFAULT_GET_RESULT_OBJECT
    }

    /**
     * 获取报核保服务方法返回值的闭包
     * @return 闭包调用的返回值，将作为服务方法的返回值
     */
    protected getInsureResultObject() {
        _DEFAULT_GET_RESULT_OBJECT
    }

    /**
     * 获取承保服务方法返回值的闭包
     * @return 闭包调用的返回值，将作为服务方法的返回值
     */
    protected getOrderResultObject() {
        _DEFAULT_GET_RESULT_OBJECT
    }

    /**
     * 子类创建context
     * @param object
     * @return
     */
    abstract protected createContext(object)


    private static createCommonContext(requestObject, getResultObject) {
        [
            getResultObject     : getResultObject,
            additionalParameters: requestObject.additionalParameters
        ]
    }

}
