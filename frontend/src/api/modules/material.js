import request from '../request';

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
  getMaterials
}; 