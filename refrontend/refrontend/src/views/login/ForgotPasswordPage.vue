<!-- ForgotPassword.vue -->
<template>
	<div
		class="forgot-password-container"
		:class="{ 'dialog-mode': isDialog }"
	>
		<div class="forgot-password-box">
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

			<div
				v-if="resetSuccess"
				class="success-message"
			>
				<el-icon class="success-icon"><CircleCheckFilled /></el-icon>
				<h3>重置链接已发送</h3>
				<p>我们已向您的邮箱发送了密码重置链接，请查收邮件并按照提示操作。</p>
				<el-button
					type="primary"
					class="back-button"
					@click="goToLogin"
				>
					返回登录
				</el-button>
			</div>

			<el-form
				v-else
				:model="forgotPasswordForm"
				ref="forgotPasswordFormRef"
				:rules="rules"
				class="forgot-password-form"
				label-position="top"
			>
				<div class="form-header">
					<h2>找回密码</h2>
					<p>请输入您的注册邮箱，我们将向您发送重置密码的链接</p>
				</div>

				<el-form-item
					label="邮箱"
					prop="email"
					class="compact-form-item"
				>
					<el-input
						v-model="forgotPasswordForm.email"
						placeholder="请输入注册邮箱"
						:prefix-icon="Message"
						class="input-field"
					/>
				</el-form-item>

				<div
					v-if="showVerification"
					class="verification-container"
				>
					<el-form-item
						label="验证码"
						prop="verificationCode"
						class="compact-form-item"
					>
						<div class="verification-input-group">
							<el-input
								v-model="forgotPasswordForm.verificationCode"
								placeholder="请输入验证码"
								class="verification-input"
							/>
							<el-button
								type="primary"
								class="send-code-button"
								:disabled="cooldown > 0"
								@click="sendVerificationCode"
							>
								{{ cooldown > 0 ? `重新发送(${cooldown}s)` : "获取验证码" }}
							</el-button>
						</div>
					</el-form-item>
				</div>

				<el-form-item class="button-item">
					<el-button
						type="primary"
						@click="submitForm('forgotPasswordFormRef')"
						class="submit-button"
						:loading="loading"
					>
						{{ loading ? "提交中..." : "提交" }}
					</el-button>
				</el-form-item>
			</el-form>

			<div class="forgot-password-footer">
				<p>
					想起密码了?
					<el-link
						type="primary"
						:underline="false"
						@click="goToLogin"
						>返回登录</el-link
					>
				</p>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, reactive, onBeforeUnmount } from "vue"
import { sendVerificationCodes, forgotPassword } from "@/apis/modules/weChat.js"
import {
	handleApiError,
	handleBusinessError,
	showSuccess,
	ErrorLevel,
} from "@/utils/errorHandler.js"
import { useRouter } from "vue-router"

// 添加路由实例
const router = useRouter()

// 处理弹窗模式
const props = defineProps({
	isDialog: {
		type: Boolean,
		default: false,
	},
})

// emit事件用于在弹窗模式下切换回登录组件
const emit = defineEmits(["switch-to-login"])

// 表单数据
const forgotPasswordForm = reactive({
	email: "",
	verificationCode: "",
})

// 状态变量
const loading = ref(false)
const showVerification = ref(false) // 是否显示验证码输入
const resetSuccess = ref(false) // 密码重置链接是否发送成功
const cooldown = ref(0) // 验证码倒计时
let cooldownTimer = null // 倒计时计时器

// 表单规则
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

const rules = {
	email: [{ required: true, validator: validateEmail, trigger: "blur" }],
	verificationCode: [
		{ required: true, message: "请输入验证码", trigger: "blur" },
		{ min: 6, max: 6, message: "验证码长度应为6位", trigger: "blur" },
	],
}

// 表单引用
const forgotPasswordFormRef = ref(null)

// 发送验证码
const sendVerificationCode = async () => {
	// 先验证邮箱是否有效
	forgotPasswordFormRef.value.validateField("email", async (valid) => {
		if (!valid) {
			try {
				loading.value = true
				// 发送验证码请求
				const response = await sendVerificationCodes(forgotPasswordForm.email)

				// 使用业务错误处理工具检查响应
				const error = handleBusinessError(
					response,
					"验证码发送失败",
					ErrorLevel.WARNING
				)
				if (error) return // 如果有错误，handleBusinessError已经显示了错误消息

				showSuccess("验证码已发送，请查收邮件")
				showVerification.value = true
				startCooldown()
			} catch (error) {
				console.error("发送验证码请求出错", error)
				handleApiError(error, "验证码发送失败，请稍后重试", ErrorLevel.WARNING)
			} finally {
				loading.value = false
			}
		}
	})
}

// 开始倒计时
const startCooldown = () => {
	cooldown.value = 60
	cooldownTimer = setInterval(() => {
		cooldown.value--
		if (cooldown.value <= 0) {
			clearInterval(cooldownTimer)
		}
	}, 1000)
}

// 提交表单
const submitForm = (formName) => {
	if (!forgotPasswordFormRef.value) return

	forgotPasswordFormRef.value.validate(async (valid) => {
		if (valid) {
			await handleForgotPassword()
		} else {
			ElMessage.warning("请正确填写信息")
			return false
		}
	})
}

// 处理忘记密码
const handleForgotPassword = async () => {
	loading.value = true
	try {
		// 准备请求数据
		const requestData = {
			email: forgotPasswordForm.email,
		}

		// 如果显示了验证码输入，则添加验证码
		if (showVerification.value) {
			requestData.verificationCode = forgotPasswordForm.verificationCode
		}

		// 发送密码重置请求
		const response = await forgotPassword(requestData)

		// 使用业务错误处理工具检查响应
		const error = handleBusinessError(response, "密码重置请求失败")
		if (error) return // 如果有错误，handleBusinessError已经显示了错误消息

		// 密码重置邮件发送成功
		resetSuccess.value = true

		// 显示成功消息
		showSuccess("重置密码链接已发送到您的邮箱", true)
	} catch (error) {
		console.error("密码重置请求出错", error)
		handleApiError(error, "密码重置请求失败，请稍后重试")
	} finally {
		loading.value = false
	}
}

// 返回登录页面
const goToLogin = () => {
	if (props.isDialog) {
		// 弹窗模式下，触发事件通知父组件切换为登录弹窗
		emit("switch-to-login")
	} else {
		// 独立页面模式下，使用路由导航
		router.push({ name: "Login" })
	}
}

// 组件销毁前清除计时器
onBeforeUnmount(() => {
	if (cooldownTimer) {
		clearInterval(cooldownTimer)
	}
})
</script>

<style scoped>
.forgot-password-container {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 100vh;
	background: linear-gradient(135deg, #43cea2, #185a9d, #6e48aa, #f46b45);
	background-size: 400% 400%;
	animation: gradientBG 20s ease infinite;
}

/* 弹窗模式下的样式修改 */
.forgot-password-container.dialog-mode {
	min-height: auto;
	background: none;
	animation: none;
	padding: 0;
}

.dialog-mode .forgot-password-box {
	width: 100%;
	box-shadow: none;
	padding: 20px;
	transform: none;
}

.dialog-mode .forgot-password-box:hover {
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

.forgot-password-box {
	width: 380px;
	background-color: rgba(255, 255, 255, 0.95);
	border-radius: 12px;
	box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
	padding: 30px;
	transition: all 0.3s ease;
}

.forgot-password-box:hover {
	box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
	transform: translateY(-5px);
}

.logo-area {
	text-align: center;
	margin-bottom: 18px;
}

.logo-circle {
	width: 75px;
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
	font-size: 24px;
	font-weight: 600;
	color: #333;
	margin: 0;
}

.brand-slogan {
	color: #666;
	font-size: 13px;
	margin-top: 4px;
}

/* 分隔线样式 */
.separator {
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 15px 0 18px;
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

/* 表单头部 */
.form-header {
	text-align: center;
	margin-bottom: 20px;
}

.form-header h2 {
	font-size: 18px;
	color: #333;
	margin: 0 0 8px;
}

.form-header p {
	color: #666;
	font-size: 13px;
	margin: 0;
	line-height: 1.5;
}

.forgot-password-form {
	margin-top: 10px;
}

.compact-form-item {
	margin-bottom: 15px !important;
}

/* 验证码区域 */
.verification-container {
	margin-top: 5px;
}

.verification-input-group {
	display: flex;
	gap: 8px;
}

.verification-input {
	flex: 1;
}

.send-code-button {
	width: 110px;
	font-size: 13px;
	padding: 0 5px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
}

.send-code-button:hover {
	background: linear-gradient(135deg, #36b57b, #144e84);
}

/* 表单标签样式 */
:deep(.el-form-item__label) {
	padding-bottom: 4px !important;
	font-size: 14px !important;
	line-height: 1.3 !important;
}

/* 输入框样式 */
.input-field {
	border-radius: 6px;
	height: 38px;
	font-size: 14px;
}

.input-field:focus,
.input-field:hover {
	box-shadow: 0 0 0 2px rgba(67, 206, 162, 0.2);
}

.button-item {
	margin-bottom: 0 !important;
	margin-top: 10px;
}

/* 提交按钮 */
.submit-button {
	width: 100%;
	height: 38px;
	border-radius: 6px;
	font-size: 15px;
	letter-spacing: 1px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
	transition: all 0.3s ease;
}

.submit-button:hover {
	background: linear-gradient(135deg, #36b57b, #144e84);
	box-shadow: 0 5px 15px rgba(67, 206, 162, 0.4);
	transform: translateY(-2px);
}

.forgot-password-footer {
	text-align: center;
	margin-top: 15px;
	color: #666;
	font-size: 13px;
}

/* 成功消息样式 */
.success-message {
	text-align: center;
	padding: 20px 10px;
}

.success-icon {
	font-size: 50px;
	color: #43cea2;
	margin-bottom: 10px;
}

.success-message h3 {
	font-size: 18px;
	color: #333;
	margin: 10px 0;
}

.success-message p {
	color: #666;
	font-size: 14px;
	line-height: 1.6;
	margin-bottom: 20px;
}

.back-button {
	width: 100%;
	height: 38px;
	border-radius: 6px;
	font-size: 15px;
	background: linear-gradient(135deg, #43cea2, #185a9d);
	border: none;
	transition: all 0.3s ease;
}

.back-button:hover {
	background: linear-gradient(135deg, #36b57b, #144e84);
	box-shadow: 0 5px 15px rgba(67, 206, 162, 0.4);
	transform: translateY(-2px);
}

/* 响应式设计 */
@media screen and (max-width: 480px) {
	.forgot-password-box {
		width: 90%;
		padding: 20px;
	}
}
</style>
