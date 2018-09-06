package com.gsafety.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.gsafety.scheduler.model.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Author: huangll
 * Written on 2018/8/31.
 */
@Slf4j
@DisallowConcurrentExecution
public class RemoteCallJob extends QuartzJobBean {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    log.info("run remote job");
    String s = context.getJobDetail().getJobDataMap().getString("jobInfo");
    JobInfo jobInfo = JSON.parseObject(s, JobInfo.class);

    String result;
    try {
      Map map = restTemplate.postForObject(jobInfo.getUrl(), JSON.parseObject(jobInfo.getBody()), Map.class);
      result = JSON.toJSONString(map);
    } catch (Exception e) {
      log.error("执行远程调用失败", e);
      result = "执行远程调用失败:" + e.toString();
    }

    String insertSql = "insert into job_record(id,group_name,job_name,job_type,result,run_time) values(null,'"
        + jobInfo.getGroup() + "','" + jobInfo.getJobName() + "','shell','" + result + "',NOW())";
    jdbcTemplate.execute(insertSql);
  }
}
