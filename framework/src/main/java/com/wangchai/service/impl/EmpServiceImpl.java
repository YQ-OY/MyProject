package com.wangchai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.EmpDto;
import com.wangchai.domain.dto.EmpEditDto;
import com.wangchai.domain.entity.Emp;
import com.wangchai.domain.entity.Salary;
import com.wangchai.domain.vo.EmpAgeDistVo;
import com.wangchai.domain.vo.EmpCountVo;
import com.wangchai.domain.vo.EmpVo;
import com.wangchai.domain.vo.PageVo;
import com.wangchai.enums.AppHttpCodeEnum;
import com.wangchai.exception.SystemException;
import com.wangchai.mapper.EmpMapper;
import com.wangchai.mapper.SalaryMapper;
import com.wangchai.service.EmpService;
import com.wangchai.service.SalaryService;
import com.wangchai.service.DeptService;
import com.wangchai.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp> implements EmpService {

    @Autowired
    private SalaryMapper salaryMapper;

    @Autowired
    private DeptService deptService;

    // 查询员工统计数据
    @Override
    public ResponseResult countData() {

        double employeeRate = (double) getBaseMapper().countData() / count() * 100;
        employeeRate = Math.round(employeeRate * 100) / 100.0;
        EmpCountVo empCountVo = new EmpCountVo((long) count(), employeeRate);

        return ResponseResult.okResult(empCountVo);
    }

    // 查询员工年龄分布情况
    @Override
    public ResponseResult EmployeeAgeDistData() {

        List<EmpAgeDistVo> empAgeDistVoList = getBaseMapper().EmployeeAgeDistData();

        return ResponseResult.okResult(empAgeDistVoList);
    }

    // 分页查询员工列表
    @Override
    public ResponseResult<PageVo> pageEmpList(Integer pageNum, Integer pageSize, EmpDto empDto) {

        Page<EmpVo> page = new Page<>(pageNum, pageSize);

        Page<EmpVo> empVoPage = getBaseMapper().selectEmpVoPage(page, empDto);

        PageVo pageVo = new PageVo(empVoPage.getRecords(), empVoPage.getTotal());
        return ResponseResult.okResult(pageVo);

    }

    // 添加员工
    @Override
    public ResponseResult addEmp(EmpEditDto empEditDto) {

        // 校验员工编号是否存在
        if (!checkEmpIdUnique(empEditDto.getEmpId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.EMP_ID_EXIST);
        }

        // 检验主管是否为空
        if (empEditDto.getManagerId() != null) {
            // 校验主管是否存在
            if (!checkManagerIdExist(empEditDto.getManagerId())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_EXIST);
            }
        }


        // 保存到 emp 表
        Emp emp = BeanCopyUtils.copyBean(empEditDto, Emp.class);

        emp.setDeptId(deptService.getByName(empEditDto.getDeptName()).getDeptId());

        save(emp);

        return ResponseResult.okResult();
    }

    // 校验员工编号是否存在
    public boolean checkEmpIdUnique(Long empId) {
        LambdaQueryWrapper<Emp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Emp::getEmpId, empId);
        return count(queryWrapper) == 0;
    }

    // 校验主管是否存在
    public boolean checkManagerIdExist(Long managerId) {
        LambdaQueryWrapper<Emp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Emp::getEmpId, managerId);
        return count(queryWrapper) > 0;
    }

    // 删除员工
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务注解：异常时回滚
    public ResponseResult deleteEmp(String empIds) {
        List<Long> idList = Arrays.stream(empIds.split(","))
                .map(String::trim)
                .filter(idStr -> !idStr.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 先删除薪资表中关联的记录
        salaryMapper.delete(new LambdaQueryWrapper<Salary>()
                .in(Salary::getEmpId, idList));

        // 再删除员工表记录
        removeByIds(idList);

        return ResponseResult.okResult();
    }

    // 获取员工信息
    @Override
    public ResponseResult empDetailInfo(Long empId) {
        Emp emp = getById(empId);
        if (emp == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.EMP_NOT_EXIST);
        }

        // 转换为 EmpVo 对象
        EmpVo empVo = BeanCopyUtils.copyBean(emp, EmpVo.class);
        // 转换部门名称
        empVo.setDeptName(emp.getDeptId() != null ? deptService.getById(emp.getDeptId()).getName() : "无部门");

        return ResponseResult.okResult(empVo);
    }

    // 修改员工
    @Override
    public ResponseResult updateEmp(EmpEditDto empEditDto) {
        Emp emp = BeanCopyUtils.copyBean(empEditDto, Emp.class);

        // 检验主管是否为空
        if (empEditDto.getManagerId() != null) {
            // 校验主管是否存在
            if (!checkManagerIdExist(empEditDto.getManagerId())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_EXIST);
            }
        }


        // 2. 获取部门ID（保留原有逻辑）
        Long deptId = deptService.getByName(empEditDto.getDeptName()).getDeptId();

        // 3. 构建更新条件：用UpdateWrapper指定所有要更新的字段（包括null值）
        UpdateWrapper<Emp> updateWrapper = new UpdateWrapper<>();
        // 条件：更新指定ID的员工
        updateWrapper.eq("emp_id", empEditDto.getEmpId())
                // 强制更新managerId（即使为null也会写入SQL）
                .setSql("manager_id = " + (empEditDto.getManagerId() == null ? "NULL" : empEditDto.getManagerId()))
                .set("photo", empEditDto.getPhoto())
                .set("name", empEditDto.getName())
                .set("gender", empEditDto.getGender())
                .set("dept_id", deptId)
                .set("job", empEditDto.getJob())
                .set("birthday", empEditDto.getBirthday())
                .set("entryday", empEditDto.getEntryday());

        update(null, updateWrapper);
        return ResponseResult.okResult();
    }


}
