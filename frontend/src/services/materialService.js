import { material } from '../api';
import { ElMessage } from 'element-plus';

/**
 * 获取素材库图片
 * @returns {Promise<Array<string>>} 素材图片URL列表
 */
export async function getMaterials() {
  try {
    const response = await material.getMaterials();
    
    if (response.data && response.data.code === 1) {
      return response.data.data || [];
    } else {
      const errorMsg = response.data?.msg || '获取素材库图片失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '获取素材库图片时发生错误';
    ElMessage.error(errorMsg);
    return [];
  }
}

export default {
  getMaterials
}; 