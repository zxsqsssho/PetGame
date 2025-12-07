// 此配置把以 /api 开头的请求代理到 http://localhost:8080/PetGame`，开发时直接请求 /api/...` 即可
// 先在项目根目录执行（shell 命令，确保重启 dev server）
// npm install -D @vitejs/plugin-vue
// npm install vue@3

// file: `vite.config.js`
// file: vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
    plugins: [vue()],

    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')   // ← 加上这个
        }
    },

    server: {
        proxy: {
            "/api": {
                target: "http://localhost:8080/PetGameBackend",
                changeOrigin: true,
                rewrite: (p) => p.replace(/^\/api/, '/api') // 更稳妥
            }
        }
    }
})




