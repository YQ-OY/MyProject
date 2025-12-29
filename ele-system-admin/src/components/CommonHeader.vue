<template>
    <div class="header">
        <!-- 左侧 -->
        <div class="lt-content">
            <!-- 折叠 -->
            <el-button size="small" class="header-btn" @click="handleCollapse">
                <el-icon size="20">
                    <Menu v-if="!isCollapse" />
                    <Grid v-else />
                </el-icon>
            </el-button>
            <!-- 刷新 -->
            <el-button size="small" class="header-btn" @click="handleRefresh">
                <el-icon size="20">
                    <Refresh />
                </el-icon>
            </el-button>
            <!-- 面包屑 -->
            <el-breadcrumb separator="/" class="bread">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <!-- 中间 -->
        <div class="center-content">
            <p class="saying">居其位，安其职，尽其诚；任其事，尽其责，成其功</p>
        </div>
        <!-- 右侧 -->
        <div class="rt-content">
            <!-- <theme-button value="dark" id="btn" size="3"></theme-button> -->
            <!-- 黑白模式 -->
            <el-button size="small" class="header-btn" @click="switchColor()" v-model="value1">
                <el-icon size="20">
                    <Sunny />
                </el-icon>
            </el-button>
            <!-- 头像框 -->
            <!-- 头像区域：动态绑定用户信息里的头像 -->
            <div class="avatar-wrapper" @click="openDrawer">
                <!-- 头像增加过渡动画 -->
                <el-avatar :size="48" :src="formData.photo" class="user-avatar">
                    {{ loginName.charAt(0).toUpperCase() }} <!-- 首字母大写更美观 -->
                </el-avatar>
                <!-- 用户名增加溢出省略 -->
                <span class="username-text">{{ loginName }}</span>
            </div>


            <!-- 个人信息抽屉 -->
            <el-drawer v-model="drawerVisible" :with-header="false" direction="rtl" size="520px" class="custom-drawer"
                @close="handleDrawerClose">
                <!-- 抽屉内容 -->
                <div class="drawer-container">
                    <h4 class="drawer-title">个人信息</h4>

                    <!-- 核心：表单（根据isEdit控制是否可编辑） -->
                    <el-form ref="formRef" :model="formData" label-width="100px" size="default" class="user-form">
                        <!-- 头像展示 -->
                        <el-form-item label="个人头像" class="photo-form-item">
                            <div class="avatar-name-section">
                                <!-- 优化：统一上传区域布局 -->
                                <div class="photo-upload-group">
                                    <!-- 上传组件 + tooltip -->
                                    <el-tooltip content="支持JPG/PNG格式，大小≤2MB" placement="top" effect="light"
                                        :show-after="100">
                                        <el-upload name="img" class="avatar-uploader" :action="uploadURL"
                                            :show-file-list="false" :headers="uploadHeaders"
                                            :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                                            <!-- 上传后显示头像 / 未上传显示上传按钮 -->
                                            <img v-if="formData.photo" :src="formData.photo" class="avatar-img"
                                                alt="个人头像" />
                                            <div v-else class="upload-btn-placeholder">
                                                <el-icon class="upload-icon">
                                                    <Plus />
                                                </el-icon>
                                                <span class="upload-btn-text">点击上传</span>
                                            </div>
                                        </el-upload>
                                    </el-tooltip>
                                </div>
                                <h2 class="drawer-username">{{ formData.name }}</h2>
                            </div>
                        </el-form-item>

                        <!-- 单列表单项 -->
                        <el-form-item label="员工ID" prop="userId">
                            <el-input v-model="formData.userId" clearable :prefix-icon="UserFilled" disabled />
                        </el-form-item>
                        <el-form-item label="用户名" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入用户名" clearable :prefix-icon="UserFilled"
                                :disabled="!isEdit" />
                        </el-form-item>
                        <el-form-item label="密码" prop="password">
                            <el-input v-model="formData.password" placeholder="请输入密码" clearable :prefix-icon="Lock"
                                :disabled="!isEdit" show-password />
                        </el-form-item>
                        <el-form-item label="性别" prop="gender">
                            <el-radio-group v-model="formData.gender" :disabled="!isEdit">
                                <el-radio label="男" class="radio-item">男</el-radio>
                                <el-radio label="女" class="radio-item">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="formData.email" placeholder="请输入邮箱" clearable :prefix-icon="Message"
                                :disabled="!isEdit" />
                        </el-form-item>
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="formData.phone" placeholder="请输入手机号" clearable :prefix-icon="Phone"
                                :disabled="!isEdit" />
                        </el-form-item>
                    </el-form>

                    <!-- 底部按钮区域（根据编辑状态切换按钮） -->
                    <div class="btn-group">
                        <el-button v-if="!isEdit" type="primary" class="drawer-btn edit-btn" @click="handleEdit">
                            <el-icon>
                                <Edit />
                            </el-icon>
                            编辑
                        </el-button>
                        <el-button v-else type="success" class="drawer-btn save-btn" @click="handleSave">
                            <el-icon>
                                <Check />
                            </el-icon>
                            保存
                        </el-button>
                        <el-button type="danger" class="drawer-btn logout-btn" @click="handleLogout">
                            <el-icon>
                                <SwitchButton />
                            </el-icon>
                            退出
                        </el-button>
                    </div>
                </div>
            </el-drawer>

        </div>

        <!-- 美化后的退出登录确认对话框 -->
        <el-dialog v-model="showExitDialog" title="" width="500px" center :show-close="false" class="exit-dialog">
            <!-- 自定义弹窗头部 -->
            <div class="dialog-header">
                <el-icon class="dialog-icon">
                    <SwitchButton />
                </el-icon>
                <h3 class="dialog-title">退出登录</h3>
            </div>

            <!-- 弹窗内容 -->
            <div class="dialog-content">
                <p class="content-text">确定要退出当前账号吗？</p>
                <p class="content-desc">退出后将返回登录页面，需要重新登录才能继续操作</p>
            </div>

            <!-- 弹窗底部按钮 -->
            <template #footer>
                <div class="dialog-footer">
                    <el-button class="cancel-btn" @click="showExitDialog = false" :border="false">
                        取消
                    </el-button>
                    <el-button class="confirm-btn" type="primary" @click="confirmExit" :border="false" size="default">
                        确认退出
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, defineEmits } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAllDataStore } from '@/stores/counter'
import {
    Menu, Grid, Refresh, Sunny, Moon, SwitchButton, Edit, Check,
    UserFilled, Lock, Message, Phone
} from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, logout } from '@/api/userInfo'


const router = useRouter()
const store = useAllDataStore()
const loginName = ref(localStorage.getItem('loginName') || '')
const password = ref(localStorage.getItem('password') || '')
const formRef = ref(null)
const uploadURL = ref('')
const emit = defineEmits(['refresh-main'])
// 编辑状态（控制表单是否可编辑）
const isEdit = ref(false)
// 表单数据
const formData = reactive({
    userId: "",
    name: "",
    password: "",
    email: "",
    phone: "",
    gender: "",
    photo: ""
})

// 控制退出确认对话框显示/隐藏
const showExitDialog = ref(false)

const uploadHeaders = ref({})

// 页面挂载时：自动查用户信息（含头像）
onMounted(async () => {
    if (loginName.value) {
        await loadUserInfo()
    }
    uploadURL.value = 'http://localhost:9999/api/upload'

    // 核心：从本地缓存获取token，添加到上传请求头
    const token = localStorage.getItem('token') // 假设登录后token存在localStorage
    if (token) {
        uploadHeaders.value = {
            'token': token // 和后端JWT过滤器读取的请求头key一致
        }
    }

})

// 菜单折叠状态
const isCollapse = computed(() => store.state.isCollapse)

const handleCollapse = () => {
    store.toggleCollapse();
}

// 刷新
const handleRefresh = () => {
    emit('refresh-main')
}

// 加载用户信息
const loadUserInfo = async () => {
    try {
        // 调用接口获取用户信息（实际项目中替换为真实接口逻辑）
        const res = await getUserInfo(loginName.value)
        if (res.code === 200) {
            // 赋值到表单
            Object.assign(formData, res.data)
            formData.password = password.value
        } else {
            ElMessage.error('获取用户信息失败')
        }
    } catch (error) {
        console.error('加载用户信息失败：', error)
        ElMessage.error('获取用户信息失败，请重试')
    }
}

// 编辑按钮逻辑：切换为可编辑状态
const handleEdit = () => {
    isEdit.value = true
}

// 保存按钮逻辑：提交修改
const handleSave = async () => {
    try {
        // 表单校验
        await formRef.value.validate()
        // 调用更新接口（实际项目中替换为真实接口）
        const res = await updateUserInfo(formData)
        if (res.code === 200) {
            ElMessage.success('修改个人信息成功')
            localStorage.setItem('password', formData.password)
            password.value = formData.password
            // 切换回不可编辑状态
            isEdit.value = false
        } else {
            ElMessage.error('修改失败：' + res.msg)
        }
    } catch (error) {
        if (error.name === 'ValidationError') {
            ElMessage.warning('请完善表单信息')
        } else {
            console.error('保存失败：', error)
            ElMessage.error('修改个人信息失败，请重试')
        }
    }
}

// 抽屉控制
const drawerVisible = ref(false)
const openDrawer = async () => {
    drawerVisible.value = true
    // 打开抽屉时加载用户数据
    await loadUserInfo()
}
const handleDrawerClose = () => {
    drawerVisible.value = false
    // 关闭抽屉时重置编辑状态和表单
    isEdit.value = false
}

// 头像上传成功回调
const handleAvatarSuccess = (response) => {
    // 假设接口返回 { code: 200, data: '照片URL' }
    if (response.code === 200) {
        formData.photo = response.data
        ElMessage.success('照片上传成功')
    } else {
        ElMessage.error('照片上传失败')
    }
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
    const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg' || file.type === 'image/webp'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPG) {
        ElMessage.error('只能上传 JPG/PNG 格式的图片！')
    }
    if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB！')
    }
    return isJPG && isLt2M
}

const handleLogout = () => {
    showExitDialog.value = true
}

// 确认退出：执行真正的退出逻辑
const confirmExit = async () => {
    try {
        // 等待后端退出接口响应
        await logout()
        ElMessage.success('退出登录成功')
    } catch (error) {
        ElMessage.warning('网络异常，已强制退出本地状态')
    } finally {
        // 无论接口是否成功，都清空本地存储并跳转
        localStorage.removeItem('token')
        localStorage.removeItem('loginName')
        window.location.href = '/login'
        // 关闭对话框
        showExitDialog.value = false
    }
}

</script>

<style scoped lang="less">
// 自定义字体
@font-face {
    font-family: "SiYu";
    src: url("@/assets/font/思雨手写行楷.ttf") format("truetype");
    font-weight: normal;
    font-style: normal;
}

.header {
    display: flex;
    justify-content: space-between; // 左/中/右 分布
    align-items: center;
    width: 100%;
    height: 60px; // 固定高度（替代100%，避免父级无高度失效）
    background-color: #FCFAFC;
    padding: 0 20px; // 左右内边距，避免贴边
    box-sizing: border-box; // 内边距不占宽度
    border-bottom: 1px solid #eee; // 底部分隔线
}

// 左侧区域
.lt-content {
    display: flex;
    align-items: center;
    gap: 15px;
}

// 中间名言区域
.center-content {
    flex: 1; // 占满中间剩余空间
    display: flex;
    justify-content: center;
}

// 右侧区域
.rt-content {
    display: flex;
    align-items: center;
    gap: 15px; // 主题按钮/头像 间距
}

// 按钮统一样式
.header-btn {
    border: none; // 去掉默认边框
    background: transparent;
    width: 35px;
    height: 35px;
    border-radius: 8px;
    margin: 0;

    &:hover {
        background-color: #e8f4ff; // 悬浮浅蓝
    }
}


// 名言样式优化
.saying {
    font-size: 20px;
    font-weight: bold;
    font-family: "SiYu";
    letter-spacing: 3px;
    color: #333;
    // margin: 0; // 清除默认margin
    padding: 8px 0;
}

// 面包屑样式
.bread {
    font-size: 14px;
    color: #666;
}

// 头像+用户名容器：优化布局+交互反馈
.avatar-wrapper {
    display: flex; // 横向排列
    align-items: center; // 垂直居中
    gap: 8px; // 头像与用户名间距
    cursor: pointer;
    padding: 6px 10px; // 扩大点击区域
    border-radius: 24px; // 圆角容器，提升柔和感
    transition: all 0.3s ease; // 统一过渡动画
}

// 头像样式：增强质感+动效
.user-avatar {
    border: 2px solid #e8f4ff; // 浅蓝边框（呼应主题）
    box-shadow: 0 2px 6px rgba(14, 121, 255, 0.15); // 淡蓝阴影
    transition: all 0.3s ease;

    // 头像hover动效
    &:hover {
        transform: scale(1.08); // 轻微放大
        border-color: #0E79FF; // 主题色边框
    }

    // 无头像时的首字母样式
    &:not([src]) {
        background-color: #0E79FF; // 主题色背景
        color: #fff; // 白色文字
        font-weight: 600;
    }
}

// 用户名样式
.username-text {
    font-size: 14px;
    font-weight: 500;
    color: #1d2129; // 深灰主色
    max-width: 80px; // 限制宽度，避免过长
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    transition: color 0.3s ease;

    // 用户名hover变色
    &:hover {
        color: #0E79FF; // 主题色强调
    }
}

// 抽屉外层容器
:deep(.custom-drawer) {
    .el-drawer__wrapper {
        .el-drawer {
            border-radius: 16px 0 0 16px; // 右侧抽屉左侧圆角
            box-shadow: -8px 0 24px rgba(0, 0, 0, 0.08); // 柔和阴影
            overflow: hidden;
        }
    }
}

.drawer-container {
    padding: 30px 25px;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-image: url("@/assets/images/background.png");
    background-size: 100% 100%;
    /* 为伪元素定位 */
    position: relative;
    /* 确保内容在遮罩上层 */
    z-index: 1;

    // 取消原有背景图和遮罩，改为简约风格
    &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(255, 255, 255, 0.2);
        z-index: -1;
    }
}

/* 抽屉标题 */
.drawer-title {
    color: #1d2129;
    font-size: 22px;
    margin-bottom: 25px;
    font-weight: 600;
    position: relative;
    padding-bottom: 12px;

    &::after {
        content: "";
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 60px;
        height: 3px;
        background: linear-gradient(90deg, #0E79FF, #409EFF);
        border-radius: 3px;
    }
}

/* 头像+用户名区域 */
.avatar-name-section {
    text-align: center;
    margin-bottom: 30px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

/* 上传区域组合 */
.photo-upload-group {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    margin-bottom: 15px;
}

/* 头像上传组件 */
.avatar-uploader {
    width: 120px;
    height: 120px;
    border-radius: 50%; // 圆形上传框（更符合头像习惯）
    border: 2px dashed #e0e6ed;
    background-color: #f8fafc;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;

    // 上传后显示的头像
    .avatar-img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover; // 避免头像拉伸
    }

    // 未上传时的占位按钮
    .upload-btn-placeholder {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 6px;
        color: #94a3b8;
    }

    .upload-icon {
        font-size: 30px;
    }

    .upload-btn-text {
        font-size: 14px;
    }

    // hover效果
    &:hover {
        border-color: #0E79FF;
        background-color: #f0f7ff;
        color: #0E79FF;
    }
}

/* 用户名样式 */
.drawer-username {
    font-size: 20px;
    font-weight: 600;
    color: #1d2129;
    margin: 0;
    letter-spacing: 0.5px;
}

/* 头像表单项 */
.photo-form-item {
    display: flex;
    justify-content: center;
    margin-bottom: 25px;
    border-bottom: 1px solid #f0f5fa;
    padding: 0 0 20px;
}

/* 表单整体 */
.user-form {
    width: 100%;
    flex: 1;
    padding: 0 15px;

    // 普通表单项
    .el-form-item {
        margin-bottom: 20px;
        width: 100%;

        // 标签样式
        :deep(.el-form-item__label) {
            color: #3e4653;
            font-weight: 500;
            font-size: 14px;
        }

        // 输入框样式
        :deep(.el-input) {
            .el-input__wrapper {
                border-radius: 8px;
                border: 1px solid #e5e7eb;
                box-shadow: none;
                padding: 0 12px;
                height: 42px;

                &:hover {
                    border-color: #c4cdd5;
                }

                &.is-focus {
                    border-color: #0E79FF;
                    box-shadow: 0 0 0 2px rgba(14, 121, 255, 0.1);
                }
            }
        }

        // 单选框样式
        .radio-item {
            margin-right: 20px;
            font-size: 14px;
            color: #4e5969;

            :deep(.el-radio__label) {
                padding-left: 6px;
            }

            :deep(.el-radio__input.is-checked .el-radio__inner) {
                background-color: #0E79FF;
                border-color: #0E79FF;
            }
        }
    }
}

/* 底部按钮组 */
.btn-group {
    width: 100%;
    display: flex;
    gap: 15px;
    margin-top: 30px;
    padding: 0 15px;
}

/* 按钮统一样式 */
.drawer-btn {
    flex: 1;
    padding: 12px 0;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.5px;
    transition: all 0.3s ease;
    border: none !important;
    height: 30px;

    // 编辑按钮
    &.edit-btn {
        background-color: #0E79FF;
        color: #fff;

        &:hover {
            background-color: #0965d9;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(14, 121, 255, 0.2);
        }
    }

    // 保存按钮
    &.save-btn {
        background-color: #00b42a;
        color: #fff;

        &:hover {
            background-color: #00a127;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0, 180, 42, 0.2);
        }
    }

    // 退出按钮
    &.logout-btn {
        background-color: #ff4d4f;
        color: #fff;

        &:hover {
            background-color: #f53f3f;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
        }
    }

    &:active {
        transform: translateY(0);
    }
}

/* 抽屉body样式 */
:deep(.el-drawer__body) {
    padding: 0;
    overflow: auto;
    background-color: #fff;
}

// 退出对话框
:deep(.exit-dialog) {
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    overflow: hidden;

    // 隐藏默认头部
    .el-dialog__header {
        display: none;
    }

    // 自定义头部
    .dialog-header {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px 0 10px;
        border-bottom: 1px solid #f5f7fa;

        .dialog-icon {
            font-size: 24px;
            color: #0E79FF;
            margin-right: 10px;
        }

        .dialog-title {
            font-size: 18px;
            font-weight: 600;
            color: #1D2129;
            margin: 0;
        }
    }

    // 自定义内容区
    .dialog-content {
        padding: 20px 30px;
        text-align: center;

        .content-text {
            font-size: 16px;
            color: #1D2129;
            margin: 0 0 8px;
            font-weight: 500;
        }

        .content-desc {
            font-size: 14px;
            color: #667085;
            margin: 0;
            line-height: 1.5;
        }
    }

    // 自定义底部按钮区
    .dialog-footer {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 16px 30px 20px;
        gap: 16px;
        border-top: 1px solid #f5f7fa;

        .cancel-btn {
            width: 120px;
            height: 40px;
            background-color: #f9fafb;
            color: #344054;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s ease;

            &:hover {
                background-color: #f2f4f7;
                color: #1D2129;
            }
        }

        .confirm-btn {
            width: 120px;
            height: 40px;
            background-color: #0E79FF;
            color: #ffffff;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s ease;

            &:hover {
                background-color: #0965d9;
                transform: translateY(-1px);
            }

            &:active {
                transform: translateY(0);
            }
        }
    }

    // 内容区padding重置
    .el-dialog__body {
        padding: 0;
        margin: 0;
    }

    // 底部区域重置
    .el-dialog__footer {
        padding: 0;
        margin: 0;
        border: none;
    }
}
</style>