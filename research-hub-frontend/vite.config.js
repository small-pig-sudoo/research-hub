import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  // ���ػ�������
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
          // ȷ������д /api ·������Ϊ��˽ӿڶ��� /api ǰ׺
          // rewrite: (path) => path // ����ԭ·��
        }
      }
    },
    // ��������֧��
    define: {
      '__VUE_PROD_DEVTOOLS__': false,
      '__VUE_OPTIONS_API__': true,
      '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': false
    }
  }
})