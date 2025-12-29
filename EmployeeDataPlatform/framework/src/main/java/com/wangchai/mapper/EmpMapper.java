package com.wangchai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangchai.domain.dto.EmpDto;
import com.wangchai.domain.entity.Emp;
import com.wangchai.domain.vo.EmpAgeDistVo;
import com.wangchai.domain.vo.EmpVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmpMapper extends BaseMapper<Emp> {

    Long countData();

    Page<EmpVo> selectEmpVoPage(Page<EmpVo> page, @Param("dto") EmpDto empDto);

     List<EmpAgeDistVo> EmployeeAgeDistData();

}
