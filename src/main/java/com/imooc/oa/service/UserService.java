package com.imooc.oa.service;

import com.imooc.oa.dao.RbacDao;
import com.imooc.oa.dao.UserDao;
import com.imooc.oa.entity.Node;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.exception.BussinessException;
import com.imooc.oa.utils.MD5Utils;

import java.util.List;


public class UserService {
  private UserDao userDao = new UserDao();
  private RbacDao rbacDao = new RbacDao();


  /**
   * 根據前台輸入進行登入效驗
   *
   * @param username 前台輸入的用戶名
   * @param password 前台輸入的密碼
   * @return
   */
  public User checkLogin(String username, String password) {
    User user = userDao.selectByUsername(username);
    if (user == null) {
      throw new BussinessException("L001", "用戶名不存在");
    }
    String md5 = MD5Utils.md5Digest(password, user.getSalt());
    if (!md5.equals(user.getPassword())) {
      System.out.println("有來有來有來有來有來有來有來有來有來有來有來有來有來");
      throw new BussinessException("L002", "密碼錯誤");
    }
    return user;
  }

  public List<Node> selectNodeByUserId(Long userId) {
    List<Node> nodeList = rbacDao.selectNodeByUserId(userId);
    return nodeList;
  }

}
