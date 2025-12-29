package com.wangchai.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryDto {
    // 薪资编号
    private Long salaryId;
    // 员工姓名
    private String name;
    // 部门名称
    private List<String> deptName;
}
