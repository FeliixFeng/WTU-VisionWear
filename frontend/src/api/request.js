import axios from 'axios';
import { getValidToken } from "../utils/auth.js";
import { handleApiError } from "../utils/errorHandler.js";
import router from '../router'; // 导入路由实例

// 封装 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    // baseURL: 'http://8.155.5.178:8080/api',
    timeout: 1000 * 30,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器，动态获取最新的token
request.interceptors.request.use(config => {
    const token = getValidToken();
    
    // 如果有token则添加到请求头
    if (token) {
        config.headers.token = token;
    } else {
        // 清除可能存在的旧token
        delete config.headers.token;
        
        // 如果是需要认证的接口，可以在这里进行路由拦截
        const noAuthRequired = [
            '/auth/login',
            '/auth/register', 
            '/auth/forgot-password',
            '/auth/send-verification-code'
        ];
        
        const isAuthRequired = !noAuthRequired.some(path => config.url.includes(path));
        
        if (isAuthRequired) {
            // 如果当前不在登录页，自动跳转到登录页
            const currentPath = router.currentRoute.value.path;
            if (currentPath !== '/login' && currentPath !== '/register') {
                router.push('/login');
                return Promise.reject(new Error('请先登录'));
            }
        }
    }
    
    return config;
}, error => {
    return Promise.reject(error);
});

// 响应拦截器
request.interceptors.response.use(
    response => {
        // 统一处理业务码
        const { data } = response;
        
        // 特定业务码处理
        if (data && data.code !== undefined) {
            // 如果是未登录状态
            if (data.code === 401 || data.code === -2) {
                // 清除本地存储的认证信息
                localStorage.removeItem('token');
                localStorage.removeItem('userName');
                localStorage.removeItem('userId');
                
                // 跳转到登录页
                router.push('/login');
                return Promise.reject(new Error('登录已过期，请重新登录'));
            }
        }
        
        // 正常返回响应
        return response;
    },
    error => {
        // 使用统一的错误处理工具处理错误
        // 但不在拦截器中显示错误消息，而是返回错误让调用处决定如何显示
        // 这样调用代码可以提供上下文相关的错误信息
        
        // 对于401错误，自动重定向到登录页
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            // 清除本地存储的认证信息
            localStorage.removeItem('token');
            localStorage.removeItem('userName');
            localStorage.removeItem('userId');
            
            // 跳转到登录页，带上原始访问路径用于登录后重定向
            const currentPath = router.currentRoute.value.fullPath;
            router.push(`/login?redirect=${encodeURIComponent(currentPath)}`);
        }
        
        // 返回错误给调用方处理
        return Promise.reject(error);
    }
);

export default request; 