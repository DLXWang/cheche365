package com.cheche365.cheche.fanhua.web.controller.insurance

import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.MD5
import com.cheche365.cheche.fanhua.service.SyncService
import com.cheche365.cheche.fanhua.web.model.insurance.RecordInsuranceCoverViewModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cglib.beans.BeanMap
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.util.concurrent.TimeUnit

/**
 * 泛华保单推送
 * Created by zhangtc on 2017/11/30.
 */
@RestController
@RequestMapping("/fanhua/insurance")
class RecordInsuranceController {

    private Logger logger = LoggerFactory.getLogger(this.getClass())
    private static final String FANHUA_INSURANCE = "fanhua.insurance.mark."

    @Autowired
    private StringRedisTemplate stringRedisTemplate
    @Autowired
    SyncService syncService
    @Autowired
    Environment env


    @PostMapping
    def recordInsurance(@RequestBody Map vm) {
        Long stamp = System.currentTimeMillis()
        if (vm == null) {
            return [
                'status' : 'fail',
                'message': '缺失签名信息'
            ]
        }
        logger.info("泛华请求报文-{}-：{}",stamp,vm)
        def status = checkParam(vm)
        logger.info("泛华请求处理结果-{}-：{}",stamp,vm)
        if (!status[0])
            return [
                'status' : 'fail',
                'message': status[1]
            ]

        if (checkExists(vm.res)) {
            return [
                'status' : 'success',
                'message': '成功接收'
            ]
        }

        if (vm.sign == MD5.MD5Encode(MD5.MD5Encode(env.getProperty("fanhua.signkey")) + vm.random)) {
            syncService.saveRequest(CacheUtil.doJacksonSerialize(vm))
            saveExists(vm.res)
            logger.info("泛华请求返回-{}-：success",stamp,vm)
            [
                'status' : 'success',
                'message': '成功接收'
            ]
        } else {
            logger.info("泛华请求返回-{}-：验签失败",stamp,vm)
            [
                'status' : 'fail',
                'message': '验签失败'
            ]
        }

    }

    def Boolean checkExists(Object res) {
        def resMark = MD5.MD5Encode(CacheUtil.doJacksonSerialize(res))
        stringRedisTemplate.hasKey(FANHUA_INSURANCE + resMark)
    }

    def saveExists(Object res) {
        def resMark = MD5.MD5Encode(CacheUtil.doJacksonSerialize(res))
        stringRedisTemplate.opsForValue().set(FANHUA_INSURANCE + resMark, '0', 6L, TimeUnit.HOURS)  //泛华5小时重发一次
    }

    def checkParam(Map vm) {
        RecordInsuranceCoverViewModel cover = mapToBean(vm, new RecordInsuranceCoverViewModel())
        return cover.checkParam()
    }

    static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean)
        beanMap.putAll(map)
        return bean
    }
}
