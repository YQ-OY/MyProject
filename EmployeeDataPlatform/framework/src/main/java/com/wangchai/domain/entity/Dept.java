package com.wangchai.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("DEPT")
public class Dept {
    @TableId(value = "DEPT_ID")
    private Long deptId;
    private String name;
    // 允许为空
    @TableField(
            updateStrategy = FieldStrategy.IGNORED,
            jdbcType = JdbcType.NUMERIC // 关键：适配Oracle的null值更新
    )
    private Long managerEmpId;
    private String description;
}
