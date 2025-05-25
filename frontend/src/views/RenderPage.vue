<template>
  <div class="welcome-container">
    <div class="welcome-card">
      <h1 class="welcome-title">欢迎使用 VisionWear</h1>
      <p class="welcome-subtitle">AI定义时尚，科技塑造未来</p>
      
      <div class="feature-section">
        <h2>功能导航</h2>
        <div class="feature-grid">
          <div class="feature-item" @click="navigateTo('/home/fashion/option1')">
            <el-icon class="feature-icon"><Picture /></el-icon>
            <h3>图灵绘境</h3>
            <p>基于AI生成时尚图像</p>
          </div>
          
          <div class="feature-item" @click="navigateTo('/home/style-extension')">
            <el-icon class="feature-icon"><PictureFilled /></el-icon>
            <h3>风格延伸</h3>
            <p>将您的图片转换为不同风格</p>
          </div>
          
          <div class="feature-item" @click="navigateTo('/home/sketch')">
            <el-icon class="feature-icon"><Edit /></el-icon>
            <h3>线稿成图</h3>
            <p>将线稿快速转化为完整图像</p>
          </div>
          
          <div class="feature-item" @click="navigateTo('/home/fashion/option2')">
            <el-icon class="feature-icon"><Brush /></el-icon>
            <h3>图片融合</h3>
            <p>将两张图片融合在一起</p>
          </div>
        </div>
      </div>
      
      <div class="recent-activity">
        <h2>开始使用</h2>
        <p>请从左侧菜单选择功能或点击上方卡片直接访问</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Picture, PictureFilled, Edit, Brush } from '@element-plus/icons-vue'

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.welcome-container {
  padding: 20px;
  height: 100%;
}

.welcome-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-title {
  font-size: 28px;
  color: #409EFF;
  margin-bottom: 8px;
}

.welcome-subtitle {
  font-size: 16px;
  color: #909399;
  margin-bottom: 30px;
}

.feature-section {
  margin-bottom: 30px;
}

.feature-section h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #303133;
  font-weight: 500;
  border-bottom: 1px solid #EBEEF5;
  padding-bottom: 10px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.feature-item {
  background: #F5F7FA;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid transparent;
}

.feature-item:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
  border-color: #DCDFE6;
}

.feature-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 12px;
}

.feature-item h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
}

.feature-item p {
  font-size: 14px;
  color: #909399;
}

.recent-activity {
  margin-top: 20px;
}

.recent-activity h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #303133;
  font-weight: 500;
  border-bottom: 1px solid #EBEEF5;
  padding-bottom: 10px;
}

.recent-activity p {
  color: #606266;
  font-size: 14px;
}
</style>