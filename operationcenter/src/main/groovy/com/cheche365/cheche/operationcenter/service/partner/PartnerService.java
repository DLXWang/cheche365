package com.cheche365.cheche.operationcenter.service.partner;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.CooperationMode;
import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.core.model.PartnerAttachment;
import com.cheche365.cheche.core.model.PartnerType;
import com.cheche365.cheche.core.repository.CooperationModeRepository;
import com.cheche365.cheche.core.repository.PartnerAttachmentRepository;
import com.cheche365.cheche.core.repository.PartnerRepository;
import com.cheche365.cheche.core.repository.PartnerTypeRepository;
import com.cheche365.cheche.core.service.IResourceService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.CooperationModeViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerAttachmentViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerTypeViewModel;
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sunhuazhong on 2015/8/26.
 */
@Service(value = "partnerService")
@Transactional
public class PartnerService extends BaseService implements IPartnerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String REMOVE_PARTNER_ATTACHMENT_FILES_PARTNERID_KEY = "RemovePartnerAttachmentFilesPartnerIdKey";

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private PartnerTypeRepository partnerTypeRepository;

    @Autowired
    private PartnerAttachmentRepository partnerAttachmentRepository;

    @Autowired
    private CooperationModeRepository cooperationModeRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addPartner(PartnerViewModel partnerViewModel) {
        // 保存合作商
        Partner partner = partnerRepository.save(this.createPartner(partnerViewModel));
        //更新文件所属合作商
        updatePartnerAttachment(partnerViewModel, partner);
    }

    private void updatePartnerAttachment(PartnerViewModel partnerViewModel, Partner partner) {
        Long partnerAttachmentId = partnerViewModel.getPartnerAttachment().getId();
        if (0 == partnerViewModel.getId()) {
            if (0 != partnerAttachmentId) {
                PartnerAttachment partnerAttachment = partnerAttachmentRepository.findOne(partnerAttachmentId);
                AssertUtil.notNull(partnerAttachment, "can not find partnerAttachment by id -> " + partnerAttachmentId);
                logger.info("the partnerAttachment of partner which id equals {} is {}, need to update partnerAttachment of partner.",
                        partnerAttachmentId, partner.getId());
                partnerAttachment.setPartner(partner);
                partnerAttachmentRepository.save(partnerAttachment);
            } else {
                logger.info("the partnerAttachment of partner which id equals {} is null, do not need to save partnerAttachment.", partner.getId());
            }
        } else {
            if (0 == partnerAttachmentId) {
                if (StringUtils.isNotBlank(partnerViewModel.getPartnerAttachment().getContractUrl())
                        || StringUtils.isNotBlank(partnerViewModel.getPartnerAttachment().getTechnicalDocumentUrl())) {
                    logger.info("old partner's partnerAttachment is null, need to new one and save.");
                    PartnerAttachment partnerAttachmentNew = new PartnerAttachment();
                    String[] contains = new String[]{"contractUrl", "contractName", "technicalDocumentUrl", "technicalDocumentName"};
                    BeanUtil.copyPropertiesContain(partnerViewModel.getPartnerAttachment(), partnerAttachmentNew, contains);
                    partnerAttachmentNew.setPartner(partner);
                    partnerAttachmentRepository.save(partnerAttachmentNew);
                } else {
                    logger.warn("do not have modifies for partnerAttachment.");
                }
            } else {
                PartnerAttachment partnerAttachment = partnerAttachmentRepository.findOne(partnerAttachmentId);
                logger.info("old partner's partnerAttachment is not null, need update partnerAttachment data.");
                String[] contains = new String[]{"contractUrl", "contractName", "technicalDocumentUrl", "technicalDocumentName"};
                BeanUtil.copyPropertiesContain(partnerViewModel.getPartnerAttachment(), partnerAttachment, contains);
                partnerAttachmentRepository.save(partnerAttachment);
            }
        }
    }

    @Override
    public PartnerViewModel findById(Long id) {
        Partner partner = partnerRepository.findOne(id);
        AssertUtil.notNull(partner, "can not find partner by id -> " + id);
        return this.createViewData(partner);
    }

    @Override
    public void updatePartner(PartnerViewModel viewModel) {
        Long partnerId = viewModel.getId();
        Partner partner = partnerRepository.findOne(partnerId);
        AssertUtil.notNull(partner, "can not find partner by id -> " + partnerId);
        this.addPartner(viewModel);
    }

    @Override
    public boolean delete(Long id) {
        try {
            Partner partner = partnerRepository.findOne(id);
            // 删除该合作商附件
            PartnerAttachment partnerAttachment = partnerAttachmentRepository.findFirstByPartner(partner);
            if (partnerAttachment != null) {
                partnerAttachmentRepository.delete(partnerAttachment);
            }
            // 删除该合作商合作方式
            /*List<PartnerCooperationMode> modeList = partnerCooperationModeRepository.findByPartner(partner);
            if(!CollectionUtils.isEmpty(modeList)) {
                modeList.forEach(mode -> {
                    partnerCooperationModeRepository.delete(mode);
                });
            }*/
            // 删除该合作商的商务活动

            // 删除该合作商
            partnerRepository.delete(partner);
        } catch (Exception ex) {
            logger.error("delete partner error", ex);
        }
        return false;
    }

    @Override
    public DataTablesPageViewModel<PartnerViewModel> search(PublicQuery query) {
        try {
            Page<Partner> page = this.findBySpecAndPaginate(query,
                    this.buildPageable(query.getCurrentPage(), query.getPageSize()));
            List<PartnerViewModel> pageViewDataList = new ArrayList<>();
            for (Partner partner : page.getContent()) {
                PartnerViewModel viewData = createViewData(partner);
                pageViewDataList.add(viewData);
            }
            PageInfo pageInfo = createPageInfo(page);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), pageViewDataList);
        } catch (Exception e) {
            logger.error("find Partner info by page has error", e);
        }
        return null;
    }

    @Override
    public String getPartnerFilePath(Long partnerId, Integer fileType) {
        String basePath = resourceService.getProperties().getWechatQRCodePath();
        // 根据partnerId获取合作商对象partner
        // 根据合作商获取合作商文件对象partnerAttachment
        PartnerAttachment partnerAttachment = new PartnerAttachment();
        String contractPath = partnerAttachment.getContractUrl().substring(
                partnerAttachment.getContractUrl().indexOf(basePath) + basePath.length(),
                partnerAttachment.getContractUrl().length());
        java.nio.file.Path filePath = Paths.get(resourceService.getResourceAbsolutePath(resourceService.getProperties().getWechatQRCodePath()), contractPath);
        return filePath.toFile().getAbsolutePath();
    }

    public void switchStatus(Long partnerId, Integer operationType) {
        // 合作商
        Partner partner = partnerRepository.findOne(partnerId);
        AssertUtil.notNull(partner, "can not find partner by id -> " + partnerId);

        // 启用或禁用合作商
        if (operationType == 1) {
            partner.setEnable(true);
        } else {
            partner.setEnable(false);
        }
        partner.setUpdateTime(new Date());

        partnerRepository.save(partner);
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

    /**
     * 分页查询
     *
     * @param pageable 分页信息
     * @return Page<Partner>
     */
    private Page<Partner> findBySpecAndPaginate(PublicQuery publicQuery, Pageable pageable) throws Exception {
        return partnerRepository.findAll((root, query, cb) -> {
            CriteriaQuery<Partner> criteriaQuery = cb.createQuery(Partner.class);

            //条件构造
            List<Predicate> predicateList = new ArrayList<>();

            if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                Path<String> namePath = root.get("name");
                // 合作商名称
                predicateList.add(cb.like(namePath, publicQuery.getKeyword() + "%"));
            }

            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }

    /**
     * 组建合作商对象
     *
     * @param viewModel
     * @return
     */
    private Partner createPartner(PartnerViewModel viewModel) {
        Partner partner = new Partner();
        if (viewModel.getId() != 0) {
            partner = partnerRepository.findOne(viewModel.getId());
        }
        partner.setName(viewModel.getName());//合作商名称
        partner.setCooperationTime(DateUtils.getDate(
                viewModel.getCooperationTime(), DateUtils.DATE_SHORTDATE_PATTERN));//预计首次合作时间
        partner.setPartnerType(partnerTypeRepository.findOne(viewModel.getPartnerType().getId()));//合作商类型
        partner.setEnable(viewModel.getId() != 0 && partner.isEnable());//是否启用，0-禁用，1-启用
        partner.setComment(viewModel.getComment());//备注
        partner.setCreateTime(viewModel.getId() == 0 ? Calendar.getInstance().getTime() : partner.getCreateTime());
        partner.setUpdateTime(Calendar.getInstance().getTime());
        partner.setOperator(internalUserManageService.getCurrentInternalUser());
        partner.setCooperationModes(this.createCooperationModes(viewModel));

        return partner;
    }

    private List<CooperationMode> createCooperationModes(PartnerViewModel viewModel) {
        List<CooperationMode> cooperationModeList = new ArrayList<>();
        String[] modelIds = viewModel.getCooperationMode().split(",");

        for (String modelId : modelIds) {
            CooperationMode mode = cooperationModeRepository.findOne(Long.parseLong(modelId));
            AssertUtil.notNull(mode, "illegal modelId, can not find CooperationMode by modelId -> " + modelId);
            cooperationModeList.add(mode);
        }

        return cooperationModeList;
    }

    @Override
    public PartnerAttachmentViewModel addPartnerAttachmentFiles(Integer fileType, Long attachmentId, Long partnerId,
                                                                String url, String originalFileName) {
        PartnerAttachment partnerAttachment;
        PartnerAttachment partnerAttachmentNew = new PartnerAttachment();

        if (0 != attachmentId) {
            partnerAttachment = partnerAttachmentRepository.findOne(attachmentId);
            AssertUtil.notNull(partnerAttachment, "can not find partnerAttachment by id -> " + attachmentId);
            BeanUtil.copyPropertiesContain(partnerAttachment, partnerAttachmentNew);
        }

        this.setFileValues(partnerAttachmentNew, fileType, originalFileName, url);

        if (0 == partnerId) {
            partnerAttachmentRepository.save(partnerAttachmentNew);
        } else {
            logger.info("partnerId is not null, so do not update db data, only update return json is ok!");
        }

        return this.createAttachmentViewModel(partnerAttachmentNew);
    }

    @Override
    public synchronized PartnerAttachmentViewModel removePartnerAttachmentFiles(Long partnerId, Integer fileType, Long attachmentId) {
        String partnerIdCache = stringRedisTemplate.opsForValue().get(REMOVE_PARTNER_ATTACHMENT_FILES_PARTNERID_KEY);
        if (StringUtils.isNotEmpty(partnerIdCache)) {
            logger.info(" 当前partnerId对应的数据正在被别人处理,本次操作跳过");
            return null;
        }
        CacheUtil.putValueWithExpire(stringRedisTemplate, REMOVE_PARTNER_ATTACHMENT_FILES_PARTNERID_KEY, partnerId + "", 1, TimeUnit.MINUTES);
        PartnerAttachment partnerAttachmentNew = new PartnerAttachment();
        PartnerAttachment partnerAttachment = partnerAttachmentRepository.findOne(attachmentId);
        AssertUtil.notNull(partnerAttachment, "can not find partnerAttachment by id -> " + attachmentId);
        BeanUtil.copyPropertiesContain(partnerAttachment, partnerAttachmentNew);

        this.setFileValues(partnerAttachmentNew, fileType, "", "");

        if (0 == partnerId) {
            partnerAttachmentRepository.save(partnerAttachmentNew);
        } else {
            logger.info("partnerId is not null, so do not update db data, only update return json is ok!");
        }

        if (PartnerAttachment.isEmptyEntity(partnerAttachmentNew)) {
            logger.warn("partnerAttachment is empty, then delete it by id -> {}", attachmentId);
            partnerAttachmentRepository.delete(attachmentId);
            return new PartnerAttachmentViewModel();
        }
        stringRedisTemplate.delete(REMOVE_PARTNER_ATTACHMENT_FILES_PARTNERID_KEY);
        return this.createAttachmentViewModel(partnerAttachmentNew);
    }

    private void setFileValues(PartnerAttachment partnerAttachment, Integer fileType, String fileName, String fileUrl) {
        if (FileType.CONTRACT.value.equals(fileType)) {
            partnerAttachment.setContractName(fileName);
            partnerAttachment.setContractUrl(fileUrl);
        } else if (FileType.TECHNICAL.value.equals(fileType)) {
            partnerAttachment.setTechnicalDocumentName(fileName);
            partnerAttachment.setTechnicalDocumentUrl(fileUrl);
        } else {
            throw new IllegalArgumentException("illegal fileType -> " + fileType);
        }
    }

    /**
     * 组建合作商对象，返回到前端显示
     *
     * @param partner
     * @return
     */
    private PartnerViewModel createViewData(Partner partner) {
        PartnerViewModel viewModel = new PartnerViewModel();
        viewModel.setId(partner.getId());
        viewModel.setName(partner.getName());//合作商名称
        viewModel.setEnable(partner.isEnable());//是否启用，0-禁用，1-启用
        viewModel.setPartnerType(this.createPartnerTypeViewModel(partner.getPartnerType()));//合作商类型
        viewModel.setCooperationTime(DateUtils.getDateString(
                partner.getCooperationTime(), DateUtils.DATE_SHORTDATE_PATTERN));//预计首次合作时间
        viewModel.setComment(partner.getComment());//备注
        viewModel.setCreateTime(DateUtils.getDateString(
                partner.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(
                partner.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperator(partner.getOperator() == null ? "" : partner.getOperator().getName());//操作人
        //合作方式
        viewModel.setCooperationModes(this.createCooperationModeViewModels(partner.getCooperationModes()));
        //合作商上传文件
        PartnerAttachment partnerAttachment = partnerAttachmentRepository.findFirstByPartner(partner);
        viewModel.setPartnerAttachment(this.createAttachmentViewModel(partnerAttachment));
        return viewModel;
    }

    private List<CooperationModeViewModel> createCooperationModeViewModels(List<CooperationMode> cooperationModes) {
        if (null == cooperationModes)
            return null;

        List<CooperationModeViewModel> cooperationModeViewModels = new ArrayList<>();
        cooperationModes.forEach(model -> {
            CooperationModeViewModel cooperationModeViewModel = new CooperationModeViewModel();
            String[] contains = new String[]{"id", "name", "description"};
            BeanUtil.copyPropertiesContain(model, cooperationModeViewModel, contains);
            cooperationModeViewModels.add(cooperationModeViewModel);
        });

        return cooperationModeViewModels;
    }

    private PartnerTypeViewModel createPartnerTypeViewModel(PartnerType partnerType) {
        if (null == partnerType)
            return null;

        PartnerTypeViewModel partnerTypeViewModel = new PartnerTypeViewModel();
        String[] contains = new String[]{"id", "name", "description"};
        BeanUtil.copyPropertiesContain(partnerType, partnerTypeViewModel, contains);
        return partnerTypeViewModel;
    }

    private PartnerAttachmentViewModel createAttachmentViewModel(PartnerAttachment partnerAttachment) {
        if (null == partnerAttachment)
            return null;

        PartnerAttachmentViewModel partnerAttachmentViewModel = new PartnerAttachmentViewModel();
        partnerAttachmentViewModel.setId(partnerAttachment.getId());
        partnerAttachmentViewModel.setPartnerId(partnerAttachment.getPartner() != null ? partnerAttachment.getPartner().getId() : null);
        partnerAttachmentViewModel.setContractUrl(partnerAttachment.getContractUrl());
        partnerAttachmentViewModel.setContractName(partnerAttachment.getContractName());
        partnerAttachmentViewModel.setTechnicalDocumentUrl(partnerAttachment.getTechnicalDocumentUrl());
        partnerAttachmentViewModel.setTechnicalDocumentName(partnerAttachment.getTechnicalDocumentName());

        return partnerAttachmentViewModel;
    }

    public enum FileType {
        CONTRACT(1),
        TECHNICAL(2);

        private final Integer value;

        FileType(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    @Override
    public List<CooperationModeViewModel> getCooperationModeByPartnerId(Long partnerId) {
        Partner partner = partnerRepository.findOne(partnerId);
        List<CooperationMode> cooperationModeList = partner.getCooperationModes();
        return createPartnerCooperationMode(cooperationModeList);
    }

    /**
     * 组建合作商合作方式对象
     *
     * @param cooperationModeList
     * @return
     */
    private List<CooperationModeViewModel> createPartnerCooperationMode(List<CooperationMode> cooperationModeList) {
        if (cooperationModeList == null)
            return null;

        List<CooperationModeViewModel> viewDataList = new ArrayList<>();
        cooperationModeList.forEach(cooperationMode -> {
            CooperationModeViewModel viewData = new CooperationModeViewModel();
            viewData.setId(cooperationMode.getId());
            viewData.setName(cooperationMode.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }

    @Override
    public boolean checkPartnerName(String name) {
        Partner partner = partnerRepository.findFirstByName(name);
        if (partner == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询所有合作商
     * @return
     */
    public List<Partner> selectAll() {
        return partnerRepository.findAll();
    }

    /**
     * 通过id查询partner信息
     * @param partner
     * @return
     */
    public Partner findOne(Partner partner) {
        return partnerRepository.findOne(partner.getId());
    }
}
