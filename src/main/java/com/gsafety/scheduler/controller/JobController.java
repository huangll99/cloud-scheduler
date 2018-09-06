package com.gsafety.scheduler.controller;

import com.alibaba.fastjson.JSON;
import com.gsafety.scheduler.common.JobType;
import com.gsafety.scheduler.common.TriggerType;
import com.gsafety.scheduler.job.JobRecord;
import com.gsafety.scheduler.job.RemoteCallJob;
import com.gsafety.scheduler.job.ShellJob;
import com.gsafety.scheduler.model.JobInfo;
import com.gsafety.scheduler.model.JobList;
import com.gsafety.scheduler.model.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: huangll
 * Written on 2018/8/31.
 */
@Slf4j
@RestController
public class JobController {

  @Autowired
  private Scheduler scheduler;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @ApiOperation("添加任务")
  @PostMapping("/job")
  public Result addJob(@RequestBody JobInfo jobInfo) throws SchedulerException {
    JobDetail jobDetail;
    JobBuilder jobBuilder = JobBuilder.newJob(getJobClass(jobInfo.getJobType())).usingJobData("jobInfo", JSON.toJSONString(jobInfo));
    if (jobInfo.getGroup() != null) {
      jobDetail = jobBuilder.withIdentity(jobInfo.getJobName(), jobInfo.getGroup()).storeDurably().withDescription(jobInfo.getJobDesc()).build();
    } else {
      jobDetail = jobBuilder.withIdentity(jobInfo.getJobName()).storeDurably().withDescription(jobInfo.getJobDesc()).build();
    }

    Trigger trigger;
    if (TriggerType.SIMLE.value().equals(jobInfo.getTriggerType())) {
      SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
      if (jobInfo.getCount() != null) {
        simpleScheduleBuilder.withRepeatCount(jobInfo.getCount()).withIntervalInSeconds(jobInfo.getPeriod());
      } else {
        simpleScheduleBuilder.repeatForever().withIntervalInSeconds(jobInfo.getPeriod());
      }
      trigger = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobInfo.getJobName(), jobInfo.getGroup()).withSchedule(simpleScheduleBuilder).build();
    } else {
      if (!CronExpression.isValidExpression(jobInfo.getCron())) {
        return Result.builder().success(false).msg("wrong cron: " + jobInfo.getCron()).build();
      }
      CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(jobInfo.getCron());
      trigger = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobInfo.getJobName(), jobInfo.getGroup()).withSchedule(cronScheduleBuilder).build();
    }

    scheduler.scheduleJob(jobDetail, trigger);
    return Result.builder().success(true).msg("ok").build();
  }

  private Class<? extends QuartzJobBean> getJobClass(Integer jobType) {
    if (JobType.SHELL.value().equals(jobType)) {
      return ShellJob.class;
    } else if (JobType.REMOTE.value().equals(jobType)) {
      return RemoteCallJob.class;
    }
    throw new RuntimeException("unknown job type");
  }

  @ApiOperation("分组查询")
  @GetMapping("/group")
  public Result<List<String>> groups() throws SchedulerException {
    List<String> jobGroupNames = scheduler.getJobGroupNames();
    return Result.<List<String>>builder().success(true).msg("ok").data(jobGroupNames).build();
  }

  @ApiOperation("任务查询")
  @GetMapping("/jobs")
  public Result<List<JobInfo>> jobs(String group) throws SchedulerException {
    Set<JobKey> jobKeys;
    if (StringUtils.isEmpty(group)) {
      jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
    } else {
      jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(group));
    }

    List<JobInfo> jobInfos = jobKeys.stream().map(jobKey -> {
      try {
        return scheduler.getJobDetail(jobKey);
      } catch (SchedulerException e) {
        log.error("quartz error", e);
      }
      return null;
    }).map(jobDetail -> {
          JobInfo jobInfo = JSON.parseObject(jobDetail.getJobDataMap().getString("jobInfo"), JobInfo.class);
          Trigger.TriggerState triggerState = null;
          try {
            triggerState = scheduler.getTriggerState(TriggerKey.triggerKey(jobInfo.getJobName(), jobInfo.getGroup()));
          } catch (SchedulerException e) {
            log.error("", e);
          }
          jobInfo.setTriggerState(getTriggerState(triggerState));
          return jobInfo;
        }
    ).collect(Collectors.toList());

    return Result.<List<JobInfo>>builder().success(true).msg("ok").data(jobInfos).build();
  }

  private Integer getTriggerState(Trigger.TriggerState triggerState) {
    switch (triggerState) {
      case NORMAL:
        return 0;
      case PAUSED:
        return 1;
      case COMPLETE:
        return 2;
      case ERROR:
        return 3;
      case BLOCKED:
        return 4;
      default:
        return -1;
    }
  }

  @ApiOperation("暂停任务")
  @PostMapping("/pause")
  public Result pause(@RequestBody JobList jobList) {
    jobList.getJobList().stream().forEach(job -> {
      try {
        scheduler.pauseJob(JobKey.jobKey(job.getJobName(), job.getGroup()));
      } catch (SchedulerException e) {
        log.error("", e);
      }
    });
    return Result.builder().success(false).msg("ok").build();
  }

  @ApiOperation("激活任务")
  @PostMapping("/activate")
  public Result activate(@RequestBody JobList jobList) {
    jobList.getJobList().stream().forEach(job -> {
      try {
        scheduler.resumeJob(JobKey.jobKey(job.getJobName(), job.getGroup()));
      } catch (SchedulerException e) {
        log.error("", e);
      }
    });
    return Result.builder().success(false).msg("ok").build();
  }

  @ApiOperation("删除任务")
  @PostMapping("/delete")
  public Result delete(@RequestBody JobList jobList) {
    jobList.getJobList().stream().forEach(job -> {
      try {
        scheduler.deleteJob(JobKey.jobKey(job.getJobName(), job.getGroup()));
      } catch (SchedulerException e) {
        log.error("", e);
      }
    });
    return Result.builder().success(false).msg("ok").build();
  }

  @ApiOperation("查看任务详情")
  @GetMapping("/jobView/{job}")
  public Result<JobInfo> jobView(@PathVariable(name = "job") String job) throws SchedulerException {
    String[] words = job.split("_");
    JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(words[1], words[0]));
    String s = jobDetail.getJobDataMap().getString("jobInfo");
    JobInfo jobInfo = JSON.parseObject(s, JobInfo.class);

    List<JobRecord> jobRecords = jdbcTemplate.query("select * from job_record where group_name=? and job_name=?",
        new Object[]{jobInfo.getGroup(), jobInfo.getJobName()}, new RowMapper<JobRecord>() {
      @Override
      public JobRecord mapRow(ResultSet resultSet, int i) throws SQLException {
        JobRecord jobRecord = new JobRecord();
        jobRecord.setId(resultSet.getInt("id"));
        jobRecord.setResult(resultSet.getString("result"));
        jobRecord.setJobType(resultSet.getString("job_type"));
        jobRecord.setRunTime(resultSet.getDate("run_time"));
        return jobRecord;
      }
    });
    jobInfo.setJobRecords(jobRecords);

    return Result.<JobInfo>builder().success(true).msg("ok").data(jobInfo).build();
  }
}
