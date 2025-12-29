import axios from 'axios';
import { ElMessage } from 'element-plus';
// 【新增】如果是Vue3 + VueRouter项目，导入路由实例用于401跳转登录页
import router from '@/router'; // 请根据你的路由文件路径调整

const NETWORK_ERROR = '网络错误，请检查网络连接后重试';

// 读取 Vite 环境变量
const BASE_URL = import.meta.env.VITE_BASE_API;
console.log('BASE_URL:', BASE_URL);

const service = axios.create({
  baseURL: BASE_URL,
  timeout: 5000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
});

// ===================== 关键修改1：请求拦截器添加 Token 自动携带逻辑 =====================
service.interceptors.request.use(
  function (config) {
    console.log("请求参数：", config.params); // 保留原有打印
    
    // 1. 从localStorage读取Token（也可根据需求改用sessionStorage）
    const token = localStorage.getItem('token');
    // 2. Token存在时，添加到请求头（字段名和后端约定，常用Authorization/Bearer格式）
    if (token) {
      // 若后端要求直接传token（非Bearer格式），则改为：
      config.headers.token = token;
    }

    return config;
  },
  function (error) {
    ElMessage.error('请求发送失败：' + error.message);
    return Promise.reject(error);
  }
);

// ===================== 关键修改2：响应拦截器完善 401 Token 失效处理 =====================
service.interceptors.response.use(
  (res) => {
    const { code, msg, data } = res.data;
    if (code === 200) {
      return { code, msg, data };
    } else {
      // 业务错误提示（保留原有逻辑）
      ElMessage.error(msg || NETWORK_ERROR);
      return Promise.reject(msg || NETWORK_ERROR);
    }
  },
  // 【补充】处理HTTP状态码错误（比如401/500等，原有代码只处理了业务码）
  (error) => {
    // 1. 网络层错误（比如超时、断网）
    if (!error.response) {
      ElMessage.error(NETWORK_ERROR);
      return Promise.reject(NETWORK_ERROR);
    }

    // 2. HTTP状态码错误处理
    const { status, data } = error.response;
    switch (status) {
      // 401：Token失效/未登录
      case 401:
        ElMessage.error(data.msg || '登录状态已过期，请重新登录');
        // 清除无效Token
        localStorage.removeItem('token');
        // 跳转到登录页（VueRouter方式，非Vue项目可改用window.location.href = '/login'）
        router.push('/login');
        break;
      // 403：权限不足
      case 403:
        ElMessage.error('暂无权限访问该资源');
        break;
      // 500：服务器内部错误
      case 500:
        ElMessage.error('服务器内部错误，请稍后重试');
        break;
      // 其他状态码
      default:
        ElMessage.error(`请求失败：${data.msg || `HTTP ${status}`}`);
    }

    return Promise.reject(error);
  }
);

// 核心修复：重构request函数的参数处理逻辑（保留原有逻辑，无修改）
function request(options) {
  // 1. 统一method为小写，默认get
  options.method = (options.method || 'get').toLowerCase();
  
  // 2. 修复GET/POST参数逻辑：不再覆盖原有params/data
  if (options.method === 'get') {
    options.params = options.params || options.data || {};
    delete options.data; // 清空data，避免axios混淆
  } else if (options.method === 'post') {
    options.data = options.data || options.params || {};
    delete options.params; // 清空params，避免axios混淆
  }

  // 3. 发起请求
  return service(options);
}

export default request;