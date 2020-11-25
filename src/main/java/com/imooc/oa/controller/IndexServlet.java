package com.imooc.oa.controller;

import com.imooc.oa.entity.Department;
import com.imooc.oa.entity.Employee;
import com.imooc.oa.entity.Node;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.DepartmentService;
import com.imooc.oa.service.EmployeeService;
import com.imooc.oa.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IndexServlet",urlPatterns = "/index")
public class IndexServlet extends HttpServlet {
  private UserService userService = new UserService();
  private EmployeeService employeeService = new EmployeeService();
  private DepartmentService departmentService = new DepartmentService();

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute("login_user");
    Employee employee = employeeService.selectById(user.getEmployeeId());
    Department department = departmentService.selectById(employee.getDepartmentId());
    List<Node> nodeList = userService.selectNodeByUserId(user.getUserId());
    request.setAttribute("node_list",nodeList);
    session.setAttribute("current_employee", employee);
    session.setAttribute("current_department", department);
    request.getRequestDispatcher("index.ftl").forward(request,response);
  }
}
