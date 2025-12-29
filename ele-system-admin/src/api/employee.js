import request from "@/utils/request";

// 查询员工统计数据
export function EmployeeCountData() {
  return request({
    url: '/employee/countData',
    method: 'get'
  })
}

// 查询员工年龄分布情况————折柱混合图
export function EmployeeAgeDistData() {
  return request({
    url: '/employee/EmployeeAgeDistData',
    method: 'get'
  })
}

// 查询员工列表
export function listEmp(query) {
  return request({
    url: '/employee/list',
    method: 'get',
    params: query
  })
}
// 查询员工详细
export function getEmp(empId) {
  return request({
    url: '/employee/' + empId,
    method: 'get'
  })
}

// 新增员工
export function addEmp(data) {
  return request({
    url: '/employee',
    method: 'post',
    data: data
  })
}

// 删除员工
export function delEmp(id) {
  // 处理：如果是数组，转成逗号分隔字符串；如果是单个值，直接转字符串
  const idsStr = Array.isArray(id) ? id.join(',') : String(id);
  return request({
    url: `/employee?ids=${idsStr}`, 
    method: 'delete'
  });
}

// 修改员工
export function updateEmp(data) {
  return request({
    url: '/employee',
    method: 'put',
    data: data
  })
}


