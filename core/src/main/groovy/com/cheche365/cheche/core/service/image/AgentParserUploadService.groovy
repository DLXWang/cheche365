package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.AGENT_PARSER_INSURE_10
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9

@Service
@Order(7)
@Slf4j
class AgentParserUploadService extends PurchaseOrderUploadService {

    AgentParserUploadService(PurchaseOrderImageService poiService,
                          PurchaseOrderRepository poRepo,
                          OrderImageService oiService) {
        super(poiService, poRepo, oiService)
    }

    @Override
    Map toUpload(Map initParams) {
        if (OrderStatus.Enum.INSURE_FAILURE_7 == initParams.order.status) {
            return poiService.toUploadImage(AGENT_PARSER_INSURE_10)
        }
        null
    }

    @Override
    boolean support(Map initParams) {
        return AGENTPARSER_9 == initParams.quoteRecord?.type && initParams.order?.status == OrderStatus.Enum.INSURE_FAILURE_7
    }

    @Override
    PurchaseOrderImageScene getImageScene(Map initParams) {
        return AGENT_PARSER_INSURE_10
    }
}
