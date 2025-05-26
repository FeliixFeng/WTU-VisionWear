<template>
  <div class="partial-redraw-container">
    <el-alert
      title="功能开发中"
      type="warning"
      description="该功能正在开发中，暂不可用"
      :closable="false"
      show-icon
    />
    
    <h2>局部重绘</h2>
    <div class="content">
      <p class="description">上传图片并标记需要重绘的区域，AI将自动生成新内容</p>
      
      <div class="workspace">
        <div class="upload-area">
          <div class="upload-box">
            <el-upload
              action="#"
              class="upload-image"
              drag
              :auto-upload="false">
              <el-icon class="el-icon--upload"><Upload /></el-icon>
              <div class="el-upload__text">拖拽图片到此处或 <em>点击上传</em></div>
            </el-upload>
          </div>
        </div>
        
        <div class="tools-area">
          <h4>绘画工具</h4>
          <div class="tools-group">
            <el-radio-group v-model="selectedTool">
              <el-radio-button label="brush">画笔</el-radio-button>
              <el-radio-button label="eraser">橡皮擦</el-radio-button>
            </el-radio-group>
          </div>
          
          <h4>画笔大小</h4>
          <el-slider v-model="brushSize" :min="5" :max="50"></el-slider>
          
          <div class="actions">
            <el-button type="primary" :disabled="true">应用重绘</el-button>
            <el-button>清除标记</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Upload } from '@element-plus/icons-vue'

const selectedTool = ref('brush')
const brushSize = ref(20)
</script>

<style scoped>
.partial-redraw-container {
  padding: 20px;
}

.content {
  margin-top: 20px;
}

.description {
  margin-bottom: 20px;
  color: #666;
}

.workspace {
  display: flex;
  gap: 20px;
}

.upload-area {
  flex: 2;
}

.upload-box {
  border-radius: 8px;
  overflow: hidden;
}

.upload-image {
  width: 100%;
}

.tools-area {
  flex: 1;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #606266;
}

.tools-group {
  margin-bottom: 20px;
}

.actions {
  margin-top: 30px;
  display: flex;
  gap: 10px;
}

.el-alert {
  margin-bottom: 20px;
}
</style>