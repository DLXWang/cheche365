package com.cheche365.cheche.operationcenter.service.appointmentInsurance;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.AppointmentInsurance;
import com.cheche365.cheche.core.model.QRCodeChannel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QRCodeChannelRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.operationcenter.web.model.appointmentInsurance.AppointmentInsuranceViewModel;
import com.cheche365.cheche.core.model.WechatQRCode;
import com.cheche365.cheche.core.repository.WechatQRCodeRepository;
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

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangshitao on 2015/11/25.
 */
@Service("opcAppointmentInsuranceService")
public class AppointmentInsuranceService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private AppointmentInsuranceRepository appointmentInsuranceRepository;

    @Autowired
    private QRCodeChannelRepository qrCodeChannelRepository;

    @Autowired
    private WechatQRCodeRepository wechatQRCodeRepository;

    public PageViewModel<AppointmentInsuranceViewModel> search(Integer currentPage, Integer pageSize, String keyword,String expireBefore, Integer keyType) {
        try {
            Page<AppointmentInsurance> appointmentInsurancePage = this.findBySpecAndPaginate(keyword, keyType,expireBefore,
                this.buildPageable(currentPage, pageSize));
            return this.createResult(appointmentInsurancePage);
        } catch (Exception e) {
            logger.error("find AppointmentInsurance info by page has error", e);
        }
        return null;
    }

    /**
     * 编辑地推用户
     * @param appointmentInsuranceId
     * @param userId
     * @param name
     * @param licensePlateNo
     * @param expireBefore
     */
    public void appointmentInsurance(String appointmentInsuranceId,String userId,String name,String licensePlateNo,String expireBefore,String comment){
        AppointmentInsurance appointmentInsurance = new AppointmentInsurance();
        appointmentInsurance = this.appointmentInsuranceRepository.findById(Long.parseLong(appointmentInsuranceId));
        User user = appointmentInsurance.getUser();
        user.setId(Long.parseLong(userId));
        user.setName(name);
        appointmentInsurance.setId(Long.parseLong(appointmentInsuranceId));
        appointmentInsurance.setUser(user);
        appointmentInsurance.setLicensePlateNo(licensePlateNo);
        appointmentInsurance.setUpdateTime(new Date());
        appointmentInsurance.setExpireBefore(DateUtils.getDate(expireBefore, DateUtils.DATE_SHORTDATE_PATTERN));
        appointmentInsurance.setComment(comment);
        appointmentInsuranceRepository.save(appointmentInsurance);
    }

    /**
     * 封装展示层实体
     * @param page 分页信息
     * @return PageViewModel<PageViewData>
     */
    public PageViewModel<AppointmentInsuranceViewModel> createResult(Page page) throws Exception {
        PageViewModel model = new PageViewModel<AppointmentInsuranceViewModel>();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);

        List<AppointmentInsuranceViewModel> pageViewDataList = new ArrayList<AppointmentInsuranceViewModel>();
        for (AppointmentInsurance appointmentInsurance : (List<AppointmentInsurance>)page.getContent()) {
            AppointmentInsuranceViewModel viewData = createViewData(appointmentInsurance);
            pageViewDataList.add(viewData);
        }
        model.setViewList(pageViewDataList);

        return model;
    }

    /**
     * 组建地推用户信息，返回到前端显示
     * @param appointmentInsurance
     * @return AppointmentInsuranceViewModel
     */
    private AppointmentInsuranceViewModel createViewData(AppointmentInsurance appointmentInsurance) {
        AppointmentInsuranceViewModel viewModel = new AppointmentInsuranceViewModel();
        QRCodeChannel qrCodeChannel = new QRCodeChannel();
        qrCodeChannel = qrCodeChannelRepository.findQrCodeChannelCodeByUserId(appointmentInsurance.getUser().getId());
        if(qrCodeChannel == null){
            viewModel.setQrCodeChannelCode("");
            viewModel.setQrCodeChannelName("");
        }else{
            QRCodeChannel qrCodeChannel1 = qrCodeChannelRepository.findQrCodeChannelCodeByUserId(appointmentInsurance.getUser().getId());
            WechatQRCode wechatQRCode = wechatQRCodeRepository.findOne(qrCodeChannel.getWechatQRCode());
            viewModel.setActionName(wechatQRCode.getActionName());
            viewModel.setQrCodeChannelId(qrCodeChannel.getId());
            viewModel.setQrCodeChannelCode(qrCodeChannel1.getCode());
            viewModel.setQrCodeChannelName(qrCodeChannel1.getName());
        }
        viewModel.setId(appointmentInsurance.getId());
        viewModel.setUser(appointmentInsurance.getUser().getId());
        viewModel.setName(appointmentInsurance.getUser().getName());
        viewModel.setMobile(appointmentInsurance.getUser().getMobile());
        viewModel.setLicensePlateNo(appointmentInsurance.getLicensePlateNo());
        String date = String.valueOf(appointmentInsurance.getExpireBefore());
        viewModel.setExpireBefore(DateUtils.getDateString(appointmentInsurance.getExpireBefore(),DateUtils.DATE_SHORTDATE_PATTERN));
        viewModel.setCreateTime(DateUtils.getDateString(appointmentInsurance.getCreateTime(),DateUtils.DATE_LONGTIME24_PATTERN));

        viewModel.setCount(String.valueOf(purchaseOrderRepository.findOrderCountByUserId(String.valueOf(appointmentInsurance.getUser().getId()))));
        viewModel.setTotalMoney(String.valueOf(purchaseOrderRepository.findSumMoneyByUserId(String.valueOf(appointmentInsurance.getUser().getId()))).equals("null") ? "0" : String.valueOf(purchaseOrderRepository.findSumMoneyByUserId(String.valueOf(appointmentInsurance.getUser().getId()))));
        viewModel.setStatus(String.valueOf(appointmentInsurance.getStatus()));
        viewModel.setComment(appointmentInsurance.getComment());
        viewModel.setMobile(appointmentInsurance.getUser().getMobile());
        return viewModel;
    }
    /**
     * 分页查询
     * @param keyword  关键字
     * @param pageable 分页信息
     * @param expireBefore 车险到期日
     * @return Page<AppointmentInsurance>
     */
    private Page<AppointmentInsurance> findBySpecAndPaginate(String keyword, int keyType,String expireBefore, Pageable pageable) throws Exception {
        logger.info(pageable.toString());
        return appointmentInsuranceRepository.findAll(new Specification<AppointmentInsurance>() {
            @Override
            public Predicate toPredicate(Root<AppointmentInsurance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<AppointmentInsurance> criteriaQuery = cb.createQuery(AppointmentInsurance.class);

                //获取实体属性
                Path<String> namePath = root.get("user").get("name");
                Path<String> mobilePath = root.get("user").get("mobile");
                Path<String> licensePlateNoPath = root.get("licensePlateNo");
                Path<Date> expireBeforePath = root.get("expireBefore");
                Path<String> sourcePath = root.get("source");

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(keyword)) {
                    // 姓名
                    if (keyType == 1) {
                        predicateList.add(cb.like(namePath, keyword + "%"));
                    }
                    // 手机号
                    else if (keyType == 2) {
                        predicateList.add(cb.like(mobilePath, keyword + "%"));
                    }
                    //车牌号
                    else if (keyType == 3) {
                        predicateList.add(cb.like(licensePlateNoPath, keyword + "%"));
                    }
                }
                if (expireBefore != null && !"".equals(expireBefore.trim())) {
                    Expression<Date> endDateExpression = cb.literal(DateUtils.getNextDayStart(expireBefore));
                    predicateList.add(
                        cb.lessThan(expireBeforePath,endDateExpression)
                    );
                }
                predicateList.add(cb.equal(sourcePath, "1"));
                criteriaQuery.groupBy(namePath);
                criteriaQuery.groupBy(licensePlateNoPath);
                criteriaQuery.orderBy(cb.desc(expireBeforePath));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 查找单个保险
     * @AppointmentInsuranceRepositoryId
     */
    public AppointmentInsuranceViewModel findOne(Long AppointmentInsuranceRepositoryId){
        AppointmentInsurance appointmentInsurance = new AppointmentInsurance();
        appointmentInsurance = appointmentInsuranceRepository.findById(AppointmentInsuranceRepositoryId);
        AppointmentInsuranceViewModel viewModel = new AppointmentInsuranceViewModel();
        viewModel.setId(appointmentInsurance.getId());
        viewModel.setUser(appointmentInsurance.getUser().getId());
        viewModel.setName(appointmentInsurance.getUser().getName());
        viewModel.setLicensePlateNo(appointmentInsurance.getLicensePlateNo());
        viewModel.setExpireBefore(DateUtils.getDateString(appointmentInsurance.getExpireBefore(), DateUtils.DATE_SHORTDATE_PATTERN));
        viewModel.setComment(appointmentInsurance.getComment());
        return viewModel;
    }

    /**
     * 更新处理状态
     * @param appointmentInsuranceId
     * @param status
     */
    public void updateStatus(String appointmentInsuranceId,Integer status){
        AppointmentInsurance appointmentInsurance = new AppointmentInsurance();
        appointmentInsurance = this.appointmentInsuranceRepository.findById(Long.parseLong(appointmentInsuranceId));
        appointmentInsurance.setStatus(status);
        appointmentInsuranceRepository.save(appointmentInsurance);
    }

    /**
     * 导出excel
     */
    public HSSFWorkbook createExportExcel() {
        List<AppointmentInsurance> list = appointmentInsuranceRepository.findexcel();
        List<AppointmentInsuranceViewModel> dataList = new ArrayList<>();
        for (AppointmentInsurance appointmentInsurance : list) {
            AppointmentInsuranceViewModel viewData = createViewData(appointmentInsurance);
            dataList.add(viewData);
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("地推查询结果");

        HSSFFont font = ExcelUtil.createFont(workbook, "宋体", (short) 12);
        HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, font);

        HSSFFont fontTitle = ExcelUtil.createFont(workbook, "宋体", (short) 14);
        HSSFCellStyle cellStyleTitle = ExcelUtil.createCellStyle(workbook, fontTitle);

        ExcelUtil.createStrCellValues(sheet, 0, this.createtitle(sheet), cellStyleTitle);

        Integer index = 1;
        Integer number = 1;
        for (AppointmentInsuranceViewModel data : dataList) {
            ExcelUtil.createStrCellValues(sheet, index, this.createtitleContent(data,number), cellStyle);
            index ++;
            number ++;
        }

        return workbook;
    }

    /**
     * 需要导出的字段
     * @param sheet
     * @return
     */
    public String[] createtitle(HSSFSheet sheet){
        sheet.setColumnWidth(0, 18 * 200);
        sheet.setColumnWidth(1, 18 * 250);
        sheet.setColumnWidth(2, 18 * 400);
        sheet.setColumnWidth(3, 18 * 300);
        sheet.setColumnWidth(4, 18 * 400);
        sheet.setColumnWidth(5, 18 * 400);
        sheet.setColumnWidth(6, 18 * 400);
        sheet.setColumnWidth(7, 18 * 250);
        sheet.setColumnWidth(8, 18 * 250);
        sheet.setColumnWidth(9, 18 * 400);
        sheet.setColumnWidth(10, 18 * 250);
        sheet.setColumnWidth(11, 18 * 200);
        sheet.setColumnWidth(12, 18 * 400);
        return new String[]{"序号","用户ID","用户姓名","用户手机号","车牌号","车险到期日",
            "提交时间","支付单数","支付总金额","二维码渠道号","二维码渠道名","状态","备注"};
    }

    /**
     * 添加导出的数据
     * @param data
     * @param number
     * @return
     */
    public String[] createtitleContent(AppointmentInsuranceViewModel data,Integer number){
        return new String[]{String.valueOf(number),
            String.valueOf(data.getUser()),
            StringUtils.trimToEmpty(data.getName()),
            StringUtils.trimToEmpty(data.getMobile()),
            StringUtils.trimToEmpty(data.getLicensePlateNo()),
            StringUtils.trimToEmpty(data.getExpireBefore()),
            StringUtils.trimToEmpty(String.valueOf(data.getCreateTime())),
            StringUtils.trimToEmpty(data.getCount()),
            StringUtils.trimToEmpty(data.getTotalMoney()),
            StringUtils.trimToEmpty(data.getQrCodeChannelCode()),
            StringUtils.trimToEmpty(data.getQrCodeChannelName()),
            StringUtils.trimToEmpty(data.getStatus().trim().equals("1")?"已处理":"未处理"),
            StringUtils.trimToEmpty(data.getComment())
            };
    }

    /**
     * 构建分页信息
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }
}
