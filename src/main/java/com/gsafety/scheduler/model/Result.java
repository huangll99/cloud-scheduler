package com.gsafety.scheduler.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Author: huangll
 * Written on 18/3/12.
 */
@Builder
@Getter
@Setter
public class Result<T> {

  @ApiModelProperty("是否成功（true:成功，false:失败）")
  private boolean success;

  @ApiModelProperty("提示消息")
  private String msg;

  private T data;

}
