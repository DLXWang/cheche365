package com.cheche365.cheche.operationcenter.service.wechat.channel;

import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.model.QRCodeChannel;
import com.cheche365.cheche.core.repository.QRCodeChannelRepository;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.operationcenter.web.model.wechat.channel.QRCodeChannelViewModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangfei on 2015/7/28.
 */
@Service(value = "foreverQCChannelService")
@Qualifier("foreverQCChannelService")
@Transactional
public class ForeverQCChannelService extends QCChannelService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Integer WECHAT_QRCODE_UPDATE_CREATE = 1;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private QRCodeChannelRepository qrCodeChannelRepository;

    @Autowired
    private DoubleDBService doubleDBService;

    public QRCodeChannelViewModel updateChannel(QRCodeChannelViewModel model, Long channelId) {
        try {
            String comment = model.getComment();
            if(StringUtils.isNotBlank(comment)){
                model.setComment(comment.replace("\n", "\\r\\n"));
            }

            if(WECHAT_QRCODE_UPDATE_CREATE.equals(model.getUpdateFlag())) {
                // 创建新二维码渠道
                QRCodeChannel newChannel = createNewChannel(model, channelId);
                return this.findOne(newChannel.getId());
            } else {
                // 修改原二维码渠道
                updateOrginalChannel(model, channelId);
                return this.findOne(channelId);
            }
        } catch (Exception ex) {
            logger.error("update qrcode channel error.", ex);
        }
        return null;
    }

    private void updateOrginalChannel(QRCodeChannelViewModel model, Long channelId) {
        QRCodeChannel originQRCodeChannel = qrCodeChannelRepository.findOne(channelId);
        String name = originQRCodeChannel.getName();
        Double rebate = originQRCodeChannel.getRebate();

        // 修改二维码渠道信息
        String[] contains = new String[]{"name","department", "rebate", "comment"};
        BeanUtil.copyPropertiesContain(model, originQRCodeChannel, contains);
        originQRCodeChannel.setUpdateTime(new Date());
        qrCodeChannelRepository.save(originQRCodeChannel);

        // 操作日志
        if(!model.getName().equals(name)) {
            MoApplicationLog applicationLog = new MoApplicationLog();
            applicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
            applicationLog.setLogMessage("渠道名称：" + name + "更改为" + model.getName());//日志信息
            applicationLog.setLogType(LogType.Enum.UPDATE_WECHAT_QRCODE_CHANNEL_NAME_18);//修改二维码渠道名称
            applicationLog.setObjId(originQRCodeChannel.getId() + "");//对象id
            applicationLog.setObjTable("qrcode_channel");//对象表名
            applicationLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());//操作人
            applicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
            doubleDBService.saveApplicationLog(applicationLog);
        }
        if(!model.getRebate().equals(rebate)) {
            MoApplicationLog applicationLog = new MoApplicationLog();
            applicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
            applicationLog.setLogMessage("返点金额：" + rebate + "更改为" + model.getRebate());//日志信息
            applicationLog.setLogType(LogType.Enum.UPDATE_WECHAT_QRCODE_CHANNEL_REBATE_19);//修改二维码渠道返点金额
            applicationLog.setObjId(originQRCodeChannel.getId() + "");//对象id
            applicationLog.setObjTable("qrcode_channel");//对象表名
            applicationLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());//操作人
            applicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
            doubleDBService.saveApplicationLog(applicationLog);
        }
    }

    private QRCodeChannel createNewChannel(QRCodeChannelViewModel model, Long channelId) {
        QRCodeChannel originQRCodeChannel = qrCodeChannelRepository.findOne(channelId);
        // 新建一个渠道，使用以前渠道的二维码
        QRCodeChannel newChannel = new QRCodeChannel();
        String[] contains = new String[]{"name","department", "rebate", "comment"};
        BeanUtil.copyPropertiesContain(model, newChannel, contains);
        newChannel.setCode(this.getNextChannelNo(QRCodeType.format(model.getQrCodeType())));
        newChannel.setUpdateTime(new Date());
        newChannel.setCreateTime(new Date());
        newChannel.setOperator(internalUserManageService.getCurrentInternalUser());
        newChannel.setWechatQRCode(originQRCodeChannel.getWechatQRCode());
        qrCodeChannelRepository.save(newChannel);

        // 原渠道编辑为不可用
        originQRCodeChannel.setDisable(true);
        qrCodeChannelRepository.save(originQRCodeChannel);

        return newChannel;
    }

    public String[] supplyListExcelContent(QRCodeChannelViewModel data) {
        return new String[]{
            StringUtils.trimToEmpty(data.getCode()),
            StringUtils.trimToEmpty(data.getName()),
            StringUtils.trimToEmpty(data.getDepartment()),
            data.getScanCount()+"",
            data.getSubscribeCount()+"",
            data.getBindingMobileCount()+"",
            data.getSuccessOrderCount()+"",
            StringUtils.trimToEmpty(String.valueOf(data.getRebate())),
            StringUtils.trimToEmpty(data.getComment())};
    }

    public String[] supplyListExcelTitle(HSSFSheet sheet) {
        sheet.setColumnWidth(0, 18 * 200);
        sheet.setColumnWidth(1, 18 * 400);
        sheet.setColumnWidth(2, 18 * 400);
        sheet.setColumnWidth(3, 18 * 150);
        sheet.setColumnWidth(4, 18 * 150);
        sheet.setColumnWidth(5, 18 * 250);
        sheet.setColumnWidth(6, 18 * 250);
        sheet.setColumnWidth(7, 18 * 250);
        sheet.setColumnWidth(8, 18 * 800);
        return new String[]{"渠道号","渠道名","所属部门","扫描数","关注数","绑定手机数","成功订单数","返点金额(元)","备注"};
    }
}
