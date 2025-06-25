<template>
  <div class="container">
    <!-- 左边栏 -->
    <aside class="sidebar">
      <router-link to="/home" class="logo-container" style="cursor: pointer; text-decoration: none;">
        <div class="logo">Vision<span class="logo-x">Wear</span></div>
      </router-link>

      <router-link
          to="/home/fashion/option1"
          class="menu-item el-menu-item"
          :class="{ active: activeFunction === 'model' }"
          @click="selectMenu('model')"
      >
        <el-icon><Grid /></el-icon>服装创意生成
      </router-link>

      <div v-if="showSubMenu" class="submenu">
        <router-link
            to="/home/fashion/option1"
            class="menu-item sub el-menu-item"
            :class="{ active: activeFunction === '选项1' }"
            @click="selectMenu('选项1')"
        >
          图灵绘境
        </router-link>

        <router-link
            to="/home/fashion/option2"
            class="menu-item sub el-menu-item"
            :class="{ active: activeFunction === '选项2' }"
            @click="selectMenu('选项2')"
        >
          图片融合
        </router-link>

        <router-link
            to="/home/fashion/option3"
            class="menu-item sub el-menu-item"
            :class="{ active: activeFunction === '选项3' }"
            @click="selectMenu('选项3')"
        >
          素材设计
        </router-link>
      </div>

      <ul class="function-list">
        <router-link
            to="/home/sketch"
            class="menu-item el-menu-item"
            :class="{ active: activeFunction === '线稿成图' }"
            @click="selectMenu('线稿成图')"
        >
          <el-icon><Edit /></el-icon>线稿成图
        </router-link>

        <router-link
            to="/home/redraw"
            class="menu-item el-menu-item"
            :class="{ active: activeFunction === '局部重绘' }"
            @click="selectMenu('局部重绘')"
        >
          <el-icon><Brush /></el-icon>局部重绘
        </router-link>

        <router-link
            to="/home/style-transfer"
            class="menu-item el-menu-item"
            :class="{ active: activeFunction === '风格迁移' }"
            @click="selectMenu('风格迁移')"
        >
          <el-icon><PictureFilled /></el-icon>风格迁移
        </router-link>

        <router-link
            to="/home/style-extension"
            class="menu-item el-menu-item"
            :class="{ active: activeFunction === '风格延伸' }"
            @click="selectMenu('风格延伸')"
        >
          <el-icon><Picture /></el-icon>风格延伸
        </router-link>
      </ul>
    </aside>

    <!-- 主体 -->
    <main class="main" :class="{ 'expanded': isCollapse }">
      <!-- 顶部栏 -->
      <header class="header">
        <div class="nav">
          <router-link to="/home/material" class="nav-link">
            <el-icon><Folder /></el-icon>素材管理
          </router-link>
          <router-link to="/home/design" class="nav-link">
            <el-icon><SetUp /></el-icon>设计页面
          </router-link>
          <router-link to="/home/image-processing" class="nav-link">
            <el-icon><PictureRounded /></el-icon>图片处理
          </router-link>
          <router-link to="/home/my-pictures" class="nav-link">
            <el-icon><Picture /></el-icon>我的图片
          </router-link>
        </div>
        <UserProfileComponent></UserProfileComponent>
      </header>

      <!-- 展示图像区域 -->
      <router-view class="page-view" />
    </main>
  </div>
</template>

<script>
import UserProfileComponent from '../user/UserProfileComponent.vue';
import MyPicture from '../user/MyPicture.vue';
import { ref } from 'vue';
import {
  Grid, Edit, Brush,
  PictureFilled, Picture, Monitor,
  Folder, SetUp, PictureRounded, Search
} from '@element-plus/icons-vue';

export default {
  components: {
    MyPicture,
    UserProfileComponent,
    Grid, Edit, Brush,
    PictureFilled, Picture, Monitor,
    Folder, SetUp, PictureRounded, Search
  },
  setup() {
    const searchText = ref('');
    return {
      searchText
    };
  },
  data() {
    return {
      showSubMenu: false,
      showUserPanel: false,
      activeFunction: '', // 用于标识当前激活的菜单项
    };
  },
  methods: {
    selectMenu(name) {
      this.activeFunction = name;
      if (name === 'model') {
        this.showSubMenu = true;
        // 当点击服装创意生成时，自动选中图灵绘境子菜单
        this.activeFunction = '选项1';
      } else if (!['选项1', '选项2', '选项3'].includes(name)) {
        this.showSubMenu = false;
      }
    },
    handleClickOutside(e) {
      const panel = this.$el.querySelector('.user-panel-float');
      const icon = this.$el.querySelector('.user-icon-area');
      if (panel && icon && !panel.contains(e.target) && !icon.contains(e.target)) {
        this.showUserPanel = false;
      }
    },
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside);
  },
};
</script>

<style scoped>
.container {
  display: flex;
  height: 100vh;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 14px;
  color: #333;
  background-color: #ffffff;
  background-image: linear-gradient(135deg, #ffffff, #f9fafa);
}

.sidebar {
  width: 240px;
  background: #ffffff;
  padding: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  overflow-x: hidden;
  position: relative;
  z-index: 100;
  border-radius: 0 16px 16px 0;
  border-left: 4px solid #6a98e9;
}

.logo-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 28px 16px;
  margin-bottom: 10px;
  position: relative;
}

.logo-container::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 15%;
  width: 70%;
  height: 1px;
  background: rgba(106, 152, 233, 0.2);
}

.logo {
  font-size: 28px;
  font-weight: 700;
  color: #3f8cda;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  letter-spacing: 0.5px;
}

.logo-x {
  color: #e2725b;
  font-weight: 800;
}

.submenu {
  margin-left: 16px;
  padding-left: 14px;
  border-left: 2px solid rgba(106, 152, 233, 0.3);
  margin-top: 4px;
  margin-bottom: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 22px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  margin: 8px 10px;
  text-decoration: none;
  color: #5c6b7a;
  font-weight: 500;
}

.menu-item .el-icon {
  margin-right: 14px;
  font-size: 18px;
  transition: transform 0.2s ease;
  color: #3f8cda;
}

.menu-item.sub {
  font-size: 14px;
  color: #5c6b7a;
  padding: 12px 16px;
  margin: 6px 10px;
}

.menu-item:hover {
  background-color: rgba(106, 152, 233, 0.08);
  transform: translateX(3px);
  color: #3f8cda;
}

.menu-item:hover .el-icon {
  transform: scale(1.1);
}

.menu-item.active {
  background-color: rgba(106, 152, 233, 0.15);
  color: #3f8cda;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(106, 152, 233, 0.1);
}

.menu-item.sub.active {
  font-weight: 600;
  color: #3f8cda;
  background-color: rgba(106, 152, 233, 0.1);
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  transition: all 0.3s ease;
  margin-left: 10px;
}

.main.expanded {
  margin-left: -156px;
}

.header {
  height: 64px !important; /* Force the height to stay consistent */
  min-height: 64px;        /* Add min-height as backup */
  max-height: 64px;        /* Prevent expansion */
  background: linear-gradient(90deg, #6a98e9, #77cdf3);
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  border-radius: 12px;
  margin: 12px 12px 0 12px;
}

.nav {
  display: flex;
  align-items: center;
  flex: 1;
}

.nav-link {
  display: flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.95);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 10px 18px;
  border-radius: 10px;
}

.nav-link:not(:last-child) {
  margin-right: 24px;
}

.nav-link:hover {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.nav-link .el-icon {
  margin-right: 10px;
  font-size: 18px;
  transition: transform 0.2s ease;
}

.nav-link:hover .el-icon {
  transform: scale(1.1);
}

.page-view {
  flex: 1;
  padding: 28px;
  background-color: white;
  margin: 16px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid rgba(106, 152, 233, 0.05);
}

.page-view:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
}

.function-list {
  list-style-type: none;
  padding: 0;
  margin: 20px 0 0 0;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-input__inner) {
  height: 48px;
  line-height: 48px;
}

:deep(.el-input-group__append) {
  padding: 0;
}

:deep(.el-input-group__append .el-button) {
  border-radius: 0 12px 12px 0;
  height: 48px;
  padding: 0 20px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 54px;
  line-height: 54px;
}

</style>