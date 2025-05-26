// 导入请求实例
import request from './request';

// 导入API模块
import auth from './modules/auth';
import user from './modules/user';
import image from './modules/image';
import material from './modules/material';

// 导出请求实例和API模块
export {
  request,
  auth,
  user,
  image,
  material
};

// 默认导出
export default {
  request,
  auth,
  user,
  image,
  material
};
