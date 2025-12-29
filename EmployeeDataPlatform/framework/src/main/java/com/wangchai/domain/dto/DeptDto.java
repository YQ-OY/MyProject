package com.wangchai.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptDto {

    // 部门名称
    private String name;
    // 主管编号
    private Long managerEmpId;

}
