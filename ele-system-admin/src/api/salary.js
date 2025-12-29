import request from "@/utils/request";

// 查询薪资统计数据
export function SalaryCountData() {
  return request({
    url: '/salary/countData',
    method: 'get'
  })
}

// 查询薪资差距及相关数据————水平条形图
export function SalaryDeptGapData() {
  return request({
    url: '/salary/SalaryDeptGapData',
    method: 'get'
  })
}

// 查询薪资列表
export function listSalary(query) {
  return request({
    url: '/salary/list',
    method: 'get',
    params: query
  })
}
// 查询薪资详细
export function getSalary(salaryId) {
  return request({
    url: '/salary/' + salaryId,
    method: 'get'
  })
}

// 新增薪资
export function addSalary(data) {
  return request({
    url: '/salary',
    method: 'post',
    data: data
  })
}

// 删除薪资
export function delSalary(id) {
  // 处理：如果是数组，转成逗号分隔字符串；如果是单个值，直接转字符串
  const idsStr = Array.isArray(id) ? id.join(',') : String(id);
  return request({
    url: `/salary?ids=${idsStr}`, 
    method: 'delete'
  });
}

// 修改薪资
export function updateSalary(data) {
  return request({
    url: '/salary',
    method: 'put',
    data: data
  })
}
