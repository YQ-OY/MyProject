import request from "@/utils/request";

// 查询部门统计数据
export function DepartmentCountData() {
  return request({
    url: '/department/countData',
    method: 'get'
  })
}

// 获取部门筛选列表——在员工管理用的
export function getDeptOptions() {
  return request({
    url: '/department/options', // 后端提供的部门下拉/筛选接口
    method: 'get'
  })
}

// 查询部门列表
export function listDept(query) {
  return request({
    url: '/department/list',
    method: 'get',
    params: query
  })
}
// 查询部门详细
export function getDept(deptId) {
  return request({
    url: '/department/' + deptId,
    method: 'get'
  })
}

// 新增部门
export function addDept(data) {
  return request({
    url: '/department',
    method: 'post',
    data: data
  })
}

// 删除部门
export function delDept(id) {
  // 处理：如果是数组，转成逗号分隔字符串；如果是单个值，直接转字符串
  const idsStr = Array.isArray(id) ? id.join(',') : String(id);
  return request({
    url: `/department?ids=${idsStr}`, 
    method: 'delete'
  });
}

// 修改部门
export function updateDept(data) {
  return request({
    url: '/department',
    method: 'put',
    data: data
  })
}

// 查询部门人数————饼图
export function DeptPersonCountData() {
  return request({
    url: '/department/DeptPersonCountData',
    method: 'get'
  })
}