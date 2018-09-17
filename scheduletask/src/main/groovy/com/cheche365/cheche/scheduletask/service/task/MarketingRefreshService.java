package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.MarketingRuleRepository;
import com.cheche365.cheche.core.repository.MarketingSharedRepository;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.FileUtil;
import com.cheche365.cheche.scheduletask.service.common.TaskRunningService;
import com.cheche365.cheche.scheduletask.util.banner.ActivityPageJsonObject;
import com.cheche365.cheche.scheduletask.util.banner.BannerElement;
import com.cheche365.cheche.scheduletask.util.banner.MarketingBanner;
import com.cheche365.cheche.scheduletask.util.banner.MarketingBannerUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;

import static com.cheche365.cheche.core.model.ActivityType.Enum.*;
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.*;

/**
 * Created by xu.yelong on 2016/8/16.
 */
@Service
public class MarketingRefreshService {

    @Autowired
    ApplicationContextHolder applicationContextHolder;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private MarketingRuleRepository marketingRuleRepository;

    @Autowired
    private MarketingSharedRepository marketingSharedRepository;

    @Autowired
    private TaskRunningService taskRunningService;

    private Logger logger = LoggerFactory.getLogger(MarketingRefreshService.class);

    private static Map insuranceCompanyLogos;

    private static Map activityTypeImages;

    private static Map spokespersons;

    private static final String INSURANCE_COMPANY_LOG_DIR = "insurance_company_logo/";
    private static final String SPOKES_PERSON_DIR = "spokesperson/";
    private static String bannerPath = null;
    private static String basePath = null;


    public void dispatcher() {
        init();
        List<MarketingRule> newPreEffectiveList = refreshStatus();
        this.createImageAndDataFile(newPreEffectiveList);
        //检查自动过期数据，并置为失效
        List<MarketingRule> expireList = autoExpire();

        List<MarketingRule> marketingRules = new ArrayList<>();
        logger.debug("begin to sync marketing to autohome , count -->{}", marketingRules.size());
        if (!marketingRules.isEmpty()) {
            Date date = marketingRules.get(0).getStatus().getId().equals(MarketingRuleStatus.Enum.EFFECTIVE_2.getId()) ? marketingRules.get(0).getEffectiveDate() : marketingRules.get(0).getExpireDate();
        }
    }

    @Transactional
    public void createImageAndDataFile(List<MarketingRule> newPreEffectiveList) {
        if (CollectionUtils.isEmpty(newPreEffectiveList)) {
            return;
        }
        if (basePath == null || bannerPath == null) {
            init();
        }
        marketingRuleRepository.save(newPreEffectiveList);
        createImage(newPreEffectiveList);
        createDataFile(newPreEffectiveList);
    }

    public List<MarketingRule> autoExpire() {
        String today = DateUtils.getDateString(new Date(), DateUtils.DATE_SHORTDATE_PATTERN);
        List<MarketingRule> marketingRuleList = marketingRuleRepository.findByStatusAndExpireDate(MarketingRuleStatus.Enum.EFFECTIVE_2.getId(), today);
        for (MarketingRule marketingRule : marketingRuleList) {
            marketingRule.setStatus(MarketingRuleStatus.Enum.EXPIRED_3);
        }
        marketingRuleRepository.save(marketingRuleList);
        return marketingRuleList;
    }

    public void init() {

        bannerPath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getBannerPath());
        basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getIosPath());

        insuranceCompanyLogos = new HashMap() {{
            put(PICC_10000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "picc.png"));
            put(PINGAN_20000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "pingan.png"));
            put(CPIC_25000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "cpic.png"));
            put(SINOSIG_15000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "sinosig.png"));
            put(CHINALIFE_40000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "chinalife.png"));
            put(CIC_45000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "cic.png"));
            put(AXATP_55000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "axatp.png"));
            put(ANSWERN_65000.getId(), new File(bannerPath + INSURANCE_COMPANY_LOG_DIR + "answer.png"));
        }};

        activityTypeImages = new HashMap() {{
            put(FULL_SEND_5.getId(), getActivityTypeImage("full_send"));
            put(FULL_REDUCE_4.getId(), getActivityTypeImage("full_reduce"));
            put(INSURANCE_PACKAGE_DEDUCT_6.getId(), getActivityTypeImage("insurance_package_deduct"));
            put(DISCOUNT_SEND_7.getId(), getActivityTypeImage("discount_send"));
        }};

        spokespersons = new HashMap() {{
            put(PINGAN_20000.getId(), new File(bannerPath + SPOKES_PERSON_DIR + "pingan.png"));
            put(PICC_10000.getId(), new File(bannerPath + SPOKES_PERSON_DIR + "picc.png"));
            put(CHINALIFE_40000.getId(), new File(bannerPath + SPOKES_PERSON_DIR + "chinalife.png"));
        }};
    }

    private File[] getActivityTypeImage(String subPath) {
        File dir = new File(bannerPath + subPath);
        File[] files = dir.listFiles();
        return files;
    }

    @Transactional
    public List<MarketingRule> refreshStatus() {
        String today = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
        //生效活动列表
        List<MarketingRule> marketingRuleEffectiveList = marketingRuleRepository.findByStatus(MarketingRuleStatus.Enum.EFFECTIVE_2);
        //未生效活动列表
        List<MarketingRule> marketingRulePreEffectiveList = marketingRuleRepository.findByStatusAndEffectiveDate(MarketingRuleStatus.Enum.PRE_EFFECTIVE_1.getId(), today);
        //置为失效列表
        List<MarketingRule> marketingRuleExpired = new ArrayList<>();
        marketingRulePreEffectiveList.forEach(marketingRulePreEffective -> {
            for (MarketingRule marketingRuleEffective : marketingRuleEffectiveList) {
                if (marketingRuleEffective.equals(marketingRulePreEffective)) {
                    marketingRuleEffective.setStatus(MarketingRuleStatus.Enum.EXPIRED_3);
                    marketingRuleEffective.setExpireDate(new Date());
                    marketingRuleExpired.add(marketingRuleEffective);
                }
            }
            marketingRulePreEffective.setEffectiveDate(DateUtils.getDate(new Date(), DateUtils.DATE_SHORTDATE_PATTERN));
            marketingRulePreEffective.setStatus(MarketingRuleStatus.Enum.EFFECTIVE_2);
        });
        marketingRuleRepository.save(marketingRuleExpired);
        marketingRuleRepository.save(marketingRulePreEffectiveList);
        return marketingRulePreEffectiveList;
    }

    private void createDataFile(List<MarketingRule> marketingRuleList) {
        for (MarketingRule marketingRule : marketingRuleList) {
//            if(Channel.Enum.ORDER_CENTER_CHANNELS.indexOf(marketingRule.getChannel())>-1){
//                continue;
//            }
            String dirPath = new StringBuffer(basePath).append("activity/data/").append(marketingRule.getArea().getId()).toString();
            FileUtil.isNotExistCreateDirPath(dirPath);
            String filePath = new StringBuffer(dirPath).append(File.separator).append(getFileName(marketingRule, ".json")).toString();
            ActivityPageJsonObject activityPageJsonObject = new ActivityPageJsonObject();
            ActivityPageJsonObject.Share share = activityPageJsonObject.new Share();
            MarketingShared marketingShared = marketingSharedRepository.findFirstByMarketingRule(marketingRule);
            if (marketingShared != null) {
                share.setWechatTitle(marketingShared.getWechatMainTitle());
                share.setWechatSubTitle(marketingShared.getWechatSubTitle());
                share.setAlipayTitle(marketingShared.getAlipayMainTitle());
                share.setAlipaySubTitle(marketingShared.getAlipaySubTitle());
                share.setImgUrl(resourceService.absoluteUrl(basePath, "/activity/image/shared/shared.png"));
                if (!StringUtils.isEmpty(marketingShared.getSharedIcon())) {
                    share.setImgUrl(resourceService.absoluteUrl(basePath + File.separator, marketingShared.getSharedIcon()));
                }
            }
            ActivityPageJsonObject.Info info = activityPageJsonObject.new Info();
            info.setTitle(marketingRule.getTitle());
            info.setSubTitle(marketingRule.getSubTitle());
            info.setTopImage(resourceService.absoluteUrl(basePath, "/activity/image/shared/top.jpg"));
            if (!StringUtils.isEmpty(marketingRule.getTopImage())) {
                info.setTopImage(resourceService.absoluteUrl(basePath + File.separator, marketingRule.getTopImage()));
            }
            info.setTopImage(info.getTopImage().replace("http:", ""));
            ActivityPageJsonObject.Rule[] rules = new ActivityPageJsonObject.Rule[5];
            rules[0] = activityPageJsonObject.new Rule("活动支持的地区:" + marketingRule.getArea().getName());
            rules[1] = activityPageJsonObject.new Rule("支持的保险公司:" + marketingRule.getInsuranceCompany().getName());
            rules[2] = activityPageJsonObject.new Rule("优惠政策:" + marketingRule.getDescription().substring(0, marketingRule.getDescription().lastIndexOf("|")));
            rules[3] = activityPageJsonObject.new Rule(marketingRule.getDescription().substring(marketingRule.getDescription().lastIndexOf("|") + 1, marketingRule.getDescription().length()));

            String tel = WebConstants.CHECHE_CUSTOMER_SERVICE_MOBILE;
            String channelName = marketingRule.getChannel().getParent().getDescription();
            if (Channel.orderCenterChannels().indexOf(marketingRule.getChannel()) > -1 || Channel.self().indexOf(marketingRule.getChannel()) > -1) {
                channelName = WebConstants.LONG_COMPANY_NAME;
            }

            rules[4] = activityPageJsonObject.new Rule(channelName + "保留本次活动最终解释权，如有疑问请致电<a href='tel://" + tel + "'>" + tel + "</a>咨询。");
            info.setRules(rules);
            activityPageJsonObject.setInfo(info);
            activityPageJsonObject.setShare(share);
            activityPageJsonObject.setCode(marketingRule.getMarketing().getCode());
            activityPageJsonObject.setThirdPartner(marketingRule.getChannel().isThirdPartnerChannel());
            String json = CacheUtil.doJacksonSerialize(activityPageJsonObject);
            try {
                FileUtil.writeFile(filePath, json.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException | IllegalArgumentException | NullPointerException e) {
                logger.error("create data.json error,marketingCode:-->{},json:-->{}", marketingRule.getMarketing().getCode(), json, e);
            }
        }
    }

    private void createImage(List<MarketingRule> marketingRuleList) {
        logger.debug("create marketing banner begin , count -->{}", marketingRuleList.size());
        for (MarketingRule marketingRule : marketingRuleList) {
            if (Channel.orderCenterChannels().indexOf(marketingRule.getChannel()) > -1) {
                continue;
            }
            String dirPath = new StringBuffer(basePath).append("activity/image/banner/").append(marketingRule.getArea().getId()).toString();
            FileUtil.isNotExistCreateDirPath(dirPath);
            String filePath = new StringBuffer(dirPath).append(File.separator).append(getFileName(marketingRule, ".jpg")).toString();
            MarketingBanner marketingBanner = new MarketingBanner(220, 750, Color.WHITE, 0, Color.WHITE, filePath);
            BannerElement bannerElement = new BannerElement();
            BannerElement.BannerFont mainTitle = bannerElement.new BannerFont();
            mainTitle.setText(marketingRule.getTitle());
            mainTitle.setX(32);
            mainTitle.setY(126);
            mainTitle.setColor(new Color(51, 51, 51));
            mainTitle.setFont(new Font("黑体", 0, 30));

            BannerElement.BannerFont subTitle = bannerElement.new BannerFont();
            subTitle.setText(marketingRule.getSubTitle());
            subTitle.setX(32);
            subTitle.setY(170);
            subTitle.setColor(new Color(153, 153, 153));
            subTitle.setFont(new Font("黑体", 0, 26));

            BannerElement.BannerImage logo = bannerElement.new BannerImage();
            logo.setWidth(188);
            logo.setHeight(48);
            logo.setX(32);
            logo.setY(24);
            try {
                logo.setBufferedImage(getBannerLog(marketingRule.getInsuranceCompany()));
                BannerElement.BannerImage image = bannerElement.new BannerImage();
                image.setWidth(268);
                image.setHeight(220);
                image.setX(482);
                image.setY(0);
                image.setBufferedImage(getBannerImage(marketingRule));
                marketingBanner.addElement(logo);
                marketingBanner.addElement(image);
                marketingBanner.addElement(mainTitle);
                marketingBanner.addElement(subTitle);
                MarketingBannerUtil.draw(marketingBanner);
            } catch (IOException | IllegalArgumentException | NullPointerException e) {
                logger.error("create marketing banner error ,marketing code -->{},insurance company -->{},area -->{},channel -->{}",
                        marketingRule.getMarketing().getCode(), marketingRule.getInsuranceCompany().getId(), marketingRule.getArea().getId(), marketingRule.getChannel().getId(), e);
            }
        }
    }

    private BufferedImage getBannerImage(MarketingRule marketingRule) throws IOException {
        Long companyId = marketingRule.getInsuranceCompany().getId();
        Object obj = spokespersons.get(companyId);
        if (obj != null) {
            return ImageIO.read((File) obj);
        }
        File[] files = (File[]) activityTypeImages.get(marketingRule.getActivityType().getId());
        int index = new Random().nextInt(files.length);
        return ImageIO.read(files[index]);
    }

    private BufferedImage getBannerLog(InsuranceCompany insuranceCompany) throws IOException {
        if (insuranceCompanyLogos.get(insuranceCompany.getId()) == null) {
            logger.debug("get insurance company logo error,company name ->{}", insuranceCompany.getName());
            return null;
        }
        return ImageIO.read((File) insuranceCompanyLogos.get(insuranceCompany.getId()));
    }


    private String getFileName(MarketingRule marketingRule, String suffix) {
        String fileName = new StringBuffer().append(marketingRule.getMarketing().getCode())
                .append("_").append(marketingRule.getArea().getId())
                .append("_").append(marketingRule.getChannel().getId())
                .append("_").append(marketingRule.getInsuranceCompany().getId())
                .append("_").append(marketingRule.getVersion())
                .append(suffix).toString();
        return fileName;
    }

    @PostConstruct
    public void createMarketingRuleDataAndImage() {
        initInsuranceCompany();
        new Thread(new CreateImageAndDataFileThread()).start();
    }

    private void initInsuranceCompany() {
        Boolean starting = true;
        while (starting) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InsuranceCompany insuranceCompany = InsuranceCompany.Enum.PICC_10000;
            starting = insuranceCompany == null;
        }
    }

    class CreateImageAndDataFileThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                    List<String> marketingRuleIds = taskRunningService.getRedisMarketingRule();
                    if (CollectionUtils.isEmpty(marketingRuleIds)) {
                        continue;
                    }
                    List<MarketingRule> newPreEffectiveList = new ArrayList<>();
                    for (String ruleId : marketingRuleIds) {
                        MarketingRule rule = marketingRuleRepository.findOne(Long.parseLong(ruleId));
                        if (rule == null) {
                            continue;
                        }
                        logger.debug("running createImageAndDataFile for marketingRule task,got new message,marketingRuleId -->{}", ruleId);
                        rule.setVersion(rule.getVersion() + 1);
                        rule.setUpdate_time(new Date());
                        newPreEffectiveList.add(rule);
                    }
                    createImageAndDataFile(newPreEffectiveList);
                } catch (NullPointerException e) {
                    logger.error("running createImageAndDataFile for marketingRule task error", e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}


