import request from '@/utils/request'

// 注册
export function userRegister(name,password,email) {
    return request({
        url: '/userInfo/register',
        method: 'post',
        headers: {
            isToken :false
        },
        data: {"name":name,"password":password,"email":email}
    })
}

// forgetPassword
export function forgetPassword(name,email,newPassword) {
    return request({
        url: '/userInfo/forgetPassword',
        method: 'put',
        headers: {
            isToken :false
        },
        data: {"name":name,"email":email,"newPassword":newPassword}
    })
}

// 登录
export function userLogin(name,password) {
    return request({
        url: '/login',
        method: 'post',
        headers: {
            isToken: false
          },
        data: {'name':name,'password':password}
    })
}

// 退出登录
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}



// 获取用户信息
export function getUserInfo(name) {
  return request({
    url: '/userInfo/' + name,
    method: 'get'
  })
}

// 修改用户信息
export function updateUserInfo(data) {
  return request({
    url: '/userInfo',
    method: 'put',
    data: data
  })
}