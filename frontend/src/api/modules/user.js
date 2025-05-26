import request from '../request';

/**
 * 获取用户信息
 * @returns {Promise} - 请求结果
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  });
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 * @returns {Promise} - 请求结果
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  });
}

/**
 * 修改密码
 * @param {Object} data - {oldPassword, newPassword}
 * @returns {Promise} - 请求结果
 */
export function changePassword(data) {
  return request({
    url: '/user/change-password',
    method: 'post',
    data
  });
}

/**
 * 获取用户所有图片
 * @returns {Promise} - 返回用户所有图片URL列表
 */
export function getAllUserImages() {
  return request({
    url: '/user/getAllImage',
    method: 'get'
  });
}

/**
 * 获取素材库图片
 * @returns {Promise} - 返回素材库图片URL列表
 */
export function getMaterials() {
  return request({
    url: '/user/getMaterial',
    method: 'get'
  });
}

export default {
  getUserInfo,
  updateUserInfo,
  changePassword,
  getAllUserImages,
  getMaterials
}; 