package com.wangchai.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpVo {

    private Long empId;

    private String photo;
    private String name;
    private String gender;

    private String deptName;

    private String job;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date entryday;

    private Long managerId;
    private String managerName;

}
