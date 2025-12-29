package com.wangchai.controller;

import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.SalaryDto;
import com.wangchai.domain.entity.Salary;
import com.wangchai.domain.vo.PageVo;
import com.wangchai.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salary")
public class SalaryController {


    @Autowired
    private SalaryService salaryService;

    // 统计薪资数据
    @GetMapping("/countData")
    public ResponseResult countData() {
        return salaryService.countData();
    }

    // 部门薪资 gap 数据
    @GetMapping("/SalaryDeptGapData")
    public ResponseResult SalaryDeptGapData() {
        return salaryService.SalaryDeptGapData();
    }

    // 分页查询薪资列表
    @GetMapping("/list")
    public ResponseResult<PageVo> pageSalaryList(@RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize,
                                              SalaryDto salaryDto) {
        return salaryService.pageSalaryList(pageNum, pageSize, salaryDto);
    }

    // 添加薪资
    @PostMapping
    public ResponseResult addSalary(@RequestBody Salary salary){
        return salaryService.addSalary(salary);
    }

    // 删除薪资
    @DeleteMapping
    public ResponseResult delete(@RequestParam(value = "ids", required = false) String salaryIds){
        return salaryService.deleteSalary(salaryIds);
    }

    // 获取薪资信息
    @GetMapping("/{id}")
    public ResponseResult salaryDetailInfo(@PathVariable(value = "id")Long salaryId){
        return salaryService.salaryDetailInfo(salaryId);
    }

    // 修改薪资
    @PutMapping
    public ResponseResult updateSalary(@RequestBody Salary salary){
        return salaryService.updateSalary(salary);
    }


}
