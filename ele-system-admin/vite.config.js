import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite' // 新增loadEnv：读取环境变量
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // 加载当前环境的环境变量（适配.env.development/.env.production）
  const env = loadEnv(mode, process.cwd())

  // 定义前端运行端口：优先级「环境变量port」> 81（和原vue.config.js一致）
  const port = process.env.port || process.env.npm_config_port || 9999

  return {
    // 1. 插件配置（保留你原有配置，Element Plus样式定制）
    plugins: [
      vue(),
      vueDevTools(),
    ],

    // 2. 路径解析（保留原有@别名）
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    },


    // 4. 开发服务器配置
    server: {
      host: '0.0.0.0', // 允许局域网访问
      port: port, // 前端运行端口
      open: true, // 启动后自动打开浏览器
      proxy: {
        // 接口代理：对应原vue.config.js的proxy，适配Vite写法
        [env.VITE_BASE_API]: { // 注意：Vite环境变量前缀是VITE_（替代原VUE_APP_）
          target: 'http://localhost:6666', // 后端接口地址+端口
          changeOrigin: true, // 开启跨域（和原配置一致）
          rewrite: (path) => {
            const rewritten = path.replace(new RegExp(`^${env.VITE_BASE_API}`), '');
            console.log('原路径：', path, '重写后：', rewritten); // 检查是否正确移除前缀
            return rewritten;
          }
        }

      },
    },

    // 5. 打包配置（对应原vue.config.js的outputDir/assetsDir等）
    build: {
      outDir: 'dist', // 打包输出目录（对应原outputDir，默认dist）
      assetsDir: 'static', // 静态资源子目录（对应原assetsDir，默认assets，改为static）
      sourcemap: false, // 生产环境关闭sourcemap（对应原productionSourceMap）
      chunkSizeWarningLimit: 1500, // 分包大小警告阈值（可选优化）
      // 代码分割（对标原vue.config.js的chainWebpack代码分割）
      rollupOptions: {
        output: {
          // 分包规则：拆分第三方库、公共组件
          manualChunks(id) {
            if (id.includes('node_modules')) {
              // 把node_modules拆分成单独chunk
              return id.toString().split('node_modules/')[1].split('/')[0].toString()
            }
            // 可自定义更多拆分规则（比如Element Plus单独拆分）
            if (id.includes('element-plus')) {
              return 'chunk-elementPlus'
            }
          }
        }
      }
    },

    // 6. 环境变量配置（可选：自定义环境变量前缀，默认VITE_）
    envPrefix: 'VITE_',

  }
})