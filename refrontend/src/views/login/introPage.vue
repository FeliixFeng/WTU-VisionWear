<template>
	<div class="intro-page">
		<!-- 装饰性背景元素 -->
		<div class="bg-decorations">
			<div class="decoration-circle decoration-1"></div>
			<div class="decoration-circle decoration-2"></div>
			<div class="decoration-circle decoration-3"></div>
			<div class="floating-shapes">
				<div class="shape shape-1"></div>
				<div class="shape shape-2"></div>
				<div class="shape shape-3"></div>
			</div>
		</div>

		<!-- 导航栏 -->
		<div
			class="nav-container"
			:class="{ 'nav-scrolled': navScrolled }"
		>
			<div class="container">
				<el-row
					class="nav-row"
					justify="space-between"
					align="middle"
				>
					<el-col :span="8">
						<div class="logo">
							<div class="logo-icon">
								<div class="logo-sparkle">✨</div>
							</div>
							<div class="logo-content">
								<span class="logo-text">VisionWear</span>
								<span class="logo-tagline">AI时尚先锋</span>
							</div>
						</div>
					</el-col>
					<el-col :span="16">
						<div class="nav-menu">
							<el-menu
								mode="horizontal"
								:ellipsis="false"
								class="menu-items"
							>
								<el-menu-item index="1">
									<span class="menu-text">功能</span>
									<div class="menu-underline"></div>
								</el-menu-item>
								<el-menu-item index="2">
									<span class="menu-text">价格</span>
									<div class="menu-underline"></div>
								</el-menu-item>
								<el-menu-item index="3">
									<span class="menu-text">关于我们</span>
									<div class="menu-underline"></div>
								</el-menu-item>
								<el-menu-item index="4">
									<span class="menu-text">联系我们</span>
									<div class="menu-underline"></div>
								</el-menu-item>
							</el-menu>
							<div class="nav-buttons">
								<el-button
									@click="showLoginDialog"
									type="text"
									class="login-btn"
									>登录</el-button
								>
								<el-button
									@click="showRegisterDialog"
									type="primary"
									class="register-btn"
								>
									<span>免费注册</span>
									<div class="btn-glow"></div>
								</el-button>
							</div>
						</div>
					</el-col>
				</el-row>
			</div>
		</div>

		<!-- 英雄区域 -->
		<div class="hero-section">
			<div class="hero-bg-animation"></div>
			<div class="hero-particles"></div>
			<div class="container">
				<el-row :gutter="100">
					<el-col
						:xs="24"
						:sm="24"
						:md="12"
						:lg="12"
						:xl="12"
					>
						<div
							class="hero-content"
							ref="heroContent"
						>
							<div class="badge-container">
								<div class="badge">
									<span class="badge-icon">🚀</span>
									<span>时尚科技</span>
									<div class="badge-shimmer"></div>
								</div>
							</div>
							<h1 class="hero-title">
								<span class="title-line">AI定义时尚</span>
								<span class="title-line">创意塑造风格</span>
								<div class="title-cursor"></div>
							</h1>
							<p class="hero-description">
								VisionWear将前沿AI技术与时尚设计完美融合，为您打造个性化穿搭方案与独特风格体验。
							</p>
							<div class="hero-features">
								<div class="feature-item">
									<div class="feature-icon">🎨</div>
									<span>智能设计方案</span>
									<div class="feature-ripple"></div>
								</div>
								<div class="feature-item">
									<div class="feature-icon">🔄</div>
									<span>个性化穿搭</span>
									<div class="feature-ripple"></div>
								</div>
								<div class="feature-item">
									<div class="feature-icon">💬</div>
									<span>时尚顾问</span>
									<div class="feature-ripple"></div>
								</div>
							</div>
							<div class="hero-buttons">
								<el-button
									type="primary"
									size="large"
									@click="showRegisterDialog"
									class="cta-primary"
								>
									<span>立即体验</span>
									<div class="btn-particles"></div>
								</el-button>
								<el-button
									type="info"
									size="large"
									plain
									class="cta-secondary"
								>
									<span>探索功能</span>
									<div class="btn-border-animation"></div>
								</el-button>
							</div>
						</div>
					</el-col>
					<el-col
						:xs="24"
						:sm="24"
						:md="12"
						:lg="12"
						:xl="12"
					>
						<div
							class="hero-image"
							ref="heroImage"
						>
							<div class="image-frame">
								<div class="frame-decoration"></div>
								<el-carousel
									:interval="4000"
									arrow="hover"
									height="500px"
									class="hero-carousel"
								>
									<el-carousel-item
										v-for="(item, index) in carouselImages"
										:key="index"
									>
										<div class="carousel-item-container">
											<img
												:src="item.src"
												:alt="item.alt"
												class="hero-carousel-image"
											/>
											<div class="carousel-overlay">
												<h3 class="carousel-title">{{ item.title }}</h3>
												<p class="carousel-description">
													{{ item.description }}
												</p>
											</div>
										</div>
									</el-carousel-item>
								</el-carousel>
							</div>
						</div>
					</el-col>
				</el-row>
			</div>
		</div>

		<!-- AI功能演示区域 -->
		<div class="demo-section">
			<div class="container">
				<div class="section-header">
					<h2 class="section-title">AI时尚魔法，一键体验</h2>
					<p class="section-subtitle">看看我们的AI如何为您打造完美穿搭</p>
					<div class="title-decoration"></div>
				</div>

				<div class="demo-container">
					<div
						class="demo-step"
						v-for="(step, index) in demoSteps"
						:key="index"
					>
						<div class="step-number">{{ index + 1 }}</div>
						<div class="step-icon">{{ step.icon }}</div>
						<h3 class="step-title">{{ step.title }}</h3>
						<p class="step-description">{{ step.description }}</p>
						<div class="step-animation">
							<div
								class="animation-dot"
								:style="{ animationDelay: index * 0.5 + 's' }"
							></div>
						</div>
					</div>
				</div>

				<div class="demo-showcase">
					<div class="showcase-item active">
						<div class="showcase-content">
							<h4>智能风格分析</h4>
							<p>上传您的照片，AI立即分析您的体型、肤色和个人风格偏好</p>
							<div class="progress-demo">
								<div
									class="progress-bar-demo"
									style="width: 85%"
								>
									<span class="progress-text">风格分析中... 85%</span>
								</div>
							</div>
						</div>
						<div class="showcase-visual">
							<div class="ai-brain">
								<div class="brain-pulse"></div>
								<span class="brain-icon">🧠</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 特点展示区域 -->
		<div
			class="features-section"
			ref="featuresSection"
		>
			<div class="container">
				<div class="section-header">
					<h2 class="section-title">时尚科技的完美融合</h2>
					<p class="section-subtitle">重新定义您的穿衣体验</p>
					<div class="title-decoration"></div>
				</div>

				<div class="features-grid">
					<div
						class="feature-card"
						v-for="(feature, index) in features"
						:key="index"
					>
						<div class="card-glow"></div>
						<div class="feature-icon-container">
							<div class="feature-icon">{{ feature.icon }}</div>
							<div class="icon-ring"></div>
						</div>
						<h3 class="feature-title">{{ feature.title }}</h3>
						<p class="feature-description">{{ feature.description }}</p>
						<div class="card-shine"></div>
					</div>
				</div>
			</div>
		</div>

		<!-- 数据展示区域 -->
		<div class="stats-section">
			<div class="container">
				<div class="stats-grid">
					<div
						class="stat-card"
						v-for="(stat, index) in stats"
						:key="index"
					>
						<div class="stat-icon">{{ stat.icon }}</div>
						<div
							class="stat-number"
							:data-target="stat.number"
						>
							{{ animatedStats[index] }}
						</div>
						<div class="stat-label">{{ stat.label }}</div>
						<div class="stat-progress">
							<div
								class="progress-bar"
								:style="{ width: stat.progress + '%' }"
							></div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 合作伙伴区域 -->
		<div class="partners-section">
			<div class="container">
				<h3 class="partners-title">全球时尚品牌信赖之选</h3>
				<div class="partners-grid">
					<div
						class="partner-item"
						v-for="(partner, index) in partners"
						:key="index"
					>
						<div class="partner-logo">{{ partner.name }}</div>
						<div class="partner-glow"></div>
					</div>
				</div>
			</div>
		</div>

		<!-- 内容区域样式 -->
		<div class="content-section">
			<div class="container">
				<div class="content-wrapper">
					<div class="content-image">
						<div class="image-container">
							<img
								src="https://img.freepik.com/free-photo/high-fashion-look-glamour-portrait-beautiful-sexy-stylish-caucasian-young-woman-model_158538-1400.jpg"
								alt="智能时尚"
							/>
							<div class="image-overlay">
								<div class="overlay-text">智能时尚</div>
							</div>
						</div>
					</div>
					<div class="content-text">
						<h2 class="section-title">智能时尚的新时代</h2>
						<p class="section-description">
							VisionWear
							利用最先进的人工智能技术，结合对时尚潮流的深入理解，为每位用户提供量身定制的时尚体验。
							无论您是追求个性风格还是希望跟上最新潮流，我们的平台都能满足您的需求。
						</p>
						<div class="features-list">
							<div class="feature-point">
								<div class="point-icon">✓</div>
								<span>AI驱动的个性化推荐</span>
							</div>
							<div class="feature-point">
								<div class="point-icon">✓</div>
								<span>实时时尚趋势分析</span>
							</div>
							<div class="feature-point">
								<div class="point-icon">✓</div>
								<span>虚拟试衣体验</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 用户评价区域 -->
		<div class="testimonial-section">
			<div class="container">
				<div class="section-header">
					<h2 class="section-title">用户评价</h2>
					<p class="section-subtitle">听听用户们怎么说</p>
					<div class="title-decoration"></div>
				</div>

				<el-carousel
					:interval="4000"
					type="card"
					height="350px"
					class="testimonial-carousel"
				>
					<el-carousel-item
						v-for="(item, index) in testimonials"
						:key="index"
					>
						<div class="testimonial-card">
							<div class="testimonial-quote">"</div>
							<div class="testimonial-content">
								<div class="rating-stars">
									<span
										v-for="i in 5"
										:key="i"
										class="star"
										>⭐</span
									>
								</div>
								<p class="testimonial-text">{{ item.text }}</p>
								<div class="testimonial-author">
									<div class="author-avatar">
										<img
											:src="item.avatar"
											:alt="item.name"
										/>
										<div class="verified-badge">✓</div>
									</div>
									<div class="author-info">
										<p class="author-name">{{ item.name }}</p>
										<p class="author-role">{{ item.role }}</p>
										<p class="review-date">{{ item.date }}</p>
									</div>
								</div>
							</div>
							<div class="card-gradient"></div>
						</div>
					</el-carousel-item>
				</el-carousel>
			</div>
		</div>

		<!-- FAQ区域 -->
		<div class="faq-section">
			<div class="container">
				<div class="section-header">
					<h2 class="section-title">常见问题</h2>
					<p class="section-subtitle">快速了解VisionWear</p>
					<div class="title-decoration"></div>
				</div>

				<div class="faq-container">
					<div
						class="faq-item"
						v-for="(faq, index) in faqs"
						:key="index"
						@click="toggleFaq(index)"
					>
						<div class="faq-question">
							<span>{{ faq.question }}</span>
							<div
								class="faq-icon"
								:class="{ active: faq.open }"
							>
								+
							</div>
						</div>
						<div
							class="faq-answer"
							:class="{ open: faq.open }"
						>
							<p>{{ faq.answer }}</p>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 行动召唤区域 -->
		<div class="cta-section">
			<div class="cta-bg-animation"></div>
			<div class="container">
				<div class="cta-card">
					<div class="cta-icon">🚀</div>
					<h2 class="cta-title">准备好开始您的时尚旅程了吗？</h2>
					<p class="cta-description">立即注册，体验AI驱动的智能时尚生活方式</p>
					<el-button
						type="primary"
						size="large"
						@click="showRegisterDialog"
						class="cta-button"
					>
						<span>免费开始</span>
						<div class="btn-sparkle"></div>
					</el-button>
				</div>
			</div>
		</div>

		<!-- 简化的页脚 -->
		<div class="footer">
			<div class="container">
				<div class="footer-content">
					<div class="footer-logo">
						<span class="logo-text">VisionWear</span>
						<span class="logo-tagline">AI时尚先锋</span>
					</div>
					<p class="footer-text">
						&copy; {{ new Date().getFullYear() }} VisionWear. 保留所有权利。
					</p>
				</div>
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
			<LoginPage
				v-if="loginDialogVisible"
				:is-dialog="true"
				@switch-to-register="switchToRegister"
				@switch-to-forgot-password="switchToForgotPassword"
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
			<ForgotPasswordPage
				v-if="forgotPasswordDialogVisible"
				:is-dialog="true"
				@switch-to-login="switchToLogin"
			/>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue"
// 导入您修改后的登录和注册页面组件
import LoginPage from "./LoginPage.vue"
import RegisterPage from "./RegisterPage.vue"
import ForgotPasswordPage from "./ForgotPasswordPage.vue"

// 所有状态变量
const loginDialogVisible = ref(false)
const registerDialogVisible = ref(false)
const forgotPasswordDialogVisible = ref(false)
const navScrolled = ref(false)
const animatedStats = ref([0, 0, 0])

// 所有数据
const demoSteps = ref([
	{
		icon: "📸",
		title: "上传照片",
		description: "简单上传您的照片，AI开始分析",
	},
	{
		icon: "🤖",
		title: "AI分析",
		description: "智能识别体型、肤色、风格偏好",
	},
	{
		icon: "🎨",
		title: "生成方案",
		description: "个性化穿搭方案即时生成",
	},
	{
		icon: "✨",
		title: "完美呈现",
		description: "获得专属的时尚搭配建议",
	},
])

const partners = ref([
	{ name: "ZARA" },
	{ name: "H&M" },
	{ name: "UNIQLO" },
	{ name: "Nike" },
	{ name: "Adidas" },
	{ name: "Gucci" },
])

const faqs = ref([
	{
		question: "VisionWear的AI技术准确度如何？",
		answer:
			"我们的AI经过数百万时尚数据训练，准确度达到95%以上，能够精准理解您的个人风格和偏好。",
		open: false,
	},
	{
		question: "使用VisionWear需要付费吗？",
		answer:
			"我们提供免费的基础功能，包括基本的穿搭建议。高级功能如个性化定制、专业搭配师咨询等需要升级会员。",
		open: false,
	},
	{
		question: "我的个人数据安全吗？",
		answer:
			"我们采用银行级别的数据加密技术，严格遵守数据保护法规，您的所有个人信息都将得到最高级别的保护。",
		open: false,
	},
	{
		question: "支持哪些设备使用？",
		answer:
			"VisionWear支持所有主流设备，包括手机、平板、电脑，同时提供iOS和Android原生应用。",
		open: false,
	},
])

const carouselImages = ref([
	{
		src: "https://img.freepik.com/free-photo/full-length-portrait-smiling-pretty-woman-isolated-white-wall_171337-9592.jpg",
		alt: "时尚穿搭",
		title: "春季新风尚",
		description: "轻盈柔和的色彩，展现春日清新活力",
	},
	{
		src: "https://img.freepik.com/free-photo/stylish-caucasian-fashion-influencer-with-dark-hair-posing-yellow-suit-blue-background_273443-2579.jpg",
		alt: "简约风格",
		title: "简约不简单",
		description: "极简主义设计，彰显个性魅力",
	},
	{
		src: "https://img.freepik.com/free-photo/pretty-young-stylish-sexy-woman-pink-luxury-dress-summer-fashion-trend-chic-style-sunglasses-blue-studio-background-shopping-holding-paper-bags-talking-mobile-phone-shopaholic_285396-2957.jpg",
		alt: "潮流购物",
		title: "探索时尚边界",
		description: "突破传统，创造属于自己的风格语言",
	},
	{
		src: "https://img.freepik.com/free-photo/full-length-portrait-happy-excited-girl-bright-colorful-clothes-sunglasses-isolated_231208-5946.jpg",
		alt: "多彩穿搭",
		title: "色彩的力量",
		description: "用大胆的色彩组合表达自我态度",
	},
	{
		src: "https://img.freepik.com/free-photo/high-fashion-look-glamor-closeup-portrait-beautiful-black-young-woman-model-with-yellow-bright-makeup-with-perfect-clean-skin-with-gold-jewelery-isolated-bright-pink_158538-21748.jpg",
		alt: "高级定制",
		title: "精致细节之美",
		description: "每一处细节都经过精心设计，打造完美形象",
	},
])

const features = ref([
	{
		icon: "👔",
		title: "个性化风格定制",
		description: "基于您的喜好、体型与场合需求，智能推荐最适合您的穿搭方案",
	},
	{
		icon: "🎨",
		title: "色彩搭配顾问",
		description: "专业的色彩分析系统，让您的穿搭更加和谐、突显个人气质",
	},
	{
		icon: "💫",
		title: "趋势预测分析",
		description: "实时捕捉全球时尚动向，让您走在潮流前沿，展现前卫品味",
	},
	{
		icon: "📱",
		title: "虚拟换装体验",
		description: "通过AR技术在线尝试不同搭配，轻松找到最适合的衣着风格",
	},
])

const stats = ref([
	{ icon: "👥", number: 5000, label: "用户选择", progress: 85 },
	{ icon: "⭐", number: 98, label: "满意度", progress: 98 },
	{ icon: "🕒", number: 24, label: "小时客户支持", progress: 100 },
])

const testimonials = ref([
	{
		name: "张小姐",
		role: "时尚博主",
		avatar: "https://randomuser.me/api/portraits/women/44.jpg",
		text: "VisionWear彻底改变了我的穿衣方式！AI推荐的搭配总是令人惊艳，省去了我大量选择服装的时间。",
		date: "2024年12月",
	},
	{
		name: "李先生",
		role: "企业经理",
		avatar: "https://randomuser.me/api/portraits/men/32.jpg",
		text: "作为一个对时尚了解不多的人，VisionWear为我提供了专业级别的穿搭建议，让我在各种场合都能得体亮相。",
		date: "2024年11月",
	},
	{
		name: "王女士",
		role: "服装设计师",
		avatar: "https://randomuser.me/api/portraits/women/68.jpg",
		text: "平台的AI算法非常智能，能够精准捕捉最新的时尚趋势，作为设计师的我都从中获得了很多灵感！",
		date: "2024年12月",
	},
	{
		name: "刘先生",
		role: "创业者",
		avatar: "https://randomuser.me/api/portraits/men/75.jpg",
		text: "界面简洁易用，AI推荐很精准。现在我的衣橱利用率提高了80%，每天穿搭都很有自信！",
		date: "2025年1月",
	},
])

// 所有方法
const toggleFaq = (index) => {
	faqs.value[index].open = !faqs.value[index].open
}

const handleScroll = () => {
	navScrolled.value = window.scrollY > 50
}

const animateNumbers = () => {
	stats.value.forEach((stat, index) => {
		let current = 0
		const target = stat.number
		const increment = target / 100
		const timer = setInterval(() => {
			current += increment
			if (current >= target) {
				current = target
				clearInterval(timer)
			}
			animatedStats.value[index] = Math.floor(current)
		}, 20)
	})
}

const showLoginDialog = () => {
	loginDialogVisible.value = true
	registerDialogVisible.value = false
	forgotPasswordDialogVisible.value = false
}

const showRegisterDialog = () => {
	registerDialogVisible.value = true
	loginDialogVisible.value = false
	forgotPasswordDialogVisible.value = false
}

const switchToLogin = () => {
	loginDialogVisible.value = true
	registerDialogVisible.value = false
	forgotPasswordDialogVisible.value = false
}

const switchToRegister = () => {
	registerDialogVisible.value = true
	loginDialogVisible.value = false
	forgotPasswordDialogVisible.value = false
}

const switchToForgotPassword = () => {
	forgotPasswordDialogVisible.value = true
	loginDialogVisible.value = false
	registerDialogVisible.value = false
}

onMounted(() => {
	window.addEventListener("scroll", handleScroll)
	setTimeout(animateNumbers, 1000)
})

onUnmounted(() => {
	window.removeEventListener("scroll", handleScroll)
})
</script>

<style scoped>
/* 全局样式 */
* {
	box-sizing: border-box;
}

.container {
	max-width: 1400px;
	margin: 0 auto;
	padding: 0 20px;
}

/* 背景装饰元素 */
.bg-decorations {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	pointer-events: none;
	z-index: -1;
	overflow: hidden;
}

.decoration-circle {
	position: absolute;
	border-radius: 50%;
	background: linear-gradient(
		135deg,
		rgba(67, 206, 162, 0.1),
		rgba(24, 90, 157, 0.1)
	);
	animation: float 20s infinite ease-in-out;
}

.decoration-1 {
	width: 300px;
	height: 300px;
	top: 10%;
	right: -150px;
	animation-delay: 0s;
}

.decoration-2 {
	width: 200px;
	height: 200px;
	top: 60%;
	left: -100px;
	animation-delay: 5s;
}

.decoration-3 {
	width: 150px;
	height: 150px;
	bottom: 20%;
	right: 10%;
	animation-delay: 10s;
}

.floating-shapes {
	position: absolute;
	width: 100%;
	height: 100%;
}

.shape {
	position: absolute;
	opacity: 0.1;
	animation: drift 15s infinite linear;
}

.shape-1 {
	width: 0;
	height: 0;
	border-left: 20px solid transparent;
	border-right: 20px solid transparent;
	border-bottom: 35px solid #43cea2;
	top: 20%;
	left: 20%;
	animation-delay: 0s;
}

.shape-2 {
	width: 30px;
	height: 30px;
	background: #185a9d;
	top: 70%;
	right: 30%;
	animation-delay: 7s;
}

.shape-3 {
	width: 25px;
	height: 25px;
	background: #43cea2;
	border-radius: 50%;
	top: 40%;
	left: 80%;
	animation-delay: 14s;
}

@keyframes float {
	0%,
	100% {
		transform: translateY(0px) rotate(0deg);
	}
	50% {
		transform: translateY(-20px) rotate(180deg);
	}
}

@keyframes drift {
	0% {
		transform: translateX(-100px) rotate(0deg);
	}
	100% {
		transform: translateX(calc(100vw + 100px)) rotate(360deg);
	}
}

/* 导航栏样式 */
.nav-container {
	height: 80px;
	background-color: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(20px);
	-webkit-backdrop-filter: blur(20px);
	border-bottom: 1px solid rgba(255, 255, 255, 0.2);
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	z-index: 1000;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-container.nav-scrolled {
	background-color: rgba(255, 255, 255, 0.95);
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
	height: 70px;
}

.nav-row {
	height: 100%;
}

.logo {
	display: flex;
	align-items: center;
	gap: 12px;
}

.logo-icon {
	width: 45px;
	height: 45px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	overflow: hidden;
}

.logo-icon::before {
	content: "";
	position: absolute;
	top: -50%;
	left: -50%;
	width: 200%;
	height: 200%;
	background: linear-gradient(
		45deg,
		transparent,
		rgba(255, 255, 255, 0.3),
		transparent
	);
	animation: shine 3s infinite;
}

.logo-sparkle {
	font-size: 20px;
	animation: sparkle 2s infinite;
}

.logo-content {
	display: flex;
	flex-direction: column;
}

.logo-text {
	font-size: 24px;
	font-weight: 800;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
	transition: all 0.3s ease;
}

.logo-tagline {
	font-size: 11px;
	color: #666;
	font-weight: 500;
	margin-top: -2px;
}

.nav-menu {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	gap: 30px;
}

.menu-items {
	background-color: transparent;
	border-bottom: none;
}

.menu-items :deep(.el-menu-item) {
	font-size: 15px;
	font-weight: 500;
	position: relative;
	padding: 0 20px;
	border-bottom: none !important;
}

.menu-text {
	position: relative;
	z-index: 2;
}

.menu-underline {
	position: absolute;
	bottom: -5px;
	left: 50%;
	width: 0;
	height: 2px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	transition: all 0.3s ease;
	transform: translateX(-50%);
}

.menu-items :deep(.el-menu-item:hover) .menu-underline {
	width: 100%;
}

.nav-buttons {
	display: flex;
	align-items: center;
	gap: 15px;
}

.login-btn {
	font-weight: 500;
	transition: all 0.3s ease;
}

.login-btn:hover {
	color: #43cea2;
	transform: translateY(-1px);
}

.register-btn {
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
	border-radius: 25px;
	padding: 10px 25px;
	position: relative;
	overflow: hidden;
	transition: all 0.3s ease;
}

.register-btn:hover {
	transform: translateY(-2px);
	box-shadow: 0 8px 25px rgba(67, 206, 162, 0.4);
}

.btn-glow {
	position: absolute;
	top: -50%;
	left: -50%;
	width: 200%;
	height: 200%;
	background: radial-gradient(
		circle,
		rgba(255, 255, 255, 0.3) 0%,
		transparent 70%
	);
	opacity: 0;
	transition: opacity 0.3s ease;
}

.register-btn:hover .btn-glow {
	opacity: 1;
	animation: glow-pulse 1.5s infinite;
}

/* 英雄区域样式 */
.hero-section {
	min-height: 100vh;
	padding: 120px 0 80px;
	background: linear-gradient(
		135deg,
		rgba(67, 206, 162, 0.05),
		rgba(24, 90, 157, 0.05)
	);
	position: relative;
	overflow: hidden;
	display: flex;
	align-items: center;
}

.hero-bg-animation {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: radial-gradient(
			circle at 20% 80%,
			rgba(67, 206, 162, 0.1) 0%,
			transparent 50%
		),
		radial-gradient(
			circle at 80% 20%,
			rgba(24, 90, 157, 0.1) 0%,
			transparent 50%
		);
	animation: bg-float 20s infinite ease-in-out;
}

.hero-particles {
	position: absolute;
	width: 100%;
	height: 100%;
	background-image: radial-gradient(
			2px 2px at 20px 30px,
			rgba(67, 206, 162, 0.3),
			transparent
		),
		radial-gradient(2px 2px at 40px 70px, rgba(24, 90, 157, 0.3), transparent),
		radial-gradient(1px 1px at 90px 40px, rgba(67, 206, 162, 0.4), transparent);
	background-repeat: repeat;
	background-size: 100px 100px;
	animation: particle-float 15s infinite linear;
}

.hero-content {
	animation: slide-in-left 1s ease-out;
	padding-right: 30px;
}

.badge-container {
	margin-bottom: 25px;
}

.badge {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	padding: 10px 20px;
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	border: 1px solid rgba(67, 206, 162, 0.2);
	border-radius: 50px;
	font-size: 14px;
	font-weight: 600;
	color: #185a9d;
	position: relative;
	overflow: hidden;
	box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.badge-icon {
	animation: bounce 2s infinite;
}

.badge-shimmer {
	position: absolute;
	top: -50%;
	left: -50%;
	width: 200%;
	height: 200%;
	background: linear-gradient(
		45deg,
		transparent,
		rgba(255, 255, 255, 0.5),
		transparent
	);
	animation: shimmer 3s infinite;
}

.hero-title {
	font-size: 52px;
	font-weight: 900;
	margin-bottom: 30px;
	line-height: 1.1;
	position: relative;
}

.title-line {
	display: block;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
	animation: title-glow 3s ease-in-out infinite alternate;
}

.title-cursor {
	display: inline-block;
	width: 3px;
	height: 50px;
	background: #43cea2;
	margin-left: 5px;
	animation: blink 1s infinite;
}

.hero-description {
	font-size: 18px;
	color: #555;
	margin-bottom: 35px;
	line-height: 1.7;
	opacity: 0;
	animation: fade-in 1s ease-out 0.5s forwards;
}

.hero-features {
	display: flex;
	flex-wrap: wrap;
	gap: 15px;
	margin-bottom: 40px;
	opacity: 0;
	animation: fade-in 1s ease-out 0.7s forwards;
}

.feature-item {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 12px 20px;
	background: rgba(255, 255, 255, 0.8);
	backdrop-filter: blur(10px);
	border-radius: 50px;
	font-size: 14px;
	font-weight: 500;
	color: #333;
	box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
	position: relative;
	overflow: hidden;
	cursor: pointer;
}

.feature-item:hover {
	transform: translateY(-3px);
	box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
	background: rgba(255, 255, 255, 0.95);
}

.feature-icon {
	font-size: 16px;
	animation: icon-spin 3s infinite ease-in-out;
}

.feature-ripple {
	position: absolute;
	top: 50%;
	left: 50%;
	width: 0;
	height: 0;
	background: radial-gradient(
		circle,
		rgba(67, 206, 162, 0.3) 0%,
		transparent 70%
	);
	transition: all 0.6s ease;
	transform: translate(-50%, -50%);
	border-radius: 50%;
}

.feature-item:hover .feature-ripple {
	width: 200px;
	height: 200px;
}

.hero-buttons {
	display: flex;
	gap: 20px;
	opacity: 0;
	animation: fade-in 1s ease-out 0.9s forwards;
}

.cta-primary {
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
	border-radius: 50px;
	padding: 15px 35px;
	font-size: 16px;
	font-weight: 600;
	position: relative;
	overflow: hidden;
	transition: all 0.3s ease;
	box-shadow: 0 6px 20px rgba(67, 206, 162, 0.3);
}

.cta-primary:hover {
	transform: translateY(-3px);
	box-shadow: 0 10px 30px rgba(67, 206, 162, 0.4);
}

.cta-secondary {
	border: 2px solid #43cea2;
	border-radius: 50px;
	padding: 13px 35px;
	font-size: 16px;
	font-weight: 600;
	color: #43cea2;
	position: relative;
	overflow: hidden;
	transition: all 0.3s ease;
}

.cta-secondary:hover {
	background: #43cea2;
	color: white;
	transform: translateY(-3px);
}

.btn-border-animation {
	position: absolute;
	top: 0;
	left: -100%;
	width: 100%;
	height: 100%;
	background: linear-gradient(
		90deg,
		transparent,
		rgba(67, 206, 162, 0.2),
		transparent
	);
	transition: all 0.5s ease;
}

.cta-secondary:hover .btn-border-animation {
	left: 100%;
}

.btn-particles {
	position: absolute;
	top: 50%;
	left: 50%;
	width: 5px;
	height: 5px;
	background: rgba(255, 255, 255, 0.8);
	border-radius: 50%;
	transform: translate(-50%, -50%);
	opacity: 0;
}

.cta-primary:hover .btn-particles {
	animation: particle-burst 0.6s ease-out;
}

/* 英雄图片样式 */
.hero-image {
	animation: slide-in-right 1s ease-out;
	padding-left: 30px;
}

.image-frame {
	position: relative;
	border-radius: 20px;
	overflow: hidden;
	box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.frame-decoration {
	position: absolute;
	top: -10px;
	right: -10px;
	width: 60px;
	height: 60px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 50%;
	z-index: 10;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 20px;
}

.frame-decoration::before {
	content: "✨";
	animation: sparkle 2s infinite;
}

.hero-carousel {
	border-radius: 20px;
	overflow: hidden;
}

.carousel-item-container {
	position: relative;
	height: 100%;
}

.hero-carousel-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
	transition: transform 0.5s ease;
}

.carousel-item-container:hover .hero-carousel-image {
	transform: scale(1.05);
}

.carousel-overlay {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
	color: white;
	padding: 30px;
	transform: translateY(100%);
	transition: all 0.3s ease;
}

.carousel-item-container:hover .carousel-overlay {
	transform: translateY(0);
}

.carousel-title {
	font-size: 18px;
	font-weight: 600;
	margin-bottom: 8px;
}

.carousel-description {
	font-size: 14px;
	opacity: 0.9;
	line-height: 1.5;
}

/* AI功能演示区域 */
.demo-section {
	padding: 120px 0;
	background: linear-gradient(
		135deg,
		rgba(67, 206, 162, 0.02),
		rgba(24, 90, 157, 0.02)
	);
	position: relative;
	overflow: hidden;
}

.demo-section::before {
	content: "";
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(67,206,162,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
	opacity: 0.3;
}

.demo-container {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
	gap: 40px;
	margin-bottom: 80px;
	position: relative;
	z-index: 2;
}

.demo-step {
	text-align: center;
	padding: 40px 20px;
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	border-radius: 20px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
	transition: all 0.4s ease;
	position: relative;
	overflow: hidden;
	cursor: pointer;
}

.demo-step:hover {
	transform: translateY(-15px);
	box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.step-number {
	position: absolute;
	top: -15px;
	right: -15px;
	width: 40px;
	height: 40px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-weight: bold;
	font-size: 16px;
	box-shadow: 0 5px 15px rgba(67, 206, 162, 0.4);
}

.step-icon {
	font-size: 50px;
	margin-bottom: 20px;
	animation: bounce-gentle 3s infinite ease-in-out;
}

.step-title {
	font-size: 20px;
	font-weight: 600;
	margin-bottom: 15px;
	color: #333;
}

.step-description {
	color: #666;
	line-height: 1.6;
	font-size: 14px;
}

.step-animation {
	position: absolute;
	bottom: 20px;
	left: 50%;
	transform: translateX(-50%);
}

.animation-dot {
	width: 8px;
	height: 8px;
	background: #43cea2;
	border-radius: 50%;
	animation: pulse-dot 2s infinite ease-in-out;
}

.demo-showcase {
	background: white;
	border-radius: 20px;
	padding: 40px;
	box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
	position: relative;
	z-index: 2;
}

.showcase-item {
	display: grid;
	grid-template-columns: 1fr auto;
	gap: 40px;
	align-items: center;
}

.showcase-content h4 {
	font-size: 24px;
	font-weight: 600;
	margin-bottom: 15px;
	color: #333;
}

.showcase-content p {
	color: #666;
	line-height: 1.6;
	margin-bottom: 20px;
}

.progress-demo {
	background: #f0f0f0;
	border-radius: 25px;
	height: 30px;
	overflow: hidden;
	position: relative;
}

.progress-bar-demo {
	height: 100%;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 25px;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: width 3s ease;
	animation: progress-shimmer 2s infinite;
}

.progress-text {
	color: white;
	font-size: 14px;
	font-weight: 600;
}

.showcase-visual {
	display: flex;
	justify-content: center;
	align-items: center;
}

.ai-brain {
	width: 120px;
	height: 120px;
	border-radius: 50%;
	background: linear-gradient(
		135deg,
		rgba(67, 206, 162, 0.2),
		rgba(24, 90, 157, 0.2)
	);
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	animation: brain-float 4s infinite ease-in-out;
}

.brain-pulse {
	position: absolute;
	top: -10px;
	left: -10px;
	right: -10px;
	bottom: -10px;
	border: 2px solid rgba(67, 206, 162, 0.3);
	border-radius: 50%;
	animation: pulse-ring 2s infinite ease-out;
}

.brain-pulse::before {
	content: "";
	position: absolute;
	top: -15px;
	left: -15px;
	right: -15px;
	bottom: -15px;
	border: 1px solid rgba(67, 206, 162, 0.2);
	border-radius: 50%;
	animation: pulse-ring 2s infinite 0.5s ease-out;
}

.brain-icon {
	font-size: 40px;
	z-index: 2;
}

/* 特性展示区域 */
.features-section {
	padding: 120px 0;
	background: white;
	position: relative;
}

.section-header {
	text-align: center;
	margin-bottom: 80px;
	position: relative;
}

.section-title {
	font-size: 42px;
	font-weight: 800;
	margin-bottom: 20px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
	position: relative;
}

.section-subtitle {
	font-size: 18px;
	color: #666;
	margin-bottom: 30px;
}

.title-decoration {
	width: 80px;
	height: 4px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	margin: 0 auto;
	border-radius: 2px;
	position: relative;
}

.title-decoration::before {
	content: "";
	position: absolute;
	top: -2px;
	left: -10px;
	right: -10px;
	bottom: -2px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 4px;
	opacity: 0.3;
	animation: pulse-glow 2s infinite;
}

.features-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
	gap: 40px;
}

.feature-card {
	background: white;
	border-radius: 20px;
	padding: 40px 30px;
	text-align: center;
	position: relative;
	overflow: hidden;
	transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
	border: 1px solid rgba(67, 206, 162, 0.1);
	cursor: pointer;
}

.feature-card:hover {
	transform: translateY(-15px) scale(1.02);
	box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
	border-color: rgba(67, 206, 162, 0.3);
}

.card-glow {
	position: absolute;
	top: -50%;
	left: -50%;
	width: 200%;
	height: 200%;
	background: radial-gradient(
		circle,
		rgba(67, 206, 162, 0.1) 0%,
		transparent 70%
	);
	opacity: 0;
	transition: opacity 0.4s ease;
}

.feature-card:hover .card-glow {
	opacity: 1;
}

.feature-icon-container {
	width: 80px;
	height: 80px;
	margin: 0 auto 25px;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
}

.feature-icon {
	font-size: 35px;
	z-index: 2;
	position: relative;
}

.icon-ring {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	border: 2px solid rgba(67, 206, 162, 0.2);
	border-radius: 50%;
	transition: all 0.4s ease;
}

.feature-card:hover .icon-ring {
	border-color: #43cea2;
	transform: scale(1.2);
	animation: ring-pulse 1.5s infinite;
}

.feature-title {
	font-size: 20px;
	font-weight: 600;
	margin-bottom: 15px;
	color: #333;
}

.feature-description {
	color: #666;
	line-height: 1.6;
	font-size: 15px;
}

.card-shine {
	position: absolute;
	top: -50%;
	left: -50%;
	width: 200%;
	height: 200%;
	background: linear-gradient(
		45deg,
		transparent,
		rgba(255, 255, 255, 0.1),
		transparent
	);
	opacity: 0;
	transition: all 0.6s ease;
}

.feature-card:hover .card-shine {
	opacity: 1;
	animation: shine-sweep 1s ease-out;
}

/* 统计数据区域 */
.stats-section {
	padding: 80px 0;
	background: linear-gradient(
		135deg,
		rgba(67, 206, 162, 0.05),
		rgba(24, 90, 157, 0.05)
	);
}

.stats-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
	gap: 40px;
}

.stat-card {
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	border-radius: 20px;
	padding: 40px 30px;
	text-align: center;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
	border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-card:hover {
	transform: translateY(-10px);
	box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
	font-size: 40px;
	margin-bottom: 20px;
	display: block;
}

.stat-number {
	font-size: 48px;
	font-weight: 800;
	color: #43cea2;
	margin-bottom: 10px;
	font-family: "Monaco", monospace;
}

.stat-label {
	font-size: 16px;
	color: #666;
	font-weight: 500;
	margin-bottom: 20px;
}

.stat-progress {
	width: 100%;
	height: 4px;
	background: rgba(67, 206, 162, 0.2);
	border-radius: 2px;
	overflow: hidden;
}

.progress-bar {
	height: 100%;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 2px;
	transition: width 2s ease;
	animation: progress-glow 3s infinite;
}

/* 合作伙伴区域 */
.partners-section {
	padding: 60px 0;
	background: white;
	border-top: 1px solid rgba(67, 206, 162, 0.1);
	border-bottom: 1px solid rgba(67, 206, 162, 0.1);
}

.partners-title {
	text-align: center;
	font-size: 18px;
	color: #666;
	margin-bottom: 40px;
	font-weight: 500;
}

.partners-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
	gap: 40px;
	align-items: center;
}

.partner-item {
	text-align: center;
	position: relative;
	padding: 20px;
	transition: all 0.3s ease;
	cursor: pointer;
}

.partner-item:hover {
	transform: translateY(-5px);
}

.partner-logo {
	font-size: 20px;
	font-weight: 700;
	color: #999;
	transition: all 0.3s ease;
	letter-spacing: 2px;
}

.partner-item:hover .partner-logo {
	color: #43cea2;
}

.partner-glow {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: radial-gradient(
		circle,
		rgba(67, 206, 162, 0.1) 0%,
		transparent 70%
	);
	opacity: 0;
	transition: opacity 0.3s ease;
}

.partner-item:hover .partner-glow {
	opacity: 1;
}

/* 内容区域 */
.content-section {
	padding: 120px 0;
	background: white;
}

.content-wrapper {
	display: flex;
	align-items: center;
	gap: 80px;
}

.content-image {
	flex: 1;
}

.image-container {
	position: relative;
	border-radius: 20px;
	overflow: hidden;
	box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.image-container img {
	width: 100%;
	transition: transform 0.5s ease;
}

.image-container:hover img {
	transform: scale(1.05);
}

.image-overlay {
	position: absolute;
	bottom: 20px;
	left: 20px;
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	padding: 15px 25px;
	border-radius: 10px;
	color: #333;
	font-weight: 600;
	transform: translateY(100%);
	transition: all 0.3s ease;
}

.image-container:hover .image-overlay {
	transform: translateY(0);
}

.content-text {
	flex: 1;
}

.content-text .section-title {
	text-align: left;
	margin-bottom: 25px;
	font-size: 36px;
}

.section-description {
	font-size: 16px;
	color: #555;
	line-height: 1.8;
	margin-bottom: 35px;
}

.features-list {
	display: flex;
	flex-direction: column;
	gap: 15px;
}

.feature-point {
	display: flex;
	align-items: center;
	gap: 15px;
	font-size: 16px;
	color: #333;
}

.point-icon {
	width: 25px;
	height: 25px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 12px;
	font-weight: bold;
	flex-shrink: 0;
}

/* 用户评价区域 */
.testimonial-section {
	padding: 120px 0;
	background: linear-gradient(
		135deg,
		rgba(67, 206, 162, 0.03),
		rgba(24, 90, 157, 0.03)
	);
}

.testimonial-carousel {
	margin-top: 60px;
}

.testimonial-card {
	background: white;
	border-radius: 20px;
	padding: 40px;
	height: 100%;
	position: relative;
	overflow: hidden;
	box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
}

.testimonial-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.testimonial-quote {
	font-size: 60px;
	color: #43cea2;
	opacity: 0.3;
	position: absolute;
	top: 20px;
	right: 30px;
	font-family: serif;
}

.rating-stars {
	margin-bottom: 20px;
}

.star {
	font-size: 16px;
	margin-right: 2px;
	animation: star-twinkle 2s infinite ease-in-out;
}

.star:nth-child(1) {
	animation-delay: 0s;
}
.star:nth-child(2) {
	animation-delay: 0.2s;
}
.star:nth-child(3) {
	animation-delay: 0.4s;
}
.star:nth-child(4) {
	animation-delay: 0.6s;
}
.star:nth-child(5) {
	animation-delay: 0.8s;
}

.testimonial-content {
	position: relative;
	z-index: 2;
}

.testimonial-text {
	font-size: 16px;
	color: #555;
	line-height: 1.7;
	margin-bottom: 30px;
	font-style: italic;
}

.testimonial-author {
	display: flex;
	align-items: center;
	gap: 15px;
}

.author-avatar {
	width: 50px;
	height: 50px;
	border-radius: 50%;
	overflow: hidden;
	border: 3px solid #43cea2;
	position: relative;
}

.author-avatar::before {
	content: "";
	position: absolute;
	top: -2px;
	left: -2px;
	right: -2px;
	bottom: -2px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 50%;
	z-index: -1;
	animation: avatar-glow 3s infinite;
}

.author-avatar img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.verified-badge {
	position: absolute;
	bottom: -5px;
	right: -5px;
	width: 20px;
	height: 20px;
	background: #43cea2;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 10px;
	font-weight: bold;
	border: 2px solid white;
}

.author-info {
	flex: 1;
}

.author-name {
	font-size: 16px;
	font-weight: 600;
	color: #333;
	margin-bottom: 3px;
}

.author-role {
	font-size: 14px;
	color: #666;
	margin-bottom: 2px;
}

.review-date {
	font-size: 12px;
	color: #999;
}

.card-gradient {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	height: 100px;
	background: linear-gradient(transparent, rgba(67, 206, 162, 0.05));
	opacity: 0;
	transition: opacity 0.3s ease;
}

.testimonial-card:hover .card-gradient {
	opacity: 1;
}

/* FAQ区域 */
.faq-section {
	padding: 120px 0;
	background: white;
}

.faq-container {
	max-width: 800px;
	margin: 0 auto;
}

.faq-item {
	background: rgba(255, 255, 255, 0.9);
	border: 1px solid rgba(67, 206, 162, 0.1);
	border-radius: 15px;
	margin-bottom: 20px;
	overflow: hidden;
	transition: all 0.3s ease;
	cursor: pointer;
}

.faq-item:hover {
	border-color: rgba(67, 206, 162, 0.3);
	box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.faq-question {
	padding: 25px 30px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 16px;
	font-weight: 600;
	color: #333;
	transition: all 0.3s ease;
}

.faq-item:hover .faq-question {
	color: #43cea2;
}

.faq-icon {
	width: 30px;
	height: 30px;
	border: 2px solid #43cea2;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 18px;
	font-weight: bold;
	color: #43cea2;
	transition: all 0.3s ease;
}

.faq-icon.active {
	transform: rotate(45deg);
	background: #43cea2;
	color: white;
}

.faq-answer {
	max-height: 0;
	overflow: hidden;
	transition: max-height 0.3s ease;
	background: rgba(67, 206, 162, 0.02);
}

.faq-answer.open {
	max-height: 200px;
}

.faq-answer p {
	padding: 0 30px 25px;
	color: #666;
	line-height: 1.6;
	margin: 0;
}

/* 行动召唤区域 */
.cta-section {
	padding: 100px 0;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	position: relative;
	overflow: hidden;
}

.cta-bg-animation {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: radial-gradient(
			circle at 20% 80%,
			rgba(255, 255, 255, 0.1) 0%,
			transparent 50%
		),
		radial-gradient(
			circle at 80% 20%,
			rgba(255, 255, 255, 0.1) 0%,
			transparent 50%
		);
	animation: bg-float 15s infinite ease-in-out;
}

.cta-card {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 30px;
	padding: 80px 60px;
	text-align: center;
	position: relative;
	box-shadow: 0 30px 60px rgba(0, 0, 0, 0.2);
	border: 1px solid rgba(255, 255, 255, 0.3);
}

.cta-icon {
	font-size: 60px;
	margin-bottom: 30px;
	animation: float-icon 3s infinite ease-in-out;
}

.cta-title {
	font-size: 36px;
	font-weight: 800;
	color: #333;
	margin-bottom: 20px;
	line-height: 1.2;
}

.cta-description {
	font-size: 18px;
	color: #666;
	margin-bottom: 40px;
	line-height: 1.6;
}

.cta-button {
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
	border-radius: 50px;
	padding: 18px 45px;
	font-size: 18px;
	font-weight: 600;
	position: relative;
	overflow: hidden;
	transition: all 0.3s ease;
	box-shadow: 0 10px 30px rgba(67, 206, 162, 0.4);
}

.cta-button:hover {
	transform: translateY(-5px);
	box-shadow: 0 15px 40px rgba(67, 206, 162, 0.5);
}

.btn-sparkle {
	position: absolute;
	top: 50%;
	left: 20%;
	width: 4px;
	height: 4px;
	background: white;
	border-radius: 50%;
	opacity: 0;
	animation: sparkle-drift 2s infinite;
}

.btn-sparkle::before,
.btn-sparkle::after {
	content: "";
	position: absolute;
	width: 4px;
	height: 4px;
	background: white;
	border-radius: 50%;
}

.btn-sparkle::before {
	top: -10px;
	left: 15px;
	animation: sparkle-drift 2s infinite 0.5s;
}

.btn-sparkle::after {
	top: 10px;
	left: -10px;
	animation: sparkle-drift 2s infinite 1s;
}

/* 页脚样式 */
.footer {
	padding: 60px 0;
	background: linear-gradient(135deg, #2c3e50, #1a252f);
	color: white;
	position: relative;
	overflow: hidden;
}

.footer::before {
	content: "";
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 1px;
	background: linear-gradient(
		90deg,
		transparent,
		rgba(67, 206, 162, 0.5),
		transparent
	);
}

.footer-content {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.footer-logo {
	display: flex;
	flex-direction: column;
}

.footer-logo .logo-text {
	font-size: 24px;
	font-weight: 800;
	background: linear-gradient(135deg, #43cea2, #8dd6bd);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
}

.footer-logo .logo-tagline {
	font-size: 12px;
	color: #aaa;
	margin-top: -2px;
}

.footer-text {
	color: #bbb;
	font-size: 14px;
}

/* 认证弹窗样式 */
.auth-dialog {
	border-radius: 20px;
	overflow: hidden;
	box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
}

.auth-dialog :deep(.el-dialog__header) {
	display: none;
}

.auth-dialog :deep(.el-dialog__body) {
	padding: 0;
}

.auth-dialog :deep(.el-dialog__headerbtn) {
	top: 20px;
	right: 20px;
	z-index: 10;
	width: 35px;
	height: 35px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	transition: all 0.3s ease;
}

.auth-dialog :deep(.el-dialog__headerbtn:hover) {
	background: white;
	transform: scale(1.1);
}

.auth-dialog :deep(.el-dialog__close) {
	color: #333;
	font-size: 16px;
	font-weight: bold;
}

/* 动画定义 */
@keyframes slide-in-left {
	from {
		opacity: 0;
		transform: translateX(-50px);
	}
	to {
		opacity: 1;
		transform: translateX(0);
	}
}

@keyframes slide-in-right {
	from {
		opacity: 0;
		transform: translateX(50px);
	}
	to {
		opacity: 1;
		transform: translateX(0);
	}
}

@keyframes fade-in {
	from {
		opacity: 0;
		transform: translateY(20px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

@keyframes bg-float {
	0%,
	100% {
		transform: scale(1) rotate(0deg);
	}
	50% {
		transform: scale(1.1) rotate(10deg);
	}
}

@keyframes particle-float {
	0% {
		transform: translateY(0);
	}
	100% {
		transform: translateY(-10px);
	}
}

@keyframes bounce {
	0%,
	20%,
	50%,
	80%,
	100% {
		transform: translateY(0);
	}
	40% {
		transform: translateY(-5px);
	}
	60% {
		transform: translateY(-3px);
	}
}

@keyframes shimmer {
	0% {
		transform: translateX(-100%);
	}
	100% {
		transform: translateX(100%);
	}
}

@keyframes blink {
	0%,
	50% {
		opacity: 1;
	}
	51%,
	100% {
		opacity: 0;
	}
}

@keyframes title-glow {
	0%,
	100% {
		filter: brightness(1);
	}
	50% {
		filter: brightness(1.2);
	}
}

@keyframes icon-spin {
	0% {
		transform: rotate(0deg);
	}
	25% {
		transform: rotate(5deg);
	}
	75% {
		transform: rotate(-5deg);
	}
	100% {
		transform: rotate(0deg);
	}
}

@keyframes particle-burst {
	0% {
		opacity: 1;
		transform: translate(-50%, -50%) scale(1);
	}
	100% {
		opacity: 0;
		transform: translate(-50%, -50%) scale(3);
	}
}

@keyframes shine {
	0% {
		transform: translateX(-100%) skewX(-15deg);
	}
	100% {
		transform: translateX(200%) skewX(-15deg);
	}
}

@keyframes sparkle {
	0%,
	100% {
		transform: scale(1) rotate(0deg);
	}
	50% {
		transform: scale(1.2) rotate(180deg);
	}
}

@keyframes glow-pulse {
	0%,
	100% {
		opacity: 0.5;
	}
	50% {
		opacity: 0.8;
	}
}

@keyframes pulse-glow {
	0%,
	100% {
		opacity: 0.3;
	}
	50% {
		opacity: 0.6;
	}
}

@keyframes ring-pulse {
	0% {
		transform: scale(1.2);
		opacity: 1;
	}
	100% {
		transform: scale(1.5);
		opacity: 0;
	}
}

@keyframes shine-sweep {
	0% {
		transform: translateX(-100%) rotate(45deg);
	}
	100% {
		transform: translateX(100%) rotate(45deg);
	}
}

@keyframes progress-glow {
	0%,
	100% {
		box-shadow: 0 0 5px rgba(67, 206, 162, 0.5);
	}
	50% {
		box-shadow: 0 0 20px rgba(67, 206, 162, 0.8);
	}
}

@keyframes avatar-glow {
	0%,
	100% {
		opacity: 0.7;
	}
	50% {
		opacity: 1;
	}
}

@keyframes float-icon {
	0%,
	100% {
		transform: translateY(0);
	}
	50% {
		transform: translateY(-10px);
	}
}

@keyframes sparkle-drift {
	0% {
		opacity: 0;
		transform: translateY(0) scale(0);
	}
	50% {
		opacity: 1;
		transform: translateY(-20px) scale(1);
	}
	100% {
		opacity: 0;
		transform: translateY(-40px) scale(0);
	}
}

@keyframes bounce-gentle {
	0%,
	100% {
		transform: translateY(0);
	}
	50% {
		transform: translateY(-5px);
	}
}

@keyframes pulse-dot {
	0%,
	100% {
		opacity: 0.7;
		transform: scale(1);
	}
	50% {
		opacity: 1;
		transform: scale(1.3);
	}
}

@keyframes progress-shimmer {
	0% {
		background-position: -200px 0;
	}
	100% {
		background-position: calc(200px + 100%) 0;
	}
}

@keyframes brain-float {
	0%,
	100% {
		transform: translateY(0) rotate(0deg);
	}
	50% {
		transform: translateY(-10px) rotate(5deg);
	}
}

@keyframes pulse-ring {
	0% {
		transform: scale(1);
		opacity: 1;
	}
	100% {
		transform: scale(1.3);
		opacity: 0;
	}
}

@keyframes star-twinkle {
	0%,
	100% {
		opacity: 1;
		transform: scale(1);
	}
	50% {
		opacity: 0.7;
		transform: scale(1.2);
	}
}

/* 响应式样式 */
@media (max-width: 1200px) {
	.hero-title {
		font-size: 46px;
	}

	.section-title {
		font-size: 36px;
	}

	.cta-title {
		font-size: 30px;
	}
}

@media (max-width: 991px) {
	.hero-section {
		padding: 140px 0 80px;
	}

	.hero-content {
		margin-bottom: 50px;
	}

	.hero-title {
		font-size: 38px;
	}

	.hero-description {
		font-size: 16px;
	}

	.content-wrapper {
		flex-direction: column;
		gap: 50px;
	}

	.footer-content {
		flex-direction: column;
		gap: 20px;
		text-align: center;
	}
}

@media (max-width: 767px) {
	.nav-buttons {
		display: none;
	}

	.hero-title {
		font-size: 32px;
	}

	.section-title {
		font-size: 28px;
	}

	.cta-title {
		font-size: 24px;
	}

	.features-grid {
		grid-template-columns: 1fr;
	}

	.stats-grid {
		grid-template-columns: 1fr;
	}

	.hero-buttons {
		flex-direction: column;
	}

	.cta-card {
		padding: 50px 30px;
	}
}

@media (max-width: 480px) {
	.container {
		padding: 0 15px;
	}

	.hero-title {
		font-size: 28px;
	}

	.hero-features {
		flex-direction: column;
	}

	.feature-item {
		justify-content: center;
	}
}
</style>
