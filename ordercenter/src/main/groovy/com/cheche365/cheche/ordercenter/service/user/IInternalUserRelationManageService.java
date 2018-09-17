package com.cheche365.cheche.ordercenter.service.user;

import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.user.InternalUserRelationData;

/**
 * Created by sunhuazhong on 2015/7/9.
 */
public interface IInternalUserRelationManageService {

    /**
     * 绑定用户组
     * @param customerId
     * @param internalId
     * @param externalId
     * @return
     */
    boolean add(Long customerId, Long internalId, Long externalId);

    /**
     * 修改用户组
     * @param relationData
     */
    ModelAndViewResult update(InternalUserRelationData relationData);

    /**
     * 删除用户组
     * @param id
     */
    ModelAndViewResult delete(Long id);

    /**
     * 根据id查询用户组
     * @param id
     * @return
     */
    ModelAndViewResult findOne(Long id);

    /**
     * 根据条件查询用户组
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    PageViewModel<InternalUserRelationData> listInternalUserRelation(Integer currentPage, Integer pageSize, String keyword);
}
