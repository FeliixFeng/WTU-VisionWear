import { createPinia } from 'pinia';
import { markRaw } from 'vue';
import router from '../router';

// 创建pinia实例
const pinia = createPinia();

// 使用插件为所有store提供路由器
pinia.use(({ store }) => {
  store.router = markRaw(router);
});

export default pinia;
