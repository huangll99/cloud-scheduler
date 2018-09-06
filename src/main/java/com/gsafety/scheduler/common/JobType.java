package com.gsafety.scheduler.common;

/**
 * Author: huangll
 * Written on 2018/8/31.
 */
public enum JobType {
  SHELL(0), REMOTE(1);

  int value;

  JobType(int v) {
    this.value = v;
  }

  public Integer value() {
    return this.value;
  }
}
