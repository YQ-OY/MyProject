<template>
  <div class="LoginPage">
    <!-- 流体背景Canvas -->
    <canvas id="fluid-canvas"></canvas>

    <!-- 滑块验证弹窗：替换原隐藏容器 -->
    <el-dialog v-model="captchaDialogVisible" title="安全验证" width="380px" align-center center
      :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" class="captcha-dialog">
      <!-- 滑块验证核心容器 -->
      <div id="captcha-slider" class="captcha-slider"></div>
    </el-dialog>

    <!-- 新增：忘记密码弹窗 -->
    <el-dialog v-model="forgetPwdDialogVisible" title="重置密码" width="380px" align-center center
      :close-on-click-modal="true" class="forget-pwd-dialog" :show-close="true">
      <div class="forget-pwd-form">
        <input type="text" class="form_input" placeholder="请输入用户名" v-model="forgetPwdForm.name" />
        <input type="email" class="form_input" placeholder="请输入绑定的邮箱" v-model="forgetPwdForm.email" />
        <input type="password" class="form_input" placeholder="请输入新密码（不少于6位）" v-model="forgetPwdForm.newPassword" />
        <button class="form_button button submit" @click="forgetPwdSubmit">提交重置</button>
      </div>
    </el-dialog>

    <div class="shell">
      <!-- 注册表单 -->
      <div class="container a-container" ref="aContainer">
        <form class="form" ref="aForm">
          <h2 class="form_title title">创建账号</h2>
          <div class="form_icons" style="margin-bottom: 15px;">
            <i class="iconfont icon-QQ"></i>
            <i class="iconfont icon-weixin"></i>
            <i class="iconfont icon-gitee-fill-round"></i>
          </div>
          <input type="text" class="form_input" placeholder="请输入用户名" v-model="registerForm.name" />
          <input type="email" class="form_input" placeholder="请输入邮箱" v-model="registerForm.email" />
          <input type="password" class="form_input" placeholder="请输入密码" v-model="registerForm.password" />
          <button class="form_button button submit" @click="registerSubmit">SIGN UP</button>
        </form>
      </div>

      <!-- 登录表单 -->
      <div class="container b-container is-hidden" ref="bContainer">
        <form class="form" ref="bForm">
          <h2 class="form_title title">登录系统</h2>
          <div class="form_icons" style="margin-bottom: 15px;">
            <i class="iconfont icon-QQ"></i>
            <i class="iconfont icon-weixin"></i>
            <i class="iconfont icon-gitee-fill-round"></i>
          </div>
          <input type="text" class="form_input" placeholder="请输入用户名" v-model="loginForm.name" />
          <input type="password" class="form_input" placeholder="请输入密码" v-model="loginForm.password" />
          <!-- 新增：绑定忘记密码点击事件 -->
          <a class="form_link" @click="openForgetPwdDialog">忘记密码？</a>
          <button class="form_button button submit" @click="loginSubmit">SIGN IN</button>
        </form>
      </div>

      <!-- 切换面板 -->
      <div class="switch" ref="switchCnt">
        <div class="switch_circle"></div>
        <div class="switch_circle switch_circle-t"></div>
        <div class="switch_container" ref="switchC1">
          <h2 class="switch_title title" style="letter-spacing: 0;">Welcome Back ! </h2>
          <p class="switch_description description">已经有账号了嘛，快去登入系统来开始美好的一天吧！！！</p>
          <button class="switch_button button switch-btn">SIGN IN</button>
        </div>
        <div class="switch_container is-hidden" ref="switchC2">
          <h2 class="switch_title title" style="letter-spacing: 0;">Hello Friend! </h2>
          <p class="switch_description description">去注册一个账号，成为企业的一颗螺丝钉，共创未来征程！</p>
          <button class="switch_button button switch-btn">SIGN UP</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, nextTick } from 'vue'
import '@/assets/css/iconfont.css'
import '@/assets/css/captcha-slider.css'
import CaptchaSlider from '@/assets/js/captcha-slider.js'

import { userLogin, userRegister, forgetPassword } from '@/api/userInfo'
import { useRouter } from 'vue-router'
import { ElMessage, ElDialog } from 'element-plus'

const router = useRouter()

// 登录/注册表单数据
const loginForm = ref({ name: '', password: '' })
const registerForm = ref({ name: '', email: '', password: '' })
// 新增：忘记密码表单数据
const forgetPwdForm = ref({ name: '', email: '', newPassword: '' })

// DOM引用
const switchCnt = ref(null)
const switchC1 = ref(null)
const switchC2 = ref(null)
const switchCircle = ref([])
const switchBtn = ref([])
const aContainer = ref(null)
const bContainer = ref(null)

// 滑块验证弹窗显隐控制（核心）
const captchaDialogVisible = ref(false)
// 新增：忘记密码弹窗显隐控制
const forgetPwdDialogVisible = ref(false)

// ========== 核心：封装滑块验证方法（适配el-dialog） ==========
const verifyCaptcha = () => {
  return new Promise(async (resolve, reject) => {
    // 1. 打开弹窗
    captchaDialogVisible.value = true
    await nextTick() // 等待弹窗DOM渲染完成

    // 2. 执行滑块验证
    CaptchaSlider.init({
      id: 'captcha-slider',
      width: 310,
      height: 155,
    }).then(() => {
      ElMessage.success('滑块验证成功！')
      captchaDialogVisible.value = false // 验证成功关闭弹窗
      resolve()
    }).catch(() => {
      captchaDialogVisible.value = false // 验证失败关闭弹窗
      reject(new Error('滑块验证失败，请重新验证'))
    })
  })
}

// 切换登录/注册表单逻辑
const changeForm = () => {
  switchCnt.value.classList.add("is-gx");
  setTimeout(() => switchCnt.value.classList.remove("is-gx"), 1500)

  switchCnt.value.classList.toggle("is-txr")
  switchCircle.value[0].classList.toggle("is-txr")
  switchCircle.value[1].classList.toggle("is-txr")

  switchC1.value.classList.toggle("is-hidden")
  switchC2.value.classList.toggle("is-hidden")
  aContainer.value.classList.toggle("is-txl")
  aContainer.value.classList.toggle("is-hidden")
  bContainer.value.classList.toggle("is-txl")
  bContainer.value.classList.toggle("is-hidden")
}

// 登录提交处理
const loginSubmit = async (e) => {
  e.preventDefault()
  const { name, password } = loginForm.value

  // 表单基础验证
  if (!name.trim()) return ElMessage.warning('请输入用户名！')
  if (!password.trim()) return ElMessage.warning('请输入密码！')

  try {
    ElMessage.info('请完成安全验证后继续')
    await verifyCaptcha() // 触发滑块弹窗验证

    // 验证成功调用登录接口
    const response = await userLogin(name, password)
    localStorage.setItem('token', response.data.token)
    localStorage.setItem('loginName', name)
    localStorage.setItem('password', password)
    ElMessage.success('登录成功！')
    await router.push('/home')
  } catch (error) {
     ElMessage.closeAll()
    ElMessage.error(`登录失败：${error.message || '注册信息有误或验证失败'}`)
  }
}

// 注册提交处理
const registerSubmit = async (e) => {
  e.preventDefault()
  const { name, email, password } = registerForm.value

  // 表单基础验证
  if (!name.trim()) return ElMessage.warning('请输入用户名！')
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!emailReg.test(email)) return ElMessage.warning('请输入正确的邮箱格式！')
  if (!password.trim() || password.length < 6) return ElMessage.warning('密码长度不能少于6位！')

  try {
    ElMessage.info('请完成安全验证后继续')
    await verifyCaptcha() // 触发滑块弹窗验证

    // 验证成功调用注册接口
    await userRegister(name, password, email)
    ElMessage.success('注册成功！请登录')
    registerForm.value = { name: '', email: '', password: '' }
    changeForm()
  } catch (error) {
    //  ElMessage.closeAll()
    ElMessage.error(`注册失败：${error.message || '注册信息有误或验证失败'}`)
  }
}

// 新增：打开忘记密码弹窗
const openForgetPwdDialog = () => {
  // 清空表单数据，避免残留
  forgetPwdForm.value = { name: '', email: '', newPassword: '' }
  forgetPwdDialogVisible.value = true
}

// 新增：忘记密码提交处理
const forgetPwdSubmit = async () => {
  const { name, email, newPassword } = forgetPwdForm.value
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/

  // 表单基础验证
  if (!name.trim()) return ElMessage.warning('请输入用户名！')
  if (!emailReg.test(email)) return ElMessage.warning('请输入正确的邮箱格式！')
  if (!newPassword.trim() || newPassword.length < 6) return ElMessage.warning('新密码长度不能少于6位！')

  try {
    ElMessage.info('请完成安全验证后继续')
    await verifyCaptcha() // 触发滑块弹窗验证

    // 验证成功调用重置密码接口（需你替换为实际接口）
    await forgetPassword(name, email, newPassword)
    // 模拟接口调用成功
    ElMessage.success('密码重置成功！请使用新密码登录')
    forgetPwdDialogVisible.value = false // 关闭弹窗
  } catch (error) {
    ElMessage.error(`密码重置失败：${error.message || '信息有误或验证失败'}`)
  }
}

// 组件挂载后初始化
onMounted(() => {
  window.ga =
    window.ga ||
    function () {
      (ga.q = ga.q || []).push(arguments);
    };
  ga.l = +new Date();
  ga("create", "UA-105392568-1", "auto");
  ga("send", "pageview");
  // 动态加载流体背景脚本（路径修正）
  const script = document.createElement('script');
  script.src = '/src/assets/js/loginScript.js'; // 修正路径前缀

  document.body.appendChild(script);

  // 绑定切换按钮事件
  switchCircle.value = document.querySelectorAll(".switch_circle")
  switchBtn.value = document.querySelectorAll(".switch-btn")
  switchBtn.value.forEach(btn => btn.addEventListener("click", changeForm))
})
</script>

<style lang="less" scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  user-select: none;
}

.LoginPage {
  width: 100%;
  height: 98.3vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  background-color: transparent;
  position: relative;
  color: #4a5568;
}

#fluid-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

// 滑块弹窗样式优化
.captcha-dialog {
  :deep(.el-dialog) {
    z-index: 9999; // 确保弹窗层级最高
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  :deep(.el-dialog__header) {
    padding: 15px 20px 10px;
    border-bottom: 1px solid #f0f0f0;

    .el-dialog__title {
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
    }
  }

  :deep(.el-dialog__body) {
    padding: 10px 20px 20px;
    margin: 0;
  }
}

// 优化：忘记密码弹窗样式（和整体风格统一）
.forget-pwd-dialog {
  :deep(.el-dialog) {
    z-index: 9999;
    border-radius: 16px; // 和登录容器圆角一致
    box-shadow: 0 12px 32px rgba(99, 102, 241, 0.2); // 柔和的紫色阴影
    background-color: rgba(255, 255, 255, 0.95); // 半透明白背景，和登录框统一
    backdrop-filter: blur(8px); // 增加毛玻璃效果，和主容器呼应
  }

  :deep(.el-dialog__header) {
    padding: 20px 24px 12px;
    border-bottom: 1px solid #f5f5f5;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b; // 和登录标题配色一致
      letter-spacing: 0.5px;
    }

    .el-dialog__headerbtn .el-icon-close {
      font-size: 18px;
      color: #94a3b8;
      transition: 0.2s;

      &:hover {
        color: #6366f1; //  hover时变主紫色
        transform: scale(1.1);
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 24px;
    margin: 0;
  }
}

// 优化：忘记密码表单样式（和登录表单输入框统一）
.forget-pwd-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 12px; // 输入框之间的间距更舒适

  .form_input {
    width: 100%; // 占满弹窗宽度
    height: 48px;
    padding-left: 20px;
    font-size: 14px;
    letter-spacing: 0.15px;
    border: none;
    outline: none;
    background-color: rgba(247, 248, 250, 0.9);
    border-radius: 12px; // 圆角更柔和
    box-shadow: inset 1px 1px 3px rgba(0, 0, 0, 0.05), inset -1px -1px 3px rgba(255, 255, 255, 0.9);
    color: #2d3748;
    transition: 0.25s ease;

    &:focus {
      box-shadow: inset 2px 2px 5px rgba(0, 0, 0, 0.08), inset -2px -2px 5px rgba(255, 255, 255, 0.95);
      background-color: rgba(255, 255, 255, 0.95);
    }
  }

  .form_button {
    width: 100%; // 按钮占满宽度，更协调
    margin-top: 16px; // 按钮和输入框的间距
  }
}

// 滑块容器样式（适配弹窗）
.captcha-slider {
  width: 310px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  // padding: 10px;
  border-radius: 8px;
}

.shell {
  position: relative;
  width: 1000px;
  min-width: 1000px;
  min-height: 600px;
  height: 600px;
  padding: 25px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08), 0 5px 15px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border-radius: 24px;
  backdrop-filter: blur(8px);
  z-index: 2;
}

// 响应式适配
@media (max-width: 1200px) {
  .shell {
    transform: scale(0.7);
  }
}

@media (max-width: 1000px) {
  .shell {
    transform: scale(0.6);
  }
}

@media (max-width: 800px) {
  .shell {
    transform: scale(0.5);
  }
}

@media (max-width: 600px) {
  .shell {
    transform: scale(0.4);
  }
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  width: 600px;
  height: 100%;
  padding: 25px;
  background-color: rgba(255, 255, 255, 0.85);
  transition: 1.25s;
  border-radius: 24px;
}

.form {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  width: 100%;
  height: 100%;

  .form_input {
    width: 350px;
    height: 48px;
    margin: 8px 0;
    padding-left: 25px;
    font-size: 14px;
    letter-spacing: 0.15px;
    border: none;
    outline: none;
    background-color: rgba(247, 248, 250, 0.9);
    transition: 0.25s ease;
    border-radius: 16px;
    box-shadow: inset 1px 1px 3px rgba(0, 0, 0, 0.05), inset -1px -1px 3px rgba(255, 255, 255, 0.9);
    color: #2d3748;

    &:focus {
      box-shadow: inset 2px 2px 5px rgba(0, 0, 0, 0.08), inset -2px -2px 5px rgba(255, 255, 255, 0.95);
      background-color: rgba(255, 255, 255, 0.95);
    }
  }
}

.iconfont {
  margin: 0 8px;
  border: rgba(96, 165, 250, 0.3) 2px solid;
  border-radius: 50%;
  font-size: 28px;
  padding: 6px;
  color: #6366f1;
  opacity: 0.7;
  transition: 0.2s ease;

  &:hover {
    opacity: 1;
    transform: scale(1.05);
    border-color: rgba(96, 165, 250, 0.8);
    cursor: pointer;
  }
}

.form_link {
  color: #6366f1;
  font-size: 15px;
  margin-top: 15px;
  border-bottom: 1px solid rgba(99, 102, 241, 0.3);
  line-height: 2;
  transition: 0.2s;
  cursor: pointer; // 新增：添加鼠标指针样式

  &:hover {
    color: #4f46e5;
    border-color: rgba(99, 102, 241, 0.8);
  }
}

.title {
  font-size: 36px;
  font-weight: 700;
  line-height: 3;
  color: #1e293b;
  letter-spacing: 8px;
}

.description {
  font-size: 15px;
  letter-spacing: 0.25px;
  text-align: center;
  line-height: 1.8;
  color: #64748b;
  margin-bottom: 20px;
}

.button {
  width: 200px;
  height: 56px;
  border-radius: 32px;
  margin-top: 30px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1.15px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #ffffff;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.3);
  border: none;
  outline: none;
  cursor: pointer;
  transition: 0.3s ease;
}

.a-container {
  left: calc(100% - 600px);
}

.b-container {
  left: calc(100% - 600px);
}

.switch {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 400px;
  padding: 50px;
  z-index: 200;
  transition: 1.25s;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.9) 0%, rgba(139, 92, 246, 0.9) 100%);
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-radius: 24px;
}

.switch_circle {
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.1);
  box-shadow: inset 2px 2px 5px rgba(0, 0, 0, 0.05), inset -2px -2px 5px rgba(255, 255, 255, 0.2);
  bottom: -60%;
  left: -60%;
  transition: 1.25s;
  backdrop-filter: blur(5px);
}

.switch_circle-t {
  top: -30%;
  left: 60%;
  width: 300px;
  height: 300px;
  background-color: rgba(255, 255, 255, 0.15);
}

.switch_container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: absolute;
  width: 400px;
  padding: 50px 55px;
  transition: 1.25s;
  color: #ffffff;
}

.switch_container .title {
  color: #ffffff;
  letter-spacing: 5px;
}

.switch_container .description {
  color: rgba(255, 255, 255, 0.9);
}

.switch_button {
  background: #ffffff;
  color: #6366f1;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.switch_button:hover,
.submit:hover {
  box-shadow: 0 10px 25px rgba(99, 102, 241, 0.4);
  transform: scale(0.985);
  transition: 0.25s;
}

.switch_button:active,
.switch_button:focus,
.submit:active,
.submit:focus {
  box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);
  transform: scale(0.97);
  transition: 0.25s;
}

.is-txr {
  left: calc(100% - 400px);
  transition: 1.25s;
  transform-origin: left;
}

.is-txl {
  left: 0;
  transition: 1.25s;
  transform-origin: right;
}

.is-hidden {
  visibility: hidden;
  opacity: 0;
  position: absolute;
  transition: 1.25s;
}

.is-gx {
  animation: is-gx 1.25s;
}

@keyframes is-gx {

  0%,
  10%,
  100% {
    width: 400px;
  }

  30%,
  50% {
    width: 500px;
  }
}
</style>