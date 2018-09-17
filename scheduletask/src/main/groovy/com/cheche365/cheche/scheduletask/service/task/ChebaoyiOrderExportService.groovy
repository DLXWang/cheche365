package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.AttributeType
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.DeliveryInfo
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OrderOperationInfo
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderAttribute
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAttributeRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.scheduletask.model.ChebaoyiOrderExportInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * a端订单导出
 * Created by zhangpengcheng on 2018/6/25.
 */
@Service
public class ChebaoyiOrderExportService {

    Logger logger = LoggerFactory.getLogger(ChebaoyiOrderExportService.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderAttributeRepository purchaseOrderAttributeRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private AreaRepository areaRepository;


    Map<String, List<ChebaoyiOrderExportInfo>> getChebaoyiOrderExport() {
        //结束时间
        Date endTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(), 0, 0, 0, 0), DateUtils.DATE_LONGTIME24_PATTERN);
        //开始时间
        Date startTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(), -1, 0, 0, 0), DateUtils.DATE_LONGTIME24_PATTERN);
        //toa渠道列表
        List<Channel> toAChannels = Channel.agents();
        //查找对应订单
        List<PurchaseOrder> chebaoyiList = purchaseOrderRepository.findChebaoyi(startTime, endTime);
        List<ChebaoyiOrderExportInfo> resultList = new ArrayList<ChebaoyiOrderExportInfo>();
        for (PurchaseOrder po : chebaoyiList) {
            //判断toa渠道
            if (toAChannels.contains(po.getSourceChannel())) {
                String prov = "";
                String city = "";
                String district = "";
                //拼接邮件所需字段
                ChebaoyiOrderExportInfo flag = new ChebaoyiOrderExportInfo();
                flag.setPlatform(po.getSourceChannel().getDescription());
                flag.setDrivedCity(po.getArea().getName());
                flag.setPlateNum(po.getAuto().getLicensePlateNo());
                flag.setVin(po.getAuto().getVinNo());
                flag.setEngineNo(po.getAuto().getEngineNo());
                flag.setBrandNo(po.getAuto().getAutoType().getCode());
                flag.setOwner(po.getAuto().getOwner());
                flag.setGetMailed(po.getDeliveryAddress().getName());
                if (po.getDeliveryAddress().getProvince() != null && po.getDeliveryAddress().getProvince() != "") {
                    Area provA = areaRepository.findById(Long.parseLong(po.getDeliveryAddress().getProvince()));
                    if (provA != null) prov = provA.getName();
                }
                if (po.getDeliveryAddress().getCity() != null && po.getDeliveryAddress().getCity() != "") {
                    Area cityA = areaRepository.findById(Long.parseLong(po.getDeliveryAddress().getCity()));
                    if (cityA != null) city = cityA.getName();
                }
                if (po.getDeliveryAddress().getDistrict() != null && po.getDeliveryAddress().getDistrict() != "") {
                    Area districtA = areaRepository.findById(Long.parseLong(po.getDeliveryAddress().getDistrict()));
                    if (districtA != null) district = districtA.getName();
                }
                flag.setGetAddress(prov + city + district + po.getDeliveryAddress().getStreet());
                flag.setCustomerPhone(po.getDeliveryAddress().getMobile());

                CompulsoryInsurance ci = compulsoryInsuranceRepository.findByQuoteRecordId(po.getObjId());
                Insurance ins = insuranceRepository.findByQuoteRecordId(po.getObjId());



                flag.setTaxNum(String.valueOf(ci != null ? ci.getAutoTax() : 0));
                flag.setForceNum(String.valueOf(ci != null ? ci.getCompulsoryPremium() : 0));
                flag.setJiaoshangNo(ci != null ? ci.getPolicyNo() : "");



                flag.setInsuranceNum(String.valueOf(ins != null ? ins.getPremium() : 0));
                flag.setInsuranceNo(ins != null ? ins.getPolicyNo() : "");
                flag.setCompany(ins != null ? ins.getInsuranceCompany().getName() : ci.getInsuranceCompany().getName());
                flag.setInsPerson(ins != null ? ins.getInsuredName() : ci.getInsuredName());

                flag.setIdentityNo(po.getAuto().getIdentity());
                flag.setOrderNo(po.getOrderNo());
                flag.setMailAddress(po.getApplicant().getEmail());

                flag.setAgentAccount(po.getApplicant().getMobile());

//            PurchaseOrderAttribute poa = purchaseOrderAttributeRepository.findByPurchaseOrderAndType(po, AttributeType.Enum.BOTPY_ACCOUNT_1);

                PurchaseOrderAttribute poa = purchaseOrderAttributeRepository.findFirstByPurchaseOrder(po)
                if (poa != null) {
                    flag.setOrderAccount(poa.getValue());
                }
                resultList.add(flag);

            }
        }
        Map<String, List<ChebaoyiOrderExportInfo>> orderExportInfoMap = new HashMap<>();
        orderExportInfoMap.put("dataInputAmount", resultList);

        return orderExportInfoMap;
    }
}
