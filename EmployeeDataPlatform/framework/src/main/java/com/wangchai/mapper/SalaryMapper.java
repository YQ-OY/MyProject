package com.wangchai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangchai.domain.dto.EmpDto;
import com.wangchai.domain.dto.SalaryDto;
import com.wangchai.domain.entity.Salary;
import com.wangchai.domain.vo.EmpVo;
import com.wangchai.domain.vo.SalaryCountVo;
import com.wangchai.domain.vo.SalaryGapVo;
import com.wangchai.domain.vo.SalaryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SalaryMapper extends BaseMapper<Salary> {
     SalaryCountVo sumBaseAndFine();

     Page<SalaryVo> selectSalaryVoPage(Page<SalaryVo> page, @Param("dto") SalaryDto salaryDto);

     List<SalaryGapVo> SalaryDeptGapData();


}
