package com.gsafety.scheduler.controller;

import com.alibaba.fastjson.JSON;
import com.gsafety.scheduler.model.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Author: huangll
 * Written on 2018/9/4.
 */
@RestController
public class EchoController {

  @PostMapping("/echo")
  public Result echo(@RequestBody Map map) {

    System.out.println(JSON.toJSONString(map));

    return Result.builder().success(true).msg("ok").data("成功了").build();
  }

}
