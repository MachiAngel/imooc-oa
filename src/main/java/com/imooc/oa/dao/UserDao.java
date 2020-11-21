package com.imooc.oa.dao;

import com.imooc.oa.entity.User;
import com.imooc.oa.utils.MybatisUtils;

public class UserDao {
  /**
   * 按用戶名查詢用戶
   * @param username 用戶名
   * @return User對象包含對應的用戶信息,null則代表對象不存在
   */
  public User selectByUsername(String username){
    User user = (User)MybatisUtils.executeQuery(sqlSession -> sqlSession.selectOne("usermapper.selectByUsername", username));
    return  user;
  }
}
