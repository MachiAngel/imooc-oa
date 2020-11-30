package com.imooc.oa.service;

import com.imooc.oa.entity.LeaveForm;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.junit.Assert.*;

public class LeaveFormServiceTest {
  LeaveFormService leaveFormService = new LeaveFormService();

  /**
   * 市场部员工请假单(72小时以上)测试用例
   * @throws ParseException
   */
  @Test
  public void createLeaveForm1() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
    LeaveForm form = new LeaveForm();
    form.setEmployeeId(8l);
    form.setStartTime(sdf.parse("2020032608"));
    form.setEndTime(sdf.parse("2020040118"));
    form.setFormType(1);
    form.setReason("市场部员工请假单(72小时以上)");
    form.setCreateTime(new Date());
    LeaveForm savedForm = leaveFormService.createLeaveForm(form);
    System.out.println(savedForm.getFormId());
  }

  /**
   * 市场部员工请假单(72小时内)测试用例
   * @throws ParseException
   */
  @Test
  public void createLeaveForm2() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
    LeaveForm form = new LeaveForm();
    form.setEmployeeId(8l);
    form.setStartTime(sdf.parse("2020032608"));
    form.setEndTime(sdf.parse("2020032718"));
    form.setFormType(1);
    form.setReason("市场部员工请假单(72小时内)");
    form.setCreateTime(new Date());
    LeaveForm savedForm = leaveFormService.createLeaveForm(form);
    System.out.println(savedForm.getFormId());
  }

  /**
   * 研发部部门经理请假单测试用例
   * @throws ParseException
   */
  @Test
  public void createLeaveForm3() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
    LeaveForm form = new LeaveForm();
    form.setEmployeeId(2l);
    form.setStartTime(sdf.parse("2020032608"));
    form.setEndTime(sdf.parse("2020040118"));
    form.setFormType(1);
    form.setReason("研发部部门经理请假单");
    form.setCreateTime(new Date());
    LeaveForm savedForm = leaveFormService.createLeaveForm(form);
    System.out.println(savedForm.getFormId());
  }

  /**
   * 总经理请假单测试用例
   * @throws ParseException
   */
  @Test
  public void createLeaveForm4() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
    LeaveForm form = new LeaveForm();
    form.setEmployeeId(1l);
    form.setStartTime(sdf.parse("2020032608"));
    form.setEndTime(sdf.parse("2020040118"));
    form.setFormType(1);
    form.setReason("总经理请假单");
    form.setCreateTime(new Date());
    LeaveForm savedForm = leaveFormService.createLeaveForm(form);
    System.out.println(savedForm.getFormId());
  }

  @Test
  public void audit1() {
    leaveFormService.audit(31l,2l,"approved","祝早日康復");
  }

  @Test
  public void audit2() {
    leaveFormService.audit(32l, 2l, "refused", "工期緊張，請勿拖延");
  }

  /**
   * 部門經理請假, 總經理審批通過
   */
  @Test
  public void audit3() {
    leaveFormService.audit(33l, 1l, "approved", "同意");
  }

}
