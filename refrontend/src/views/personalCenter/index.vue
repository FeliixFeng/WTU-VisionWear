<script setup>
import { useRouter } from "vue-router"
import { onBeforeMount, ref, computed } from "vue"
import { useUesrInformationStore } from "@/store/userInformationStore.js"
import dbUtils from "utils/util.strotage"
import avatarUpload from "./component/avatarUpload.vue"

const userData = dbUtils.get("userData")
const router = useRouter()
const userInformation = ref()
const userImages = ref([])
const showPasswordForm = ref(false)
const createTime = ref("") //注册日期
const uesrInformationStore = useUesrInformationStore()
const formRef = ref()
const PasswordRef = ref()
const pswResult = ref()
const formData = ref({
	avatar: "", //头像url
	nickName: "", //昵称
	birthday: "", //生日
	email: "", //email
	phone: "", //phone
})
//根据接口定义修改密码的相关信息
const PasswordInformation = ref({
	username: "",
	oldPassword: "",
	newPassword: "",
})
const rules = ref({
	nickName: [
		{ required: true, message: "请输入昵称", trigger: "blur" },
		{
			validator: (rule, value, callback) => {
				if (!value) return callback()
				const lengthValid = value.length >= 2 && value.length <= 20
				const patternValid =
					/^[\u4e00-\u9fa5_a-zA-Z0-9.,!?@#￥%&*()（）《》“”"':;、\-+=<>{}[\]，。？！【】‘；：——|\\\/]+$/.test(
						value
					)
				const noSpace = !/\s/.test(value)
				if (!lengthValid) {
					callback(new Error("昵称长度必须在 2 到 20 之间"))
				} else if (!patternValid) {
					callback(new Error("昵称只能包含中英文、数字和常用符号"))
				} else if (!noSpace) {
					callback(new Error("昵称不能包含空格或回车等空白字符"))
				} else {
					callback()
				}
			},
			trigger: "blur",
		},
	],
	birthday: [
		{
			validator: (rule, value, callback) => {
				if (!value) return callback()
				const selectedDate = new Date(value)
				const now = new Date()
				if (selectedDate > now) {
					callback(new Error("生日不能晚于当前日期"))
				} else {
					callback()
				}
			},
			trigger: "change",
		},
	],
	email: [
		{
			type: "email",
			message: "请输入合法的邮箱地址",
			trigger: ["blur", "change"],
		},
	],
	phone: [
		{
			pattern: /^1[3-9]\d{9}$/, // 简单国内手机号校验，可替换为更复杂规则
			message: "请输入合法的手机号",
			trigger: ["blur", "change"],
		},
	],
})
const passwordRules = computed(() => {
	if (!showPasswordForm.value) {
		return {} // 不显示表单时，不做任何校验
	}
	return {
		oldPassword: [
			{ required: true, message: "请输入旧密码", trigger: "blur" },
			{
				validator: (rule, value, callback) => {
					if (/\s/.test(value)) {
						callback(new Error("旧密码不能包含空格或回车"))
					} else if (value.length < 5 || value.length > 20) {
						callback(new Error("旧密码长度需为 5-20 个字符"))
					} else {
						callback()
					}
				},
				trigger: "blur",
			},
		],
		newPassword: [
			{ required: true, message: "请输入新密码", trigger: "blur" },
			{
				validator: (rule, value, callback) => {
					if (/\s/.test(value)) {
						callback(new Error("新密码不能包含空格或回车"))
					} else if (value.length < 5 || value.length > 20) {
						callback(new Error("新密码长度需为 5-20 个字符"))
					} else {
						callback()
					}
				},
				trigger: "blur",
			},
		],
	}
})

// 返回上一个路由
const ret = () => {
	router.back()
}

const initializeForm = () => {
	formData.value.avatar = userInformation.value.avatar
	formData.value.nickName = userInformation.value.nickName
	formData.value.birthday = userInformation.value.birthday
	formData.value.email = userInformation.value.email
	formData.value.phone = userInformation.value.phone
	createTime.value = userInformation.value.createTime
	PasswordInformation.value.username = userData.userName
}

const changPswd = (pswdData) => {
	if (!PasswordRef.value) return
	PasswordRef.value.validate(async (valid) => {
		// 确保信息填写正确才发送请求
		if (valid) {
			pswResult.value = await uesrInformationStore.doChangePassword(pswdData)
		}
	})
}

const save = () => {
	if (!formRef.value) return
	formRef.value.validate(async (valid) => {
		// 确保信息填写正确才发送请求
		if (valid) {
			//修改信息
			console.log("修改后的数据：", formData.value)
			uesrInformationStore.doChangeUserInfo(formData.value)
			initializeForm()
			// 修改密码
			if (showPasswordForm.value) {
				changPswd(PasswordInformation.value)
				showPasswordForm.value = false
			}
		}
	})
}

const updateAvatar = (newAvatarUrl) => {
	formData.value.avatar = newAvatarUrl
}

onBeforeMount(async () => {
	const res = await uesrInformationStore.doGetUserInformation(userData.userId)
	userInformation.value = res.data
	const res2 = await uesrInformationStore.doGetUserImages()
	userImages.value = res2.data
	initializeForm()
})
</script>
<template>
	<div class="personalCenter">
		<main class="center">
			<i
				class="iconfont icon-fanhui return"
				@click="ret"
			></i>
			<avatarUpload @updateAvatar="updateAvatar"></avatarUpload>
			<div class="data">
				<el-form
					ref="formRef"
					:model="formData"
					:rules="rules"
					label-width="200px"
					style="width: 750px; margin-top: 40px"
				>
					<el-form-item
						label="昵称"
						prop="nickName"
					>
						<el-input v-model="formData.nickName" />
					</el-form-item>
					<el-form-item
						label="生日"
						prop="birthday"
					>
						<el-date-picker
							v-model="formData.birthday"
							type="date"
							aria-label="Pick a date"
							placeholder="选择你的生日"
							style="width: 100%"
							value-format="YYYY-MM-DD"
						/>
					</el-form-item>
					<el-form-item
						label="email"
						prop="email"
					>
						<el-input v-model="formData.email" />
					</el-form-item>
					<el-form-item
						label="phone"
						prop="phone"
					>
						<el-input v-model="formData.phone" />
					</el-form-item>
					<el-form-item
						label="注册日期"
						prop="createTime"
					>
						<el-input
							v-model="createTime"
							disabled
						/>
					</el-form-item>
				</el-form>
				<el-form
					:validate-on-rule-change="false"
					v-show="showPasswordForm"
					ref="PasswordRef"
					:model="PasswordInformation"
					:rules="passwordRules"
					label-width="200px"
					style="width: 750px; margin-top: 40px"
				>
					<el-form-item
						label="旧密码"
						prop="oldPassword"
					>
						<el-input
							v-model="PasswordInformation.oldPassword"
							type="password"
							show-password
						/>
					</el-form-item>
					<el-form-item
						label="新密码"
						prop="newPassword"
					>
						<el-input
							v-model="PasswordInformation.newPassword"
							type="password"
							show-password
						/> </el-form-item
				></el-form>
				<el-button
					style="float: right; width: 100px; margin-right: 130px"
					type="success"
					@click="save"
					>保存</el-button
				>
				<el-button
					style="float: right; width: 100px; margin-right: 20px"
					type="success"
					native-type="button"
					@click="showPasswordForm = true"
					>修改密码</el-button
				>
			</div>

			<div class="showMyImage">
				<p>我的照片</p>
				<div class="images">
					<el-image
						v-for="(item, index) in userImages"
						class="picture"
						style="width: 20%; height: 200px"
						:src="item"
						:preview-src-list="userImages"
						:initial-index="index"
						lazy
						preview-teleported
						fit="cover"
					></el-image>
				</div>
			</div>
		</main>
	</div>
</template>
<style scoped>
.personalCenter {
	position: relative;
	background-color: #f2f2f2;
}
.center {
	width: 880px;
	margin: 1px auto;
	background-color: white;
	border-radius: 1%;
	padding-bottom: 150px;
}
.return {
	position: absolute;
	top: 5px;
	left: 280px;
	font-size: 30px;
	color: rgb(163, 163, 172);
	cursor: pointer;
}
.return:hover {
	color: aqua;
}
.headPortrait {
	width: 800px;
	height: 200px;
	margin: 0 auto;
	background-color: white;
	display: flex;
	justify-content: center;
	align-items: center;
	border-bottom: 1px dashed rgb(54, 171, 157);
}
.el-form-item {
	margin-bottom: 40px;
}
.showMyImage {
	width: 800px;
	height: 850px;
	margin: 20px auto;
}
p {
	margin-top: 120px;
	font-size: 20px;
	border-top: 1px dashed green;
	padding: 20px 0 5px 0;
}
.images {
	width: 800px;
	height: 900px;
	/* border: 1px dashed aqua; */
	overflow-y: auto;
}
.picture {
	float: left;
	padding: 5px;
	border: 3px solid transparent; /* 初始边框透明，防止布局跳动 */
	transition: all 0.3s ease;
}

.picture:hover {
	transform: scale(1.1); /* 放大一点点 */
	border: 2px solid #49ec98; /* 出现边框 */
	box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3); /* 加点阴影更立体 */
}
</style>
