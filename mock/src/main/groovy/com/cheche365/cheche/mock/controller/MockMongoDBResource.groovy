package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by wenling on 2017/8/29.
 */
@RestController
@RequestMapping("/v1.6/mock/mongo")
@Slf4j
class MockMongoDBResource extends  ContextResource{
    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;
    @Autowired
    private  DoubleDBService dbService;

    @NonProduction
    @RequestMapping(value="testMongo",method= RequestMethod.POST)
    persistTestMongo(@RequestBody String type){
        log.info("正在执行mongodb:saveApplicationLog操作");
        dbService.saveApplicationLog(new MoApplicationLog(logType:new LogType(id:1l),logMessage:CacheUtil.doJacksonSerialize(createLogMessage(type)),logId:"log",createTime:new Date()))
    }

    @NonProduction
    @RequestMapping(value="testGetMongo",method= RequestMethod.POST)
    persistTestGetMongo(@RequestBody String id){
        log.info("正在执行mongodb:find操作");
        applicationLogMongoRepository.findOne(Long.valueOf(id))
    }

    def createLogMessage(type){
        def map=["desc.a.b.n":"测试"+new Random().nextInt(10000),"type":2]
        def res;
        switch (type){
            case "json":
                res=map;
                break;
            case "arr":
                res=[new Random().nextInt(10000),map,"---数组---"]
                break;
            default:
                res=map.toString()
        }
        res
    }
}
