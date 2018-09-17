package com.cheche365.cheche.admin.web.controller.task;

import com.cheche365.cheche.admin.service.task.TaskJobService;
import com.cheche365.cheche.admin.service.task.TaskOnceService;
import com.cheche365.cheche.admin.web.model.task.TaskJobDetailViewModel;
import com.cheche365.cheche.admin.web.model.task.TaskJobQuery;
import com.cheche365.cheche.admin.web.model.task.TaskJobViewModel;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.TaskJobDetail;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xu.yelong on 2016-04-13.
 */
@RestController
@RequestMapping("/admin/task")
public class TaskJobController {

    @Autowired
    @Qualifier("jobService")
    TaskJobService taskJobService;

    @Autowired
    TaskOnceService taskOnceService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public PageViewModel<TaskJobViewModel> findAll(@RequestParam(value = "jobName") String jobName,
                                                   @RequestParam(value = "status") String status,
                                                   @RequestParam(value = "currentPage") Integer currentPage,
                                                   @RequestParam(value = "pageSize") Integer pageSize) {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list taskJob info, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list taskJob info, pageSize can not be null or less than 1");
        }
        TaskJobQuery query = new TaskJobQuery();query.setJobName(jobName);query.setStatus(status);query.setCurrentPage(currentPage);query.setPageSize(pageSize);
        return taskJobService.findAll(query);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public TaskJobViewModel findOne(@PathVariable(value="id") Long id){
        return TaskJobViewModel.createViewModel(taskJobService.findById(id));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultModel update(@Valid TaskJobViewModel viewModel, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
          return new ResultModel(false,"保存失败");
        }
        if(!taskJobService.isValidExpression(viewModel.getJobCronExpression())){
            return new ResultModel(false,"时间表达式错误");
        }
        try{
            if(taskJobService.save(viewModel)){
                return new ResultModel(true,"保存成功");
            }
        }catch(Exception e){
            return new ResultModel(false,"保存失败");
        }
        return new ResultModel(false,"保存失败");
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public ResultModel reset(){
        if(taskJobService.reset()){
            return new ResultModel(true,"同步成功！");
        }
        return new ResultModel(false,"同步失败");
    }

    @RequestMapping(value = "/findRedis", method = RequestMethod.GET)
    public String findRedisByKey(String redisKey){
        return taskJobService.findRedisByKey(redisKey);
    }

    @RequestMapping(value = "/updateRedis", method = RequestMethod.POST)
    public ResultModel updateRedis(@RequestParam(value = "redisKey", required = true) String redisKey,
                                   @RequestParam(value = "redisValue", required = true) String redisValue){
        if(!stringRedisTemplate.hasKey(redisKey))
            return new ResultModel(false,"redis中不包含该key");
        //如果redis中该redisKey对应的value的类型为String类型的，才进行数据的更新
        if(!DataType.STRING.equals(stringRedisTemplate.type(redisKey)))
            return new ResultModel(false,"该key对应值不是字符串类型");
        stringRedisTemplate.opsForValue().set(redisKey, redisValue);
        return new ResultModel(true,"保存成功");
    }

    @RequestMapping(value = "/findJobDetailByJob", method = RequestMethod.GET)
    public DataTablePageViewModel<TaskJobDetailViewModel> findJobDetailByJob(TaskJobQuery query){
        Page<TaskJobDetail> infoPage = taskJobService.findJobDetailByJob(query);
        PageInfo pageInfo = baseService.createPageInfo(infoPage);
        return new DataTablePageViewModel<TaskJobDetailViewModel>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), infoListToModels(infoPage.getContent()));
    }

    /**
     * 供给flowRunner测试
     */
    @RequestMapping(value = "/once", method = RequestMethod.GET)
    public Map once() {
        if (!taskOnceService.isOnceRunning()) {
            taskOnceService.doOnce();
        }
        return taskOnceService.getOnce();

    }

    private List<TaskJobDetailViewModel> infoListToModels(List<TaskJobDetail> infoList){
        List<TaskJobDetailViewModel> list = new ArrayList<TaskJobDetailViewModel>();
        infoList.forEach(info->list.add(TaskJobDetailViewModel.createViewModel(info)));
        return list;
    }
}
