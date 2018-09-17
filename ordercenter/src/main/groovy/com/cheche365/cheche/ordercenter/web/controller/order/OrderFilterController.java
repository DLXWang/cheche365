package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.common.excel.entity.params.ExportParams;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.OrderSourceType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.OrderOperationInfoService;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.service.order.OrderFilterService;
import com.cheche365.cheche.ordercenter.service.order.OrderFinderService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.web.model.order.OrderViewData;
import com.cheche365.cheche.ordercenter.web.model.order.OrderFilterRequestParams;
import com.cheche365.cheche.ordercenter.web.model.order.StopRestartOrderViewData;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 出单中心订单筛选
 * Created by wangfei on 2015/4/30.
 */
@RestController
@RequestMapping("/orderCenter/order")
public class OrderFilterController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderFilterService orderFilterService;

    @Autowired
    private OrderFinderService orderFinderService;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;
    /**
     * 订单查询
     */
    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    @VisitorPermission("or0103")
    public DataTablePageViewModel<OrderViewData> find(OrderFilterRequestParams reqParams, PublicQuery query) {
        Page<OrderOperationInfo> page = orderFilterService.filterOrders(reqParams);
        List<OrderViewData> modelList = new ArrayList<OrderViewData>();
        page.getContent().forEach(orderOperationInfo -> modelList.add(createResult(orderOperationInfo)));
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<OrderViewData>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @VisitorPermission("or0103")
    public Long count(OrderFilterRequestParams reqParams) {
        return orderFilterService.countOrders(reqParams);
    }

    @RequestMapping(value = "/exportDelivery", method = RequestMethod.GET)
    @VisitorPermission("or0103")
    public ResultModel exportDelivery(@RequestParam(value = "checkedIds") String[] checkedIds, HttpServletResponse response) {
        List<Object[]> dataList = orderFilterService.findDeliveryInfo(checkedIds);
        return exportDeliveryData(dataList, response);
    }


    /**
     * 停复驶订单查询
     */
    @RequestMapping(value = "/stopRestartFilter", method = RequestMethod.GET)
    @VisitorPermission("or0103")
    public DataTablePageViewModel<StopRestartOrderViewData> findOrder(OrderFilterRequestParams reqParams, PublicQuery query) {
        Page<Object[]> page = orderFilterService.filterStopRestartOrders(reqParams);
        List<String> idList = new ArrayList<>();
        page.getContent().forEach(object -> idList.add(object[0].toString()));
        Map<String, String> stopMap = new HashMap<String, String>();
        Map<String, String> restartMap = new HashMap<String, String>();
        if (idList.size() > 0) {
            orderFilterService.findStopDataByIds(idList).forEach(object -> stopMap.put(object[0].toString(), object[1].toString() + "," + object[2].toString()));
            orderFilterService.findRestartDataByIds(idList).forEach(object -> restartMap.put(object[0].toString(), object[1] == null ? "" : object[1].toString()));
        }
        List<StopRestartOrderViewData> modelList = new ArrayList<>();
        page.getContent().forEach(object -> modelList.add(orderFilterService.getStopRestartOrderViewData(object, stopMap, restartMap)));

        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
    }

    /**
     * 停复驶订单查询结果导出
     */
    @RequestMapping(value = "/exportStopRestart", method = RequestMethod.GET)
    @VisitorPermission("or0103")
    public ResultModel exportStopRestart(@RequestParam(value = "checkedIds") String[] checkedIds, HttpServletResponse response) {
        List<Object[]> dataList = orderFilterService.findCheckedOrderInfo(checkedIds);
        Map<String, String> stopMap = new HashMap<String, String>();
        Map<String, String> restartMap = new HashMap<String, String>();
        orderFilterService.findStopDataByIds(Arrays.asList(checkedIds)).forEach(object -> stopMap.put(object[0].toString(), object[1].toString() + "," + object[2].toString()));
        orderFilterService.findRestartDataByIds(Arrays.asList(checkedIds)).forEach(object -> restartMap.put(object[0].toString(), object[1] == null ? "" : object[1].toString()));
        return exportStopRestartData(dataList, stopMap, restartMap, response);
    }

    /**
     * 封装展示层实体
     *
     * @return PageViewModel<OrderViewData>
     */
    public OrderViewData createResult(OrderOperationInfo orderOperationInfo) {
        OrderViewData viewData = orderFinderService.getOrderViewData(orderOperationInfo);
        viewData.setOptionValue("");
        return viewData;
    }

    /**
     * 订单查询 导出选中订单
     */
    public ResultModel exportDeliveryData(List<Object[]> exportDataList, HttpServletResponse response) {
        ExportParams params = new ExportParams("订单配送信息表");
        List<ExcelExportEntity> orderDeliveryInfo = new ArrayList<>();
        orderDeliveryInfo.add(new ExcelExportEntity("序 号", "number", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("群号", "null", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("出单时间", "orderDate", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("支付金额（实付金额）", "paidAmount", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("差额", "null", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("出单机构", "null", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("付款机构", "null", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("保险公司", "insuranceCompany", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("出单平台", "channel", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("车主", "owner", 15));
        orderDeliveryInfo.add(new ExcelExportEntity("车牌号", "licensePlateNo", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("订单号", "orderNo", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("保费总额", "null", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("交强险", "compulsoryPremium", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("车船税", "autoTax", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("商业险", "premium", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("交强点位", "null", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("商业点位", "null", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("点位优惠", "null", 10));

        orderDeliveryInfo.add(new ExcelExportEntity("活动优惠", "gift", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("加油卡", "null", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("京东", "null", 25));
        orderDeliveryInfo.add(new ExcelExportEntity("付款方式", "null", 10));
        orderDeliveryInfo.add(new ExcelExportEntity("实物礼品", "null", 25));

        orderDeliveryInfo.add(new ExcelExportEntity("备注", "null", 10));

        orderDeliveryInfo.add(new ExcelExportEntity("客户电话（用户下单手机号）", "userMobile", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("被保险人身份证号", "insuredIdNo", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("车架号", "vinNo", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("发动机号", "engineNo", 20));

        orderDeliveryInfo.add(new ExcelExportEntity("地址", "address", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("收件人", "receiverPerson", 20));
        orderDeliveryInfo.add(new ExcelExportEntity("电话", "mobile", 20));
        List deliveryList = new ArrayList<>();
        int i = 1;
        for (Object[] obj : exportDataList) {
            Map deliveryMap = new HashMap<String, Object>();
            OrderOperationInfo orderOperationInfo = orderOperationInfoService.getByPurchaseOrderId(Long.valueOf(String.valueOf(obj[0])));
            PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
            deliveryMap.put("null", "");
            deliveryMap.put("number", i);
            deliveryMap.put("orderDate", orderOperationInfo.getConfirmOrderDate() != null?DateUtils.getDateString(orderOperationInfo.getConfirmOrderDate(), DateUtils.DATE_LONGTIME24_PATTERN):"");
            deliveryMap.put("paidAmount", obj[12]);
            deliveryMap.put("channel", purchaseOrder.getSourceChannel().getDescription());
            deliveryMap.put("insuranceCompany", obj[1]);
            deliveryMap.put("owner", obj[2]);
            deliveryMap.put("licensePlateNo", obj[3]);
            deliveryMap.put("orderNo", obj[4]);
            deliveryMap.put("compulsoryPremium", obj[14]);
            deliveryMap.put("autoTax", obj[15]);
            deliveryMap.put("premium", obj[16]);
            deliveryMap.put("gift", purchaseOrderGiftService.getGiftInfo(Long.valueOf(String.valueOf(obj[0])), purchaseOrder));
            String obj6 = (obj[6] == null)?"":obj[6].toString();
            String obj7 = (obj[7] == null)?"":obj[7].toString();
            String obj8 = (obj[8] == null)?"":obj[8].toString();


            String address = ((obj[5] == null) ? obj6 : (obj[5].toString() + obj6)) + obj7 + obj8;
            deliveryMap.put("address", address);
            deliveryMap.put("receiverPerson", obj[9]);
            deliveryMap.put("userMobile", purchaseOrder.getApplicant() == null ? "":purchaseOrder.getApplicant().getMobile());
            deliveryMap.put("insuredIdNo", Objects.toString(obj[17],Objects.toString(obj[21],"")) + " ");
            deliveryMap.put("vinNo", obj[18]);
            deliveryMap.put("engineNo", obj[19]);
            deliveryMap.put("mobile", obj[10]);
            deliveryList.add(deliveryMap);
            i++;
        }
        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
        Workbook workbook = ExcelExportUtil.exportExcel(params, orderDeliveryInfo, deliveryList);
        return ResponseOutUtil.excelExport(workbook, response, currentDate + "单证中心数据表.xls");
    }

    /**
     * 停复驶订单查询 导出选中订单
     */
    public ResultModel exportStopRestartData(List<Object[]> exportDataList, Map<String, String> stopMap, Map<String, String> restartMap, HttpServletResponse response) {
        ExportParams params = new ExportParams("停复驶订单信息表");
        List<ExcelExportEntity> stopRestartInfo = new ArrayList<>();
        stopRestartInfo.add(new ExcelExportEntity("订单创建时间", "createTime", 20));
        stopRestartInfo.add(new ExcelExportEntity("订单编号", "orderNo", 20));
        stopRestartInfo.add(new ExcelExportEntity("车主姓名", "owner", 15));
        stopRestartInfo.add(new ExcelExportEntity("车牌号", "licenseNo", 20));
        stopRestartInfo.add(new ExcelExportEntity("城市", "quoteArea", 25));
        stopRestartInfo.add(new ExcelExportEntity("产品平台", "channel", 25));
        stopRestartInfo.add(new ExcelExportEntity("来源(微车CPS)", "orderSource", 20));
        stopRestartInfo.add(new ExcelExportEntity("保费实付金额", "paidAmount", 20));
        stopRestartInfo.add(new ExcelExportEntity("商业险金额", "premium", 20));
        stopRestartInfo.add(new ExcelExportEntity("累计停驶次数", "stopNum", 25));
        stopRestartInfo.add(new ExcelExportEntity("累计停驶天数", "totalStopDays", 20));
        stopRestartInfo.add(new ExcelExportEntity("累计复驶天数", "totalRestartDays", 20));
        stopRestartInfo.add(new ExcelExportEntity("累计退还保费", "totalRefundAmount", 20));
        stopRestartInfo.add(new ExcelExportEntity("当前停复驶状态", "status", 20));
        stopRestartInfo.add(new ExcelExportEntity("最近停驶日", "lastStopBeginDate", 20));
        stopRestartInfo.add(new ExcelExportEntity("最近停驶天数", "lastStopDays", 20));
        stopRestartInfo.add(new ExcelExportEntity("最近复驶日", "lastRestartBeginDate", 20));
        List stopRestartList = new ArrayList<>();
        for (Object[] obj : exportDataList) {
            Map stopRestartMap = new HashMap<String, Object>();
            stopRestartMap.put("createTime", obj[1].toString().substring(0, 19));
            stopRestartMap.put("orderNo", obj[2]);
            stopRestartMap.put("owner", obj[3]);
            stopRestartMap.put("licenseNo", obj[4]);
            stopRestartMap.put("quoteArea", obj[5]);
            stopRestartMap.put("channel", obj[6]);

            if (obj[7] != null) {
                if (String.valueOf(obj[7]).equals(OrderSourceType.Enum.CPS_CHANNEL_1.getName()) && (String.valueOf(obj[8]).equals("407") || String.valueOf(obj[8]).equals("408"))) {
                    stopRestartMap.put("orderSource", "微车CPS");
                } else {
                    stopRestartMap.put("orderSource", obj[7]);
                }
            }

            stopRestartMap.put("paidAmount", obj[9]);
            stopRestartMap.put("premium", obj[10]);
            stopRestartMap.put("status", obj[11]);
            stopRestartMap.put("stopNum", obj[12]);
            stopRestartMap.put("totalStopDays", obj[13]);
            stopRestartMap.put("totalRestartDays", obj[14]);
            if (stopMap.get(obj[0].toString()) != null) {
                String[] lastStopData = stopMap.get(obj[0].toString()).split(",");
                stopRestartMap.put("lastStopBeginDate", lastStopData[0]);
                stopRestartMap.put("lastStopDays", lastStopData[1]);
            }
            if (restartMap.get(obj[0].toString()) != null) {
                stopRestartMap.put("lastRestartBeginDate", restartMap.get(obj[0].toString()));
            }

            stopRestartList.add(stopRestartMap);
        }
        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
        Workbook workbook = ExcelExportUtil.exportExcel(params, stopRestartInfo, stopRestartList);
        return ResponseOutUtil.excelExport(workbook, response, currentDate + "导出停复驶订单信息表.xls");
    }

}
