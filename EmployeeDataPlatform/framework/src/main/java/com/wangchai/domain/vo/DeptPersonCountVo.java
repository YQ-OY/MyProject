package com.wangchai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptPersonCountVo {
    // 部门名称
    private String name;
    // 部门人数
    private Integer value;
}
