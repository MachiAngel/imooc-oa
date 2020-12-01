package com.imooc.oa.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.oa.entity.*;
import com.imooc.oa.service.DepartmentService;
import com.imooc.oa.service.EmployeeService;
import com.imooc.oa.service.NoticeService;
import com.imooc.oa.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NoticeServlet",urlPatterns = "/notice/list")
public class NoticeServlet extends HttpServlet {

  private NoticeService noticeService = new NoticeService();

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    //得到當前用戶的登入狀態
    User user = (User) session.getAttribute("login_user");
    List<Notice> noticeList = noticeService.getNoticeList(user.getEmployeeId());

    Map result = new HashMap();
    result.put("code", "0");
    result.put("msg","");
    result.put("count", noticeList.size());
    result.put("data", noticeList);
    String json = JSON.toJSONString(result);
    response.setContentType("text/html;charset=utf-8");
    response.getWriter().println(json);

  }
}
