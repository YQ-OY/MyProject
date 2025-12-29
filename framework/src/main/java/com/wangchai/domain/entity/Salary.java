package com.wangchai.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("SALARY")
public class Salary {
    @TableId(value = "SALARY_ID")
    private Long salaryId;
    private Long empId;
    private Double base;
    private Double subsidy;
    private Double fine;
    private Double total;
}
