<template>
  <el-aside :width="width" class="custom-aside">
    <!-- 美化后的退出登录确认对话框 -->
    <el-dialog
      v-model="showExitDialog"
      title=""
      width="500px"
      center
      :show-close="false"
      class="exit-dialog"
    >
      <!-- 自定义弹窗头部 -->
      <div class="dialog-header">
        <el-icon class="dialog-icon"><SwitchButton /></el-icon>
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
          <el-button 
            class="cancel-btn" 
            @click="showExitDialog = false"
            :border="false"
          >
            取消
          </el-button>
          <el-button 
            class="confirm-btn" 
            type="primary" 
            @click="confirmExit"
            :border="false"
            size="default"
          >
            确认退出
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-menu 
      :default-active="currentRoutePath" 
      class="el-menu-vertical custom-menu" 
      :collapse="isCollapse" 
      @select="handleMenuSelect"  
      collapse-transition
    >
      <div class="sidebar-title">
        <img src="@/assets/images/logo.png" alt="logo" width="40px" height="40px">
        <h5 v-show="!isCollapse" class="logo-text">员工数据平台</h5>
      </div>

      <el-menu-item v-for="item in list" :index="item.path" :key="item.path">
        <div class="div-icon">
          <component class="icons" :is="item.icon"></component>
        </div>
        <span class="labels">{{ item.label }}</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAllDataStore } from '@/stores/counter'
import { logout } from '@/api/userInfo' 
import { ElMessage } from 'element-plus'
// 导入Element Plus图标（必须）
import { 
  Notification, User, Discount, Money 
} from '@element-plus/icons-vue'

// 定义菜单项类型
interface MenuItem {
  path: string
  name: string
  label: string
  icon: string
  url: string
}

const route = useRoute()
const router = useRouter()

// 控制退出确认对话框显示/隐藏
const showExitDialog = ref<boolean>(false)

// 实时获取当前路由path
const currentRoutePath = computed<string>(() => route.path)

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

// 菜单选中事件（手动处理路由跳转/自定义事件）
const handleMenuSelect = (key: string) => {
  // 找到选中的菜单项
  const menuItem = list.value.find((item: MenuItem) => item.path === key)
  if (!menuItem) return

  // 如果是退出登录，仅打开确认对话框
  if (menuItem.name === 'exit') {
    showExitDialog.value = true
    return
  }

  // 其他菜单项：手动路由跳转
  router.push(key)
}

const store = useAllDataStore()
const isCollapse = computed<boolean>(() => store.state.isCollapse)
const width = computed<string>(() => store.state.isCollapse ? '65px' : '205px')

// 菜单列表（添加类型约束）
const list = ref<MenuItem[]>([
  {
    path: '/home',
    name: 'home',
    label: '平台首页',
    icon: 'Notification',
    url: 'Announcement'
  },
  {
    path: '/employee',
    name: 'employee',
    label: '员工管理',
    icon: 'User',
    url: 'Employee'
  },
  {
    path: '/department',
    name: 'department',
    label: '部门管理',
    icon: 'Discount',
    url: 'Department'
  },
  {
    path: '/salary',
    name: 'salary',
    label: '薪资管理',
    icon: 'Money',
    url: 'Salary'
  },
  {
    path: '/exit',  // 仅作为标识，无需配置路由
    name: 'exit',
    label: '退出登录',
    icon: 'SwitchButton',
    url: 'Exit'
  }
])
</script>

<style lang="less" scoped>
// 侧边栏整体样式
.custom-aside {
  background-color: #FCFAFC;
  height: 100%;
  box-sizing: border-box;
  padding-top: 0;
  transition: width 0.5s ease-in-out;

  // 标题 及 logo 样式
  .sidebar-title {
    height: 60px;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: all 0.5s ease-in-out;

    .logo-text {
      font-size: 16px;
      font-weight: 600;
      color: #165DFF;
      letter-spacing: 1.5px;
      margin-left: 10px;
      transition: opacity 0.2s ease-in-out;
      opacity: 1;
      width: auto;
      white-space: nowrap;
    }
  }
}

// 图标样式
.div-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 35px;
  height: 35px;
  border-radius: 6px;
  margin-right: 10px;
  transition: margin-right 0.3s ease-in-out;

  .icons {
    width: 25px;
    height: 25px;
    color: #667085;
    transition: all 0.3s ease-in-out;
  }
}

// 菜单文字样式
.labels {
  font-size: 16px;
  color: #344054;
}

.custom-menu {
  background-color: transparent;
  border-right: none;

  // 菜单折叠时样式
  &.el-menu--collapse {
    :deep(.el-menu-item) { 
      justify-content: center;
      padding: 0;

      .div-icon {
        margin-right: 0;
        margin: 0;
      }
    }
  }

  // 菜单项基础样式
  .el-menu-item {
    line-height: 50px;
    padding: 0;
    border-radius: 8px;
    margin: 4px 8px;
    transition: all 0.5s ease;

    // 鼠标悬浮态
    &:hover {
      background-color: #F4F1F4;
    }

    // 选中态
    &.is-active {
      background-color: #ffffff;
      box-shadow: 3px 3px 15px rgba(0, 0, 0, 0.08);

      .div-icon {
        background-color: #0E79FF;
        .icons {
          color: #ffffff;
        }
      }

      .labels {
        color: #0E79FF;
        font-weight: 800;
      }
    }
  }
}

// ========== 美化后的退出对话框样式 ==========
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

// 抽屉样式穿透调整
:deep(.el-drawer__body) {
  padding: 0;
  overflow: auto;
}
</style>