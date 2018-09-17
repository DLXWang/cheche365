package com.cheche365.cheche.admin.service.task;

import com.cheche365.cheche.admin.web.model.task.TaskJobQuery;
import com.cheche365.cheche.admin.web.model.task.TaskJobViewModel;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.QuotePhoto;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.SchedulingJob;
import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.manage.common.model.TaskJobDetail;
import com.cheche365.cheche.manage.common.model.TaskJobOperate;
import com.cheche365.cheche.manage.common.repository.TaskJobDetailRepository;
import com.cheche365.cheche.manage.common.repository.TaskJobRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.util.*;

/**
 * Created by xu.yelong on 2016-04-19.
 */
@Service("jobService")
public class TaskJobService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskJobRepository taskJobRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TaskJobDetailRepository taskJobDetailRepository;

    private static final String SCHEDULES_TASK_RUNNING_LIST = "schedules.task.running.list";
    private static final String SCHEDULES_TASK_RUNNING_OPERATE = "schedules.task.running.operate";

    public List<TaskJob> findByEnable() {
        return taskJobRepository.findByStatus(true);
    }

    @Transactional
    public boolean save(TaskJobViewModel viewModel) {
        //新增:启用：操作，禁用：不操作
        //修改：启用：操作，禁用：操作
        boolean isAdd = false;
        Integer operate = null;
        if (viewModel.getId() == null && viewModel.getStatus()) {
            isAdd = true;
            operate = TaskJobOperate.Enum.ADD;
        }
        TaskJob taskJob = createTaskJob(viewModel);
        taskJobRepository.save(taskJob);
        if (!isAdd) {
            if (taskJob.getStatus()) {
                operate = TaskJobOperate.Enum.UPD;
            } else {
                operate = TaskJobOperate.Enum.DEL;
            }
        }
        Long result = stringRedisTemplate.opsForList().leftPush(SCHEDULES_TASK_RUNNING_OPERATE, CacheUtil.doJacksonSerialize(new TaskJobOperate(taskJob, operate)));
        return result > 0;
    }

    public TaskJob findById(Long id) {
        return taskJobRepository.findOne(id);
    }

    public TaskJob findByJobName(String jobName) {
        return taskJobRepository.findByJobName(jobName);
    }

    public PageViewModel<TaskJobViewModel> findAll(TaskJobQuery query) {
        Pageable pageable = super.buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, "updateTime");
        Page<TaskJob> taskJobPage = this.findBySpecAndPaginate(pageable, query);
        stringRedisTemplate.opsForList().leftPush(SCHEDULES_TASK_RUNNING_OPERATE, CacheUtil.doJacksonSerialize(new TaskJobOperate(null, TaskJobOperate.Enum.READ)));
        return this.createResult(taskJobPage);
    }

    public Boolean reset() {
        Long result = stringRedisTemplate.opsForList().leftPush(SCHEDULES_TASK_RUNNING_OPERATE, CacheUtil.doJacksonSerialize(new TaskJobOperate(null, TaskJobOperate.Enum.RESET)));
        return result > 0;
    }

    private Page<TaskJob> findBySpecAndPaginate(Pageable pageable, TaskJobQuery taskJobuery) {
        return taskJobRepository.findAll(new Specification<TaskJob>() {
            @Override
            public Predicate toPredicate(Root<TaskJob> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QuotePhoto> criteriaQuery = cb.createQuery(QuotePhoto.class);
                List<Predicate> predicateList = new ArrayList<Predicate>();
                if (!StringUtil.isNull(taskJobuery.getJobName())) {
                    predicateList.add(cb.like(root.get("jobName"), taskJobuery.getJobName() + "%"));
                }
                if (!StringUtil.isNull(taskJobuery.getStatus())) {
                    predicateList.add(cb.equal(root.get("status"), Long.valueOf(taskJobuery.getStatus())));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    private Map<String, SchedulingJob> getSchedulingJobs() {
        Object obj = stringRedisTemplate.opsForValue().get(SCHEDULES_TASK_RUNNING_LIST);
        List<SchedulingJob> schedulingJobs = obj == null ? null : CacheUtil.doListJacksonDeserialize(String.valueOf(obj), SchedulingJob.class);
        Map schedulingJobMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(schedulingJobs)) {
            for (SchedulingJob schedulingJob : schedulingJobs) {
                schedulingJobMap.put(schedulingJob.getJobId(), schedulingJob);
            }
        }
        return schedulingJobMap;
    }

    public PageViewModel<TaskJobViewModel> createResult(Page page) {
        PageViewModel model = new PageViewModel<TaskJobViewModel>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);
        List<TaskJobViewModel> pageViewDataList = new ArrayList<>();
        Map<String, SchedulingJob> schedulingJobMap = getSchedulingJobs();
        for (TaskJob taskJob : (List<TaskJob>) page.getContent()) {
            SchedulingJob schedulingJob = schedulingJobMap.get(taskJob.getId().toString());
            TaskJobViewModel viewModel = TaskJobViewModel.createViewModel(taskJob, schedulingJob);
            pageViewDataList.add(viewModel);
        }
        model.setViewList(pageViewDataList);
        return model;
    }

    private TaskJob createTaskJob(TaskJobViewModel viewModel) {
        TaskJob taskJob = new TaskJob();
        if (viewModel.getId() != null) {
            taskJob = this.findById(viewModel.getId());
        } else {
            taskJob.setCreateTime(new Date());
        }
        String[] properties = new String[]{
                "id", "jobClass", "jobName", "jobCronExpression",
                "paramKey1", "paramValue1", "paramKey2", "paramValue2",
                "paramKey3", "paramValue3", "comment", "status"
        };
        taskJob.setJobClass(viewModel.getJobClass().trim());
        taskJob.setUpdateTime(new Date());
        taskJob.setOperator(internalUserManageService.getCurrentInternalUser());
        BeanUtil.copyPropertiesContain(viewModel, taskJob, properties);
        return taskJob;
    }

    public boolean isValidExpression(String cronExpression) {
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            return date != null && date.after(new Date());
        } catch (ParseException e) {
            logger.debug("表达式校验异常!", e);
        }
        return false;
    }

    public String findRedisByKey(String redisKey) {
        //只有字符串类型的才进行返回
        if (DataType.STRING.equals(stringRedisTemplate.type(redisKey)))
            return stringRedisTemplate.opsForValue().get(redisKey);
        return null;
    }

    public Page<TaskJobDetail> findJobDetailByJob(TaskJobQuery query) {
        return findInfoBySpecAndPaginate(buildPageable(query.getCurrentPage(), query.getPageSize(), Sort.Direction.DESC, "id"), query.getTaskJobId());
    }

    private Page<TaskJobDetail> findInfoBySpecAndPaginate(Pageable pageable, Long taskJobId) {
        return taskJobDetailRepository.findAll(new Specification<TaskJobDetail>() {
            @Override
            public Predicate toPredicate(Root<TaskJobDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<TaskJobDetail> criteriaQuery = cb.createQuery(TaskJobDetail.class);
                List<Predicate> predicateList = Arrays.asList(cb.equal(root.get("taskJob").get("id"), taskJobId));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

}
