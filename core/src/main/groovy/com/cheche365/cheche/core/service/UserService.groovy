package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserSource
import com.cheche365.cheche.core.model.UserType
import com.cheche365.cheche.core.repository.UserRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanWrapper
import org.springframework.beans.PropertyAccessorFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID

/**
 * Created by zhengwei on 3/23/15.
 */

@Service
class UserService {

    @Autowired
    private UserRepository userRepo

    @Transactional
    User addUser(User user) {
        return userRepo.save(user)
    }

    User getUserById(long id) {
        User user = this.userRepo.findOne(id)
        return bindUser(user)
    }

    User getBindingUser(String mobile) {
        return userRepo.findByMobileAndBound(mobile)
    }

    void validRegisterUser(String mobile) {
        User userFromDb = this.getUserByMobile(mobile)
        if (null != userFromDb) {
            throw new BusinessException(INPUT_FIELD_NOT_VALID, "手机号已绑定，请直接登录")
        }
    }

    User getUserByMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null
        }
        User user = getBindingUser(mobile)
        if (user) {
            return user
        }
        user = userRepo.findByMobile(mobile)
        return bindUser(user)
    }

    private User bindUser(User user) {
        if (user && user.mobile && Boolean.TRUE != user.isBound()) {
            user.setBound(Boolean.TRUE)
            user.setUpdateTime(Calendar.getInstance().getTime())
            userRepo.save(user)
        }
        user
    }

    User findOrCreateUser(String mobile, Channel channel, UserSource userSource) {
        User user = getUserByMobile(mobile)
        if (user) {
            return user
        }
        return createUser(mobile, channel, userSource)
    }

    @Transactional
    User createUser(String mobile, Channel channel, UserSource userSource) {
        User user = new User()
        user.setMobile(mobile)
        user.setBound(StringUtils.isBlank(mobile) ? Boolean.FALSE : Boolean.TRUE)
        user.setAudit(1)
        user.setCreateTime(Calendar.getInstance().getTime())
        user.setUpdateTime(Calendar.getInstance().getTime())
        user.setUserType(UserType.Enum.Customer)
        user.setRegisterChannel(channel)
        user.setRegisterUser(StringUtils.isBlank(mobile) ? Boolean.FALSE : Boolean.TRUE)
        user.setUserSource(userSource)
        userRepo.save(user)
        return user
    }

    @Transactional
    User boundMobile(User user, String mobile) {
        User boundUser = getBindingUser(mobile)
        if (boundUser != null && (user.getId().longValue() != boundUser.getId().longValue())) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "这个电话号码已经被绑定到其他用户")
        }

        user.setMobile(mobile)
        user.setBound(Boolean.TRUE)
        user.setUpdateTime(Calendar.getInstance().getTime())
        return userRepo.save(user)
    }

    def checkInfo(User user){
        def needToComplete = []

        if (StringUtils.isEmpty(user.getName())){
            needToComplete << 'name'
        }
        if (StringUtils.isEmpty(user.getIdentity())){
            needToComplete << 'identity'
        }
        if (needToComplete){
            throw new BusinessException(INPUT_FIELD_NOT_VALID, "以下信息不完善${needToComplete}")
        }

    }

    User modifyUser(User existedUser, User user) {
        BeanWrapper sourceUser = PropertyAccessorFactory.forBeanPropertyAccess(user)
        BeanWrapper targetUser = PropertyAccessorFactory.forBeanPropertyAccess(existedUser)
        ["name", "identityType", "identity", "employeeNum", "gender", "email", "area","useDefaultEmail"].each {
            if (sourceUser.getPropertyValue(it) != null) {
                targetUser.setPropertyValue(it, sourceUser.getPropertyValue(it))
            }
        }
        existedUser.setUpdateTime(new Date())

        return userRepo.save(existedUser)
    }

    User findInviterByPurchaseOrderId(Long id) {
        return userRepo.findInviterByPurchaseOrderId(id)
    }

    User findTopInviterByByPurchaseOrderId(Long id){
        return userRepo.findTopInviterByByPurchaseOrderId(id)
    }

}
