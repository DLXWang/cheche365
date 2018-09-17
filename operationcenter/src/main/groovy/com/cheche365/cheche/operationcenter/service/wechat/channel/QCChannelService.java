package com.cheche365.cheche.operationcenter.service.wechat.channel;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.QRCodeChannel;
import com.cheche365.cheche.core.model.QRCodeStatistics;
import com.cheche365.cheche.core.repository.QRCodeChannelRepository;
import com.cheche365.cheche.core.repository.QRCodeStatisticsRepository;
import com.cheche365.cheche.core.service.IResourceService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.operationcenter.constants.ChannelKeyTypeEnum;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.operationcenter.web.model.wechat.channel.QRCodeChannelViewModel;
import com.cheche365.cheche.wechat.QRCodeManager;
import com.cheche365.cheche.core.model.WechatQRCode;
import com.cheche365.cheche.core.repository.WechatQRCodeRepository;
import com.cheche365.cheche.core.repository.WechatUserChannelRepository;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wangfei on 2015/7/28.
 */
@Service(value = "qcChannelService")
@Transactional
public abstract class QCChannelService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(QCChannelService.class);

    private static final int DEFAULT_CURRENT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 1000;

    private Lock lock = new ReentrantLock();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private QRCodeChannelRepository qRCodeChannelRepository;

    @Autowired
    private QRCodeStatisticsRepository qrCodeStatisticsRepository;

    @Autowired
    private WechatQRCodeRepository wechatQRCodeRepository;

    @Autowired
    private WechatUserChannelRepository wechatUserChannelRepository;

    @Autowired
    private QRCodeChannelRepository qrCodeChannelRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private QRCodeManager qrCodeManager;

    @Autowired
    private IResourceService resourceService;

    public Map<String, String> getWechatQRCodeImagePath(String[] strChannelIds) {
        Map<String, String> imagePathMap = new HashMap<>();
        for (String channelId : strChannelIds) {
            QRCodeChannel qrCodeChannel = qRCodeChannelRepository.findOne(Long.valueOf(channelId));
            imagePathMap.put(qrCodeChannel.getName(), getWechatQRCodeImagePath(Long.valueOf(channelId)));
        }
        return imagePathMap;
    }

    public String getWechatQRCodeImagePath(Long channelId) {
        String basePath = resourceService.getProperties().getWechatQRCodePath();
        QRCodeChannel qrCodeChannel = qRCodeChannelRepository.findOne(channelId);
        WechatQRCode wechatQRCode = wechatQRCodeRepository.findOne(qrCodeChannel.getWechatQRCode());
        String WechatQRCodePath = wechatQRCode.getImageURL().substring(wechatQRCode.getImageURL().indexOf(basePath) + basePath.length(),
            wechatQRCode.getImageURL().length());
        java.nio.file.Path filePath = Paths.get(resourceService.getResourceAbsolutePath(
            resourceService.getProperties().getWechatQRCodePath()), WechatQRCodePath);
//        java.nio.file.Path filePath = Paths.get(wechatQRCode.getImageURL());
        return filePath.toFile().getAbsolutePath();
    }

    public List<QRCodeChannel> addChannels(QRCodeChannelViewModel model, QRCodeType type) {
        List<QRCodeChannel> qrCodeChannels = new ArrayList<>();
        Integer newCount = model.getNewCount();
        logger.info("add new qrCode channel count -> {}", newCount);

        String comment = model.getComment();
        if (StringUtils.isNotBlank(comment)) {
            model.setComment(comment.replace("\n", "\\r\\n"));
        }

        if (newCount == 1) {
            qrCodeChannels.add(this.createQRCodeChannel(model, null, type));
        } else {
            qrCodeChannels.addAll(this.createBatchQRCodeChannel(model, type));
        }

        Iterable<QRCodeChannel> qrCodeChannelIterable = qRCodeChannelRepository.save(qrCodeChannels);
        List<QRCodeChannel> qrCodeChannelList = new ArrayList<>();
        qrCodeChannelIterable.forEach(qrCodeChannel -> {
            if (qrCodeChannel != null)
                qrCodeChannelList.add(qrCodeChannel);
        });

        this.createWechatQRCodes(qrCodeChannelList, type);
        qRCodeChannelRepository.save(qrCodeChannelList);

        return qrCodeChannelList;
    }

    private List<QRCodeChannel> createBatchQRCodeChannel(QRCodeChannelViewModel model, QRCodeType type) {
        List<QRCodeChannel> qrCodeChannelList = new ArrayList<>();

        Integer index = 1;
        for (int i = 0; i < model.getNewCount(); i++) {
            QRCodeChannel channel = this.createQRCodeChannel(model, null, type);
            if (i == 0) {
                channel.setCode(model.getCode());
            } else {
                channel.setCode(this.getNextChannelNo(type));
            }
            channel.setName(model.getName() + index.toString());
            qrCodeChannelList.add(channel);

            index++;
        }

        return qrCodeChannelList;
    }

    private QRCodeChannel createQRCodeChannel(QRCodeChannelViewModel model, QRCodeChannel channel, QRCodeType type) {
        QRCodeChannel newChannel = new QRCodeChannel();
        if (channel != null)
            BeanUtil.copyPropertiesContain(channel, newChannel);

        String[] contains = new String[]{"id", "code", "name", "department", "rebate", "comment"};
        BeanUtil.copyPropertiesContain(model, newChannel, contains);
        newChannel.setUpdateTime(new Date());
        newChannel.setCreateTime(channel == null ? new Date() : channel.getCreateTime());
        newChannel.setOperator(internalUserManageService.getCurrentInternalUser());
        if (QRCodeType.QR_SCENE.equals(type))
            newChannel.setExpireTime(DateUtils.getDate(model.getExpireTime(), DateUtils.DATE_LONGTIME24_PATTERN));

        return newChannel;
    }

    private List<WechatQRCode> createWechatQRCodes(List<QRCodeChannel> qrCodeChannelList, QRCodeType type) {
        if (null == qrCodeChannelList || qrCodeChannelList.isEmpty())
            return null;

        List<WechatQRCode> wechatQRCodeList = new ArrayList<>();
        qrCodeChannelList.forEach(qrCodeChannel -> {
            WechatQRCode qrCode = new WechatQRCode();
            qrCode.setSceneId(qrCodeChannel.getId());
            qrCode.setTarget(qrCodeChannel.getName());
            qrCode.setActionName(this.convertToActionName(type).toString());
            if (QRCodeType.QR_SCENE.equals(type)) {
                qrCode.setExpireSeconds(DateUtils.getSecondsBetween(new Date(), qrCodeChannel.getExpireTime()));
            }

            WechatQRCode wechatQRCode = qrCodeManager.createQRCode(qrCode);
            qrCodeChannel.setWechatQRCode(wechatQRCode.getId());
            wechatQRCodeList.add(wechatQRCode);
        });

        return wechatQRCodeList;
    }

    private WechatQRCode.ActionName convertToActionName(QRCodeType type) {
        switch (type) {
            case QR_SCENE:
                return WechatQRCode.ActionName.QR_SCENE;
            case QR_LIMIT_SCENE:
                return WechatQRCode.ActionName.QR_LIMIT_SCENE;
        }

        return null;
    }

    public QRCodeChannel getByChannelCode(String channelCode) {
        return qRCodeChannelRepository.findByCode(StringUtils.trimToEmpty(channelCode));
    }

    public synchronized String getNextChannelNo(QRCodeType type) {
        String key = "orderCenter_qrcChannelId:" + type;
        long index = redisTemplate.opsForValue().increment(key, 1);
        return this.getPrefix(type) + String.format("%08d", index);
    }

    private String getPrefix(QRCodeType type) {
        switch (type) {
            case QR_SCENE:
                return "LS";
            case QR_LIMIT_SCENE:
                return "YJ";
        }

        return "";
    }

    public Page<QRCodeChannel> getQRCodeChannelByPage(PublicQuery query) {
        try {
            return findBySpecAndPaginate(query,
                this.buildPageable(query.getCurrentPage(), query.getPageSize()));
        } catch (Exception e) {
            logger.error("get qrcode channel by page has error", e);
        }
        return null;
    }

    public List<QRCodeChannelViewModel> pageViewDataList(Page page) throws Exception {

        List<QRCodeChannelViewModel> pageViewDataList = new ArrayList<>();
        List<QRCodeChannel> qrCodeChannelList = (List<QRCodeChannel>) page.getContent();
        if (!CollectionUtils.isEmpty(qrCodeChannelList)) {
            List<Long> qrCodeChannelIdList = new ArrayList<>();
            qrCodeChannelList.forEach(qrCodeChannel -> qrCodeChannelIdList.add(qrCodeChannel.getId()));
            // 扫描关注数
            List<Object[]> scanAndSubscribeCountList = qrCodeStatisticsRepository.getScanAndSubscribeCount(qrCodeChannelIdList);
            Map<String, String> scanAndSubscribeCountMap = getScanAndSubscribeCountMap(scanAndSubscribeCountList);
            // 绑定手机数
            List<Object[]> boundMobileCountList = qrCodeChannelRepository.getBoundMobileCount(qrCodeChannelIdList);
            Map<String, String> boundMobileCountMap = getBoundMobileCountMap(boundMobileCountList);
            // 成交订单数
            List<Object[]> successOrderCountList = qrCodeChannelRepository.getSuccessOrderCount(qrCodeChannelIdList);
            Map<String, String> successOrderCountMap = getSuccessOrderCountMap(successOrderCountList);

            qrCodeChannelList.forEach(qrCodeChannel -> pageViewDataList.add(createPageViewModel(qrCodeChannel,
                scanAndSubscribeCountMap, boundMobileCountMap, successOrderCountMap)));
        }

        return pageViewDataList;
    }

    /**
     * 构建分页信息
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "updateTime", "id");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<DidiInsurance>
     */
    private Page<QRCodeChannel> findBySpecAndPaginate(PublicQuery publicQuery, Pageable pageable) {
        return qRCodeChannelRepository.findAll(new Specification<QRCodeChannel>() {
            @Override
            public Predicate toPredicate(Root<QRCodeChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QRCodeChannel> criteriaQuery = cb.createQuery(QRCodeChannel.class);

                //获取实体属性
                Path<String> codePath = root.get("code");
                Path<Boolean> disablePath = root.get("disable");

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                // 只显示有效渠道
                predicateList.add(cb.equal(disablePath, Boolean.FALSE));
                // 区分临时，永久二维码
                String keyword = publicQuery.getKeyword();
                Integer keyType = publicQuery.getKeyType();
                if (QRCodeType.QR_LIMIT_SCENE.name().equals(publicQuery.getQrCodeType())
                    && (StringUtils.isBlank(keyword) || (StringUtils.isNotBlank(keyword) && keyType != 1))) {
                    predicateList.add(cb.like(codePath, "YJ%"));
                } else if (QRCodeType.QR_SCENE.name().equals(publicQuery.getQrCodeType())
                    && (StringUtils.isBlank(keyword) || (StringUtils.isNotBlank(keyword) && keyType != 1))) {
                    predicateList.add(cb.like(codePath, "LS%"));
                }
                // 条件
                if (StringUtils.isNotBlank(keyword)) {
                    // 渠道号
                    if (keyType == ChannelKeyTypeEnum.CHANNEL_CODE.ordinal()) {
                        predicateList.add(cb.like(codePath, keyword + "%"));
                    }
                    // 渠道名称
                    else if (keyType == ChannelKeyTypeEnum.CHANNEL_NAME.ordinal()) {
                        Path<String> namePath = root.get("name");
                        predicateList.add(cb.like(namePath, keyword + "%"));
                    }
                    // 到期时间
                    else if (keyType == ChannelKeyTypeEnum.EXPIRE_TIME.ordinal()) {
                        Path<Date> expireTimePath = root.get("expireTime");
                        Date expireTime = DateUtils.getDate(keyword, DateUtils.DATE_LONGTIME24_PATTERN);
                        predicateList.add(cb.equal(expireTimePath, expireTime));
                    }
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    public QRCodeChannelViewModel findOne(Long channelId) {
        // 微信二维码渠道对象
        QRCodeChannel qrCodeChannel = qRCodeChannelRepository.findOne(channelId);
        return createViewModel(qrCodeChannel);
    }

    /**
     * 构建页面数据
     *
     * @param qrCodeChannel
     * @return QRCodeChannelViewModel
     */
    public QRCodeChannelViewModel createViewModel(QRCodeChannel qrCodeChannel) {
        if (qrCodeChannel == null)
            return null;
        QRCodeChannelViewModel viewModel = new QRCodeChannelViewModel();
        //二维码渠道id，渠道号，渠道名，返点，备注,微信二维码id
        String[] containProperties = new String[]{"id", "code", "name", "department", "rebate", "comment", "wechatQRCode"};
        BeanUtil.copyPropertiesContain(qrCodeChannel, viewModel, containProperties);
        List<Object[]> countList = qrCodeStatisticsRepository.getScanAndSubscribeCount(qrCodeChannel.getId());
        viewModel.setScanCount(((BigDecimal) (countList.get(0))[0]).intValue());//扫描数
        viewModel.setSubscribeCount(((BigDecimal) (countList.get(0))[1]).intValue());//关注数
        viewModel.setBindingMobileCount(qRCodeChannelRepository.getBoundMobileCount(qrCodeChannel.getId()));//绑定手机数
        viewModel.setSuccessOrderCount(qRCodeChannelRepository.getSuccessOrderCount(qrCodeChannel.getId()));//成单数
        WechatQRCode wechatQRCode = wechatQRCodeRepository.findOne(qrCodeChannel.getWechatQRCode());
        viewModel.setImageURL(wechatQRCode.getImageURL());//二维码图片存放路径
        // 临时二维码需要有效状态，到期时间
        if (wechatQRCode.getActionName().equals(QRCodeType.QR_SCENE.name())) {
            viewModel.setExpireTime(
                DateUtils.getDateString(qrCodeChannel.getExpireTime(), DateUtils.DATE_LONGTIME24_PATTERN));//过期时间
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            viewModel.setStatus(currentTime.getTime() <= qrCodeChannel.getExpireTime().getTime() ? "有效" : "失效");//有效状态
        }
        return viewModel;
    }

    /**
     * 构建页面数据
     *
     * @param qrCodeChannel
     * @return QRCodeChannelViewModel
     */
    private QRCodeChannelViewModel createPageViewModel(QRCodeChannel qrCodeChannel, Map<String, String> scanAndSubscribeCountMap,
                                                       Map<String, String> boundMobileCountMap, Map<String, String> successOrderCountMap) {
        if (qrCodeChannel == null)
            return null;
        QRCodeChannelViewModel viewModel = new QRCodeChannelViewModel();
        //二维码渠道id，渠道号，渠道名，返点，备注,微信二维码id
        String[] containProperties = new String[]{"id", "code", "name", "department", "rebate", "comment", "wechatQRCode"};
        BeanUtil.copyPropertiesContain(qrCodeChannel, viewModel, containProperties);
        String[] scanAndSubscribeCounts = scanAndSubscribeCountMap.get(qrCodeChannel.getId().toString()) == null ?
            new String[]{"0", "0"} : scanAndSubscribeCountMap.get(qrCodeChannel.getId().toString()).split(",");
        viewModel.setScanCount(Integer.parseInt(scanAndSubscribeCounts[0]));//扫描数
        viewModel.setSubscribeCount(Integer.parseInt(scanAndSubscribeCounts[1]));//关注数
        viewModel.setBindingMobileCount(boundMobileCountMap.get(qrCodeChannel.getId().toString()) == null ?
            0 : Integer.parseInt(boundMobileCountMap.get(qrCodeChannel.getId().toString())));//绑定手机数
        viewModel.setSuccessOrderCount(successOrderCountMap.get(qrCodeChannel.getId().toString()) == null ?
            0 : Integer.parseInt(successOrderCountMap.get(qrCodeChannel.getId().toString())));//成单数
        WechatQRCode wechatQRCode = wechatQRCodeRepository.findOne(qrCodeChannel.getWechatQRCode());
        viewModel.setImageURL(wechatQRCode.getImageURL());//二维码图片存放路径
        // 临时二维码需要有效状态，到期时间
        if (wechatQRCode.getActionName().equals(QRCodeType.QR_SCENE.name())) {
            viewModel.setExpireTime(
                DateUtils.getDateString(qrCodeChannel.getExpireTime(), DateUtils.DATE_LONGTIME24_PATTERN));//过期时间
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            viewModel.setStatus(currentTime.getTime() <= qrCodeChannel.getExpireTime().getTime() ? "有效" : "失效");//有效状态
        }
        return viewModel;
    }

    public HSSFWorkbook createScanAndSubscribeCountExportExcel(String channelCode) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFFont font = ExcelUtil.createFont(workbook, "宋体", (short) 12);
        HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, font);
        HSSFFont fontTitle = ExcelUtil.createFont(workbook, "宋体", (short) 14);
        HSSFCellStyle cellStyleTitle = ExcelUtil.createCellStyle(workbook, fontTitle);

        QRCodeChannel qrCodeChannel = qRCodeChannelRepository.findByCode(channelCode);
        // 获取关注扫描数
        setScanAndSubscribeCountSheetData(workbook, qrCodeChannel, cellStyle, cellStyleTitle);
        // 获取绑定手机
        setBindingMobileSheetData(workbook, qrCodeChannel, cellStyle, cellStyleTitle);
        // 获取成交订单
        setSuccessOrderSheetData(workbook, qrCodeChannel, cellStyle, cellStyleTitle);
        return workbook;
    }

    private void setSuccessOrderSheetData(HSSFWorkbook workbook, QRCodeChannel qrCodeChannel,
                                          HSSFCellStyle cellStyle, HSSFCellStyle cellStyleTitle) {
        HSSFSheet orderSheet = workbook.createSheet("成交订单");
        // 报表头
        this.createSuccessOrderSheetTitle(orderSheet, 0, cellStyleTitle);
        // 报表体
        int pageNo = DEFAULT_CURRENT_PAGE;
        Pageable pageable = new PageRequest(pageNo - 1, DEFAULT_PAGE_SIZE);
        Page<String> successOrderPage =
            wechatUserChannelRepository.getSuccessOrderPage(qrCodeChannel.getId(), pageable);
        int index = 1;
        while (pageNo <= successOrderPage.getTotalPages() && !CollectionUtils.isEmpty(successOrderPage.getContent())) {
            List<String> successOrderList = successOrderPage.getContent();
            for (String orderNo : successOrderList) {
                ExcelUtil.createStrCellValues(orderSheet, index++, new String[]{orderNo}, cellStyle);
            }
            pageNo++;
            pageable = new PageRequest(pageNo - 1, DEFAULT_PAGE_SIZE);
            successOrderPage = wechatUserChannelRepository.getSuccessOrderPage(qrCodeChannel.getId(), pageable);
        }
    }

    private void setBindingMobileSheetData(HSSFWorkbook workbook, QRCodeChannel qrCodeChannel,
                                           HSSFCellStyle cellStyle, HSSFCellStyle cellStyleTitle) {
        HSSFSheet mobileSheet = workbook.createSheet("绑定手机号");
        // 报表头
        this.createBindingMobileSheetTitle(mobileSheet, 0, cellStyleTitle);
        // 报表体
        int pageNo = DEFAULT_CURRENT_PAGE;
        Pageable pageable = new PageRequest(pageNo - 1, DEFAULT_PAGE_SIZE);
        Page<String> bindingMobilePage =
            wechatUserChannelRepository.getBoundMobilePage(qrCodeChannel.getId(), pageable);
        int index = 1;
        while (pageNo <= bindingMobilePage.getTotalPages() && !CollectionUtils.isEmpty(bindingMobilePage.getContent())) {
            List<String> bindingMobileList = bindingMobilePage.getContent();
            for (String mobile : bindingMobileList) {
                ExcelUtil.createStrCellValues(mobileSheet, index++, new String[]{mobile}, cellStyle);
            }
            pageNo++;
            pageable = new PageRequest(pageNo - 1, DEFAULT_PAGE_SIZE);
            bindingMobilePage = wechatUserChannelRepository.getBoundMobilePage(qrCodeChannel.getId(), pageable);
        }
    }

    private void setScanAndSubscribeCountSheetData(HSSFWorkbook workbook, QRCodeChannel qrCodeChannel,
                                                   HSSFCellStyle cellStyle, HSSFCellStyle cellStyleTitle) {
        HSSFSheet sheet = workbook.createSheet("扫描关注数");
        // 报表头
        this.createScanAndSubscribeCountSheetTitle(sheet, 0, cellStyleTitle);
        // 报表体
        int pageNo = DEFAULT_CURRENT_PAGE;
        Pageable pageable = new PageRequest(pageNo - 1, DEFAULT_PAGE_SIZE);
        Date expireTime = DateUtils.getCurrentDate("yyyy-MM-dd hh:00:00");
        if (qrCodeChannel.getCode().contains("LS")) {
            expireTime = getMaxExpireTime(qrCodeChannel.getExpireTime());
        }
        Page<QRCodeStatistics> qrCodeStatisticsPage =
            qrCodeStatisticsRepository.getQRCodeStatisticsPage(qrCodeChannel, expireTime, pageable);
        int index = 1;
        while (pageNo <= qrCodeStatisticsPage.getTotalPages() && !CollectionUtils.isEmpty(qrCodeStatisticsPage.getContent())) {
            List<QRCodeStatistics> bindingMobileList = qrCodeStatisticsPage.getContent();
            for (QRCodeStatistics qrCodeStatistics : bindingMobileList) {
                String[] contents = new String[]{
                    DateUtils.getDateString(qrCodeStatistics.getStatisticsTime(), DateUtils.DATE_LONGTIME24_PATTERN),
                    qrCodeStatistics.getScanCount() + "",
                    qrCodeStatistics.getSubscribeCount() + ""
                };
                ExcelUtil.createStrCellValues(sheet, index++, contents, cellStyle);
            }
            pageNo++;
            pageable = new PageRequest(pageNo - 1, DEFAULT_PAGE_SIZE);
            qrCodeStatisticsPage = qrCodeStatisticsRepository.getQRCodeStatisticsPage(qrCodeChannel, expireTime, pageable);
        }
    }

    private Date getMaxExpireTime(Date expireTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expireTime);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        return calendar.getTime();
    }

    private void createScanAndSubscribeCountSheetTitle(HSSFSheet sheet, Integer rowNum, HSSFCellStyle cellStyle) {
        String[] titles = {"日期", "扫描数", "关注数"};
        sheet.setColumnWidth(0, 18 * 340);
        sheet.setColumnWidth(1, 18 * 150);
        sheet.setColumnWidth(2, 18 * 150);
        ExcelUtil.createStrCellValues(sheet, rowNum, titles, cellStyle);
    }

    private void createBindingMobileSheetTitle(HSSFSheet sheet, Integer rowNum, HSSFCellStyle cellStyle) {
        String[] titles = {"绑定手机"};
        sheet.setColumnWidth(0, 18 * 340);
        ExcelUtil.createStrCellValues(sheet, rowNum, titles, cellStyle);
    }

    private void createSuccessOrderSheetTitle(HSSFSheet sheet, Integer rowNum, HSSFCellStyle cellStyle) {
        String[] titles = {"成交订单"};
        sheet.setColumnWidth(0, 18 * 500);
        ExcelUtil.createStrCellValues(sheet, rowNum, titles, cellStyle);
    }

    /**
     * 查询数据导出
     *
     * @return
     */
    public HSSFWorkbook createExportExcel(PublicQuery query) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("二维码渠道查询结果");
        HSSFFont font = ExcelUtil.createFont(workbook, "宋体", (short) 12);
        HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, font);
        HSSFFont fontTitle = ExcelUtil.createFont(workbook, "宋体", (short) 14);
        HSSFCellStyle cellStyleTitle = ExcelUtil.createCellStyle(workbook, fontTitle);
        // 报表头
        ExcelUtil.createStrCellValues(sheet, 0, this.supplyListExcelTitle(sheet), cellStyleTitle);
        // 报表内容
        int pageNo = DEFAULT_CURRENT_PAGE;
        Pageable pageable = this.buildPageable(pageNo, DEFAULT_PAGE_SIZE);
        Page<QRCodeChannel> qrCodeChannelPage = this.findBySpecAndPaginate(query, pageable);
        int index = 1;
        while (pageNo <= qrCodeChannelPage.getTotalPages() && !CollectionUtils.isEmpty(qrCodeChannelPage.getContent())) {
            List<QRCodeChannel> qrCodeChannelList = qrCodeChannelPage.getContent();
            List<Long> qrCodeChannelIdList = new ArrayList<>();
            qrCodeChannelList.forEach(qrCodeChannel -> qrCodeChannelIdList.add(qrCodeChannel.getId()));
            // 扫描关注数
            List<Object[]> scanAndSubscribeCountList = qrCodeStatisticsRepository.getScanAndSubscribeCount(qrCodeChannelIdList);
            Map<String, String> scanAndSubscribeCountMap = getScanAndSubscribeCountMap(scanAndSubscribeCountList);
            // 绑定手机数
            List<Object[]> boundMobileCountList = qrCodeChannelRepository.getBoundMobileCount(qrCodeChannelIdList);
            Map<String, String> boundMobileCountMap = getBoundMobileCountMap(boundMobileCountList);
            // 成交订单数
            List<Object[]> successOrderCountList = qrCodeChannelRepository.getSuccessOrderCount(qrCodeChannelIdList);
            Map<String, String> successOrderCountMap = getSuccessOrderCountMap(successOrderCountList);
            for (QRCodeChannel qrCodeChannel : qrCodeChannelList) {
                QRCodeChannelViewModel viewModel = createPageViewModel(qrCodeChannel,
                    scanAndSubscribeCountMap, boundMobileCountMap, successOrderCountMap);
                ExcelUtil.createStrCellValues(sheet, index++, this.supplyListExcelContent(viewModel), cellStyle);
            }
            pageNo++;
            pageable = this.buildPageable(pageNo, DEFAULT_PAGE_SIZE);
            qrCodeChannelPage = this.findBySpecAndPaginate(query, pageable);
        }

        return workbook;
    }

    private Map<String, String> getSuccessOrderCountMap(List<Object[]> successOrderCountList) {
        Map<String, String> resultMap = new TreeMap<>();
        for (Object[] objects : successOrderCountList) {
            resultMap.put(objects[0].toString(), ((BigInteger) objects[1]).intValue() + "");
        }
        return resultMap;
    }

    private Map<String, String> getBoundMobileCountMap(List<Object[]> boundMobileCountList) {
        Map<String, String> resultMap = new TreeMap<>();
        for (Object[] objects : boundMobileCountList) {
            resultMap.put(objects[0].toString(), ((BigInteger) objects[1]).intValue() + "");
        }
        return resultMap;
    }

    private Map<String, String> getScanAndSubscribeCountMap(List<Object[]> scanAndSubscribeCountList) {
        Map<String, String> resultMap = new TreeMap<>();
        for (Object[] objects : scanAndSubscribeCountList) {
            resultMap.put(objects[0].toString(), ((BigDecimal) objects[1]).intValue() + "," + ((BigDecimal) objects[2]).intValue());
        }
        return resultMap;
    }

    protected abstract String[] supplyListExcelTitle(HSSFSheet sheet);

    protected abstract String[] supplyListExcelContent(QRCodeChannelViewModel data);

    public void dump() {
        lock.lock();
        try {
            List<WechatQRCode> wechatQRCodeList = wechatQRCodeRepository.findNoReferenceQRCode();
            if (!CollectionUtils.isEmpty(wechatQRCodeList)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("未生成渠道的二维码数量：" + wechatQRCodeList.size());
                }
                wechatQRCodeList.forEach(wechatQRCode -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("未生成渠道的二维码id：" + wechatQRCode.getId());
                    }
                    QRCodeChannel tempQRCodeChannel = qRCodeChannelRepository.findFirstByWechatQRCode(wechatQRCode.getId());
                    if (tempQRCodeChannel == null) {
                        // 为每一个二维码创建渠道
                        QRCodeChannel qrCodeChannel = new QRCodeChannel();
                        qrCodeChannel.setId(wechatQRCode.getSceneId());
                        qrCodeChannel.setName(wechatQRCode.getTarget());
                        qrCodeChannel.setRebate(1.0);
                        qrCodeChannel.setCode(this.getNextChannelNo(QRCodeType.format(wechatQRCode.getActionName())));
                        if (QRCodeType.QR_SCENE.name().equals(QRCodeType.format(wechatQRCode.getActionName()).name())) {
                            Date currentTime = Calendar.getInstance().getTime();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(currentTime.getTime() - wechatQRCode.getExpireSeconds());
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                            qrCodeChannel.setExpireTime(calendar.getTime());
                        }
                        qrCodeChannel.setComment(wechatQRCode.getComments());
                        qrCodeChannel.setWechatQRCode(wechatQRCode.getId());
                        qrCodeChannel.setOperator(internalUserManageService.getCurrentInternalUser());
                        qrCodeChannel.setCreateTime(new Date());
                        qrCodeChannel.setUpdateTime(new Date());
                        qRCodeChannelRepository.save(qrCodeChannel);
                    }
                });
            }
        } catch (Exception ex) {
            logger.error("处理未生成渠道的二维码错误", ex);
        } finally {
            lock.unlock();
        }
    }
}
