import { image } from '../api';
import { ElMessage } from 'element-plus';

/**
 * 文本生成图片
 * @param {Object} params - 文生图参数
 * @returns {Promise<Array<string>>} 图片ID列表
 */
export async function textToImage(params) {
  try {
    // 参数验证
    if (!params.prompt) {
      ElMessage.warning('提示词不能为空');
      return Promise.reject('提示词不能为空');
    }
    
    // 设置默认值
    const requestParams = {
      samples: 1,
      width: 1024,
      height: 1024,
      steps: 30,
      cfgScale: 7.0,
      ...params
    };
    
    const response = await image.textToImage(requestParams);
    
    if (response.data && response.data.code === 1) {
      return response.data.data || [];
    } else {
      const errorMsg = response.data?.msg || '生成图片失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '生成图片时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 图像生成图片
 * @param {Object} params - 图生图参数
 * @returns {Promise<Object>} 生成结果
 */
export async function imageToImage(params) {
  try {
    // 参数验证
    if (!params.sourceImageUrl) {
      ElMessage.warning('源图像不能为空');
      return Promise.reject('源图像不能为空');
    }
    
    if (!params.prompt) {
      ElMessage.warning('提示词不能为空');
      return Promise.reject('提示词不能为空');
    }
    
    // 处理风格参数 - 如果有风格，添加到提示词中
    let prompt = params.prompt;
    if (params.style) {
      prompt = `${prompt}, ${params.style} style`;
    }
    
    // 设置默认值并映射参数名称
    const requestParams = {
      req_key: "i2i_portrait_photo", // 固定值，与后端一致
      image_input: params.sourceImageUrl, // 映射sourceImageUrl到imageInput
      prompt: prompt, // 包含风格的提示词
      width: params.width || 1024,
      height: params.height || 1024,
      // 其他可能需要的参数
      gpen: params.gpen || 0.4,
      skin: params.skin || 0.3,
      skin_unifi: params.skinUnifi || 0,
      gen_mode: params.genMode || 'creative',
      seed: params.seed || '-1'
    };
    
    console.log('发送到后端的参数:', requestParams);
    
    const response = await image.imageToImage(requestParams);
    
    if (response.data && response.data.code === 1) {
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '图生图失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '图生图时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 线稿生成图片
 * @param {Object} params - 线稿生图参数
 * @returns {Promise<Array<string>>} 图片ID列表
 */
export async function sketchToImage(params) {
  try {
    // 参数验证
    if (!params.sketchImageUrl) {
      ElMessage.warning('线稿图片不能为空');
      return Promise.reject('线稿图片不能为空');
    }
    
    const response = await image.sketchToImage(params);
    
    if (response.data && response.data.code === 1) {
      return response.data.data || [];
    } else {
      const errorMsg = response.data?.msg || '线稿生图失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '线稿生图时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 图片融合
 * @param {Object} params - 融合参数
 * @returns {Promise<Object>} 融合结果，包含jobId
 */
export async function imageFusion(params) {
  try {
    // 参数验证
    if (!params.sourceImageUrl || !params.targetImageUrl) {
      ElMessage.warning('源图片和目标图片都不能为空');
      return Promise.reject('源图片和目标图片都不能为空');
    }
    
    const response = await image.imageFusion(params);
    
    if (response.data && response.data.code === 1) {
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '图片融合失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '图片融合时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 获取融合结果
 * @param {string} jobId - 任务ID
 * @returns {Promise<Object>} 融合结果
 */
export async function getFusionResult(jobId) {
  try {
    const response = await image.getFusionResult(jobId);
    
    if (response.data && response.data.code === 1) {
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '获取融合结果失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '获取融合结果时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 获取图片URL
 * @param {string} imageId - 图片ID
 * @returns {Promise<string>} 图片URL
 */
export async function getImageUrl(imageId) {
  try {
    const response = await image.getImageUrl(imageId);
    
    if (response.data && response.data.code === 1) {
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '获取图片URL失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '获取图片URL时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 上传图片
 * @param {File} file - 图片文件
 * @returns {Promise<string>} 上传后的图片URL
 */
export async function uploadImage(file) {
  try {
    // 验证文件类型
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      ElMessage.warning('请上传 JPG, PNG, GIF 或 WEBP 格式的图片');
      return Promise.reject('不支持的文件类型');
    }
    
    // 验证文件大小（最大10MB）
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过10MB');
      return Promise.reject('文件过大');
    }
    
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await image.uploadImage(formData);
    
    if (response.data && response.data.code === 1) {
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '上传图片失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '上传图片时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

export default {
  textToImage,
  imageToImage,
  sketchToImage,
  imageFusion,
  getFusionResult,
  getImageUrl,
  uploadImage
}; 