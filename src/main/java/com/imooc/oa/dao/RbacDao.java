package com.imooc.oa.dao;

import com.imooc.oa.utils.MybatisUtils;

import java.util.List;

public class RbacDao {
  public List selectNodeByUserId(Long userId) {
    return  (List)MybatisUtils.executeQuery(sqlSession -> sqlSession.selectList("rbacmapper.selectNodeByUserId", userId));
  }
}
