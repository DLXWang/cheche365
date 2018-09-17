package com.cheche365.cheche.admin.web.controller.account;

import com.cheche365.cheche.admin.service.RoleAndPermissionChecker;
import com.cheche365.cheche.admin.service.account.AccountService;
import com.cheche365.cheche.admin.web.model.account.AccountPermissionViewModel;
import com.cheche365.cheche.admin.web.model.account.AccountViewModel;
import com.cheche365.cheche.admin.web.model.account.InternalUserDataPermissionMode;
import com.cheche365.cheche.common.util.HashUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.constants.ManageCommonConstants;
import com.cheche365.cheche.manage.common.model.InternalUserDataPermission;
import com.cheche365.cheche.manage.common.service.InternalUserDataPermissionService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.cheche365.cheche.core.model.InsuranceCompany.allCompanies;
import static com.cheche365.cheche.core.model.InsuranceCompany.toInsuranceCompany;

/**
 * Created by liyh on 2015/9/9
 */
@RestController
@RequestMapping("/admin/account")
public class AccountManagementController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private InternalUserRoleService internalUserRoleService;

    @Autowired
    private RoleAndPermissionChecker checker;

    @Autowired
    private InsuranceCompanyService insuranceCompanyService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private InternalUserDataPermissionService internalUserDataPermissionService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 搜索
     *
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/inner", method = RequestMethod.GET)
    @VisitorPermission("ad0301")
    public PageViewModel<AccountViewModel> innerList(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                     @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam(value = "userType", required = false) Integer userType) {
        return listCommon(currentPage, pageSize, keyword, userType);
    }

    /**
     * 新增
     *
     * @param viewModel
     * @return
     */
    @RequestMapping(value = "/inner", method = RequestMethod.POST)
    @VisitorPermission("ad030101")
    public ResultModel innerAdd(@Valid AccountViewModel viewModel, BindingResult result) {
        checker.checkAssignedRolesToUser(internalUserManageService.getCurrentInternalUser(), viewModel.getRoleIds());
        return addCommon(viewModel, result);
    }

    /**
     * 修改
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/inner", method = RequestMethod.PUT)
    @VisitorPermission("ad030102")
    public ResultModel innerUpdate(@Valid AccountViewModel model, BindingResult result) {
        checker.checkAssignedRolesToUser(internalUserManageService.getCurrentInternalUser(), model.getRoleIds());
        return updateCommon(model, result);
    }

    /**
     * 修改状态
     *
     * @param id,status
     * @return
     */
    @RequestMapping(value = "/inner/updateStatus", method = RequestMethod.GET)
    @VisitorPermission("ad030103")
    public ResultModel innerUpdateStatus(@RequestParam(value = "id", required = true) Long id,
                                         @RequestParam(value = "status", required = true) boolean status) {
        boolean isSuccess = accountService.updateStatus(id, status);

        if (isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "更改状态失败");
        }
    }

    /**
     * 修改密码
     *
     * @param id
     * @param password
     * @return
     */
    @RequestMapping(value = "/inner/modifyPassword", method = RequestMethod.POST)
    @VisitorPermission("ad030105")
    public ResultModel innerModifyPassword(@RequestParam(value = "id", required = true) Long id,
                                           @RequestParam(value = "password", required = true) String password) {
        boolean isSuccess = internalUserService.modifyPassword(id, password);
        if (isSuccess) {
            InternalUser internalUser = internalUserService.getInternalUserById(id);
            stringRedisTemplate.opsForHash().delete(ManageCommonConstants.USER_LOCK_KEY, internalUser.getEmail());
            return new ResultModel(true, "修改密码成功");
        } else {
            return new ResultModel(false, "修改密码失败");
        }
    }

    /**
     * 获取单个用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AccountViewModel findOne(@PathVariable Long id) {
        InternalUser internalUser = accountService.findOne(id);
        return this.createViewData(internalUser);
    }

    @RequestMapping(value = "/outer", method = RequestMethod.GET)
    @VisitorPermission("ad0302")
    public PageViewModel<AccountViewModel> outerList(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                     @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam(value = "userType", required = false) Integer userType) {
        return listCommon(currentPage, pageSize, keyword, userType);
    }

    /**
     * 新增
     *
     * @param viewModel
     * @return
     */
    @RequestMapping(value = "/outer", method = RequestMethod.POST)
    @VisitorPermission("ad030201")
    public ResultModel outerAdd(@Valid AccountViewModel viewModel, BindingResult result) {
        checker.checkAssignedRolesToUser(internalUserManageService.getCurrentInternalUser(), viewModel.getRoleIds());
        return addCommon(viewModel, result);
    }

    /**
     * 修改
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/outer", method = RequestMethod.PUT)
    @VisitorPermission("ad030202")
    public ResultModel outerUpdate(@Valid AccountViewModel model, BindingResult result) {
        checker.checkAssignedRolesToUser(internalUserManageService.getCurrentInternalUser(), model.getRoleIds());
        return updateCommon(model, result);
    }

    /**
     * 修改状态
     *
     * @param id,status
     * @return
     */
    @RequestMapping(value = "/outer/updateStatus", method = RequestMethod.GET)
    @VisitorPermission("ad030203")
    public ResultModel outerUpdateStatus(@RequestParam(value = "id", required = true) Long id,
                                         @RequestParam(value = "status", required = true) boolean status) {
        boolean isSuccess = accountService.updateStatus(id, status);
        if (isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "更改状态失败");
        }
    }

    /**
     * 修改密码
     *
     * @param id
     * @param password
     * @return
     */
    @RequestMapping(value = "/outer/modifyPassword", method = RequestMethod.POST)
    @VisitorPermission("ad030205")
    public ResultModel outerModifyPassword(@RequestParam(value = "id", required = true) Long id,
                                           @RequestParam(value = "password", required = true) String password) {
        boolean isSuccess = internalUserService.modifyPassword(id, password);
        if (isSuccess) {
            return new ResultModel(true, "修改密码成功");
        } else {
            return new ResultModel(false, "修改密码失败");
        }
    }

    public PageViewModel<AccountViewModel> createPageViewResult(Page page) {
        PageViewModel model = new PageViewModel<AccountViewModel>();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);
        List<AccountViewModel> pageViewDataList = new ArrayList<>();
        for (InternalUser internalUser : (List<InternalUser>) page.getContent()) {
            pageViewDataList.add(createViewData(internalUser));
        }
        model.setViewList(pageViewDataList);
        return model;
    }

    public PageViewModel listCommon(Integer currentPage, Integer pageSize, String keyword, Integer userType) {
        try {
            Page<InternalUser> internalUser = accountService.list(currentPage, pageSize, keyword, userType);
            return createPageViewResult(internalUser);
        } catch (Exception e) {
            logger.error("find account info by page has error", e);
            return null;
        }
    }

    public ResultModel addCommon(AccountViewModel viewModel, BindingResult result) {
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");
        try {
            InternalUser internalUser = this.createInternalUser(viewModel);
            if (!accountService.isEmailExist(internalUser)) {
                return new ResultModel(false, "邮箱已被注册");
            }
            boolean isSuccess = accountService.addOrUpdate(internalUser, viewModel.getRoleIds());
            if (isSuccess) {
                return new ResultModel();
            } else {
                logger.error("internalUser add is failed");
                return new ResultModel(false, "添加失败");
            }
        } catch (Exception e) {
            logger.error("internalUser account add has a error", e);
            return new ResultModel(false, "系统异常");
        }
    }

    public ResultModel updateCommon(AccountViewModel model, BindingResult result) {
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");
        try {
            InternalUser internalUser = internalUserService.getInternalUserById(model.getId());
            internalUser.setName(model.getName());
            internalUser.setMobile(model.getMobile());
            internalUser.setUpdateTime(Calendar.getInstance().getTime());
            boolean isSuccess = accountService.addOrUpdate(internalUser, model.getRoleIds());
            if (isSuccess) {
                return new ResultModel();
            } else {
                logger.error("internalUser update is failed");
                return new ResultModel(false, "修改失败");
            }
        } catch (Exception e) {
            logger.error("internalUser update has a error", e);
            return new ResultModel(false, "系统异常");
        }

    }

    public AccountViewModel createViewData(InternalUser internalUser) {
        AccountViewModel viewModel = AccountViewModel.createViewModel(internalUser);
        List<InternalUserRole> internalUserRoleList = internalUserRoleService.getRolesByInternalUser(internalUser);
        viewModel.setRoleIds(internalUserRoleService.getStrRoleProperty(internalUserRoleList, InternalUserRoleService.ROLE_PROPERTY_ID));
        viewModel.setRoleName(internalUserRoleService.getStrRoleProperty(internalUserRoleList, InternalUserRoleService.ROLE_PROPERTY_NAME));
        return viewModel;
    }

    private InternalUser createInternalUser(AccountViewModel viewData) throws Exception {
        InternalUser internalUser = new InternalUser();
        String[] properties = {
            "email", "mobile", "name", "disable", "internalUserType"
        };
        BeanUtil.copyPropertiesContain(viewData, internalUser, properties);
        if (StringUtils.isNotEmpty(viewData.getPassword())) {
            internalUser.setPassword(HashUtils.getMD5(viewData.getPassword()));
        }
        if (internalUser.getCreateTime() == null) {
            internalUser.setCreateTime(Calendar.getInstance().getTime());
        }
        internalUser.setUpdateTime(Calendar.getInstance().getTime());

        return internalUser;
    }

    /*    *//**
     * 获取用户权限信息
     *
     * @param id
     * @return
     *//*
    @RequestMapping(value = "/permission/{id}", method = RequestMethod.GET)
    public AccountViewModel findByID(@PathVariable Long id) {
        InternalUser internalUser = internalUserService.getInternalUserById(id);
        return this.createViewData(internalUser);
    }*/

    /**
     * 获取所有的权限
     *
     * @param viewModel
     * @return
     */
    @RequestMapping(value = "/permissionType", method = RequestMethod.GET)
    public Object findByVal(AccountPermissionViewModel viewModel) {
        HashMap<String, Object> map = new HashMap<>();
        Object object = null;
        //根据查询对象 字段 查询已经有的权限
        InternalUser internalUser = new InternalUser();
        internalUser.setId(viewModel.getInternalID());
        InternalUserDataPermission iudp = internalUserDataPermissionService.getChossedPermission(internalUser, viewModel.getEntity(), viewModel.getField());
        //如果查不到结果就说明在数据库中还没有  显示全部的
        if (iudp != null) {
            String values = iudp.getValues();
            if (values != null && !values.equals("")) {
                String[] vals = values.split(",");
                Long[] permissions = new Long[vals.length];
                for (int i = 0; i < vals.length; i++) {
                    permissions[i] = Long.parseLong(vals[i]);
                }
                map.put("choosed", permissions);
            }
        }
        switch (viewModel.getChooseType()) {
            case 1: //地区  只有地区是在数据库中查  其余的都在缓存中取
                object = areaService.getEnalbeAreas();
                break;
            case 2: //PaymentChannel付款渠道
                object = PaymentChannel.Enum.ALL;
                break;
            case 3: //产品平台
                object = Channel.allChannels();
                break;
            case 4: //保险公司
                object = allCompanies();
                break;
            case 5: //保险公司
                object = TelMarketingCenterSource.Enum.SOURCE_LIST;
                break;
            default:
                break;
        }
        map.put("Data", object);
        return map;
    }

    //新增操作
    @RequestMapping(value = "/addPermission", method = RequestMethod.POST)
    @VisitorPermission("ad030101")
    public ResultModel addPermission(AccountPermissionViewModel viewModel) {
        if (viewModel.getValues().size() > 0) {
            InternalUserDataPermission internalUserDataPermission = view2Bean(viewModel);
            internalUserDataPermissionService.saveOrUpdate(internalUserDataPermission);
            return new ResultModel(true, "新增权限成功");
        } else {
            return new ResultModel(false, "新增权限失败");
        }
    }

    //查看所有的权限,并且返回集合
    @RequestMapping(value = "/lookPermission/{id}", method = RequestMethod.POST)
    @VisitorPermission("ad030101")
    public List<InternalUserDataPermissionMode> lookPermissions(@PathVariable Long id) {
        InternalUser internalUser = new InternalUser();
        internalUser.setId(id);
        //查询该用户所有的权限
        List<InternalUserDataPermission> internalUserDataPermissions = internalUserDataPermissionService.findByUser(internalUser);
        // 把internalUserDataPermission封装传给你前端用户显示的bean
        List<InternalUserDataPermissionMode> modeList = new ArrayList<>();
        for (InternalUserDataPermission internalUserDataPermission :
            internalUserDataPermissions) {
            //把数据库中对应的id 转换成相应的描述
            String values = getValues(internalUserDataPermission.getEntity(), internalUserDataPermission.getField(), internalUserDataPermission.getValues());
            InternalUserDataPermissionMode internalUserDataPermissionMode = new InternalUserDataPermissionMode();
            internalUserDataPermissionMode.setComment(internalUserDataPermission.getComment());
            internalUserDataPermissionMode.setEntity(internalUserDataPermission.getEntity());
            internalUserDataPermissionMode.setField(internalUserDataPermission.getField());
            internalUserDataPermissionMode.setValues(values);
            internalUserDataPermissionMode.setId(internalUserDataPermission.getId());
            internalUserDataPermissionMode.setStatus(internalUserDataPermission.getEnable());
            modeList.add(internalUserDataPermissionMode);
        }
        return modeList;
    }

    //根据对象 和字段 对数据库进行查询
    private String getValues(String entity, String field, String values) {
        //把valuse换成地区名
        if (values != null && !values.equals("")) {
            String[] vals = values.split(",");
            String[] newVal = new String[vals.length];
            //地区
            if (entity.toLowerCase().equals("purchaseorder") && field.toLowerCase().equals("auto.area.id")) {
                List<Long> ids = new ArrayList<>();
                for (String str : vals) {
                    ids.add(Long.parseLong(str));
                }
                StringBuffer stringBuffer = new StringBuffer();
                List<String> names = areaService.getPermissionNameById(ids);
                names.toArray(newVal);
                return String.join(",", newVal);
            }
            //付款渠道
            if (entity.toLowerCase().equals("purchaseorder") && field.toLowerCase().equals("channel.id")) {
                for (int i = 0; i < vals.length; i++) {
                    newVal[i] = PaymentChannel.Enum.toPaymentChannel(Long.parseLong(vals[i])).getName();
                }
                return String.join(",", newVal);
            }
            //产品平台
            if (entity.toLowerCase().equals("purchaseorder") && field.toLowerCase().equals("sourcechannel.id")) {
                for (int i = 0; i < vals.length; i++) {
                    newVal[i] = Channel.toChannel(Long.parseLong(vals[i])).getDescription();
                }
                return String.join(",", newVal);
            }
            //保险公司
            if (entity.toLowerCase().equals("insurancecompany") && field.toLowerCase().equals("id")) {
                for (int i = 0; i < vals.length; i++) {
                    newVal[i] = toInsuranceCompany(Long.parseLong(vals[i])).getName();
                }
                return String.join(",", newVal);
            }
            //电销
            if (entity.toLowerCase().equals("telmarketingcentersource") && field.toLowerCase().equals("id")) {
                for (int i = 0; i < vals.length; i++) {
                    newVal[i] = TelMarketingCenterSource.Enum.getById(Long.parseLong(vals[i])).getDescription();
                }
                return String.join(",", newVal);
            }
        }
        return String.join(",", values);
    }

    //对数据进行封装
    public InternalUserDataPermission view2Bean(AccountPermissionViewModel viewModel) {

        InternalUserDataPermission internalUserDataPermission = new InternalUserDataPermission();
        internalUserDataPermission.setEntity(viewModel.getEntity());
        internalUserDataPermission.setField(viewModel.getField());
        StringBuffer stringBuffer = new StringBuffer();
        viewModel.getValues().forEach(id -> {
            stringBuffer.append(id.toString());
            stringBuffer.append(",");
        });
        String tmp = stringBuffer.toString();
        String values = tmp.substring(0, tmp.length() - 1);
        internalUserDataPermission.setValues(values);
        InternalUser internalUser = internalUserService.findByID(viewModel.getInternalID());
        internalUserDataPermission.setInternalUser(internalUser);
        internalUserDataPermission.setComment(viewModel.getComment());
        internalUserDataPermission.setCode(viewModel.getCode());

        return internalUserDataPermission;
    }

    //更改状态
    @RequestMapping(value = "/inner/updateStatusPermission", method = RequestMethod.GET)
    @VisitorPermission("ad030101")
    public ResultModel changstatus(@RequestParam(value = "id", required = true) Long id,
                                   @RequestParam(value = "status", required = true) boolean status) {
        Boolean isSuccess = internalUserDataPermissionService.updateStatus(id, status);
        if (isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "更改状态失败");
        }

    }
}
