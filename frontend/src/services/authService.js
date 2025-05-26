import { auth } from '../api';
import { ElMessage } from 'element-plus';

/**
 * 保存认证信息到本地存储
 * @param {Object} data - 包含token和用户信息的数据
 * @private
 */
function _saveAuthInfo(data) {
  const { token, userName, userId } = data;
  
  // 保存token，包括过期时间
  localStorage.setItem('token', JSON.stringify({
    value: token,
    expire: Date.now() + 24 * 60 * 60 * 1000 // 默认24小时
  }));
  
  if (userName) localStorage.setItem('userName', userName);
  if (userId) localStorage.setItem('userId', userId);
}

/**
 * 用户登录
 * @param {Object} credentials - 登录凭证 {username, password}
 * @returns {Promise} 处理后的登录结果
 */
export async function login(credentials) {
  try {
    const response = await auth.login(credentials);
    
    if (response.data && response.data.code === 1) {
      _saveAuthInfo(response.data.data);
      return response.data.data;
    } else {
      const errorMsg = response.data?.msg || '登录失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '登录时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 用户注册
 * @param {Object} userData - 用户注册数据
 * @returns {Promise} 处理后的注册结果
 */
export async function register(userData) {
  try {
    const response = await auth.register(userData);
    
    if (response.data && response.data.code === 1) {
      ElMessage.success(response.data.msg || '注册成功');
      return response.data;
    } else {
      const errorMsg = response.data?.msg || '注册失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '注册时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 发送验证码
 * @param {string} email - 邮箱地址
 * @returns {Promise} 处理后的结果
 */
export async function sendVerificationCode(email) {
  try {
    const response = await auth.sendVerificationCode(email);
    
    if (response.data && response.data.code === 1) {
      ElMessage.success('验证码已发送');
      return response.data;
    } else {
      const errorMsg = response.data?.msg || '发送验证码失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '发送验证码时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 忘记密码/重置密码
 * @param {Object} data - 重置密码数据
 * @returns {Promise} 处理后的结果
 */
export async function forgotPassword(data) {
  try {
    const response = await auth.forgotPassword(data);
    
    if (response.data && response.data.code === 1) {
      ElMessage.success('密码重置成功');
      return response.data;
    } else {
      const errorMsg = response.data?.msg || '密码重置失败';
      ElMessage.error(errorMsg);
      return Promise.reject(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || '密码重置时发生错误';
    ElMessage.error(errorMsg);
    return Promise.reject(errorMsg);
  }
}

/**
 * 获取微信登录二维码URL
 * @returns {Promise<string>} 二维码URL或Blob
 */
export async function getWxLoginQrCode() {
  try {
    const response = await auth.wxLogin();
    return URL.createObjectURL(response.data);
  } catch (error) {
    ElMessage.error('获取微信登录二维码失败');
    return Promise.reject(error);
  }
}

/**
 * 处理微信登录回调
 * @param {Object} params - 回调参数
 * @returns {Promise} 处理后的结果
 */
export async function handleWxCallback(params) {
  try {
    const response = await auth.wxCallback(params);
    
    if (response.data) {
      // 保存用户信息
      _saveAuthInfo(response.data);
      return response.data;
    } else {
      return Promise.reject('微信登录失败');
    }
  } catch (error) {
    ElMessage.error('微信登录处理失败');
    return Promise.reject(error);
  }
}

/**
 * 注销登录
 */
export function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('userName');
  localStorage.removeItem('userId');
}

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export function isAuthenticated() {
  const tokenStr = localStorage.getItem('token');
  if (!tokenStr) return false;
  
  try {
    const tokenObj = JSON.parse(tokenStr);
    return tokenObj.expire > Date.now();
  } catch (e) {
    return false;
  }
}

export default {
  login,
  register,
  sendVerificationCode,
  forgotPassword,
  getWxLoginQrCode,
  handleWxCallback,
  logout,
  isAuthenticated
}; 