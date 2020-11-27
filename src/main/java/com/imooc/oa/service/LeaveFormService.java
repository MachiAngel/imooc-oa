package com.imooc.oa.service;

import com.imooc.oa.dao.EmployeeDao;
import com.imooc.oa.dao.LeaveFormDao;
import com.imooc.oa.dao.ProcessFlowDao;
import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.ProcessFlow;
import com.imooc.oa.utils.MybatisUtils;

import java.util.Date;

/**
 * 請假單流程
 */
public class LeaveFormService {
  /**
   * 創見請假單
   *
   * @param form 前端輸入的請假單數據
   * @return 持久化後的請假單
   */
  public LeaveForm createLeaveForm(LeaveForm form) {
    //1.持久化form表單數據,8級以下員工表單狀態為 process,8級(總經理)狀態為 approved
    //2.增加第一條流程數據,說明表單已提交,狀態為complete
    //3.分情況建立其流程數據
    //3.1 7級以下員工,生成部門經理審批任務,請假時間大於36小時,還需生成總經理審批任務
    //3.2 7級員工,生成總經理審批任務
    //3.3 8級員工,生成總經理審批任務,系統自動通過

    LeaveForm savedForm = (LeaveForm)MybatisUtils.executeUpdate(sqlSession -> {
      //1.持久化form表單數據,8級以下員工表單狀態為processing,8級(總經理)狀態為 approved
      EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
      Employee employee = employeeDao.selectById(form.getEmployeeId());
      if (employee.getLevel() == 8) {
        form.setState("approved");
      } else {
        form.setState("processing");
      }
      LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
      leaveFormDao.insert(form);
      //2.增加第一條流程數據,說明表單已提交,狀態為complete
      ProcessFlowDao processFlowDao = sqlSession.getMapper(ProcessFlowDao.class);
      ProcessFlow flow1 = new ProcessFlow();
      flow1.setFormId(form.getFormId());
      flow1.setOperatorId(form.getEmployeeId());
      flow1.setAction("apply");
      flow1.setCreateTime(new Date());
      flow1.setOrderNo(1);
      flow1.setState("complete");
      flow1.setIsLast(0);
      processFlowDao.insert(flow1);
      //3.分情況建立其流程數據
      //3.1 7級以下員工,生成部門經理審批任務,請假時間大於36小時,還需生成總經理審批任務
      if (employee.getLevel() < 7) {
        Employee dmanager = employeeDao.selectLeader(employee);
        ProcessFlow flow2 = new ProcessFlow();
        flow2.setFormId(form.getFormId());
        flow2.setOperatorId(dmanager.getEmployeeId());
        flow2.setAction("audit");
        flow2.setCreateTime(new Date());
        flow2.setOrderNo(2);
        flow2.setState("process");
        long diff = form.getEndTime().getTime() - form.getStartTime().getTime();
        float hours = diff / (1000 * 60 * 60) * 1f;
        if (hours >= BussinessConstants.MANAGER_AUDIT_HOURS) {
          flow2.setIsLast(0);
          processFlowDao.insert(flow2);
          //總經理
          Employee manager = employeeDao.selectLeader(dmanager);
          ProcessFlow flow3 = new ProcessFlow();
          flow3.setFormId(form.getFormId());
          flow3.setOperatorId(manager.getEmployeeId());
          flow3.setAction("audit");
          flow3.setCreateTime(new Date());
          flow3.setOrderNo(3);
          flow3.setState("ready");
          flow3.setIsLast(1);
          processFlowDao.insert(flow3);
        } else {
          flow2.setIsLast(1);
          processFlowDao.insert(flow2);
        }

      }else if(employee.getLevel() == 7){
        //3.2 7級員工,生成總經理審批任務
        Employee manager = employeeDao.selectLeader(employee);
        ProcessFlow flow2 = new ProcessFlow();
        flow2.setFormId(form.getFormId());
        flow2.setOperatorId(manager.getEmployeeId());
        flow2.setAction("audit");
        flow2.setCreateTime(new Date());
        flow2.setOrderNo(2);
        flow2.setState("process");
        flow2.setIsLast(1);
        processFlowDao.insert(flow2);
      }else if(employee.getLevel() == 8){
        //3.2 8級員工,生成總經理審批任務,系統自動通過
        ProcessFlow flow = new ProcessFlow();
        flow.setFormId(form.getFormId());
        flow.setOperatorId(employee.getEmployeeId());
        flow.setAction("audit");
        flow.setResult("approved");
        flow.setReason("自動通過");
        flow.setCreateTime(new Date());
        flow.setAuditTime(new Date());
        flow.setOrderNo(2);
        flow.setState("complete");
        flow.setIsLast(1);
        processFlowDao.insert(flow);
      }

      return form;
    });
    return savedForm;
  }
}
