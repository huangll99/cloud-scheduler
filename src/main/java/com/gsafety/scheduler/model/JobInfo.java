package com.gsafety.scheduler.model;

import com.gsafety.scheduler.job.JobRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Author: huangll
 * Written on 2018/8/31.
 */
@Setter
@Getter
public class JobInfo {

  private String jobName;

  private String jobDesc;

  private String group;

  private Boolean concurrentAllowed;

  private Integer triggerType; //0:简单任务 1:cron任务

  private String cron;

  private Integer period; //执行周期，单位：秒

  private Integer count; //执行次数

  private String script;

  private Integer jobType; //0:shell脚本调用 1：远程接口调用

  private Integer triggerState;

  private String url;

  private String body;

  private List<JobRecord> jobRecords;
}
