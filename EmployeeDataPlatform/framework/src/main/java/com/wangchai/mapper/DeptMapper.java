package com.wangchai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangchai.domain.dto.DeptDto;
import com.wangchai.domain.dto.EmpDto;
import com.wangchai.domain.entity.Dept;
import com.wangchai.domain.vo.DeptOptionVo;
import com.wangchai.domain.vo.DeptPersonCountVo;
import com.wangchai.domain.vo.DeptVo;
import com.wangchai.domain.vo.EmpVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeptMapper extends BaseMapper<Dept> {


    List<DeptOptionVo> getDeptOptions();

    Page<DeptVo> selectDeptVoPage(Page<DeptVo> page, @Param("dto") DeptDto deptDto);

     // 查询部门人数——饼图
    List<DeptPersonCountVo> DeptPersonCountData();


}
