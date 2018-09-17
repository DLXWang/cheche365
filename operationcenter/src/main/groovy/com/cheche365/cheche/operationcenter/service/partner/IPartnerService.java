package com.cheche365.cheche.operationcenter.service.partner;

import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.CooperationModeViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerAttachmentViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerViewModel;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/26.
 */
public interface IPartnerService {
    /**
     * 新建合作商
     * @param partnerViewModel
     */
    void addPartner(PartnerViewModel partnerViewModel);

    /**
     * 查询合作商详情
     * @param id
     * @return
     */
    PartnerViewModel findById(Long id);

    /**
     * 修改合作商
     * @param viewModel
     * @return
     */
    void updatePartner(PartnerViewModel viewModel);

    /**
     * 删除合作商
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 查询合作商
     * @return
     */
    DataTablesPageViewModel<PartnerViewModel> search(PublicQuery query);

    /**
     * 返回合作商文件地址
     * @param partnerId
     * @param fileType
     * @return
     */
    String getPartnerFilePath(Long partnerId, Integer fileType);

    /**
     * 启用或禁用合作商
     * @param partnerId
     * @param operationType
     * @return
     */
    void switchStatus(Long partnerId, Integer operationType);

    PartnerAttachmentViewModel addPartnerAttachmentFiles(Integer fileType, Long attachmentId, Long partnerId, String url, String originalFileName);

    PartnerAttachmentViewModel removePartnerAttachmentFiles(Long partnerId, Integer fileType, Long attachmentId);

    /**
     * 获取合作商的合作方式
     * @param partnerId
     * @return
     */
    List<CooperationModeViewModel> getCooperationModeByPartnerId(Long partnerId);

    /**
     * 验证合作商名称唯一性
     * @param name
     * @return
     */
    boolean checkPartnerName(String name);
}
