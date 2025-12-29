package com.wangchai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.EmpDto;
import com.wangchai.domain.dto.EmpEditDto;
import com.wangchai.domain.entity.Emp;
import com.wangchai.domain.vo.PageVo;

import java.util.List;

public interface EmpService extends IService<Emp> {

    ResponseResult countData();

    ResponseResult<PageVo> pageEmpList(Integer pageNum, Integer pageSize, EmpDto empDto);

    ResponseResult addEmp(EmpEditDto empEditDto);

    ResponseResult deleteEmp(String empIds);

    ResponseResult empDetailInfo(Long empId);

    ResponseResult updateEmp(EmpEditDto empEditDto);
    // 校验员工编号是否存在
    boolean checkEmpIdUnique(Long empId);

    // 校验主管是否存在
    boolean checkManagerIdExist(Long managerId);

    ResponseResult EmployeeAgeDistData();
}
