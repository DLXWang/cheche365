package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.manage.common.model.TaskJob;
import com.cheche365.cheche.manage.common.repository.TaskJobRepository;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xu.yelong on 2016-04-12.
 */
@Service
public class TaskJobService {
    @Autowired
    private TaskJobRepository taskJobRepository;

    public List<TaskJob> findByEnable() {
        return taskJobRepository.findByStatus(true);
    }

    public void save(TaskJob taskJob) {
        taskJobRepository.save(taskJob);
    }

    public TaskJob findById(Long id) {
        return taskJobRepository.findOne(id);
    }

    public TaskJob findByJobName(String jobName) {
        return taskJobRepository.findByJobName(jobName);
    }

    public List<TaskJob> findAll() {
        Iterable<TaskJob> taskJobs = taskJobRepository.findAll();
        if (taskJobs != null && taskJobs.iterator().hasNext()) {
            return IteratorUtils.toList(taskJobRepository.findAll().iterator());
        }
        return null;
    }
}
