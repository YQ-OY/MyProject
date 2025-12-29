package com.wangchai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpCountVo {

    private Long employeeCount; // 员工数量
    private Double employeeRate; // 员工增长率

}
