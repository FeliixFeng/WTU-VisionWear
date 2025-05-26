import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import pinia from './store'; // 导入Pinia实例

// 引入 Element Plus 和样式
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

// 引入 axios 和 API模块
import axios from 'axios';
import { request } from './api'; // 导入封装的request实例和API模块

// 创建 Vue 实例并挂载
const app = createApp(App);

// 使用 Element Plus
app.use(ElementPlus);

// 使用路由
app.use(router);

// 使用Pinia
app.use(pinia);

// 将 axios 和 request 注入到全局
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$request = request;

app.mount('#app');

export default request; // 保持兼容性，继续导出request