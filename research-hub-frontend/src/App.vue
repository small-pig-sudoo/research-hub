<template>
  <!-- 所有页面都放在统一布局里，登录页会自动隐藏侧边栏 -->
  <Layout />
</template>

<script setup>
import Layout from './components/Layout.vue'
</script>

<style>
/* 可选：全局背景色 */
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, 'Noto Sans', sans-serif;
}
</style>
