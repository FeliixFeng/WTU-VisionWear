import { defineStore } from 'pinia';
import { userService } from '../../services';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    userImages: [],
    loading: false
  }),
  
  getters: {
    // 用户是否已加载信息
    hasUserInfo: (state) => !!state.userInfo,
    
    // 用户图片数量
    imageCount: (state) => state.userImages.length
  },
  
  actions: {
    // 获取用户信息
    async fetchUserInfo() {
      if (this.userInfo) return this.userInfo;
      
      this.loading = true;
      try {
        const userInfo = await userService.getUserInfo();
        this.userInfo = userInfo;
        return userInfo;
      } catch (error) {
        this.userInfo = null;
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 更新用户信息
    async updateUserInfo(userInfo) {
      this.loading = true;
      try {
        const updatedInfo = await userService.updateUserInfo(userInfo);
        this.userInfo = updatedInfo;
        return updatedInfo;
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 修改密码
    async changePassword(passwordData) {
      this.loading = true;
      try {
        return await userService.changePassword(passwordData);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 获取用户所有图片
    async fetchUserImages() {
      this.loading = true;
      try {
        const images = await userService.getAllUserImages();
        this.userImages = images;
        return images;
      } catch (error) {
        this.userImages = [];
        return [];
      } finally {
        this.loading = false;
      }
    },
    
    // 清除用户状态
    clearUserState() {
      this.userInfo = null;
      this.userImages = [];
    }
  }
}); 