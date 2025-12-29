package com.wangchai.controller;

import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.EmpDto;
import com.wangchai.domain.dto.EmpEditDto;
import com.wangchai.domain.vo.PageVo;
import com.wangchai.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmpController {

    @Autowired
    private EmpService empService;

    // 查询员工统计数据
    @GetMapping("/countData")
    public ResponseResult countData() {
        return empService.countData();
    }

    // 查询员工年龄分布情况
    @GetMapping("/EmployeeAgeDistData")
    public ResponseResult EmployeeAgeDistData(){
        return empService.EmployeeAgeDistData();
    }

    // 分页查询员工列表
    @GetMapping("/list")
    public ResponseResult<PageVo> pageEmpList(@RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize,
                                              EmpDto empDto) {
        return empService.pageEmpList(pageNum, pageSize, empDto);
    }

    // 添加员工
    @PostMapping
    public ResponseResult addEmp(@RequestBody EmpEditDto empEditDto){
        return empService.addEmp(empEditDto);
    }

    // 删除员工
    @DeleteMapping
    public ResponseResult delete(@RequestParam(value = "ids", required = false) String empIds){
        return empService.deleteEmp(empIds);
    }

    // 获取员工信息
    @GetMapping("/{id}")
    public ResponseResult empDetailInfo(@PathVariable(value = "id")Long empId){
        return empService.empDetailInfo(empId);
    }

    // 修改员工
    @PutMapping
    public ResponseResult updateEmp(@RequestBody EmpEditDto empEditDto){
        return empService.updateEmp(empEditDto);
    }
}
