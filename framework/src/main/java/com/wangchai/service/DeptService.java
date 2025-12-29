package com.wangchai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.DeptDto;
import com.wangchai.domain.entity.Dept;
import com.wangchai.domain.vo.PageVo;

public interface DeptService extends IService<Dept> {

    ResponseResult countData();

    ResponseResult DeptPersonCountData();

    ResponseResult getDeptOptions();

    Dept getByName(String deptName);

    ResponseResult<PageVo> pageDeptList(Integer pageNum, Integer pageSize, DeptDto deptDto);

    ResponseResult addDept(Dept dept);

    ResponseResult deleteDept(String deptIds);

    ResponseResult deptDetailInfo(Long deptId);

    ResponseResult updateDept(Dept dept);


}
