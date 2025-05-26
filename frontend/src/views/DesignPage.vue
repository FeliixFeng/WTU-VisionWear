<template>
  <div class="design-container">
    <el-alert
      title="功能开发中"
      type="warning"
      description="该功能正在开发中，暂不可用"
      :closable="false"
      show-icon
    />
    
    <h2>设计页面</h2>
    <div class="content">
      <div class="design-workspace">
        <div class="toolbar">
          <div class="tool-group">
            <el-tooltip content="选择工具" placement="bottom">
              <el-button class="active-tool">
                <el-icon><Pointer /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="移动工具" placement="bottom">
              <el-button>
                <el-icon><Position /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="形状工具" placement="bottom">
              <el-button>
                <el-icon><Pointer /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="文本工具" placement="bottom">
              <el-button>
                <el-icon><EditPen /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
          
          <div class="action-group">
            <el-button type="primary">保存设计</el-button>
            <el-button type="success">导出</el-button>
          </div>
        </div>
        
        <div class="workspace-content">
          <div class="sidebar">
            <h4>图层</h4>
            <div class="layers-list">
              <div class="layer-item active">
                <el-icon><Picture /></el-icon>
                <span>背景图片</span>
              </div>
              <div class="layer-item">
                <el-icon><EditPen /></el-icon>
                <span>文本图层</span>
              </div>
              <div class="layer-item">
                <el-icon><PictureFilled /></el-icon>
                <span>形状元素</span>
              </div>
            </div>
          </div>
          
          <div class="canvas-area">
            <div class="canvas">
              <div class="canvas-content">
                <p class="placeholder-text">设计区域</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Pointer, Position, EditPen, Picture, PictureFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.design-container {
  padding: 20px;
}

.content {
  margin-top: 20px;
}

.el-alert {
  margin-bottom: 20px;
}

.design-workspace {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #ebeef5;
}

.tool-group {
  display: flex;
  gap: 5px;
}

.active-tool {
  background-color: #ecf5ff;
  color: #409eff;
}

.action-group {
  display: flex;
  gap: 10px;
}

.workspace-content {
  display: flex;
  height: 500px;
}

.sidebar {
  width: 200px;
  border-right: 1px solid #ebeef5;
  padding: 15px;
  background-color: #f8f9fa;
}

h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #606266;
}

.layers-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.layer-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.layer-item:hover {
  background-color: #f0f2f5;
}

.layer-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

.layer-item .el-icon {
  margin-right: 8px;
}

.canvas-area {
  flex: 1;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #e9ecef;
}

.canvas {
  width: 800px;
  height: 450px;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.canvas-content {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.placeholder-text {
  color: #909399;
  font-size: 18px;
}
</style>