<!-- RegisterPage.vue -->
<template>
	<div
		class="register-container"
		:class="{ 'dialog-mode': isDialog }"
	>
		<div class="register-box">
			<div class="logo-area">
				<div class="logo-circle">
					<div class="logo-inner">
						<div class="deep-learning-design">
							<!-- 神经网络图 -->
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

									<!-- 隐藏层到输出层连接 -->
									<div class="connection c10"></div>
									<div class="connection c11"></div>
									<div class="connection c12"></div>
									<div class="connection c13"></div>
									<div class="connection c14"></div>
									<div class="connection c15"></div>
									<div class="connection c16"></div>
									<div class="connection c17"></div>
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

			<!-- 优化分隔区域 -->
			<div class="separator">
				<span class="separator-line"></span>
			</div>

			<el-form
				:model="registerForm"
				ref="registerFormRef"
				:rules="rules"
				class="register-form"
				label-position="top"
			>
				<el-form-item
					label="用户名"
					prop="username"
					class="compact-form-item"
				>
					<el-input
						v-model="registerForm.username"
						placeholder="请输入用户名（3-20个字符）"
						:prefix-icon="User"
						class="input-field"
					/>
				</el-form-item>

				<el-form-item
					label="邮箱"
					prop="email"
					class="compact-form-item"
				>
					<el-input
						v-model="registerForm.email"
						placeholder="请输入邮箱"
						:prefix-icon="Message"
						class="input-field"
					/>
				</el-form-item>

				<el-form-item
					label="密码"
					prop="password"
					class="compact-form-item"
				>
					<el-input
						type="password"
						v-model="registerForm.password"
						placeholder="请输入密码（6-20个字符）"
						:prefix-icon="Lock"
						class="input-field"
					/>
				</el-form-item>

				<el-form-item
					label="确认密码"
					prop="confirmPassword"
					class="compact-form-item"
				>
					<el-input
						type="password"
						v-model="registerForm.confirmPassword"
						placeholder="请再次输入密码"
						:prefix-icon="Lock"
						class="input-field"
					/>
				</el-form-item>

				<div class="form-options">
					<el-checkbox
						v-model="acceptTerms"
						@change="validateTerms"
						>我已阅读并同意</el-checkbox
					>
					<el-link
						type="primary"
						:underline="false"
						@click="showTerms"
						>用户协议</el-link
					>
				</div>

				<el-form-item class="button-item">
					<el-button
						type="primary"
						@click="submitForm('registerFormRef')"
						class="register-button"
						:loading="loading"
					>
						{{ loading ? "注册中..." : "立即注册" }}
					</el-button>
				</el-form-item>
			</el-form>

			<div class="register-footer">
				<p>
					已有账号?
					<el-link
						type="primary"
						:underline="false"
						@click="goToLogin"
						>立即登录</el-link
					>
				</p>
			</div>
		</div>

		<!-- 用户协议对话框 -->
		<el-dialog
			v-model="termsDialogVisible"
			title="用户协议"
			width="60%"
			center
		>
			<div class="terms-content">
				<h3>DeepWear 用户协议</h3>
				<p>欢迎使用 DeepWear 平台。通过注册 DeepWear 账号，您同意以下条款：</p>
				<ol>
					<li>您提供的个人信息真实、准确、完整且不侵犯他人合法权益。</li>
					<li>您将对账号安全负责，保护好您的账号信息。</li>
					<li>您在使用本平台时应遵守相关法律法规。</li>
					<li>我们将按照隐私政策收集、使用和保护您的个人信息。</li>
					<li>平台保留在必要时修改服务条款的权利。</li>
				</ol>
				<p>如对协议有任何疑问，请联系我们的客服。</p>
			</div>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="termsDialogVisible = false">取消</el-button>
					<el-button
						type="primary"
						@click="acceptAndCloseTerms"
						>同意并继续</el-button
					>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/store/users.js"

// 新增的props，用于判断是否为弹窗模式
const props = defineProps({
	isDialog: {
		type: Boolean,
		default: false,
	},
})

// 新增的emit，用于弹窗模式下切换到登录组件
const emit = defineEmits(["switch-to-login"])

const router = useRouter()
const authStore = useAuthStore() // 使用认证状态

const registerForm = reactive({
	username: "",
	email: "",
	password: "",
	confirmPassword: "",
})

const acceptTerms = ref(false)
const termsDialogVisible = ref(false)
const loading = ref(false)

// 验证邮箱格式
const validateEmail = (rule, value, callback) => {
	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
	if (value === "") {
		callback(new Error("请输入邮箱地址"))
	} else if (!emailRegex.test(value)) {
		callback(new Error("请输入有效的邮箱地址"))
	} else {
		callback()
	}
}

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
	if (value === "") {
		callback(new Error("请再次输入密码"))
	} else if (value !== registerForm.password) {
		callback(new Error("两次输入密码不一致"))
	} else {
		callback()
	}
}

// 验证用户协议
const validateTerms = () => {
	if (!acceptTerms.value) {
		ElMessage.warning("请阅读并同意用户协议")
		return false
	}
	return true
}

const rules = {
	username: [
		{ required: true, message: "请输入用户名", trigger: "blur" },
		{ min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
	],
	email: [{ required: true, validator: validateEmail, trigger: "blur" }],
	password: [
		{ required: true, message: "请输入密码", trigger: "blur" },
		{ min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
	],
	confirmPassword: [
		{ required: true, validator: validateConfirmPassword, trigger: "blur" },
	],
}

// 表单引用
const registerFormRef = ref(null)

// 提交表单
const submitForm = (formName) => {
	if (!registerFormRef.value) return

	// 验证用户协议
	if (!validateTerms()) return

	registerFormRef.value.validate(async (valid) => {
		if (valid) {
			await handleRegister()
		} else {
			ElMessage.warning("请正确填写注册信息")
			return false
		}
	})
}

// 处理注册 - 使用authStore
const handleRegister = async () => {
	loading.value = true
	try {
		// 准备请求数据
		const registerData = {
			username: registerForm.username,
			password: registerForm.password,
			email: registerForm.email,
		}

		// 使用authStore进行注册
		const result = await authStore.DoRegister(registerData)

		// 可以选择将用户信息存储在localStorage中
		localStorage.setItem(
			"userInfo",
			JSON.stringify({
				username: registerForm.username,
				email: registerForm.email,
			})
		)

		// 注册成功后根据模式决定跳转到登录页面还是切换到登录弹窗
		if (props.isDialog) {
			emit("switch-to-login")
		} else {
			router.push({ name: "Login" })
		}
	} catch (error) {
		console.error("注册失败", error)
		// 错误已经在service层处理，这里不需要再次显示
	} finally {
		loading.value = false
	}
}

// 显示用户协议
const showTerms = () => {
	termsDialogVisible.value = true
}

// 同意并关闭用户协议
const acceptAndCloseTerms = () => {
	acceptTerms.value = true
	termsDialogVisible.value = false
}

// 跳转到登录页或切换到登录弹窗
const goToLogin = () => {
	if (props.isDialog) {
		// 弹窗模式下，触发事件通知父组件切换为登录弹窗
		emit("switch-to-login")
	} else {
		// 独立页面模式下，使用路由导航
		router.push({ name: "Login" })
	}
}

onMounted(() => {
	// 初始化认证状态
	// authStore.initAuth()
	localStorage.clear()
})
</script>

<style scoped>
.register-container {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 100vh;
	background: linear-gradient(135deg, #43cea2, #185a9d, #6e48aa, #f46b45);
	background-size: 400% 400%;
	animation: gradientBG 20s ease infinite;
}

/* 弹窗模式下的样式修改 */
.register-container.dialog-mode {
	min-height: auto;
	background: none;
	animation: none;
	padding: 0;
}

.dialog-mode .register-box {
	width: 100%;
	box-shadow: none;
	padding: 20px;
	transform: none;
}

.dialog-mode .register-box:hover {
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

.register-box {
	width: 380px;
	background-color: rgba(255, 255, 255, 0.95);
	border-radius: 12px;
	box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
	padding: 30px; /* 减小内边距 */
	transition: all 0.3s ease;
}

.register-box:hover {
	box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
	transform: translateY(-5px);
}

.logo-area {
	text-align: center;
	margin-bottom: 18px; /* 减小底部间距 */
}

.logo-circle {
	width: 75px; /* 缩小logo尺寸 */
	height: 75px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 0 auto 12px;
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
	width: 60px;
	height: 60px;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	background: linear-gradient(rgba(255, 255, 255, 0.08) 1px, transparent 1px),
		linear-gradient(90deg, rgba(255, 255, 255, 0.08) 1px, transparent 1px);
	background-size: 7px 7px;
	border-radius: 50%;
	opacity: 0.8;
	z-index: 1;
}

/* 神经网络结构 - 居中 */
.neural-network {
	position: absolute;
	width: 55px;
	height: 55px;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	z-index: 2;
}

/* 节点样式 */
.node {
	position: absolute;
	width: 5px;
	height: 5px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	box-shadow: 0 0 6px rgba(255, 255, 255, 0.8);
	z-index: 3;
}

/* 输入层节点 - 居中对齐 */
.input-node {
	left: 8px;
}
.n1 {
	top: 13px;
}
.n2 {
	top: 26px;
}
.n3 {
	top: 39px;
}

/* 隐藏层节点 - 居中对齐 */
.hidden-node {
	left: 25px;
}
.n4 {
	top: 8px;
}
.n5 {
	top: 19px;
}
.n6 {
	top: 31px;
}
.n7 {
	top: 43px;
}

/* 输出层节点 - 居中对齐 */
.output-node {
	left: 42px;
}
.n8 {
	top: 19px;
}
.n9 {
	top: 32px;
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
	width: 19px;
	top: 15px;
	left: 12px;
	transform: rotate(-20deg);
}
.c2 {
	width: 17px;
	top: 15px;
	left: 12px;
	transform: rotate(10deg);
}
.c3 {
	width: 20px;
	top: 15px;
	left: 12px;
	transform: rotate(40deg);
}
.c4 {
	width: 18px;
	top: 28px;
	left: 12px;
	transform: rotate(-30deg);
}
.c5 {
	width: 16px;
	top: 28px;
	left: 12px;
	transform: rotate(0deg);
}
.c6 {
	width: 18px;
	top: 28px;
	left: 12px;
	transform: rotate(25deg);
}
.c7 {
	width: 20px;
	top: 41px;
	left: 12px;
	transform: rotate(-40deg);
}
.c8 {
	width: 18px;
	top: 41px;
	left: 12px;
	transform: rotate(-15deg);
}
.c9 {
	width: 16px;
	top: 41px;
	left: 12px;
	transform: rotate(15deg);
}

/* 隐藏层到输出层连接 - 重新调整 */
.c10 {
	width: 19px;
	top: 11px;
	left: 29px;
	transform: rotate(25deg);
}
.c11 {
	width: 18px;
	top: 11px;
	left: 29px;
	transform: rotate(40deg);
}
.c12 {
	width: 17px;
	top: 21px;
	left: 29px;
	transform: rotate(-5deg);
}
.c13 {
	width: 17px;
	top: 21px;
	left: 29px;
	transform: rotate(15deg);
}
.c14 {
	width: 18px;
	top: 34px;
	left: 29px;
	transform: rotate(-15deg);
}
.c15 {
	width: 17px;
	top: 34px;
	left: 29px;
	transform: rotate(5deg);
}
.c16 {
	width: 19px;
	top: 45px;
	left: 29px;
	transform: rotate(-30deg);
}
.c17 {
	width: 18px;
	top: 45px;
	left: 29px;
	transform: rotate(-45deg);
}

/* 激活动画 - 重新调整位置 */
.activation {
	position: absolute;
	width: 3px;
	height: 3px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	box-shadow: 0 0 8px rgba(255, 255, 255, 0.9);
	z-index: 4;
	opacity: 0;
	animation: activate 6s infinite linear;
}

.a1 {
	top: 19px;
	left: 17px;
	animation-delay: 0s;
}

.a2 {
	top: 30px;
	left: 34px;
	animation-delay: 2s;
}

.a3 {
	top: 25px;
	left: 25px;
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
	font-size: 24px; /* 调小字体 */
	font-weight: 600;
	color: #333;
	margin: 0;
}

.brand-slogan {
	color: #666;
	font-size: 13px; /* 调小字体 */
	margin-top: 4px; /* 减少间距 */
}

/* 分隔线样式 */
.separator {
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 15px 0 18px; /* 减少上下间距 */
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

.register-form {
	margin-top: 10px; /* 减少顶部间距 */
}

.compact-form-item {
	margin-bottom: 12px !important; /* 更紧凑的表单项间距 */
}

/* 表单标签样式 */
:deep(.el-form-item__label) {
	padding-bottom: 4px !important; /* 减少标签和输入框之间的间距 */
	font-size: 14px !important;
	line-height: 1.3 !important;
}

/* 输入框样式 */
.input-field {
	border-radius: 6px;
	height: 38px; /* 减小高度 */
	font-size: 14px;
}

.input-field:focus,
.input-field:hover {
	box-shadow: 0 0 0 2px rgba(67, 206, 162, 0.2);
}

.form-options {
	display: flex;
	align-items: center;
	gap: 5px;
	margin-bottom: 15px; /* 减少底部间距 */
	font-size: 13px; /* 减小字体 */
}

.button-item {
	margin-bottom: 0 !important; /* 移除按钮下方间距 */
	margin-top: 5px; /* 减少顶部间距 */
}

/* 注册按钮 */
.register-button {
	width: 100%;
	height: 38px; /* 减小高度 */
	border-radius: 6px;
	font-size: 15px;
	letter-spacing: 1px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
	transition: all 0.3s ease;
}

.register-button:hover {
	background: linear-gradient(135deg, #36b57b, #144e84);
	box-shadow: 0 5px 15px rgba(67, 206, 162, 0.4);
	transform: translateY(-2px);
}

.register-footer {
	text-align: center;
	margin-top: 15px; /* 减少顶部间距 */
	color: #666;
	font-size: 13px; /* 减小字体 */
}

/* 用户协议对话框样式 */
.terms-content {
	max-height: 300px;
	overflow-y: auto;
	padding: 10px;
}

.terms-content h3 {
	margin-top: 0;
	color: #185a9d;
}

.terms-content p {
	margin-bottom: 12px;
	line-height: 1.5;
}

.terms-content ol {
	padding-left: 20px;
	margin-top: 10px;
	margin-bottom: 10px;
}

.terms-content li {
	margin-bottom: 8px;
	line-height: 1.5;
}

/* 响应式设计 */
@media screen and (max-width: 480px) {
	.register-box {
		width: 90%;
		padding: 20px;
	}
}
</style>
