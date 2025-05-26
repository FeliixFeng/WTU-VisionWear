import request from '../request';

/**
 * 用户登录
 * @param {Object} data - 登录信息 {username, password}
 * @returns {Promise} - 请求结果
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  });
}

/**
 * 用户注册
 * @param {Object} data - 注册信息 {username, password, email}
 * @returns {Promise} - 请求结果
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  });
}

/**
 * 忘记密码
 * @param {Object} data - 请求数据 {email, verificationCode?}
 * @returns {Promise} - 请求结果
 */
export function forgotPassword(data) {
  return request({
    url: '/auth/forgot-password',
    method: 'post',
    data
  });
}

/**
 * 发送验证码
 * @param {string} email - 用户邮箱
 * @returns {Promise} - 请求结果
 */
export function sendVerificationCode(email) {
  return request({
    url: '/auth/send-verification-code',
    method: 'post',
    data: { email }
  });
}

/**
 * 微信登录校验
 * @param {Object} params - 微信校验参数
 * @returns {Promise} - 请求结果
 */
export function wxCheck(params) {
  return request({
    url: '/auth/wxCheck',
    method: 'get',
    params
  });
}

/**
 * 获取微信登录二维码
 * @returns {Promise} - 请求结果
 */
export function wxLogin() {
  return request({
    url: '/auth/wxLogin',
    method: 'get',
    responseType: 'blob' // 返回二维码图片
  });
}

/**
 * 微信回调处理
 * @param {Object} params - 回调参数 {code, state}
 * @returns {Promise} - 请求结果
 */
export function wxCallback(params) {
  return request({
    url: '/auth/wxCallback',
    method: 'get',
    params
  });
}

export default {
  login,
  register,
  forgotPassword,
  sendVerificationCode,
  wxCheck,
  wxLogin,
  wxCallback
}; 