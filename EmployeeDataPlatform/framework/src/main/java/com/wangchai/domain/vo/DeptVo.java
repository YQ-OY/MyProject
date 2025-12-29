package com.wangchai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptVo {
    private Long deptId;
    // 部门名称
    private String name;
    private Long managerEmpId;
    // 部门经理姓名
    private String managerName;
    // 部门下的员工数量
    private Integer count;
    private String description;

}
