import { defineStore } from 'pinia';
import { imageService } from '../../services';

export const useImageStore = defineStore('image', {
  state: () => ({
    generatedImages: [],  // 生成的图片ID列表
    imageUrls: {},        // 图片ID到URL的映射
    currentOperation: '', // 当前操作类型
    loading: false
  }),
  
  getters: {
    // 获取指定ID的图片URL
    getImageUrlById: (state) => (id) => state.imageUrls[id]
  },
  
  actions: {
    // 文生图
    async generateFromText(params) {
      this.loading = true;
      this.currentOperation = 'text-to-image';
      try {
        const imageIds = await imageService.textToImage(params);
        if (imageIds && imageIds.length > 0) {
          this.generatedImages = [...this.generatedImages, ...imageIds];
        }
        return imageIds;
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
        this.currentOperation = '';
      }
    },
    
    // 图生图
    async generateFromImage(params) {
      this.loading = true;
      this.currentOperation = 'image-to-image';
      try {
        const result = await imageService.imageToImage(params);
        if (result && result.images) {
          const imageIds = result.images.map(img => img.imageId);
          this.generatedImages = [...this.generatedImages, ...imageIds.filter(id => id)];
        }
        return result;
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
        this.currentOperation = '';
      }
    },
    
    // 线稿生图
    async generateFromSketch(params) {
      this.loading = true;
      this.currentOperation = 'sketch-to-image';
      try {
        const imageIds = await imageService.sketchToImage(params);
        if (imageIds && imageIds.length > 0) {
          this.generatedImages = [...this.generatedImages, ...imageIds];
        }
        return imageIds;
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
        this.currentOperation = '';
      }
    },
    
    // 图片融合
    async fuseImages(params) {
      this.loading = true;
      this.currentOperation = 'image-fusion';
      try {
        return await imageService.imageFusion(params);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
        this.currentOperation = '';
      }
    },
    
    // 获取融合结果
    async getFusionResult(jobId) {
      this.loading = true;
      try {
        const result = await imageService.getFusionResult(jobId);
        if (result && result.imageId) {
          this.generatedImages.push(result.imageId);
        }
        return result;
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
      }
    },
    
    // 获取图片URL
    async fetchImageUrl(imageId) {
      // 如果已经缓存了URL，直接返回
      if (this.imageUrls[imageId]) {
        return this.imageUrls[imageId];
      }
      
      try {
        const url = await imageService.getImageUrl(imageId);
        // 缓存URL
        this.imageUrls[imageId] = url;
        return url;
      } catch (error) {
        return Promise.reject(error);
      }
    },
    
    // 上传图片
    async uploadImage(file) {
      this.loading = true;
      this.currentOperation = 'upload';
      try {
        return await imageService.uploadImage(file);
      } catch (error) {
        return Promise.reject(error);
      } finally {
        this.loading = false;
        this.currentOperation = '';
      }
    },
    
    // 清除生成的图片
    clearGeneratedImages() {
      this.generatedImages = [];
      this.imageUrls = {};
    }
  }
}); 