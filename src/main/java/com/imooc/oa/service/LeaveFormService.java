package com.imooc.oa.service;

import com.imooc.oa.dao.EmployeeDao;
import com.imooc.oa.dao.LeaveFormDao;
import com.imooc.oa.dao.NoticeDao;
import com.imooc.oa.dao.ProcessFlowDao;
import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.Notice;
import com.imooc.oa.entity.ProcessFlow;
import com.imooc.oa.service.exception.BussinessException;
import com.imooc.oa.utils.MybatisUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        LeaveForm savedForm = (LeaveForm) MybatisUtils.executeUpdate(sqlSession -> {
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH時");
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
                // 請假單已提交消息
                String noticeContent = String.format(
                        "您的請假申請[%s-%s]已提交,請等待上級審批.",
                        sdf.format(form.getStartTime()),
                        sdf.format(form.getEndTime())
                );
                // 通知部門經理審批消息
                NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);
                noticeDao.insert(new Notice(employee.getEmployeeId(), noticeContent));
                noticeContent = String.format(
                        "%s-%s提起請假申請[%s-%s],請儘快審批",
                        employee.getTitle(),
                        employee.getName(),
                        sdf.format(form.getStartTime()),
                        sdf.format(form.getEndTime())
                );
                noticeDao.insert(new Notice(dmanager.getEmployeeId(), noticeContent));

            } else if (employee.getLevel() == 7) {
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

                // 請假單已提交消息
                String noticeContent = String.format(
                        "您的請假申請[%s-%s]已提交,請等待上級審批.",
                        sdf.format(form.getStartTime()),
                        sdf.format(form.getEndTime())
                );
                // 通知部門經理審批消息
                NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);
                noticeDao.insert(new Notice(employee.getEmployeeId(), noticeContent));
                noticeContent = String.format(
                        "%s-%s提起請假申請[%s-%s],請儘快審批",
                        sdf.format(employee.getTitle()),
                        sdf.format(employee.getName()),
                        sdf.format(form.getStartTime()),
                        sdf.format(form.getEndTime())
                );
                noticeDao.insert(new Notice(manager.getEmployeeId(), noticeContent));
            } else if (employee.getLevel() == 8) {
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

                String noticeContent = String.format(
                        "您的請假申請[%s-%s]系統已自動批准通過",
                        sdf.format(form.getStartTime()),
                        sdf.format(form.getEndTime())
                );
                // 通知部門經理審批消息
                NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);
                noticeDao.insert(new Notice(employee.getEmployeeId(), noticeContent));
            }

            return form;
        });
        return savedForm;
    }

    /**
     * 獲取指定任務狀態及指定經辦人對應的請假單列表
     *
     * @param pfState
     * @param operatorId
     * @return
     */
    public List<Map> getLeaveFormList(String pfState, Long operatorId) {
        return (List<Map>) MybatisUtils.executeQuery(sqlSession -> {
            LeaveFormDao dao = sqlSession.getMapper(LeaveFormDao.class);
            List<Map> formList = dao.selectByParams(pfState, operatorId);
            return formList;
        });
    }

    public void audit(Long formId, Long operatorId, String result, String reason) {
        MybatisUtils.executeUpdate(sqlSession -> {
            ProcessFlowDao processFlowDao = sqlSession.getMapper(ProcessFlowDao.class);
            //1. 無論同意 還是駁回 , 當前狀態都是complete
            List<ProcessFlow> flowList = processFlowDao.selectByFormId(formId);
            if (flowList.size() == 0) {
                throw new BussinessException("PF001", "無效的審批流程");
            }
            //獲取當前任務ProcessFlow對象
            List<ProcessFlow> processList = flowList.stream()
                    .filter(p -> p.getOperatorId() == operatorId && p.getState().equals("process"))
                    .collect(Collectors.toList());
            ProcessFlow process = null;
            if (processList.size() == 0) {
                throw new BussinessException("PF002", "未找到待處理任務");
            } else {
                process = processList.get(0);
                process.setState("complete");
                process.setResult(result);
                process.setReason(reason);
                process.setAuditTime(new Date());
                processFlowDao.update(process);
            }
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            LeaveForm leaveForm = leaveFormDao.selectById(formId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH時");
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
            Employee employee = employeeDao.selectById(leaveForm.getEmployeeId()); //表單提交人信息
            Employee operator = employeeDao.selectById(operatorId); //任務經辦人信息
            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);

            //2. 如果當前任務是最後一個節點,代表流程結束,更新請假單狀態為對應的approved/refused
            if (process.getIsLast() == 1) {
                leaveForm.setState(result);
                leaveFormDao.update(leaveForm);
                String strResult = null;
                if (result.equals("approved")) {
                    strResult = "批准";
                } else if (result.equals("refused")) {
                    strResult = "駁回";
                }
                String noticeContent = String.format("您的請假申請[%s-%s]%s%s已%s,審批意見:%s",
                        sdf.format(leaveForm.getStartTime()),
                        sdf.format(leaveForm.getEndTime()),
                        operator.getTitle(),
                        operator.getName(),
                        strResult,
                        reason
                ); //發給表單提交人的通知
                noticeDao.insert(new Notice(leaveForm.getEmployeeId(),noticeContent));
                noticeContent = String.format("%s-%s提起請假申請[%s-%s]您已%s,審批意見:%s",
                        employee.getTitle(),
                        employee.getName(),
                        sdf.format(leaveForm.getStartTime()),
                        sdf.format(leaveForm.getEndTime()),
                        strResult,
                        reason
                ); //發給審批人的通知
                noticeDao.insert(new Notice(employee.getEmployeeId(),noticeContent));
            } else {
                //3. 如果當前任務不是最後一個節點且審批通過, 那下一個節點的狀態從ready 變成 process
                //4. 如果當前任務不是最後一個節點且審批駁回, 則後續所有任務狀態變成cancel,請假單狀態變成refused
                List<ProcessFlow> readyList = flowList.stream().filter(p -> p.getState().equals("ready")).collect(Collectors.toList());
                if (result.equals("approved")) {
                    ProcessFlow readyProcess = readyList.get(0);
                    readyProcess.setState("process");
                    processFlowDao.update(readyProcess);
                    //消息1: 通知表单提交人,部门经理已经审批通过,交由上级继续审批
                    String noticeContent1 = String.format("您的请假申请[%s-%s]%s%s已批准,审批意见:%s ,请继续等待上级审批" ,
                            sdf.format(leaveForm.getStartTime()) , sdf.format(leaveForm.getEndTime()),
                            operator.getTitle() , operator.getName(),reason);
                    noticeDao.insert(new Notice(leaveForm.getEmployeeId(),noticeContent1));

                    //消息2: 通知总经理有新的审批任务
                    String noticeContent2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批" ,
                            employee.getTitle() , employee.getName() , sdf.format( leaveForm.getStartTime()) , sdf.format(leaveForm.getEndTime()));
                    noticeDao.insert(new Notice(readyProcess.getOperatorId(),noticeContent2));

                    //消息3: 通知部门经理(当前经办人),员工的申请单你已批准,交由上级继续审批
                    String noticeContent3 = String.format("%s-%s提起请假申请[%s-%s]您已批准,审批意见:%s,申请转至上级领导继续审批" ,
                            employee.getTitle() , employee.getName() , sdf.format( leaveForm.getStartTime()) , sdf.format(leaveForm.getEndTime()), reason);
                    noticeDao.insert(new Notice(operator.getEmployeeId(),noticeContent3));
                } else if (result.equals("refused")) {
                    for (ProcessFlow p : readyList) {
                        p.setState("cancel");
                        processFlowDao.update(p);
                    }
                    leaveForm.setState("refused");
                    leaveFormDao.update(leaveForm);
                    //消息1: 通知申请人表单已被驳回
                    String noticeContent1 = String.format("您的请假申请[%s-%s]%s%s已驳回,审批意见:%s,审批流程已结束" ,
                            sdf.format(leaveForm.getStartTime()) , sdf.format(leaveForm.getEndTime()),
                            operator.getTitle() , operator.getName(),reason);
                    noticeDao.insert(new Notice(leaveForm.getEmployeeId(),noticeContent1));

                    //消息2: 通知经办人表单"您已驳回"
                    String noticeContent2 = String.format("%s-%s提起请假申请[%s-%s]您已驳回,审批意见:%s,审批流程已结束" ,
                            employee.getTitle() , employee.getName() , sdf.format( leaveForm.getStartTime()) , sdf.format(leaveForm.getEndTime()), reason);
                    noticeDao.insert(new Notice(operator.getEmployeeId(),noticeContent2));
                }
            }
            return null;
        });
    }
}
