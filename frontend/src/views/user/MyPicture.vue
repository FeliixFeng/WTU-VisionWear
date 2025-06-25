<template>
  <div class="my-pictures-page">
    <h2>我的图片</h2>
    <div v-if="images.length > 0" class="pictures-list">
      <div class="pictures-wrapper">
        <div class="picture-item" v-for="(item, index) in images.slice(mageIndex, mageIndex + pageSize)" :key="item + index">
          <img class="showPicture" :src="item" />
          <div class="picture-actions-bottom">
            <el-tooltip content="复制链接" placement="top">
              <span class="icon-btn" @click.stop="copyLink(item)">
                <el-icon :size="18"><DocumentCopy /></el-icon>
              </span>
            </el-tooltip>
            <el-tooltip content="下载图片" placement="top">
              <span class="icon-btn" @click.stop="downloadImage(item)">
                <el-icon :size="18"><Download /></el-icon>
              </span>
            </el-tooltip>
          </div>
        </div>
      </div>
      <div class="pager-bar">
        <el-button type="primary" class="pre" :disabled="mageIndex === 0" @click="mageIndex = mageIndex >= pageSize ? mageIndex - pageSize : 0">
          <span class="iconfont icon-fangxiang-xiangzuo"></span>
        </el-button>
        <el-button type="primary" class="next" :disabled="mageIndex >= images.length - pageSize" @click="mageIndex = mageIndex < images.length - pageSize ? mageIndex + pageSize : mageIndex">
          <span class="iconfont icon-fangxiang-xiangyou"></span>
        </el-button>
      </div>
    </div>
    <div v-else class="empty-tip">暂无图片</div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { getValidToken } from "../../utils/auth.js";
import request from "../../main.js";
import { ElMessage } from 'element-plus';
import { DocumentCopy, Download } from '@element-plus/icons-vue';
import '../../styles/download/font/iconfont.css'
const images = ref([]);
const mageIndex = ref(0);
const pageSize = 8;
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
    throw error;
  }
}
onMounted(async () => {
  images.value = await getAllImage();
});

const copyLink = async (url) => {
  try {
    await navigator.clipboard.writeText(url);
    ElMessage.success('图片链接已复制');
  } catch {
    ElMessage.error('复制失败');
  }
};

const downloadImage = (url) => {
  const a = document.createElement('a');
  a.href = url;
  a.download = url.split('/').pop() || 'image.jpg';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
};
</script>

<style scoped>
.my-pictures-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  min-height: 60vh;
  padding: 40px 0 0 0;
}

.my-pictures-page h2 {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 40px;
  color: #3f8cda;
  letter-spacing: 3px;
  text-shadow: 0 2px 8px rgba(106,152,233,0.08);
  font-family: 'Avenir Next', 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
}

.pictures-list {
  width: 100%;
  max-width: 100%;
  padding: 0;
  background: none;
  border-radius: 0;
  box-shadow: none;
  margin-bottom: 32px;
}

.pictures-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  justify-content: center;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.picture-item {
  flex: 0 0 calc((100% - 64px) / 3);
  max-width: 220px;
  min-width: 0;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fafdff 80%, #e6f0fa 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(63,140,218,0.10);
  padding: 16px;
  box-sizing: border-box;
  border: 2.5px solid transparent;
  background-clip: padding-box, border-box;
  background-origin: padding-box, border-box;
  transition: box-shadow 0.25s, transform 0.22s, border 0.22s;
  position: relative;
}
.picture-item:hover {
  box-shadow: 0 12px 36px rgba(63,140,218,0.18);
  transform: translateY(-8px) scale(1.04);
  border: 2.5px solid #6a98e9;
}

.showPicture {
  max-width: 180px;
  max-height: 140px;
  width: auto;
  height: auto;
  object-fit: contain;
  border-radius: 12px;
  border: none;
  background: #fff;
  transition: transform 0.2s cubic-bezier(.4,2,.6,1), box-shadow 0.2s;
  box-shadow: 0 2px 12px rgba(63,140,218,0.10);
}

.pager-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 40px 0 0 0;
  gap: 40px;
}

.pager-bar .el-button {
  background: linear-gradient(90deg, #6a98e9, #77cdf3);
  color: #fff;
  border: none;
  font-size: 28px;
  border-radius: 50%;
  width: 54px;
  height: 54px;
  box-shadow: 0 2px 12px rgba(63,140,218,0.10);
  transition: box-shadow 0.2s, background 0.2s, transform 0.18s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pager-bar .el-button:hover {
  background: linear-gradient(90deg, #77cdf3, #6a98e9);
  box-shadow: 0 6px 24px rgba(63,140,218,0.18);
  transform: scale(1.08);
}
.pre:disabled, .next:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.empty-tip {
  color: #b0b8c9;
  font-size: 22px;
  margin-top: 100px;
  letter-spacing: 1px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.empty-tip::before {
  content: '';
  display: block;
  width: 80px;
  height: 80px;
  margin-bottom: 18px;
  background: url('https://img.icons8.com/fluency/96/000000/no-image.png') no-repeat center/contain;
  opacity: 0.18;
}

.picture-actions,
.picture-actions-bottom {
  display: flex;
  gap: 10px;
  justify-content: center;
  align-items: center;
}
.picture-actions-bottom {
  display: flex;
  gap: 10px;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
  position: static;
  opacity: 1 !important;
}

.picture-actions-bottom .el-button {
  background: linear-gradient(90deg, #6a98e9, #77cdf3);
  color: #fff;
  border: none;
  font-size: 22px;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  box-shadow: 0 2px 8px rgba(63,140,218,0.10);
  transition: box-shadow 0.18s, background 0.18s, transform 0.16s;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}
.picture-actions-bottom .el-button:hover {
  background: linear-gradient(90deg, #77cdf3, #6a98e9);
  box-shadow: 0 6px 18px rgba(63,140,218,0.18);
  transform: scale(1.12);
}
.picture-actions-bottom .el-icon {
  font-size: 22px !important;
  color: #fff !important;
}

.icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: none;
  color: #3f8cda;
  font-size: 18px;
  cursor: pointer;
  transition: color 0.18s, background 0.18s, transform 0.16s;
  margin: 0 2px;
}
.icon-btn:hover {
  color: #e2725b;
  background: #e6f0fa;
  transform: scale(1.18);
}

.icon-btn .el-icon {
  color: #3f8cda !important;
  font-size: 18px !important;
  transition: color 0.18s;
}
.icon-btn:hover .el-icon {
  color: #e2725b !important;
}
</style>