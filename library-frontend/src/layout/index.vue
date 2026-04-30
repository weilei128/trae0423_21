<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon size="28"><Reading /></el-icon>
        <span class="logo-text">图书馆管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        :collapse="false"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/books">
          <el-icon><Reading /></el-icon>
          <span>书目管理</span>
        </el-menu-item>
        <el-menu-item index="/collections">
          <el-icon><Box /></el-icon>
          <span>馆藏管理</span>
        </el-menu-item>
        <el-menu-item index="/readers">
          <el-icon><User /></el-icon>
          <span>读者管理</span>
        </el-menu-item>
        <el-menu-item index="/borrow">
          <el-icon><Tickets /></el-icon>
          <span>借阅服务</span>
        </el-menu-item>
        <el-menu-item index="/reservation">
          <el-icon><Calendar /></el-icon>
          <span>预约续借</span>
        </el-menu-item>
        <el-menu-item index="/fine">
          <el-icon><Money /></el-icon>
          <span>逾期罚款</span>
        </el-menu-item>
        <el-menu-item index="/stats">
          <el-icon><TrendCharts /></el-icon>
          <span>统计报表</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-title">{{ currentTitle }}</div>
        <div class="header-right">
          <el-text class="welcome">欢迎，管理员</el-text>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const matched = route.matched[0]?.children?.find(r => r.path === route.path.substring(1))
  return matched?.meta?.title || '图书馆管理系统'
})
</script>

<style scoped>
.layout-container {
  height: 100%;
}

.aside {
  background-color: #304156;
  height: 100%;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #3a4a5c;
}

.logo-text {
  margin-left: 10px;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.welcome {
  color: #606266;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
