import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/common/HomePage.vue';
import FashionMain from '../views/fashion/FashionMain.vue'
import FashionOption1 from '../views/fashion/TextToImagePage.vue'
import ImageFusion from '../views/fashion/ImageFusionPage.vue'
import FashionOption3 from '../views/fashion/Fashionoption3.vue'
import RenderPage from '../views/design/RenderPage.vue'
import MaterialPage from '../views/design/MaterialPage.vue'
import DesignPage from '../views/design/DesignPage.vue'
import ImageProcessingPage from '../views/image-processing/ImageProcessingPage.vue'
import SketchToImagePage from '../views/image-processing/SketchToImagePage.vue'
import PartialRedrawPage from '../views/image-processing/PartialRedrawPage.vue'
import StyleTransferPage from '../views/image-processing/StyleTransferPage.vue'
import StyleExtensionPage from '../views/image-processing/StyleExtensionPage.vue'
import IntroPage from "../views/common/IntroPage.vue";
import MyPicture from '../views/user/MyPicture.vue';
import {ElMessage} from "element-plus";
import { getValidToken } from '../utils/auth' // 注意路径是否正确
const routes = [

    { path: '/', component: IntroPage, name: 'Intro' }, // 访问根路径时显示介绍页
    { path: '/:pathMatch(.*)*', redirect: '/' }, // 捕获所有未知路径，重定向到介绍页面
    { 
      path: '/home', 
      component: HomePage,
        meta: { requiresAuth: true },
      name: 'Home',
      children: [
        { path: '', component: RenderPage }, // 默认子页面
        { 
          path: 'fashion', 
          component: FashionMain,
          children: [
            { path: '', redirect: 'option1' } // 默认重定向到 option1
          ]
        },
        { path: 'fashion/option1', component: FashionOption1 },
        { path: 'fashion/option2', component: ImageFusion },
        { path: 'fashion/option3', component: FashionOption3 },
        { path: 'render', component: RenderPage },
        { path: 'material', component: MaterialPage },
        { path: 'design', component: DesignPage },
        { path: 'image-processing', component: ImageProcessingPage },
        { path: 'my-pictures', component: MyPicture },
        { path: 'sketch', component: SketchToImagePage },
        { path: 'redraw', component: PartialRedrawPage },
        { path: 'style-transfer', component: StyleTransferPage },
        { path: 'style-extension', component: StyleExtensionPage }
      ]
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    console.log('进入守卫，目标路径：', to.fullPath);

    const token = getValidToken();
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

    console.log('是否需要登录校验：', requiresAuth, '当前 token：', token);

    if (requiresAuth) {
        if (token) {
            console.log('有 token，放行');
            next();
        } else {
            console.log('无 token，跳转 /');
            ElMessage.warning('请先登录');
            next('/');
        }
    } else {
        console.log('不需要登录，直接放行');
        next();
    }
});



export default router;
