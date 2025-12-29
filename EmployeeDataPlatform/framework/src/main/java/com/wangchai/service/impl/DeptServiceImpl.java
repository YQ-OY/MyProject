package com.wangchai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.DeptDto;
import com.wangchai.domain.entity.Dept;
import com.wangchai.domain.entity.Emp;
import com.wangchai.domain.entity.Salary;
import com.wangchai.domain.vo.*;
import com.wangchai.enums.AppHttpCodeEnum;
import com.wangchai.mapper.DeptMapper;
import com.wangchai.mapper.EmpMapper;
import com.wangchai.service.DeptService;
import com.wangchai.service.EmpService;
import com.wangchai.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Autowired
    private EmpMapper empMapper;

    // 查询部门统计数据
    @Override
    public ResponseResult countData() {
        DeptCountVo deptCountVo = new DeptCountVo(count());

        return ResponseResult.okResult(deptCountVo);
    }

    // 查询部门人数——饼图
    @Override
    public ResponseResult DeptPersonCountData() {
        List<DeptPersonCountVo> deptPersonCountVos = getBaseMapper().DeptPersonCountData();

        return ResponseResult.okResult(deptPersonCountVos);
    }

    // 查询部门选项——员工管理下拉列表
    @Override
    public ResponseResult getDeptOptions() {

        List<DeptOptionVo> deptOptions = getBaseMapper().getDeptOptions();

        return ResponseResult.okResult(deptOptions);
    }

    // 根据部门名称查询部门实体
    @Override
    public Dept getByName(String deptName) {

        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dept::getName, deptName);

        return getOne(queryWrapper);
    }

    // 分页查询部门列表
    @Override
    public ResponseResult<PageVo> pageDeptList(Integer pageNum, Integer pageSize, DeptDto deptDto) {
        Page<DeptVo> page = new Page<>(pageNum, pageSize);

        Page<DeptVo> deptVoPage = getBaseMapper().selectDeptVoPage(page, deptDto);

        PageVo pageVo = new PageVo(deptVoPage.getRecords(), deptVoPage.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    // 添加部门
    @Override
    public ResponseResult addDept(Dept dept) {

        // 校验部门编号是否存在
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dept::getDeptId, dept.getDeptId());
        if (count(queryWrapper) > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DEPT_ID_EXIST);
        }

        // 校验前端是否传递了主管ID
        if (dept.getManagerEmpId() != null) {
            // 校验主管是否存在
            if (empMapper.selectById(dept.getManagerEmpId()) == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_EXIST);
            }
            // 仅当主管ID有效时，才更新员工信息
            updateEmp(dept.getManagerEmpId(), dept.getDeptId(), "部长");
        }

        // 保存到 dept 表
        save(dept);

        return ResponseResult.okResult();
    }

    // 删除部门
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务注解：异常时回滚
    public ResponseResult deleteDept(String deptIds) {
        List<Long> idList = Arrays.stream(deptIds.split(","))
                .map(String::trim)
                .filter(idStr -> !idStr.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 查询部门下是否有员工
        LambdaQueryWrapper<Emp> empQueryWrapper = new LambdaQueryWrapper<>();
        empQueryWrapper.in(Emp::getDeptId, idList);
        if (empMapper.selectCount(empQueryWrapper) > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DEPT_HAS_EMP);
        }

        // 删除部门表记录
        removeByIds(idList);

        return ResponseResult.okResult();
    }

    // 获取 部门信息
    @Override
    public ResponseResult deptDetailInfo(Long deptId) {
        Dept dept = getById(deptId);
        if (dept == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DEPT_NOT_EXIST);
        }

        // 转换为 DeptVo 对象
        DeptVo deptVo = BeanCopyUtils.copyBean(dept, DeptVo.class);

        return ResponseResult.okResult(deptVo);
    }

    // 修改部门
    @Override
    public ResponseResult updateDept(Dept dept) {

        // 旧主管
        Long managerEmpId = getById(dept.getDeptId()).getManagerEmpId();
        // 查看原来部门是否有主管
        if(managerEmpId != null){
            // 原来部门有主管，直接设置为部员
            updateEmp(managerEmpId, dept.getDeptId(), "部员");
        }

        // 校验前端是否传递了主管ID
        if (dept.getManagerEmpId() != null) {
            // 校验主管是否存在
            if (empMapper.selectById(dept.getManagerEmpId()) == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_EXIST);
            }
            // 仅当主管ID有效时，才更新员工信息
            updateEmp(dept.getManagerEmpId(), dept.getDeptId(), "部长");
        }

        // 更新部门信息
        updateById(dept);
        return ResponseResult.okResult();
    }

    // 更新 员工 部门 及 职位
    private void updateEmp( Long managerEmpId, Long deptId, String job) {
        Emp updateEmp = new Emp();
        // 设置 部门ID 和 职位
        updateEmp.setEmpId(managerEmpId); // 设置主键
        updateEmp.setDeptId(deptId);
        updateEmp.setJob(job);
        empMapper.updateById(updateEmp); // 按主键更新
    }


}
