<template>
  <div class="material-container">
    <el-alert
      title="功能开发中"
      type="warning"
      description="该功能正在开发中，暂不可用"
      :closable="false"
      show-icon
    />
    
    <h2>素材管理</h2>
    <div class="content">
      <div class="material-header">
        <div class="search-box">
          <el-input placeholder="搜索素材" prefix-icon="Search"></el-input>
        </div>
        <div class="actions">
          <el-button type="primary"><el-icon><Upload /></el-icon> 上传素材</el-button>
          <el-button><el-icon><FolderAdd /></el-icon> 新建文件夹</el-button>
        </div>
      </div>
      
      <div class="folder-path">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">素材库</el-breadcrumb-item>
          <el-breadcrumb-item>服装设计</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      
      <div class="material-grid">
        <!-- 文件夹 -->
        <div class="material-card folder">
          <el-icon class="folder-icon"><Folder /></el-icon>
          <div class="material-name">纹理素材</div>
          <div class="material-info">12个项目</div>
        </div>
        
        <div class="material-card folder">
          <el-icon class="folder-icon"><Folder /></el-icon>
          <div class="material-name">模型素材</div>
          <div class="material-info">8个项目</div>
        </div>
        
        <!-- 素材项目 -->
        <div class="material-card">
          <div class="material-preview">
            <img src="https://picsum.photos/300/300?random=1" alt="素材预览">
          </div>
          <div class="material-name">服装设计稿1</div>
          <div class="material-info">JPG · 2023-05-20</div>
        </div>
        
        <div class="material-card">
          <div class="material-preview">
            <img src="https://picsum.photos/300/300?random=2" alt="素材预览">
          </div>
          <div class="material-name">服装设计稿2</div>
          <div class="material-info">PNG · 2023-05-21</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Search, Upload, FolderAdd, Folder } from '@element-plus/icons-vue'
</script>

<style scoped>
.material-container {
  padding: 20px;
}

.el-alert {
  margin-bottom: 20px;
}

.content {
  margin-top: 20px;
}

.material-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-box {
  width: 300px;
}

.actions {
  display: flex;
  gap: 10px;
}

.folder-path {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.material-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.material-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  transition: transform 0.3s;
  cursor: pointer;
  background: white;
}

.material-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.material-preview {
  height: 150px;
  overflow: hidden;
}

.material-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.material-name {
  padding: 10px;
  font-weight: 500;
}

.material-info {
  padding: 0 10px 10px;
  color: #909399;
  font-size: 12px;
}

.folder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-color: #f8f9fa;
}

.folder-icon {
  font-size: 40px;
  color: #409eff;
  margin-bottom: 10px;
}
</style>