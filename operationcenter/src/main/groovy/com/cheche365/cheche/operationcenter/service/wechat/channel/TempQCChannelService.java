package com.cheche365.cheche.operationcenter.service.wechat.channel;


import com.cheche365.cheche.operationcenter.web.model.wechat.channel.QRCodeChannelViewModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wangfei on 2015/7/28.
 */
@Service(value = "tempQCChannelService")
@Qualifier("tempQCChannelService")
@Transactional
public class TempQCChannelService extends QCChannelService {

    public String[] supplyListExcelContent(QRCodeChannelViewModel data) {
        return new String[]{
            StringUtils.trimToEmpty(data.getCode()),
            StringUtils.trimToEmpty(data.getName()),
            StringUtils.trimToEmpty(data.getDepartment()),
            StringUtils.trimToEmpty(data.getStatus()),
            StringUtils.trimToEmpty(data.getExpireTime()),
            data.getScanCount()+"",
            data.getSubscribeCount()+"",
            data.getBindingMobileCount()+"",
            data.getSuccessOrderCount()+"",
            StringUtils.trimToEmpty(String.valueOf(data.getRebate()) ),
            StringUtils.trimToEmpty(data.getComment())};
    }

    public String[] supplyListExcelTitle(HSSFSheet sheet) {
        sheet.setColumnWidth(0, 18 * 200);
        sheet.setColumnWidth(1, 18 * 400);
        sheet.setColumnWidth(2, 18 * 400);
        sheet.setColumnWidth(3, 18 * 160);
        sheet.setColumnWidth(4, 18 * 400);
        sheet.setColumnWidth(5, 18 * 150);
        sheet.setColumnWidth(6, 18 * 150);
        sheet.setColumnWidth(7, 18 * 250);
        sheet.setColumnWidth(8, 18 * 250);
        sheet.setColumnWidth(9, 18 * 250);
        sheet.setColumnWidth(10, 18 * 800);
        return new String[]{"渠道号","渠道名","所属部门","有效状态","到期时间","扫描数",
            "关注数","绑定手机数","成功订单数","返点金额(元)","备注"};
    }

}
