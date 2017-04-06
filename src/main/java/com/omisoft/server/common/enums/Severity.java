package com.omisoft.server.common.enums;

/**
 * Error levels Created by nslavov on 3/28/16.
 */
public enum Severity {
  DEBUG(0), INFO(1), WARNING(2), ERROR(3), FATAL(4);

  private final int level;

  Severity(int level) {
    this.level = level;
  }

  public int value() {
    return level;
  }
}
