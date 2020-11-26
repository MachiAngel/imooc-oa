package com.imooc.oa.entity;

public class User {
  private Long userId; //user_id  在Mybatis-config已經開啟駝峰命名轉換
  private String username;
  private String password;
  private Long employeeId;
  private Integer salt;
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Long getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Long employeeId) {
    this.employeeId = employeeId;
  }

  public Integer getSalt() {
    return salt;
  }

  public void setSalt(Integer salt) {
    this.salt = salt;
  }
}
