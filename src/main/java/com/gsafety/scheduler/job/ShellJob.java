package com.gsafety.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.gsafety.scheduler.model.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: huangll
 * Written on 2018/8/31.
 */
@Slf4j
@DisallowConcurrentExecution
public class ShellJob extends QuartzJobBean {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    String s = context.getJobDetail().getJobDataMap().getString("jobInfo");
    JobInfo jobInfo = JSON.parseObject(s, JobInfo.class);
    log.info(jobInfo.getScript());

    //run shell script
    try {
      Process process = Runtime.getRuntime().exec("sh " + jobInfo.getScript());
      InputStream in = process.getInputStream();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      IOUtils.copy(in, out);
      String result = out.toString();

      String insertSql = "insert into job_record(id,group_name,job_name,job_type,result,run_time) values(null,'"
          + jobInfo.getGroup() + "','" + jobInfo.getJobName() + "','shell','" + result + "',NOW())";
      jdbcTemplate.execute(insertSql);
    } catch (IOException e) {
      log.error("执行脚本错误，脚本：" + jobInfo.getScript());
    }
  }
}
