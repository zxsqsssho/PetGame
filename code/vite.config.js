// 此配置把以 /api 开头的请求代理到 http://localhost:8080/PetGame`，开发时直接请求 /api/...` 即可
// 先在项目根目录执行（shell 命令，确保重启 dev server）
// npm install -D @vitejs/plugin-vue
// npm install vue@3

// file: `vite.config.js`
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()], // <-- 注册 .vue 处理插件
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '/PetGame/api')
            }
        }
    }
})

