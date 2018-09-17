import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerOrderRepository
import com.cheche365.cheche.core.repository.PartnerUserRepository
import com.cheche365.cheche.core.repository.WebPurchaseOrderRepository
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.partner.config.app.PartnerConfig
import com.cheche365.cheche.web.app.config.WebcoreConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Created by zhengwei on 4/22/16.
 */

@WebAppConfiguration
@ContextConfiguration( classes = [ CoreConfig, WebcoreConfig, PartnerConfig ] )
abstract class PartnerFT extends Specification{

    @Autowired
    PurchaseOrderIdService idService;

    @Autowired
    WebPurchaseOrderRepository purchaseOrderRepository

    @Autowired
    PartnerOrderRepository partnerOrderRepository

    def randomGenerator = new Random();

    @Autowired
    PartnerUserRepository partnerUserRepository;

    def PurchaseOrder nextOrder(){

        def order = getRandomPO()
        order
    }

    def PurchaseOrder getRandomPO(){

        def List<PurchaseOrder> pos = purchaseOrderRepository.getTop10ByChannel(13)
        if(pos.empty) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "purchase_order表中无可用数据，请配置")
        }
        pos.get(randomGenerator.nextInt(pos.size()))
    }

    def order(orderNo) {
        return purchaseOrderRepository.findFirstByOrderNo(orderNo)
    }

    def partnerOrder(id) {
        return partnerOrderRepository.findOne(id)
    }

}
