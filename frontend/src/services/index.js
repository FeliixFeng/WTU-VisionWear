// 导入服务
import authService from './authService';
import userService from './userService';
import imageService from './imageService';
import materialService from './materialService';

// 导出服务
export {
  authService,
  userService,
  imageService,
  materialService
};

// 默认导出
export default {
  auth: authService,
  user: userService,
  image: imageService,
  material: materialService
}; 