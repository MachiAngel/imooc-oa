package com.imooc.oa.service;

import com.imooc.oa.dao.DepartmentDao;
import com.imooc.oa.entity.Department;
import com.imooc.oa.utils.MybatisUtils;

public class DepartmentService {
    /**
     * 按編號得到部門對象
     * @param departmentId 部門編號
     * @return 部門對象, null 代表部門不存在
     */
    public Department selectById(Long departmentId) {
       return  (Department)MybatisUtils.executeQuery(sqlSession ->
               sqlSession.getMapper(DepartmentDao.class).selectById(departmentId)
       );
    }
}
