package com.wangchai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.SalaryDto;
import com.wangchai.domain.entity.Emp;
import com.wangchai.domain.entity.Salary;
import com.wangchai.domain.vo.*;
import com.wangchai.enums.AppHttpCodeEnum;
import com.wangchai.mapper.SalaryMapper;
import com.wangchai.service.EmpService;
import com.wangchai.service.SalaryService;
import com.wangchai.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements SalaryService {

    @Autowired
    private EmpService empService;

    @Override
    public ResponseResult countData() {

        SalaryCountVo salaryCountVo = new SalaryCountVo(getBaseMapper().sumBaseAndFine().getTotalBase(), getBaseMapper().sumBaseAndFine().getTotalFine());

        return ResponseResult.okResult(salaryCountVo);
    }

    @Override
    public ResponseResult SalaryDeptGapData() {
//        // 所有部门的平均薪资
//        private Double averageSalary;
//        // 部门平均薪资 - averageSalary
//        private Double value;
//        // 部门名称
//        private String deptName;
//        // 部门薪资总和
//        private Double totalSalary;
//        // 部门基础薪资总和
//        private Double totalBase;
//        // 部门补贴薪资总和
//        private Double totalBonus;
//        // 部门罚款薪资总和
//        private Double totalFine;

        List<SalaryGapVo> salaryGapVos = getBaseMapper().SalaryDeptGapData();

        return ResponseResult.okResult(salaryGapVos);

    }

    // 分页查询薪资列表
    @Override
    public ResponseResult<PageVo> pageSalaryList(Integer pageNum, Integer pageSize, SalaryDto salaryDto) {

        Page<SalaryVo> page = new Page<>(pageNum, pageSize);

        Page<SalaryVo> salaryVoPage = getBaseMapper().selectSalaryVoPage(page, salaryDto);

        PageVo pageVo = new PageVo(salaryVoPage.getRecords(), salaryVoPage.getTotal());
        return ResponseResult.okResult(pageVo);

    }

    // 添加薪资
    @Override
    public ResponseResult addSalary(Salary salary) {

        // 校验薪资编号是否存在
        if (!checkSalaryIdUnique(salary.getSalaryId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SALARY_ID_EXIST);
        }

        // 校验员工编号是否存在
        LambdaQueryWrapper<Salary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Salary::getEmpId,salary.getEmpId());
        if(count(queryWrapper)!=0){
            return ResponseResult.errorResult(AppHttpCodeEnum.EMP_ID_EXIST);
        }

        // 保存到 salary 表
        save(salary);
        return ResponseResult.okResult();
    }

    // 校验薪资编号是否存在
    private boolean checkSalaryIdUnique(Long salaryId) {
        LambdaQueryWrapper<Salary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Salary::getSalaryId, salaryId);
        return count(queryWrapper) == 0;
    }

    // 删除薪资
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务注解：异常时回滚
    public ResponseResult deleteSalary(String salaryIds) {
        List<Long> idList = Arrays.stream(salaryIds.split(","))
                .map(String::trim)
                .filter(idStr -> !idStr.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 再删除员工表记录
        removeByIds(idList);

        return ResponseResult.okResult();
    }

    // 获取薪资详情
    @Override
    public ResponseResult salaryDetailInfo(Long salaryId) {
        Salary salary = getById(salaryId);
        if (salary == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SALARY_NOT_EXIST);
        }

        return ResponseResult.okResult(salary);
    }

    // 修改薪资
    @Override
    public ResponseResult updateSalary(Salary salary) {

        if (salary.getSubsidy() == null) {
            salary.setSubsidy(0.0); // 补贴（Double类型）
        }
        if (salary.getFine() == null) {
            salary.setFine(0.0); // 罚款（Double类型）
        }

        updateById(salary);
        return ResponseResult.okResult();
    }



}
