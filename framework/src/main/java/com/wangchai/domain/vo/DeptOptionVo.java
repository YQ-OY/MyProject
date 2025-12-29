package com.wangchai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptOptionVo {

    // 下拉选项需要 label（显示文本），这里都用部门名称
    private String label;
    // value（绑定值）
    private String value;

}
