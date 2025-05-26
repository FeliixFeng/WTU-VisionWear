<template>
  <div class="style-transfer-container">
    <el-alert
      title="功能开发中"
      type="warning"
      description="该功能正在开发中，暂不可用"
      :closable="false"
      show-icon
    />
    
    <h2>风格迁移</h2>
    <div class="content">
      <p class="description">上传内容图片和风格图片，生成融合风格的新图像</p>
      
      <div class="upload-area">
        <div class="upload-box">
          <h4>内容图片</h4>
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false">
            <el-icon><Plus /></el-icon>
          </el-upload>
          <p class="upload-tip">请上传要应用风格的图片</p>
        </div>
        
        <div class="upload-box">
          <h4>风格图片</h4>
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false">
            <el-icon><Plus /></el-icon>
          </el-upload>
          <p class="upload-tip">请上传风格参考图片</p>
        </div>
      </div>
      
      <div class="action-area">
        <el-button type="primary">开始风格迁移</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>

<style scoped>
.style-transfer-container {
  padding: 20px;
}

.content {
  margin-top: 20px;
}

.description {
  margin-bottom: 20px;
  color: #666;
}

.upload-area {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.upload-box {
  flex: 1;
  padding: 20px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  text-align: center;
}

h4 {
  margin-top: 0;
  margin-bottom: 15px;
}

.upload-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.action-area {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.el-alert {
  margin-bottom: 20px;
}
</style>