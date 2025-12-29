package com.wangchai.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpEditDto {

    private Long empId;
    private Long managerId;
    private String photo;
    private String name;
    private String gender;
    private String deptName;
    private String job;
    private Date birthday;
    private Date entryday;

}
