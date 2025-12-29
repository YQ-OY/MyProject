package com.wangchai.controller;

import com.wangchai.domain.ResponseResult;
import com.wangchai.domain.dto.DeptDto;
import com.wangchai.domain.entity.Dept;
import com.wangchai.domain.vo.PageVo;
import com.wangchai.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department")
public class DeptController {


    @Autowired
    private DeptService deptService;

    // 查询部门统计数据
    @GetMapping("/countData")
    public ResponseResult countData(){
        return deptService.countData();
    }

    // 查询部门人数——饼图
    @GetMapping("/DeptPersonCountData")
    public ResponseResult DeptPersonCountData(){
        return deptService.DeptPersonCountData();
    }

    // 查询部门选项
    @GetMapping("/options")
    public ResponseResult getDeptOptions(){
        return deptService.getDeptOptions();
    }

    // 分页查询部门列表
    @GetMapping("/list")
    public ResponseResult<PageVo> pageDeptList(@RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize,
                                              DeptDto deptDto) {
        return deptService.pageDeptList(pageNum, pageSize, deptDto);
    }

    // 添加部门
    @PostMapping
    public ResponseResult addDept(@RequestBody Dept dept){
        return deptService.addDept(dept);
    }

    // 删除部门
    @DeleteMapping
    public ResponseResult delete(@RequestParam(value = "ids", required = false) String deptIds){
        return deptService.deleteDept(deptIds);
    }

    // 获取部门信息
    @GetMapping("/{id}")
    public ResponseResult deptDetailInfo(@PathVariable(value = "id")Long deptId){
        return deptService.deptDetailInfo(deptId);
    }

    // 修改部门
    @PutMapping
    public ResponseResult updateDept(@RequestBody Dept dept){
        return deptService.updateDept(dept);
    }


}
