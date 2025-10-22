<template>
	<div
		class="login-container"
		:class="{ 'dialog-mode': isDialog }"
	>
		<div class="login-box">
			<div class="logo-area">
				<div class="logo-circle">
					<div class="logo-inner">
						<div class="deep-learning-design">
							<!-- 居中的神经网络图 -->
							<div class="neural-network">
								<!-- 输入层节点 -->
								<div class="node input-node n1"></div>
								<div class="node input-node n2"></div>
								<div class="node input-node n3"></div>

								<!-- 隐藏层节点 -->
								<div class="node hidden-node n4"></div>
								<div class="node hidden-node n5"></div>
								<div class="node hidden-node n6"></div>
								<div class="node hidden-node n7"></div>

								<!-- 输出层节点 -->
								<div class="node output-node n8"></div>
								<div class="node output-node n9"></div>

								<!-- 连接线 -->
								<div class="connections">
									<!-- 输入到隐藏层连接 -->
									<div class="connection c1"></div>
									<div class="connection c2"></div>
									<div class="connection c3"></div>
									<div class="connection c4"></div>
									<div class="connection c5"></div>
									<div class="connection c6"></div>
									<div class="connection c7"></div>
									<div class="connection c8"></div>
									<div class="connection c9"></div>
									<div class="connection c10"></div>
									<div class="connection c11"></div>
									<div class="connection c12"></div>

									<!-- 隐藏层到输出层连接 -->
									<div class="connection c13"></div>
									<div class="connection c14"></div>
									<div class="connection c15"></div>
									<div class="connection c16"></div>
									<div class="connection c17"></div>
									<div class="connection c18"></div>
									<div class="connection c19"></div>
									<div class="connection c20"></div>
								</div>

								<!-- 激活动画 -->
								<div class="activation a1"></div>
								<div class="activation a2"></div>
								<div class="activation a3"></div>
							</div>

							<!-- 背景网格 -->
							<div class="grid-background"></div>
						</div>
					</div>
				</div>
				<h1 class="brand-name">DeepWear</h1>
				<p class="brand-slogan">AI定义时尚，科技塑造未来</p>
			</div>

			<!-- 优化分隔区域，移除突兀的文字 -->
			<div class="separator">
				<span class="separator-line"></span>
			</div>

			<el-form
				:model="loginForm"
				ref="loginFormRef"
				:rules="rules"
				class="login-form"
				label-position="top"
			>
				<el-form-item
					label="用户名"
					prop="username"
				>
					<el-input
						v-model="loginForm.username"
						placeholder="请输入用户名"
						:prefix-icon="User"
						class="input-field"
					/>
				</el-form-item>

				<el-form-item
					label="密码"
					prop="password"
				>
					<el-input
						type="password"
						v-model="loginForm.password"
						placeholder="请输入密码"
						:prefix-icon="Lock"
						class="input-field"
						@keyup.enter="handleLogin"
					/>
				</el-form-item>

				<div class="form-options">
					<el-checkbox v-model="rememberMe">记住我</el-checkbox>
					<el-link
						type="primary"
						:underline="false"
						@click="goToForgotPassword"
						>忘记密码?</el-link
					>
				</div>

				<el-form-item>
					<el-button
						type="primary"
						@click="submitForm('loginFormRef')"
						class="login-button"
						:loading="loading"
					>
						{{ loading ? "登录中..." : "登录" }}
					</el-button>
				</el-form-item>
			</el-form>

			<div class="login-footer">
				<p>
					还没有账号?
					<el-link
						type="primary"
						:underline="false"
						@click="goToRegister"
						>立即注册</el-link
					>
				</p>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/store/users.js"

// 新增的props，用于判断是否为弹窗模式
const props = defineProps({
	isDialog: {
		type: Boolean,
		default: false,
	},
})

// 新增的emit，用于弹窗模式下切换到注册组件
const emit = defineEmits(["switch-to-register", "switch-to-forgot-password"])

const router = useRouter()
const authStore = useAuthStore() // 使用认证状态

const loginForm = ref({
	username: "",
	password: "",
})
const rememberMe = ref(false)
const loading = ref(false)

const rules = {
	username: [
		{ required: true, message: "请输入用户名", trigger: "blur" },
		{ min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
	],
	password: [
		{ required: true, message: "请输入密码", trigger: "blur" },
		{ min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
	],
}

// 表单引用
const loginFormRef = ref(null)

const submitForm = (formName) => {
	if (!loginFormRef.value) return

	loginFormRef.value.validate((valid) => {
		if (valid) {
			handleLogin()
		} else {
			ElMessage.warning("请正确填写登录信息")
			return false
		}
	})
}

const handleLogin = async () => {
	loading.value = true
	try {
		// 使用authStore进行登录
		const data = await authStore.login(loginForm.value)

		// 保存记住我的状态
		if (rememberMe.value) {
			localStorage.setItem("rememberMe", "true")
			localStorage.setItem("savedUsername", loginForm.value.username)
		} else {
			localStorage.removeItem("rememberMe")
			localStorage.removeItem("savedUsername")
		}

		// 检查是否有重定向参数
		const redirect = router.currentRoute.value.query.redirect
		await router.push(redirect || "/")
	} catch (error) {
		console.error("登录失败", error)
		// 错误已经在store层处理，这里不需要再次显示
	} finally {
		loading.value = false
	}
}

// 跳转到注册页面或触发切换到注册弹窗的事件
const goToRegister = () => {
	if (props.isDialog) {
		// 弹窗模式下，触发事件通知父组件切换为注册弹窗
		emit("switch-to-register")
	} else {
		// 独立页面模式下，使用路由导航
		router.push({ name: "Register" })
	}
}
const goToForgotPassword = () => {
	if (props.isDialog) {
		// 弹窗模式下，触发事件通知父组件切换为忘记密码弹窗
		emit("switch-to-forgot-password")
	} else {
		// 独立页面模式下，使用路由导航
		router.push({ name: "ForgotPasswordPage" })
	}
}

onMounted(() => {
	// 检查是否有保存的用户名
	if (localStorage.getItem("rememberMe") === "true") {
		rememberMe.value = true
		loginForm.value.username = localStorage.getItem("savedUsername") || ""
	}

	// 初始化认证状态(全交给authStore.login()处理)
	// authStore.initAuth()
})
</script>

<style scoped>
.login-container {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 100vh;
	background: linear-gradient(135deg, #43cea2, #185a9d, #6e48aa, #f46b45);
	background-size: 400% 400%;
	animation: gradientBG 20s ease infinite;
}

/* 弹窗模式下的样式修改 */
.login-container.dialog-mode {
	min-height: auto;
	background: none;
	animation: none;
	padding: 0;
}

.dialog-mode .login-box {
	width: 100%;
	box-shadow: none;
	padding: 20px;
	transform: none;
}

.dialog-mode .login-box:hover {
	transform: none;
	box-shadow: none;
}

@keyframes gradientBG {
	0% {
		background-position: 0% 50%;
	}
	25% {
		background-position: 50% 100%;
	}
	50% {
		background-position: 100% 50%;
	}
	75% {
		background-position: 50% 0%;
	}
	100% {
		background-position: 0% 50%;
	}
}

.login-box {
	width: 380px; /* 调整容器宽度 */
	background-color: rgba(255, 255, 255, 0.95);
	border-radius: 12px;
	box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
	padding: 35px; /* 调整内边距 */
	transition: all 0.3s ease;
}

.login-box:hover {
	box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
	transform: translateY(-5px);
}

.logo-area {
	text-align: center;
	margin-bottom: 25px; /* 增加底部间距 */
}

.logo-circle {
	width: 85px; /* 轻微缩小logo */
	height: 85px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 0 auto 15px;
	box-shadow: 0 5px 20px rgba(24, 90, 157, 0.4);
	position: relative;
	overflow: hidden;
}

.logo-inner {
	width: 100%;
	height: 100%;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 深度学习设计元素 */
.deep-learning-design {
	position: relative;
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 背景网格 */
.grid-background {
	position: absolute;
	width: 70px;
	height: 70px;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	background: linear-gradient(rgba(255, 255, 255, 0.08) 1px, transparent 1px),
		linear-gradient(90deg, rgba(255, 255, 255, 0.08) 1px, transparent 1px);
	background-size: 8px 8px;
	border-radius: 50%;
	opacity: 0.8;
	z-index: 1;
}

/* 神经网络结构 - 修正居中 */
.neural-network {
	position: absolute;
	width: 65px;
	height: 65px;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	z-index: 2;
}

/* 节点样式 */
.node {
	position: absolute;
	width: 6px;
	height: 6px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	box-shadow: 0 0 6px rgba(255, 255, 255, 0.8);
	z-index: 3;
}

/* 输入层节点 - 居中对齐 */
.input-node {
	left: 10px;
}
.n1 {
	top: 15px;
}
.n2 {
	top: 30px;
}
.n3 {
	top: 45px;
}

/* 隐藏层节点 - 居中对齐 */
.hidden-node {
	left: 30px;
}
.n4 {
	top: 10px;
}
.n5 {
	top: 22px;
}
.n6 {
	top: 37px;
}
.n7 {
	top: 50px;
}

/* 输出层节点 - 居中对齐 */
.output-node {
	left: 50px;
}
.n8 {
	top: 23px;
}
.n9 {
	top: 38px;
}

/* 连接线容器 */
.connections {
	position: absolute;
	width: 100%;
	height: 100%;
	top: 0;
	left: 0;
	z-index: 2;
}

/* 连接线样式 - 重新调整位置 */
.connection {
	position: absolute;
	height: 1px;
	background: rgba(255, 255, 255, 0.6);
	transform-origin: 0 0;
	z-index: 2;
}

/* 输入到隐藏层连接 - 重新调整 */
.c1 {
	width: 22px;
	top: 18px;
	left: 14px;
	transform: rotate(-20deg);
}
.c2 {
	width: 20px;
	top: 18px;
	left: 14px;
	transform: rotate(10deg);
}
.c3 {
	width: 24px;
	top: 18px;
	left: 14px;
	transform: rotate(40deg);
}
.c4 {
	width: 21px;
	top: 33px;
	left: 14px;
	transform: rotate(-30deg);
}
.c5 {
	width: 19px;
	top: 33px;
	left: 14px;
	transform: rotate(0deg);
}
.c6 {
	width: 21px;
	top: 33px;
	left: 14px;
	transform: rotate(25deg);
}
.c7 {
	width: 24px;
	top: 48px;
	left: 14px;
	transform: rotate(-40deg);
}
.c8 {
	width: 21px;
	top: 48px;
	left: 14px;
	transform: rotate(-15deg);
}
.c9 {
	width: 19px;
	top: 48px;
	left: 14px;
	transform: rotate(15deg);
}

/* 隐藏层到输出层连接 - 重新调整 */
.c10 {
	width: 22px;
	top: 13px;
	left: 34px;
	transform: rotate(25deg);
}
.c11 {
	width: 21px;
	top: 13px;
	left: 34px;
	transform: rotate(40deg);
}
.c12 {
	width: 20px;
	top: 25px;
	left: 34px;
	transform: rotate(-5deg);
}
.c13 {
	width: 20px;
	top: 25px;
	left: 34px;
	transform: rotate(15deg);
}
.c14 {
	width: 21px;
	top: 40px;
	left: 34px;
	transform: rotate(-15deg);
}
.c15 {
	width: 20px;
	top: 40px;
	left: 34px;
	transform: rotate(5deg);
}
.c16 {
	width: 22px;
	top: 53px;
	left: 34px;
	transform: rotate(-30deg);
}
.c17 {
	width: 21px;
	top: 53px;
	left: 34px;
	transform: rotate(-45deg);
}

/* 激活动画 - 重新调整位置 */
.activation {
	position: absolute;
	width: 4px;
	height: 4px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	box-shadow: 0 0 8px rgba(255, 255, 255, 0.9);
	z-index: 4;
	opacity: 0;
	animation: activate 6s infinite linear;
}

.a1 {
	top: 22px;
	left: 20px;
	animation-delay: 0s;
}

.a2 {
	top: 35px;
	left: 40px;
	animation-delay: 2s;
}

.a3 {
	top: 30px;
	left: 30px;
	animation-delay: 4s;
}

@keyframes activate {
	0% {
		opacity: 0;
	}
	5% {
		opacity: 1;
	}
	15% {
		opacity: 1;
	}
	20% {
		opacity: 0;
	}
	100% {
		opacity: 0;
	}
}

.brand-name {
	font-size: 26px; /* 稍微调小字体 */
	font-weight: 600;
	color: #333;
	margin: 0;
}

.brand-slogan {
	color: #666;
	font-size: 14px;
	margin-top: 5px;
}

/* 新的分隔线样式 */
.separator {
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 20px 0 25px;
	position: relative;
}

.separator-line {
	display: block;
	width: 100%;
	height: 1px;
	background: linear-gradient(
		to right,
		rgba(67, 206, 162, 0.1),
		rgba(24, 90, 157, 0.6),
		rgba(67, 206, 162, 0.1)
	);
}

.login-form {
	margin-top: 15px;
}

.el-form-item {
	margin-bottom: 18px; /* 减小表单项间距 */
}

/* 优化输入框 */
.input-field {
	border-radius: 6px; /* 增加圆角 */
	height: 42px; /* 统一高度 */
	font-size: 14px;
}

.input-field:focus,
.input-field:hover {
	box-shadow: 0 0 0 2px rgba(67, 206, 162, 0.2);
}

.form-options {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 18px;
	font-size: 14px;
}

/* 优化登录按钮 */
.login-button {
	width: 100%;
	height: 42px; /* 与输入框统一高度 */
	border-radius: 6px; /* 增加圆角 */
	font-size: 15px;
	letter-spacing: 1px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
	transition: all 0.3s ease;
	margin-top: 5px;
}

.login-button:hover {
	background: linear-gradient(135deg, #36b57b, #144e84);
	box-shadow: 0 5px 15px rgba(67, 206, 162, 0.4);
	transform: translateY(-2px);
}

.login-footer {
	text-align: center;
	margin-top: 20px;
	color: #666;
	font-size: 14px;
}

/* 响应式设计 */
@media screen and (max-width: 480px) {
	.login-box {
		width: 90%;
		padding: 30px;
	}
}
</style>
