<template>

    <el-container class="lay-container">

        <common-aside />

        <el-container>
            <el-header class="lay-header">
                <common-header @refresh-main="refreshMainContent"></common-header>
            </el-header>

            <el-main class="lay-main">
                <router-view></router-view>
            </el-main>

        </el-container>


    </el-container>


</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import {ElMessage} from 'element-plus'
import CommonAside from '@/components/CommonAside.vue';
import CommonHeader from '@/components/CommonHeader.vue';

const router = useRouter()

// 刷新方法：跳转当前路由
const refreshMainContent = () => {
  const currentRoute = router.currentRoute.value
  // 跳转到当前路由（会触发router-view重新渲染）
  router.push({
    path: currentRoute.path,
  })
  ElMessage.success('主内容已刷新')
}

</script>

<style scoped lang="less">

.lay-container {
    height: 98.3vh;

    display: flex;
    overflow: hidden; // 关键：隐藏溢出内容，避免滚动条
}

.lay-header,
.lay-main {
    --el-header-padding: 0 !important;
    padding: 0 !important; // 兜底清除padding
}

</style>
