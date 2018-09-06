package com.gsafety.scheduler.common;

/**
 * Author: huangll
 * Written on 2018/8/31.
 */
public enum TriggerType {
  SIMLE(0), CRON(1);

  int value;

  TriggerType(int v) {
    this.value = v;
  }

  public Integer value() {
    return this.value;
  }
}
