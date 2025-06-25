<script setup>
import drag from '../utils/drag.vue';
import { ref, onMounted } from 'vue'
import { getValidToken } from "../../utils/auth.js";
import request from "../../main.js";
import { ElMessage } from 'element-plus';

const textFeature = ref('')
const sketchUrl = ref('')
const referenceUrl = ref('')
const resultImages = ref([]) // 生成结果图片列表
const userImages = ref([]) // 用户图片列表
const isGenerating = ref(false) // 生成状态
const generationError = ref('') // 添加错误状态

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
    ElMessage.warning('请先上传款式图和参考图')
    return
  }

  try {
    isGenerating.value = true
    generationError.value = '' // 清除之前的错误
    resultImages.value = [] // 清空之前的结果
    
    const token = getValidToken()
    if (!token) {
      throw new Error('未获取到有效的JWT Token，请重新登录')
    }

    // 创建符合后端ImageFusionDTO格式的请求体
    const requestBody = {
      "imageUrlList": [sketchUrl.value, referenceUrl.value],
      "dimensions": "SQUARE", // 使用默认值，可以根据需要修改
      "mode": "relax", // 使用默认值，可以根据需要修改
      "hookUrl": "", // 不使用回调
      "textFeature": textFeature.value // 添加设计特征
    }

    ElMessage.info('正在提交图片融合请求...')
    console.log('提交融合请求:', requestBody)
    
    const response = await request({
      method: 'post',
      url: '/image/image-fusion',
      data: requestBody
    })

    const result = response.data
    console.log('融合请求响应:', result)
    
    if (result.code !== 1 || !result.data || !result.data.jobId) {
      throw new Error(result.msg || '提交融合任务失败')
    }

    const jobId = result.data.jobId
    ElMessage.success(`任务提交成功! JobID: ${jobId}`)
    console.log('获取到jobId:', jobId)

    // 轮询生成结果
    try {
      console.log('开始轮询结果...')
      const pollResult = await pollGenerationResult(jobId)
      console.log('轮询完成，结果:', pollResult)
      
      if (pollResult && pollResult.images && pollResult.images.length > 0) {
        // 更新结果图片显示
        resultImages.value = pollResult.images.map(img => img.imageUrl || '')
        ElMessage.success(`成功生成 ${resultImages.value.length} 张图片!`)
        console.log('生成的图片URLs:', resultImages.value)
        
        // 刷新用户图片库
        userImages.value = await getAllImage()
      } else {
        console.warn('轮询返回了结果但没有图片:', pollResult)
        ElMessage.warning('未获取到生成图片，请稍后在图片库中查看')
      }
    } catch (pollError) {
      console.error('轮询过程中出错:', pollError)
      generationError.value = `轮询失败: ${pollError.message}`
      ElMessage.error(`获取生成结果失败: ${pollError.message}`)
      
      // 尽管轮询失败，仍然尝试刷新图片库，因为图片可能已经生成
      try {
        userImages.value = await getAllImage()
      } catch (e) {
        console.error('刷新图片库失败:', e)
      }
    }
  } catch (error) {
    console.error('生成图片过程中出错:', error)
    generationError.value = `生成失败: ${error.message}`
    ElMessage.error(`生成图片失败: ${error.message}`)
  } finally {
    isGenerating.value = false
  }
}

// 轮询生成结果的函数（如果API是异步的）
const pollGenerationResult = async (jobId) => {
  const maxAttempts = 40; // 增加到40次，因为图像融合可能需要更长时间
  const interval = 3000; // 每3秒查询一次
  let lastError = null;
  
  if (!jobId) {
    throw new Error('无效的任务ID');
  }
  
  console.log(`开始轮询任务结果，任务ID: ${jobId}`);
  ElMessage.info('图片融合任务已提交，正在等待处理...');
  
  for (let attempt = 0; attempt < maxAttempts; attempt++) {
    try {
      console.log(`轮询尝试 ${attempt + 1}/${maxAttempts}`);
      
      // 不要每次都显示消息，避免过多弹窗
      if (attempt % 5 === 0 && attempt > 0) {
        ElMessage.info(`正在等待图片融合完成 (${attempt + 1}/${maxAttempts})...`);
      }
      
      const response = await request({
        method: 'get',
        url: `/image/image-fusion/result`,
        params: { jobId }
      });
      
      const result = response.data;
      console.log('轮询返回结果:', result);
      
      // 如果成功获取到结果
      if (result.code === 1 && result.data && result.data.images && result.data.images.length > 0) {
        ElMessage.success('图片融合完成！');
        console.log('生成结果:', result.data);
        return result.data;
      }
      
      // 如果服务器返回了结果，但没有图片，可能是处理中
      console.log('尚未得到结果，继续轮询...');
      await new Promise(resolve => setTimeout(resolve, interval));
      
    } catch (error) {
      // 这里是关键修改：后端在任务未完成时会抛出异常，但这不是真正的错误，而是表示任务仍在队列中
      console.log(`轮询出错，但这可能只是表示任务仍在队列中: ${error.message}`);
      
      // 检查错误信息是否与"任务在队列中"相关
      // 注意：后端在任务状态为"ON_QUEUE"时会抛出"查询失败: "的异常
      const errorMsg = error.response?.data?.msg || error.message || '';
      
      if (errorMsg.includes('查询失败') || errorMsg.includes('ON_QUEUE')) {
        console.log('任务仍在队列中或处理中，继续轮询...');
        
        // 不要太频繁地显示消息
        if (attempt % 5 === 0) {
          ElMessage.info(`任务正在处理中，请耐心等待 (${attempt + 1}/${maxAttempts})...`);
        }
        
        // 继续轮询
        await new Promise(resolve => setTimeout(resolve, interval));
        continue;
      }
      
      // 其他类型的错误，可能是真正的错误
      lastError = error;
      console.error('轮询发生实际错误:', error);
      
      if (attempt === maxAttempts - 1) {
        // 到达最大尝试次数时才抛出错误
        throw new Error(`获取结果失败: ${error.message}`);
      }
      
      // 其他情况下继续尝试
      await new Promise(resolve => setTimeout(resolve, interval));
    }
  }
  
  // 如果达到最大尝试次数仍未成功
  if (lastError) {
    throw new Error(`获取结果超时: ${lastError.message}`);
  } else {
    throw new Error('获取结果超时');
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
          <h3>图片融合</h3>
          
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
          
          <!-- 添加错误提示 -->
          <div v-if="generationError" class="error-message">
            <el-alert
              :title="generationError"
              type="error"
              show-icon
              :closable="false"
            />
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

/* 添加错误提示样式 */
.error-message {
  margin-top: 15px;
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
  height: 480px;
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