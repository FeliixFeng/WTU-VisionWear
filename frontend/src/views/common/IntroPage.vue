<template>
  <div class="intro-page">
    <!-- 导航栏 -->
    <div class="nav-container">
      <div class="container">
        <el-row class="nav-row" justify="space-between" align="middle">
          <el-col :span="8">
            <div class="logo">
              <span class="logo-text">VisionWear</span>
              <span class="logo-tagline">AI时尚先锋</span>
            </div>
          </el-col>
          <el-col :span="16">
            <div class="nav-menu">
              <el-menu mode="horizontal" :ellipsis="false" class="menu-items">
                <el-menu-item index="1">功能</el-menu-item>
                <el-menu-item index="2">价格</el-menu-item>
                <el-menu-item index="3">关于我们</el-menu-item>
                <el-menu-item index="4">联系我们</el-menu-item>
              </el-menu>
              <div class="nav-buttons">
                <el-button @click="showLoginDialog" type="text">登录</el-button>
                <el-button @click="showRegisterDialog" type="primary" round>免费注册</el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 英雄区域 -->
    <div class="hero-section">
      <div class="animated-bg"></div>
      <div class="container">
        <el-row :gutter="40">
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <div class="hero-content">
              <div class="badge">时尚科技</div>
              <h1 class="hero-title">AI定义时尚，创意塑造风格</h1>
              <p class="hero-description">VisionWear将前沿AI技术与时尚设计完美融合，为您打造个性化穿搭方案与独特风格体验。</p>
              <div class="hero-features">
                <div class="feature-item"><i class="el-icon-brush"></i> 智能设计方案</div>
                <div class="feature-item"><i class="el-icon-refresh"></i> 个性化穿搭</div>
                <div class="feature-item"><i class="el-icon-chat-line-round"></i> 时尚顾问</div>
              </div>
              <div class="hero-buttons">
                <el-button type="primary" size="large" round @click="showRegisterDialog">立即体验</el-button>
                <el-button type="info" size="large" round plain>探索功能</el-button>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <div class="hero-image">
              <div class="image-decoration"></div>
              <el-carousel :interval="4000" arrow="always" height="500px" class="hero-carousel">
                <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
                  <img :src="item.src" :alt="item.alt" class="hero-carousel-image" />
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 特点展示区域 -->
    <div class="features-section">
      <div class="container">
        <h2 class="section-title">时尚科技的完美融合</h2>
        <p class="section-subtitle">重新定义您的穿衣体验</p>
        
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">👔</div>
            <h3>个性化风格定制</h3>
            <p>基于您的喜好、体型与场合需求，智能推荐最适合您的穿搭方案</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🎨</div>
            <h3>色彩搭配顾问</h3>
            <p>专业的色彩分析系统，让您的穿搭更加和谐、突显个人气质</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">💫</div>
            <h3>趋势预测分析</h3>
            <p>实时捕捉全球时尚动向，让您走在潮流前沿，展现前卫品味</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📱</div>
            <h3>虚拟换装体验</h3>
            <p>通过AR技术在线尝试不同搭配，轻松找到最适合的衣着风格</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 简单内容区域 -->
    <div class="content-section">
      <div class="container">
        <div class="content-wrapper">
          <div class="content-image">
            <img src="https://img.freepik.com/free-photo/high-fashion-look-glamour-portrait-beautiful-sexy-stylish-caucasian-young-woman-model_158538-1400.jpg" alt="智能时尚" />
          </div>
          <div class="content-text">
            <h2 class="section-title">智能时尚的新时代</h2>
            <p class="section-description">
              VisionWear 利用最先进的人工智能技术，结合对时尚潮流的深入理解，为每位用户提供量身定制的时尚体验。
              无论您是追求个性风格还是希望跟上最新潮流，我们的平台都能满足您的需求。
            </p>
            <div class="stats-row">
              <div class="stat-item">
                <div class="stat-number">5000+</div>
                <div class="stat-label">用户选择</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">98%</div>
                <div class="stat-label">满意度</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">24/7</div>
                <div class="stat-label">客户支持</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户评价区域 -->
    <div class="testimonial-section">
      <div class="container">
        <h2 class="section-title">用户评价</h2>
        <p class="section-subtitle">听听用户们怎么说</p>
        
        <el-carousel :interval="4000" type="card" height="300px">
          <el-carousel-item v-for="(item, index) in testimonials" :key="index">
            <div class="testimonial-card">
              <div class="testimonial-avatar">
                <img :src="item.avatar" :alt="item.name" />
              </div>
              <div class="testimonial-content">
                <p class="testimonial-text">{{ item.text }}</p>
                <p class="testimonial-author">{{ item.name }}</p>
                <p class="testimonial-role">{{ item.role }}</p>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

    <!-- 行动召唤区域 -->
    <div class="cta-section">
      <div class="container">
        <div class="cta-card">
          <h2 class="cta-title">准备好开始您的时尚旅程了吗？</h2>
          <p class="cta-description">立即注册，体验AI驱动的智能时尚生活方式</p>
          <el-button type="primary" size="large" round @click="showRegisterDialog">免费开始</el-button>
        </div>
      </div>
    </div>

    <!-- 简化的页脚 -->
    <div class="footer">
      <div class="container">
        <p>&copy; {{ new Date().getFullYear() }} VisionWear. 保留所有权利。</p>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <el-dialog
        v-model="loginDialogVisible"
        title=""
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        custom-class="auth-dialog"
    >
      <!-- 引入您现有的登录页面组件 -->
      <LoginPage
          v-if="loginDialogVisible"
          :is-dialog="true"
          @switch-to-register="switchToRegister"
          @switch-to-forgot-password = "switchToForgotPassword"
      />
    </el-dialog>

    <!-- 注册弹窗 -->
    <el-dialog
        v-model="registerDialogVisible"
        title=""
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        custom-class="auth-dialog"
    >
      <!-- 引入您现有的注册页面组件 -->
      <RegisterPage
          v-if="registerDialogVisible"
          :is-dialog="true"
          @switch-to-login="switchToLogin"
      />
    </el-dialog>

    <!-- 忘记密码弹窗 -->
    <el-dialog
        v-model="forgotPasswordDialogVisible"
        title=""
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        custom-class="auth-dialog"
    >
      <!-- 引入忘记密码页面组件 -->
      <ForgotPasswordPage
          v-if="forgotPasswordDialogVisible"
          :is-dialog="true"
          @switch-to-login="switchToLogin"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import {ref} from 'vue'
// 导入您修改后的登录和注册页面组件
import LoginPage from '../auth/LoginPage.vue'
import RegisterPage from '../auth/RegisterPage.vue'
import ForgotPasswordPage from "../auth/ForgotPasswordPage.vue";

// 弹窗状态
const loginDialogVisible = ref(false)
const registerDialogVisible = ref(false)
const forgotPasswordDialogVisible = ref(false)

// 轮播图数据
const carouselImages = ref([
  {
    src: 'https://img.freepik.com/free-photo/full-length-portrait-smiling-pretty-woman-isolated-white-wall_171337-9592.jpg',
    alt: '时尚穿搭',
    title: '春季新风尚',
    description: '轻盈柔和的色彩，展现春日清新活力'
  },
  {
    src: 'https://img.freepik.com/free-photo/stylish-caucasian-fashion-influencer-with-dark-hair-posing-yellow-suit-blue-background_273443-2579.jpg',
    alt: '简约风格',
    title: '简约不简单',
    description: '极简主义设计，彰显个性魅力'
  },
  {
    src: 'https://img.freepik.com/free-photo/pretty-young-stylish-sexy-woman-pink-luxury-dress-summer-fashion-trend-chic-style-sunglasses-blue-studio-background-shopping-holding-paper-bags-talking-mobile-phone-shopaholic_285396-2957.jpg',
    alt: '潮流购物',
    title: '探索时尚边界',
    description: '突破传统，创造属于自己的风格语言'
  },
  {
    src: 'https://img.freepik.com/free-photo/full-length-portrait-happy-excited-girl-bright-colorful-clothes-sunglasses-isolated_231208-5946.jpg',
    alt: '多彩穿搭',
    title: '色彩的力量',
    description: '用大胆的色彩组合表达自我态度'
  },
  {
    src: 'https://img.freepik.com/free-photo/high-fashion-look-glamor-closeup-portrait-beautiful-black-young-woman-model-with-yellow-bright-makeup-with-perfect-clean-skin-with-gold-jewelery-isolated-bright-pink_158538-21748.jpg',
    alt: '高级定制',
    title: '精致细节之美',
    description: '每一处细节都经过精心设计，打造完美形象'
  }
])

// 用户评价数据
const testimonials = ref([
  {
    name: '张小姐',
    role: '时尚博主',
    avatar: 'https://randomuser.me/api/portraits/women/44.jpg',
    text: 'DeepWear彻底改变了我的穿衣方式！AI推荐的搭配总是令人惊艳，省去了我大量选择服装的时间。'
  },
  {
    name: '李先生',
    role: '企业经理',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    text: '作为一个对时尚了解不多的人，DeepWear为我提供了专业级别的穿搭建议，让我在各种场合都能得体亮相。'
  },
  {
    name: '王女士',
    role: '服装设计师',
    avatar: 'https://randomuser.me/api/portraits/women/68.jpg',
    text: '平台的AI算法非常智能，能够精准捕捉最新的时尚趋势，作为设计师的我都从中获得了很多灵感！'
  }
])

// 显示登录弹窗
const showLoginDialog = () => {
  loginDialogVisible.value = true
  registerDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}

// 显示注册弹窗
const showRegisterDialog = () => {
  registerDialogVisible.value = true
  loginDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}

// 切换到登录
const switchToLogin = () => {
  loginDialogVisible.value = true
  registerDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}

// 切换到注册
const switchToRegister = () => {
  registerDialogVisible.value = true
  loginDialogVisible.value = false
  forgotPasswordDialogVisible.value = false
}
// 显示忘记密码弹窗
const switchToForgotPassword = () => {
  forgotPasswordDialogVisible.value = true
  loginDialogVisible.value = false
  registerDialogVisible.value = false
}
</script>

<style scoped>
/* 全局样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 导航栏样式 */
.nav-container {
  height: 80px;
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.05);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: all 0.3s ease;
}

.nav-container:hover {
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.1);
}

.nav-row {
  height: 80px;
}

.logo {
  display: flex;
  align-items: center;
  flex-direction: column;
  align-items: flex-start;
}

.logo-text {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: transform 0.3s ease;
}

.logo-text:hover {
  transform: translateY(-2px);
}

.logo-tagline {
  font-size: 12px;
  color: #666;
  margin-top: -2px;
}

.nav-menu {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.menu-items {
  background-color: transparent;
  border-bottom: none;
}

.menu-items :deep(.el-menu-item) {
  font-size: 15px;
  transition: all 0.3s ease;
}

.menu-items :deep(.el-menu-item:hover) {
  color: #43cea2;
}

.nav-buttons {
  margin-left: 20px;
}

.nav-buttons .el-button--primary {
  background: linear-gradient(135deg, #43cea2, #185a9d);
  border: none;
  transition: all 0.3s ease;
}

.nav-buttons .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(67, 206, 162, 0.4);
}

/* 英雄区域样式 */
.hero-section {
  padding: 180px 0 120px;
  background: linear-gradient(135deg, rgba(67, 206, 162, 0.1), rgba(24, 90, 157, 0.1));
  position: relative;
  overflow: hidden;
}

.animated-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 10% 20%, rgba(67, 206, 162, 0.05) 0%, rgba(24, 90, 157, 0.05) 90%);
  opacity: 0.8;
  animation: pulse 15s infinite alternate;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  100% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.hero-content {
  padding-right: 40px;
  animation: fadeInUp 1s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.badge {
  display: inline-block;
  padding: 6px 15px;
  background: linear-gradient(135deg, rgba(67, 206, 162, 0.2), rgba(24, 90, 157, 0.2));
  color: #185a9d;
  border-radius: 30px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 15px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.hero-title {
  font-size: 42px;
  font-weight: 800;
  color: #333;
  margin-bottom: 25px;
  line-height: 1.2;
  white-space: nowrap;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 18px;
  color: #555;
  margin-bottom: 25px;
  line-height: 1.8;
}

.hero-features {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 30px;
  font-size: 14px;
  color: #333;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  background-color: white;
}

.hero-buttons {
  display: flex;
  gap: 15px;
}

.hero-buttons .el-button--primary {
  background: linear-gradient(135deg, #43cea2, #185a9d);
  border: none;
  box-shadow: 0 5px 15px rgba(67, 206, 162, 0.3);
  transition: all 0.3s ease;
  padding: 12px 30px;
}

.hero-buttons .el-button--primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(67, 206, 162, 0.4);
}

.hero-image {
  position: relative;
  animation: fadeInRight 1s ease;
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.image-decoration {
  position: absolute;
  top: -30px;
  right: -30px;
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  border-radius: 50%;
  opacity: 0.1;
  z-index: -1;
}

.hero-image img {
  width: 100%;
  max-width: 500px;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  transition: all 0.5s ease;
  object-fit: cover;
  height: 600px;
}

.hero-image img:hover {
  transform: scale(1.02);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
}

.hero-carousel {
  width: 100%;
  max-width: 500px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.hero-carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-carousel :deep(.el-carousel__arrow) {
  background-color: rgba(67, 206, 162, 0.8);
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.hero-carousel :deep(.el-carousel__arrow:hover) {
  background-color: rgba(67, 206, 162, 1);
}

.hero-carousel :deep(.el-carousel__indicators) {
  bottom: 15px;
}

.hero-carousel :deep(.el-carousel__button) {
  background-color: rgba(67, 206, 162, 0.8);
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

/* 特点展示区域 */
.features-section {
  padding: 100px 0;
  background-color: white;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 15px;
  text-align: center;
  color: #333;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-subtitle {
  font-size: 18px;
  color: #666;
  margin-bottom: 50px;
  text-align: center;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  text-align: center;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  border-bottom: 3px solid #43cea2;
}

.feature-icon {
  font-size: 40px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

/* 内容区域样式 */
.content-section {
  padding: 100px 0;
  background: linear-gradient(135deg, rgba(67, 206, 162, 0.05), rgba(24, 90, 157, 0.05));
}

.content-wrapper {
  display: flex;
  align-items: center;
  gap: 50px;
}

.content-image {
  flex: 1;
}

.content-image img {
  width: 100%;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  transition: all 0.5s ease;
}

.content-image img:hover {
  transform: scale(1.03);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.content-text {
  flex: 1;
}

.content-text .section-title {
  text-align: left;
  margin-bottom: 20px;
}

.section-description {
  font-size: 16px;
  color: #555;
  line-height: 1.8;
  margin-bottom: 30px;
}

.stats-row {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #43cea2;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 用户评价区域 */
.testimonial-section {
  padding: 100px 0;
  background-color: white;
}

.testimonial-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.testimonial-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 20px;
  border: 3px solid #43cea2;
}

.testimonial-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.testimonial-content {
  text-align: center;
}

.testimonial-text {
  font-size: 16px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 20px;
  font-style: italic;
}

.testimonial-author {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.testimonial-role {
  font-size: 14px;
  color: #666;
}

/* 行动召唤区域 */
.cta-section {
  padding: 80px 0;
  background: linear-gradient(135deg, rgba(67, 206, 162, 0.1), rgba(24, 90, 157, 0.1));
}

.cta-card {
  background: white;
  border-radius: 20px;
  padding: 60px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.cta-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
  background: linear-gradient(135deg, #43cea2, #185a9d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.cta-description {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
}

.cta-card .el-button {
  background: linear-gradient(135deg, #43cea2, #185a9d);
  border: none;
  box-shadow: 0 10px 20px rgba(67, 206, 162, 0.3);
  padding: 12px 40px;
  font-size: 18px;
  transition: all 0.3s ease;
}

.cta-card .el-button:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(67, 206, 162, 0.4);
}

/* 页脚样式 */
.footer {
  padding: 40px 0;
  background-color: #2c3e50;
  background-image: linear-gradient(135deg, #2c3e50, #1a252f);
  color: #fff;
  text-align: center;
}

/* 认证弹窗样式 */
.auth-dialog {
  border-radius: 15px;
  overflow: hidden;
}

.auth-dialog :deep(.el-dialog__header) {
  display: none;
}

.auth-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.auth-dialog :deep(.el-dialog__headerbtn) {
  top: 15px;
  right: 15px;
  z-index: 10;
}

.auth-dialog :deep(.el-dialog__close) {
  color: #333;
  font-size: 18px;
}

/* 响应式样式 */
@media (max-width: 991px) {
  .hero-section {
    padding: 140px 0 80px;
  }

  .hero-content {
    padding-right: 0;
    margin-bottom: 50px;
  }

  .hero-title {
    font-size: 32px;
    white-space: nowrap;
  }

  .hero-description {
    font-size: 16px;
  }
}

@media (max-width: 767px) {
  .nav-buttons {
    display: none;
  }

  .hero-title {
    font-size: 26px;
    white-space: nowrap;
  }
}
</style>