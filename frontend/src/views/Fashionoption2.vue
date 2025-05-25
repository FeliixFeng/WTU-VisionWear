<script setup>
import drag from './drag.vue';
import { ref, onMounted } from 'vue'
import { getValidToken } from "../utils/auth.js";
import request from "../main.js";
import { ElMessage } from 'element-plus';

const textFeature = ref('')
const sketchUrl = ref('')
const referenceUrl = ref('')
const resultImages = ref([]) // 生成结果图片列表
const userImages = ref([]) // 用户图片列表
const isGenerating = ref(false) // 生成状态

// 获取用户所有图片
const getAllImage = async () => {
  try {
    const token = getValidToken();
    if (!token) {
      throw new Error('未登录，请先登录！');
    }
    const response = await request({
      method: 'get',
      url: `/user/getAllImage`
    });
    const imageResult = response.data;
    if (imageResult.code !== 1 || !imageResult.data) {
      throw new Error(imageResult.msg || '获取图片链接失败，请稍后重试');
    }
    return imageResult.data;
  } catch (error) {
    console.error('获取图片URL出错:', error);
    ElMessage.error('获取图片失败：' + error.message);
    return [];
  }
}

// 左侧拖拽组件的回调
const sketchChange = (url, style) => {
  sketchUrl.value = url
}

const referenceChange = (url, style) => {
  referenceUrl.value = url
}

// 生成图片
const generateImage = async () => {
  if (!sketchUrl.value || !referenceUrl.value) {
    ElMessage.warning('请先上传款式图和参考图');
    return;
  }

  try {
    isGenerating.value = true;
    const token = getValidToken()
    if (!token) {
      throw new Error('未获取到有效的JWT Token，请重新登录')
    }

    const requestBody = {
      "imageUrlList": [sketchUrl.value, referenceUrl.value],
      "dimensions": "",
      "mode": "",
      "hookUrl": "",
      "textFeature": textFeature.value // 添加设计特征
    }

    const response = await request({
      method: 'post',
      url: '/image/image-fusion',
      data: requestBody
    });

    const result = response.data

    if (result.code !== 1) {
      throw new Error(result.msg || '服务器返回错误码')
    }

    if (!result.data || !result.data.length) {
      throw new Error('响应中缺少有效的数据')
    }

    resultImages.value = result.data
    ElMessage.success('图片生成成功！');

  } catch (error) {
    console.error('生成图片出错:', error)
    ElMessage.error('生成失败：' + error.message);
  } finally {
    isGenerating.value = false;
  }
}

// 右侧图片拖拽开始
const handleDragStart = (event, imageUrl) => {
  event.dataTransfer.setData('text/uri-list', imageUrl);
  event.dataTransfer.effectAllowed = 'copy';
}

// 下载图片
const downloadImage = (url, index) => {
  const link = document.createElement('a');
  link.href = url;
  link.download = `generated_image_${index + 1}.jpg`;
  link.click();
}

// 复制图片链接
const copyImageUrl = (url) => {
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('图片链接已复制到剪贴板');
  }).catch(err => {
    ElMessage.error('复制失败');
  });
}

onMounted(async () => {
  userImages.value = await getAllImage();
});

</script>

<template>
  <div class="container">
    <el-container>
      <!-- 左侧输入区域 -->
      <el-aside width="350px" class="input-panel">
        <div class="input-section">
          <h3>图片输入</h3>
          
          <div class="drag-item">
            <drag @update="sketchChange">款式图</drag>
          </div>

          <div class="drag-item">
            <drag @update="referenceChange">参考图</drag>
          </div>

          <div class="text-feature">
            <h4>设计特征</h4>
            <el-input
              v-model="textFeature"
              :autosize="{ minRows: 4, maxRows: 6 }"
              type="textarea"
              placeholder="请输入对设计的描述或描述提示词提高生成准度"
            />
          </div>
          
          <div class="generate-btn">
            <el-button 
              type="primary" 
              size="large"
              :loading="isGenerating"
              @click="generateImage"
              style="width: 100%"
            >
              {{ isGenerating ? '生成中...' : '一键生成' }}
            </el-button>
          </div>
        </div>
      </el-aside>

      <!-- 中间结果显示区域 -->
      <el-main class="result-panel">
        <div class="result-header">
          <h3>生成结果</h3>
        </div>
        <div class="result-content">
          <div v-if="resultImages.length === 0" class="empty-result">
            <el-empty description="暂无生成结果" />
          </div>
          <div v-else class="result-grid">
            <div 
              v-for="(image, index) in resultImages" 
              :key="index" 
              class="result-item"
            >
              <img :src="image" alt="生成结果" />
              <div class="result-actions">
                <el-button 
                  size="small" 
                  type="primary" 
                  @click="downloadImage(image, index)"
                >
                  下载
                </el-button>
                <el-button 
                  size="small" 
                  @click="copyImageUrl(image)"
                >
                  复制链接
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-main>

      <!-- 右侧图片库 -->
      <el-aside width="200px" class="image-library">
        <div class="library-header">
          <h3>我的图片</h3>
          <span class="image-count">{{ userImages.length }}</span>
        </div>
        <div class="library-content">
          <div v-if="userImages.length === 0" class="empty-library">
            <el-empty description="暂无图片" />
          </div>
          <div v-else class="image-list">
            <div 
              v-for="(image, index) in userImages" 
              :key="index"
              class="image-item"
              draggable="true"
              @dragstart="handleDragStart($event, image)"
            >
              <img :src="image" :alt="`用户图片${index + 1}`" />
              <div class="image-overlay">
                <span>拖拽使用</span>
              </div>
            </div>
          </div>
        </div>
      </el-aside>
    </el-container>
  </div>
</template>

<style scoped>
.container {
  padding: 15px;
  background: white;
}

/* 左侧输入面板 */
.input-panel {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-right: 15px;
  border: 1px solid #e8e8e8;
}

.input-section {
  /* 让内容自然撑开 */
}

.input-section h3 {
  margin-top: 0;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
  font-weight: 500;
}

.drag-item {
  margin-bottom: 20px;
}

.text-feature {
  margin-bottom: 20px;
}

.text-feature h4 {
  margin-bottom: 10px;
  color: #666;
  font-weight: 500;
}

.generate-btn {
  margin-top: 30px;
}

/* 中间结果面板 */
.result-panel {
  background: white;
  border-radius: 12px;
  margin: 0 8px;
  padding: 24px;
  border: 1px solid #e8e8e8;
}

.result-header {
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.result-header h3 {
  margin: 0 0 12px 0;
  color: #333;
  font-weight: 500;
}

.result-content {
  /* 和左侧一样自然撑开 */
}

.empty-result {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.result-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
}

.result-item img {
  width: 100%;
  height: 200px;
  object-fit: contain;
  background: #f9f9f9;
}

.result-actions {
  padding: 10px;
  background: white;
  text-align: center;
}

.result-actions .el-button {
  margin: 0 5px;
}

/* 右侧图片库 */
.image-library {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-left: 15px;
  border: 1px solid #e8e8e8;
}

.library-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
  padding-bottom: 12px;
}

.library-header h3 {
  margin: 0;
  color: #333;
  font-weight: 500;
}

.image-count {
  color: #999;
  font-size: 13px;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
}

.library-content {
  /* 和左侧一样自然撑开 */
}

.empty-library {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.image-item {
  position: relative;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  cursor: grab;
  transition: all 0.2s ease;
  background: #fafafa;
  aspect-ratio: 1/1;
}

.image-item:hover {
  border-color: #409eff;
  transform: translateX(2px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.image-item:active {
  cursor: grabbing;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  display: block;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
  font-size: 12px;
  text-align: center;
}

.image-item:hover .image-overlay {
  opacity: 1;
}

/* 滚动条样式 */
.result-content::-webkit-scrollbar,
.library-content::-webkit-scrollbar {
  width: 4px;
}

.result-content::-webkit-scrollbar-track,
.library-content::-webkit-scrollbar-track {
  background: transparent;
}

.result-content::-webkit-scrollbar-thumb,
.library-content::-webkit-scrollbar-thumb {
  background: #d0d0d0;
  border-radius: 2px;
}

.result-content::-webkit-scrollbar-thumb:hover,
.library-content::-webkit-scrollbar-thumb:hover {
  background: #b0b0b0;
}
</style>