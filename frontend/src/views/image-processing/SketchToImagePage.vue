<template>
  <div class="sketch-to-image-container">
    <el-alert title="功能开发中" type="warning" description="该功能正在开发中，暂不可用" :closable="false" show-icon />

    <h2>线稿成图</h2>
    <div class="content">
      <p class="description">上传线稿，AI将为您生成完整的彩色图像</p>

      <div class="sketch-to-image-workspace">
        <div class="upload-section">
          <a-tabs default-active-key="2">
            <a-tab-pane key="1" title="上传线稿">
              <!-- <h4>上传线稿</h4> -->
              <div class="upload-area">
                <el-upload action="#" class="sketch-uploader" :auto-upload="false" drag>
                  <el-icon class="el-icon--upload">
                    <Upload />
                  </el-icon>
                  <div class="el-upload__text">拖拽线稿到此处或 <em>点击上传</em></div>
                  <template #tip>
                    <div class="el-upload__tip">支持JPG、PNG格式，建议使用清晰的线稿</div>
                  </template>
                </el-upload>
              </div>
            </a-tab-pane>
            <a-tab-pane key="2" title="画板">
              <div class="drawing-area">
                <rawingCanvas></rawingCanvas>
              </div>
            </a-tab-pane>
          </a-tabs>
        </div>

        <div class="options-section">
          <h4>生成选项</h4>
          <div class="option-form">
            <el-form label-position="top" label-width="100px">
              <el-form-item label="颜色风格">
                <el-select v-model="options.style" placeholder="选择颜色风格" style="width: 100%">
                  <el-option label="写实风格" value="realistic"></el-option>
                  <el-option label="卡通风格" value="cartoon"></el-option>
                  <el-option label="水彩风格" value="watercolor"></el-option>
                </el-select>
              </el-form-item>

              <el-form-item label="提示词">
                <el-input v-model="options.prompt" type="textarea" rows="3"
                  placeholder="描述您希望生成的内容，如：'蓝色背景，女性角色'"></el-input>
              </el-form-item>

              <el-form-item label="生成数量">
                <el-radio-group v-model="options.count">
                  <el-radio :label="1">1张</el-radio>
                  <el-radio :label="2">2张</el-radio>
                  <el-radio :label="4">4张</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>

            <div class="generate-button">
              <el-button type="primary" :disabled="true">开始生成</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import rawingCanvas from '../../components/rawingCanvas.vue'

const options = ref({
  style: 'realistic',
  prompt: '',
  count: 1
})
</script>

<style scoped>
.sketch-to-image-container {
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

.sketch-to-image-workspace {
  display: flex;
  gap: 30px;
}

.upload-section,
.options-section {
  flex: 1;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.upload-area {
  padding: 10px 0;
}

.drawing-area {
  height: 500px;
}

.sketch-uploader {
  width: 100%;
}

.option-form {
  padding: 10px 0;
}

.generate-button {
  margin-top: 20px;
  text-align: center;
}
</style>
