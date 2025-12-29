package com.wangchai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryVo {
    // 薪资编号
    private Long salaryId;
    // 员工姓名
    private String name;
    // 所属部门
    private String deptName;
    //基础工资
    private Double base;
    // 补贴
    private Double subsidy;
    // 罚金
    private Double fine;
    // 总工资
    private Double total;
}
