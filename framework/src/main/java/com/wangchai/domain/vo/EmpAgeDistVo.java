package com.wangchai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpAgeDistVo {
    // 年龄区间
    private String ageRange;
    // 男性员工数
    private String male;
    // 女性员工数
    private String female;
    // 总员工数
    private Long total;
}
