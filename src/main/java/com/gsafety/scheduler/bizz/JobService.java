package com.gsafety.scheduler.bizz;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: huangll
 * Written on 2018/8/31.
 */
@Service
public class JobService {

  @Autowired
  private Scheduler scheduler;


}
