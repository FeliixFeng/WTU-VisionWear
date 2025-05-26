import { user } from '../api';
import { ElMessage } from 'element-plus';

/**
 * 获取用户所有图片
 * @returns {Promise<Array>} 图片URL列表
 */
export async function getAllUserImages() {
  try {
    const response = await user.getAllUserImages();
    
    if (response.data && response.data.code === 1) {
      return response.data.data || [];
    } else {
      // 对于"没有找到图片"的错误不显示提示
      if (response.data?.msg !== '没有找到图片!') {
        ElMessage.error(response.data?.msg || '获取图片失败');
      }
      return [];
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '获取图片时发生错误';
    ElMessage.error(errorMsg);
    return [];
  }
}

/**
 * 获取用户信息
 * @returns {Promise<Object>} 用户信息
 */
export async function getUserInfo() {
  try {
    const response = await user.getUserInfo();
    
    if (response.data && response.data.code === 1) {
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '获取用户信息失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    // 如果API尚未实现，则从localStorage获取基本信息
    const userId = localStorage.getItem('userId');
    const userName = localStorage.getItem('userName');
    
    if (userId && userName) {
      return { userId, userName };
    }
    
    const errorMsg = error.response?.data?.msg || '获取用户信息时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 更新用户信息
 * @param {Object} userInfo - 用户信息对象
 * @returns {Promise<Object>} 更新后的用户信息
 */
export async function updateUserInfo(userInfo) {
  try {
    const response = await user.updateUserInfo(userInfo);
    
    if (response.data && response.data.code === 1) {
      ElMessage.success('用户信息更新成功');
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '更新用户信息失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '更新用户信息时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 修改密码
 * @param {Object} passwordData - 密码数据 {oldPassword, newPassword}
 * @returns {Promise} 操作结果
 */
export async function changePassword(passwordData) {
  try {
    const response = await user.changePassword(passwordData);
    
    if (response.data && response.data.code === 1) {
      ElMessage.success('密码修改成功');
      return response.data;
    } else {
      const errorMsg = response.data?.msg || '修改密码失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '修改密码时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

export default {
  getAllUserImages,
  getUserInfo,
  updateUserInfo,
  changePassword
}; 