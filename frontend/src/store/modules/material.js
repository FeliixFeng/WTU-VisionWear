import { defineStore } from 'pinia';
import { materialService } from '../../services';

export const useMaterialStore = defineStore('material', {
  state: () => ({
    materials: [],   // 素材库图片列表
    loading: false
  }),
  
  getters: {
    // 素材数量
    materialCount: (state) => state.materials.length
  },
  
  actions: {
    // 获取素材库图片
    async fetchMaterials() {
      this.loading = true;
      try {
        const materials = await materialService.getMaterials();
        this.materials = materials;
        return materials;
      } catch (error) {
        this.materials = [];
        return [];
      } finally {
        this.loading = false;
      }
    }
  }
}); 