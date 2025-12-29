package com.wangchai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryGapVo {
    // 所有部门的平均薪资
    private Double averageSalary;
    // 部门平均薪资 - averageSalary
    private Double value;
    // 部门名称
    private String deptName;
    // 部门薪资总和
    private Double totalSalary;
    // 部门基础薪资总和
    private Double totalBase;
    // 部门补贴薪资总和
    private Double totalBonus;
    // 部门罚款薪资总和
    private Double totalFine;

}
