package com.imooc.oa.service.exception;

public class BussinessException extends RuntimeException{
  private String code; //異常編碼,異常的以為標示
  private String message; //異常具體文本消息

  public BussinessException(String code, String message) {
    super(code + ":" + message);
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
