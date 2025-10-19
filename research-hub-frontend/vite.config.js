import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [vue()],
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
          // 确保不重写 /api 路径，因为后端接口都有 /api 前缀
          // rewrite: (path) => path // 保持原路径
        }
      }
    },
    // 环境变量支持
    define: {
      '__VUE_PROD_DEVTOOLS__': false,
      '__VUE_OPTIONS_API__': true,
      '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': false
    }
  }
})