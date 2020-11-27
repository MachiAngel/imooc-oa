package com.imooc.oa.controller;

import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet(name = "LeaveFormServlet",urlPatterns = "/leave/*")
public class LeaveFormServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=utf-8");
    String uri = request.getRequestURI();
    String methodName = uri.substring(uri.lastIndexOf("/") + 1);
    if (methodName.equals("create")) {
      this.create(request,response);
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    this.doPost(request,response);
  }

  private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute("login_user");
    String strFormType = request.getParameter("formType");
    String strStartTime = request.getParameter("startTime");
    String strEndTime = request.getParameter("endTime");
    String reason = request.getParameter("resson");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
    
    LeaveForm form = new LeaveForm();

  }
}
