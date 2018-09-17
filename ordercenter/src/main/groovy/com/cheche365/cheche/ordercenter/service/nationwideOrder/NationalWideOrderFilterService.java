package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AgentRepository;
import com.cheche365.cheche.core.repository.OrderCooperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.ordercenter.web.model.order.OrderFilterRequestParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by taguangyao on 2015/11/20.
 */
@Service
public class NationalWideOrderFilterService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCooperationInfoRepository orderCooperationInfoRepository;


    @Autowired
    private AgentRepository agentRepository;


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;


    public Page<OrderCooperationInfo> filterOrders(OrderFilterRequestParams reqParams) {
        try {
            Page<OrderCooperationInfo> operationInfoPage = this.filterBySpecAndPaginate(reqParams,
                this.buildPageable(reqParams.getCurrentPage(), reqParams.getPageSize()));
            return operationInfoPage;
        }catch (Exception e){
            logger.error("filter order has error", e);
        }
        return null;
    }

    public Page<OrderCooperationInfo> filterBySpecAndPaginate(OrderFilterRequestParams reqParams, Pageable pageable) {
        return orderCooperationInfoRepository.findAll(new Specification<OrderCooperationInfo>() {
            public Predicate toPredicate(Root<OrderCooperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderCooperationInfo> criteriaQuery = cb.createQuery(OrderCooperationInfo.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();

                /**
                 * 精准查找
                 */
                findByPrecision(root, cb, predicateList, reqParams);

                /**
                 * 平台类查找
                 */
                findByPlatform(root, cb, predicateList, reqParams, query);

                /**
                 * 按地区查找
                 */
                findByArea(root, cb, predicateList, reqParams);

                /**
                 * 按指定人查找
                 */
                findByPerson(root, cb, predicateList, reqParams);

                /**
                 * 按渠道查找
                 */
                findByChannel(root, cb, predicateList, reqParams);

                /**
                 * 按状态查找
                 */
                findByStatus(root, cb, predicateList, reqParams);

                /**
                 * 按日期查找
                 */
                findByDate(root, cb, predicateList, reqParams);
                /**
                 * 按出单机构查找
                 */
                findByInstitution(root, cb, predicateList, reqParams);
                Path<OrderType> orderTypePath = root.get("purchaseOrder").get("type");
                predicateList.add(cb.equal(orderTypePath, OrderType.Enum.INSURANCE.getId()));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    private void findByInstitution(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 出单机构
        if(reqParams.getInstitution() != null && reqParams.getInstitution() != 0) {
            Path<Long> institutionPath = root.get("institution").get("id");//下单时间
            predicateList.add(cb.equal(institutionPath, reqParams.getInstitution()));
        }
    }

    private void findByDate(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 下单日期
        if(StringUtils.isNotBlank(reqParams.getOrderStartDate()) && StringUtils.isNotBlank(reqParams.getOrderEndDate())) {
            Path<Date> createTimePath = root.get("purchaseOrder").get("createTime");//下单时间
            Date startDate= DateUtils.getDayStartTime(DateUtils.getDate(reqParams.getOrderStartDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Date endDate= DateUtils.getDayEndTime(DateUtils.getDate(reqParams.getOrderEndDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Expression<Date> startDateExpression = cb.literal(startDate);
            Expression<Date> endDateExpression = cb.literal(endDate);
            predicateList.add(cb.between(createTimePath, startDateExpression, endDateExpression));
        }
    }

    private void findByStatus(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 出单状态
        if (reqParams.getStatus() != null) {
            Path<Long> statusPath = root.get("status").get("id");
            predicateList.add(cb.equal(statusPath, reqParams.getStatus()));
        }
        //订单状态
        if (null != reqParams.getOrderStatus()) {
            Path<Long> orderStatusIdPath = root.get("purchaseOrder").get("status").get("id");//订单状态
            if(reqParams.getOrderStatus() == 0){
                predicateList.add(cb.isNotNull(orderStatusIdPath));
            }else {
                predicateList.add(cb.equal(orderStatusIdPath, reqParams.getOrderStatus()));
            }
        }
        //支付方式
        if (null != reqParams.getPaymentChannel()) {
            Path<Long> paymentChannelIdPath = root.get("purchaseOrder").get("channel").get("id");//支付方式
            if(reqParams.getPaymentChannel() == 0){
                predicateList.add(cb.isNotNull(paymentChannelIdPath));
            }else{
                predicateList.add(cb.equal(paymentChannelIdPath, reqParams.getPaymentChannel()));
            }
        }
    }

    private void findByChannel(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {

        // CPS渠道
        if (reqParams.getCpsChannel() != null) {
            Path<Long> orderSourceIdPath = root.get("purchaseOrder").get("orderSourceId");//订单来源对象id
            Path<OrderSourceType> orderSourceTypePath = root.get("purchaseOrder").get("orderSourceType");//订单来源类型，区分CPS渠道，大客户，滴滴专车增补保险
            predicateList.add(cb.equal(orderSourceTypePath, OrderSourceType.Enum.CPS_CHANNEL_1));
            if (reqParams.getCpsChannel() == 0) {
                predicateList.add(cb.isNotNull(orderSourceIdPath));
            } else {
                predicateList.add(cb.equal(orderSourceIdPath, reqParams.getCpsChannel()));
            }
        }
    }

    private void findByPerson(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 指定人
        if (reqParams.getAssigner() != null) {
            Path<Long> assignerPath = root.get("assigner").get("id");//指定人
            if(reqParams.getAssigner()==0){
                predicateList.add(cb.isNotNull(assignerPath));
            }else{
                predicateList.add(cb.equal(assignerPath, reqParams.getAssigner()));
            }
        }
        // 推荐人
        if (reqParams.getAgent() != null) {
            Path<Long> userTypePath = root.get("purchaseOrder").get("applicant").get("userType").get("id");// 用户类型id
            Path<Long> userPath = root.get("purchaseOrder").get("applicant").get("id");// 用户id
            predicateList.add(cb.equal(userTypePath, UserType.Enum.Agent.getId()));
            if (reqParams.getAgent() == 0) {
                predicateList.add(cb.isNotNull(userPath));
            } else {
                predicateList.add(cb.equal(userPath, agentRepository.findOne(reqParams.getAgent()).getUser().getId()));
            }
        }
    }
    private void findByArea(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 报价区域
        if (reqParams.getQuoteArea() != null) {
            Path<Long> areaPath = root.get("purchaseOrder").get("auto").get("area").get("id");
            Path<OrderType> orderTypePath = root.get("purchaseOrder").get("type");
            predicateList.add(cb.equal(orderTypePath, OrderType.Enum.INSURANCE.getId()));
            if(reqParams.getQuoteArea()==0){
                predicateList.add(cb.isNotNull(areaPath));
            }else{
                predicateList.add(cb.equal(areaPath, reqParams.getQuoteArea()));
            }
        }
    }

    private void findByPlatform(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams, CriteriaQuery<?> query) {
        // 保险公司
        if (reqParams.getInsuranceCompany() != null) {
            /**
             *  root.get("purchaseOrder").get("objId")
             *  这种的底层实现为orderOperationInfo表cross join表purchaseOrder；形成一个笛卡尔积，效率相对来说不高
             *  因此修改为orderOperationInfo表join表purchaseOrder；
             */
            Join<OrderOperationInfo, PurchaseOrder> orderJoin = root.join("purchaseOrder");
            Path<Long> objIdPath = orderJoin.get("objId");//报价记录id
            if (reqParams.getInsuranceCompany() == 0) {
                predicateList.add(cb.isNotNull(objIdPath));
            } else {
                Root<QuoteRecord> recordRoot = query.from(QuoteRecord.class);
                Join<QuoteRecord, InsuranceCompany> InsuranceCompanyJoin = recordRoot.join("insuranceCompany");
                predicateList.add(cb.equal(objIdPath,recordRoot.get("id")));
                predicateList.add(cb.equal(InsuranceCompanyJoin.get("id"), reqParams.getInsuranceCompany()));
            }
        }
        // 产品平台
        if (reqParams.getChannel() != null) {
            Path<Long> sourceChannelPath = root.get("purchaseOrder").get("sourceChannel").get("id");//来源
            if (reqParams.getChannel() == 0) {
                predicateList.add(cb.isNotNull(sourceChannelPath));
            } else {
                predicateList.add(cb.equal(sourceChannelPath, reqParams.getChannel()));
            }
        }
    }

    private void findByPrecision(Root<OrderCooperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 车主
        if(StringUtils.isNotEmpty(reqParams.getOwner())) {
            Path<String> ownerPath = root.get("purchaseOrder").get("auto").get("owner");
            predicateList.add(cb.like(ownerPath, reqParams.getOwner() + "%"));
        }
        // 手机号
        if(StringUtils.isNotEmpty(reqParams.getMobile())) {
            Path<String> mobilePath = root.get("purchaseOrder").get("applicant").get("mobile");
            predicateList.add(cb.like(mobilePath, reqParams.getMobile() + "%"));
        }
        // 车牌号
        if(StringUtils.isNotEmpty(reqParams.getLicenseNo())) {
            Path<String> licenseNoPath = root.get("purchaseOrder").get("auto").get("licensePlateNo");
            predicateList.add(cb.like(licenseNoPath, reqParams.getLicenseNo() + "%"));
        }
        // 订单号
        if (StringUtils.isNotBlank(reqParams.getOrderNo())) {
            Path<String> orderNoPath = root.get("purchaseOrder").get("orderNo");
            predicateList.add(cb.equal(orderNoPath, reqParams.getOrderNo()));
        }
    }

    /**
     * 构建分页信息
     * @param currentPage 当前页面
     * @param pageSize 每页显示数
     * @return Pageable
     */
    public Pageable buildPageable(int currentPage, int pageSize){
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage-1, pageSize, sort);
    }

    public HSSFWorkbook createExportExcel(OrderFilterRequestParams requestParams) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("合作订单查询结果");

        HSSFFont font = ExcelUtil.createFont(workbook, "宋体", (short) 12);
        HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, font);

        HSSFFont fontTitle = ExcelUtil.createFont(workbook, "宋体", (short) 14);
        HSSFCellStyle cellStyleTitle = ExcelUtil.createCellStyle(workbook, fontTitle);

        this.createExcelTitle(sheet, 0, cellStyleTitle);

        Page<OrderCooperationInfo> orderCooperationInfoPage = this.filterOrders(requestParams);
        int totalPages = orderCooperationInfoPage.getTotalPages();
        int page = requestParams.getCurrentPage();
        while(page <= totalPages && !CollectionUtils.isEmpty(orderCooperationInfoPage.getContent())) {
            List<OrderCooperationInfo> orderCooperationInfoList = orderCooperationInfoPage.getContent();
            int index = (page - 1) * requestParams.getPageSize() + 1;
            for(OrderCooperationInfo orderCooperationInfo : orderCooperationInfoList) {
                PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
                String paymentStatus;
                Payment payment = paymentRepository.findFirstByChannelInAndPurchaseOrderOrderByIdDesc(PaymentChannel.Enum.ONLINE_CHANNELS, purchaseOrder);
                if (payment != null && payment.getStatus().getId().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2.getId())) {
                    paymentStatus = OrderCenterConstants.PAID_TEXT;
                } else {
                    paymentStatus = OrderCenterConstants.NO_PAID_TEXT;
                }
                String[] contents = {
                    purchaseOrder.getOrderNo(),
                    StringUtils.trimToEmpty(purchaseOrder.getAuto().getOwner()),
                    StringUtils.trimToEmpty(purchaseOrder.getAuto().getLicensePlateNo()),
                    StringUtils.trimToEmpty(orderCooperationInfo.getArea().getName()),
                    StringUtils.trimToEmpty(orderCooperationInfo.getInsuranceCompany().getName()),
                    StringUtils.trimToEmpty(orderCooperationInfo.getInstitution() == null? "" : orderCooperationInfo.getInstitution().getName()),
                    StringUtils.trimToEmpty(String.valueOf(purchaseOrder.getPayableAmount())),
                    StringUtils.trimToEmpty(String.valueOf(purchaseOrder.getPaidAmount())),
                    StringUtils.trimToEmpty(DateUtils.getDateString(purchaseOrder.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN)),
                    StringUtils.trimToEmpty(orderCooperationInfo.getAssigner() == null ? "" : orderCooperationInfo.getAssigner().getName()),
                    StringUtils.trimToEmpty(orderCooperationInfo.getOperator() == null ? "" : orderCooperationInfo.getOperator().getName()),
                    StringUtils.trimToEmpty(DateUtils.getDateString(orderCooperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN)),
                    StringUtils.trimToEmpty(purchaseOrderService.getUserSource(purchaseOrder)),
                    StringUtils.trimToEmpty(paymentStatus),
                    StringUtils.trimToEmpty(orderCooperationInfo.getStatus() == null ? "" : orderCooperationInfo.getStatus().getStatus())};
                ExcelUtil.createStrCellValues(sheet, index, contents, cellStyle);
                index++;
            }
            page++;
            requestParams.setCurrentPage(page);
            orderCooperationInfoPage = this.filterOrders(requestParams);
        }

        return workbook;
    }

    private void createExcelTitle(HSSFSheet sheet, Integer rowNum, HSSFCellStyle cellStyle) {
        String[] titles = {"订单编号","车主","车牌号","城市","保险公司","出单机构","原始金额","实付金额","下单时间",
            "指定人","最后操作人","最后操作时间","来源","支付状态", "当前状态"};
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 5000);
        sheet.setColumnWidth(8, 7000);
        sheet.setColumnWidth(9, 5000);
        sheet.setColumnWidth(10, 5000);
        sheet.setColumnWidth(11, 7000);
        sheet.setColumnWidth(12, 5000);
        sheet.setColumnWidth(13, 5000);
        sheet.setColumnWidth(14, 5000);
        ExcelUtil.createStrCellValues(sheet, rowNum, titles, cellStyle);
    }
}
