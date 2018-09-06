package com.gsafety.scheduler.job;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Author: huangll
 * Written on 2018/9/4.
 */
@Getter
@Setter
public class JobRecord {

  private Integer id;

  private String groupName;

  private String jobName;

  private String jobType;

  private String result;

  private Date runTime;
}
