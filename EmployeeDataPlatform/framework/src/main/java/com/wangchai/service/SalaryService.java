package com.wangchai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.SalaryDto;
import com.wangchai.domain.entity.Salary;
import com.wangchai.domain.vo.PageVo;
import com.wangchai.domain.vo.SalaryCountVo;

public interface SalaryService extends IService<Salary> {

    ResponseResult countData();

    ResponseResult SalaryDeptGapData();

    ResponseResult<PageVo> pageSalaryList(Integer pageNum, Integer pageSize, SalaryDto salaryDto);

    ResponseResult addSalary(Salary salary);

    ResponseResult deleteSalary(String salaryIds);

    ResponseResult salaryDetailInfo(Long salaryId);

    ResponseResult updateSalary(Salary salary);

}
