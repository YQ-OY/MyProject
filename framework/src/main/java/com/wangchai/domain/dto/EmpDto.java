package com.wangchai.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpDto {

    // 员工姓名
    private String name;
    // 主管编号
    private Long managerId;
    // 部门名称
    private List<String> deptName;

}
