import { defineStore } from 'pinia';
import { authService } from '../../services';
import { ElMessage } from 'element-plus';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    userId: null,
    userName: null,
    isAuthenticated: false,
    loading: false
  }),
  
  getters: {
    // 获取用户信息
    userInfo: (state) => ({
      userId: state.userId,
      userName: state.userName
    })
  },
  
  actions: {
    // 初始化认证状态
    initAuth() {
      // 从本地存储获取信息
      const tokenStr = localStorage.getItem('token');
      if (tokenStr) {
        try {
          const tokenObj = JSON.parse(tokenStr);
          if (tokenObj.expire > Date.now()) {
            this.token = tokenObj.value;
            this.userId = localStorage.getItem('userId');
            this.userName = localStorage.getItem('userName');
            this.isAuthenticated = true;
          } else {
            // 过期则清除
            this.clearAuth();
          }
        } catch (e) {
          this.clearAuth();
        }
      }
    },
    
    // 登录
    async login(credentials) {
      this.loading = true;
      try {
        const data = await authService.login(credentials);
        this.token = data.token;
        this.userId = data.userId;
        this.userName = data.userName;
        this.isAuthenticated = true;
        ElMessage.success('登录成功');
        return data;
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 注册
    async register(userData) {
      this.loading = true;
      try {
        return await authService.register(userData);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 忘记密码
    async forgotPassword(data) {
      this.loading = true;
      try {
        return await authService.forgotPassword(data);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 发送验证码
    async sendVerificationCode(email) {
      this.loading = true;
      try {
        return await authService.sendVerificationCode(email);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 微信登录
    async wxLogin() {
      this.loading = true;
      try {
        return await authService.getWxLoginQrCode();
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 处理微信回调
    async handleWxCallback(params) {
      this.loading = true;
      try {
        const data = await authService.handleWxCallback(params);
        this.token = data.token;
        this.userId = data.userId;
        this.userName = data.userName;
        this.isAuthenticated = true;
        ElMessage.success('微信登录成功');
        return data;
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 注销
    logout() {
      authService.logout();
      this.clearAuth();
      ElMessage.success('已退出登录');
      
      // 跳转到登录页
      if (this.router) {
        this.router.push('/');
      }
    },
    
    // 清除认证状态
    clearAuth() {
      this.token = null;
      this.userId = null;
      this.userName = null;
      this.isAuthenticated = false;
    }
  }
}); 