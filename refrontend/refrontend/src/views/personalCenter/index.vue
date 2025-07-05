<script setup>
import { useRouter } from "vue-router"
import { onBeforeMount, ref } from "vue"
import { useUesrInformationStore } from "@/store/userInformationStore.js"
import dbUtils from "utils/util.strotage"

const userData = dbUtils.get("userData")
const router = useRouter()
const userInformation = ref()
const userImages = ref([])
const createTime = ref("") //注册日期
const uesrInformationStore = useUesrInformationStore()
const formData = ref({
	avatar: "", //头像url
	nickName: "", //昵称
	birthday: "", //生日
	email: "", //email
	phone: "", //phone
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
}

const save = () => {
	console.log("修改后的数据：", formData.value)
}

onBeforeMount(async () => {
	const res = await uesrInformationStore.doGetUserInformation(userData.userId)
	userInformation.value = res.data
	const res2 = await uesrInformationStore.doGetUserImages()
	userImages.value = res2.data
	initializeForm()
	console.log(formData.value)
	console.log(res.data)
})
</script>
<template>
	<div class="personalCenter">
		<main class="center">
			<i
				class="iconfont icon-fanhui return"
				@click="ret"
			></i>
			<div class="headPortrait">
				<div class="showAvatar"></div>
			</div>
			<div class="data">
				<el-form
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
					<el-button
						style="float: right; width: 100px"
						type="success"
						@click="save"
						>保存</el-button
					>
				</el-form>
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
	height: 820px;
	margin: 0 auto;
	background-color: white;
	border-radius: 1%;
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
.showAvatar {
	width: 150px;
	height: 150px;
	background-color: green;
	margin: auto;
}
.el-form-item {
	margin-bottom: 40px;
}
</style>
