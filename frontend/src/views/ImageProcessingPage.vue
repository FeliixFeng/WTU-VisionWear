<template>
  <div class="image-processing-container">
    <el-alert
      title="功能开发中"
      type="warning"
      description="该功能正在开发中，暂不可用"
      :closable="false"
      show-icon
    />
    
    <h2>图片处理</h2>
    <div class="content">
      <p class="description">上传图片，使用各种专业工具进行编辑和处理</p>
      
      <div class="processing-workspace">
        <div class="tools-sidebar">
          <h4>处理工具</h4>
          <div class="tools-list">
            <div class="tool-item active">
              <el-icon><Filter /></el-icon>
              <span>滤镜</span>
            </div>
            <div class="tool-item">
              <el-icon><Crop /></el-icon>
              <span>裁剪</span>
            </div>
            <div class="tool-item">
              <el-icon><PictureFilled /></el-icon>
              <span>调整</span>
            </div>
            <div class="tool-item">
              <el-icon><Refresh /></el-icon>
              <span>旋转</span>
            </div>
            <div class="tool-item">
              <el-icon><ZoomIn /></el-icon>
              <span>尺寸</span>
            </div>
          </div>
          
          <div class="upload-button">
            <el-upload
              action="#"
              :auto-upload="false"
              :show-file-list="false">
              <el-button type="primary">上传图片</el-button>
            </el-upload>
            <div class="upload-tip">最大支持10MB的JPG、PNG图片</div>
          </div>
        </div>
        
        <div class="preview-area">
          <div class="preview-placeholder">
            <el-icon class="placeholder-icon"><Picture /></el-icon>
            <p>请上传图片开始处理</p>
          </div>
        </div>
        
        <div class="settings-panel">
          <h4>滤镜设置</h4>
          <div class="filter-options">
            <div class="filter-items">
              <div class="filter-item active">
                <div class="filter-preview"></div>
                <span>原始</span>
              </div>
              <div class="filter-item">
                <div class="filter-preview filter-gray"></div>
                <span>黑白</span>
              </div>
              <div class="filter-item">
                <div class="filter-preview filter-sepia"></div>
                <span>复古</span>
              </div>
              <div class="filter-item">
                <div class="filter-preview filter-vivid"></div>
                <span>鲜艳</span>
              </div>
            </div>
            
            <div class="filter-strength">
              <span>强度</span>
              <el-slider v-model="filterStrength"></el-slider>
            </div>
            
            <div class="action-buttons">
              <el-button type="primary" disabled>应用</el-button>
              <el-button disabled>重置</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Filter, Crop, PictureFilled, Refresh, ZoomIn, Picture } from '@element-plus/icons-vue'

const filterStrength = ref(100)
</script>

<style scoped>
.image-processing-container {
  padding: 20px;
}

.content {
  margin-top: 20px;
}

.el-alert {
  margin-bottom: 20px;
}

.description {
  margin-bottom: 20px;
  color: #666;
}

.processing-workspace {
  display: flex;
  gap: 20px;
  height: 600px;
}

.tools-sidebar {
  width: 180px;
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.tools-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.tool-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
}

.tool-item:hover {
  background-color: #f5f7fa;
}

.tool-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

.tool-item .el-icon {
  margin-right: 8px;
}

.upload-button {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.preview-area {
  flex: 1;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-placeholder {
  text-align: center;
  color: #909399;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.settings-panel {
  width: 250px;
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.filter-items {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.filter-item {
  cursor: pointer;
  text-align: center;
}

.filter-item.active {
  outline: 2px solid #409eff;
  border-radius: 4px;
}

.filter-preview {
  height: 80px;
  background-color: #f5f7fa;
  background-image: url('https://picsum.photos/100/100');
  background-size: cover;
  border-radius: 4px;
  margin-bottom: 5px;
}

.filter-gray {
  filter: grayscale(100%);
}

.filter-sepia {
  filter: sepia(100%);
}

.filter-vivid {
  filter: saturate(200%);
}

.filter-strength {
  margin: 20px 0;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}
</style>