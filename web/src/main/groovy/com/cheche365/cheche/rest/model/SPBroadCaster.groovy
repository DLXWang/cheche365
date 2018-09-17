package com.cheche365.cheche.rest.model

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.rest.util.CheckUtil
import com.cheche365.cheche.rest.util.JSONUtil
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.atmosphere.cpr.MetaBroadcaster

import static com.cheche365.cheche.core.model.InsuranceCompany.apiQuoteCompanies
import static com.cheche365.cheche.rest.serverpush.SPConstants.CROSS_JVM_BROADCASTER_ID
import static com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler.*
import static com.cheche365.cheche.rest.QuoterFactory.QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH

@Slf4j
@TupleConstructor
class SPBroadCaster {

    MetaBroadcaster broadcaster
    Object id
    Object quoteFlag
    Channel channel

    private void broadMessage(data, String msgChannel,Boolean isSelf = null, String status = null, String message = null) {
        def result = new SPResult(
            id: this.id,
            quoteFlag: this.quoteFlag,
            channel: msgChannel,
            status: status ? status : SPResult.Status.SUCCESS,
            data: data,
            message: message,
            isSelf: isSelf
        )
        broadcaster.broadcastTo(CROSS_JVM_BROADCASTER_ID, JSONUtil.doSerialize(this.channel, result))
        log.debug("success broadcaster message, channel : {}, msg : {}", msgChannel, result)
    }

    void doOnSuccess(QuoteRecord result, Map additionalParameters) {
        quoteMetaInfo(result, additionalParameters)
        quoteResult(result)
        quoteMarketing(result)
    }

    void quoteResult(QuoteRecord quoteRecord) {
        def data = quoteRecord
        def isSelf = isSelf(quoteRecord.insuranceCompany)
        this.broadMessage(data, SPResult.Channel.RESULT,isSelf)
    }

    void quoteMarketing(QuoteRecord quoteRecord) {
        if (quoteRecord.marketingList) {
            def data = [
                insuranceCompany: quoteRecord.insuranceCompany,
                marketingList   : quoteRecord.marketingList
            ]
            def isSelf = isSelf(quoteRecord.insuranceCompany)
            this.broadMessage(data, SPResult.Channel.MARKETING,isSelf)
        }
    }

    void quoteMetaInfo(QuoteRecord quoteRecord, additionalParameters) {
        if (additionalParameters.get(QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH) && additionalParameters.autoModel) {
            def data = [
                insuranceCompany: quoteRecord.insuranceCompany,
                autoModel       : additionalParameters.autoModel
            ]
            def isSelf = isSelf(quoteRecord.insuranceCompany)
            this.broadMessage(data, SPResult.Channel.METAINFO, isSelf)
        }
    }



    void doOnException(QuoteRecord quoteRecord, Map additionalParameters, Exception ex, Boolean simplifiedQuote = false, Boolean isSelf = true) {
        quoteMetaInfo(quoteRecord, additionalParameters)
        def (data, String channel, String status, String message) = simplifiedQuote ? formatSimplifiedQuoteException(quoteRecord, ex) : formatAsyncQuoteException(quoteRecord, ex)

        if (additionalParameters.get(QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH) && Channel.Enum.IOS_CHEBAOYI_221 == quoteRecord.channel) {
            adaptForIOS(data)
        }
        this.broadMessage(data, channel, isSelf, status, message)
    }

    static void adaptForIOS(original) {
        if(original.supplementInfo?.first()?.key == 'autoModel') {
            def adapted = original.supplementInfo?.first().findAll{it.key != 'options'}
            adapted.options_ios = original.supplementInfo?.first().options
            original.supplementInfo[0] = adapted
            log.debug('adapt for ios auto model data structure')
        }

    }

    void quoteStageResult(QuoteRecord quoteRecord, Map quoteContext, Object msg) {
        if (CheckUtil.quoteable(quoteRecord.channel, quoteContext.sessionAttrs) && msg) {
            def data = [
                insuranceCompany: quoteRecord.insuranceCompany,
                stage           : msg.toString()
            ]
            def isSelf = isSelf(quoteRecord.insuranceCompany)
            this.broadMessage(data, SPResult.Channel.STAGE,isSelf)
        }
    }

    void quoteFinish(QuoteRecord quoteRecord) {
        def data = [
            insuranceCompany: quoteRecord.insuranceCompany,
            stage           : "报价结束"
        ]
        def isSelf = isSelf(quoteRecord.insuranceCompany)
        this.broadMessage(data, SPResult.Channel.FINISH,isSelf)
    }

    void quoteAllFinish() {
        def data = [
            stage: "本次报价结束"
        ]
        this.broadMessage(data, SPResult.Channel.ALL_FINISH)
    }

    private Boolean isSelf(InsuranceCompany insuranceCompany) {
        !apiQuoteCompanies().contains(insuranceCompany)
    }
}
