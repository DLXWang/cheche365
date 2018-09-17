package com.cheche365.cheche.ordercenter.service.user;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.InternalUserViewData;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;

import java.util.List;
import java.util.Map;

/**
 * Created by sunhuazhong on 2015/5/7.
 */
public interface IInternalUserManageService {

    /**
     * 增加新用户
     *
     * @param viewData
     * @return
     */
    ModelAndViewResult add(InternalUserViewData viewData);

    /**
     * 修改内部员工
     *
     * @param userViewData
     */
    ModelAndViewResult update(InternalUserViewData userViewData);

    /**
     * 修改内部员工密码
     *
     * @param id
     * @param password
     */
    ModelAndViewResult modifyPasswordInOrderCenter(Long id, String password);

    /**
     * 删除内部员工
     *
     * @param id
     */
    ModelAndViewResult delete(Long id);

    /**
     * 根据id查询内部员工
     *
     * @param id
     * @return
     */
    ModelAndViewResult findOneById(Long id);

    /**
     * 根据条件查询用户
     *
     * @return
     */
//    DataTablePageViewModel<InternalUserViewData> listInternalUser(PublicQuery query);

    /**
     * 获取当前登录用户
     */
    public InternalUser getCurrentInternalUser();

    /**
     * 获取当前登录用户，为空返回syatem 用户定时任务及监听器
     *
     * @return
     */
    public InternalUser getCurrentInternalUserOrSystem();

    /**
     * 获取当前登录用户
     */
    public InternalUserViewData getCurrentUser();

    /**
     * 获取当前用户的角色
     *
     * @return
     */
    public List<String> listRole();

    /**
     * 获取当前用户的权限
     *
     * @return
     */
    public List<String> listAuthority();

    /**
     * 遍历所有可用user
     *
     * @return
     */
    Map<String, String> findAllUsers();

    /**
     * 遍历所有可用角色user
     *
     * @return
     */
    Map<String, String> findAllRoleUsers();

    /**
     * 配置或者取消可用于修改订单状态的内部用户
     *
     * @param internalUserId
     * @return
     */
    boolean config(Long internalUserId);

    /**
     * 判断当前用户是否有指定权限
     *
     * @return
     */
    boolean hasPermission(Permission permission);
}
