package com.imooc.oa.dao;

import com.imooc.oa.entity.Employee;
import org.apache.ibatis.annotations.Param;

public interface EmployeeDao {
  public Employee selectById(Long employee);

  /**
   * 根據傳入的員工獲取上級主管對象
   * @param employee 員工對象
   * @return 上級主管對象
   */
  public Employee selectLeader(@Param("emp") Employee employee);
}
