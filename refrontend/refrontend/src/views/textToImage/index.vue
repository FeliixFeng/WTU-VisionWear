<script setup>
import { ref, reactive, computed } from "vue"
import { useTextToImageStore } from "@/store/textToImageStore.js"
import { useAuthStore } from "@/store/users"
import showResultPicture from "@/components/showResultPicture.vue"

const authStore = useAuthStore()
const textToImageStore = useTextToImageStore()
const resultImage = ref("")
const formRef = ref(null)
const loadingImages = ref(false)
const canGenerate = computed(() => {
	return (
		formData.prompt.trim().length >= 3 &&
		formData.width > 0 &&
		formData.height > 0
	)
})
// 风格选项,需要添加选项直接修改下面数组即可
const styleOptions = [
	{ label: "默认", value: "" },
	{ label: "模拟胶片", value: "analog-film" },
	{ label: "动漫风格", value: "anime" },
	{ label: "电影风格", value: "cinematic" },
	{ label: "漫画书风格", value: "comic-book" },
	{ label: "数字艺术", value: "digital-art" },
	{ label: "增强", value: "enhance" },
	{ label: "幻想艺术", value: "fantasy-art" },
	{ label: "等轴测图", value: "isometric" },
	{ label: "线条艺术", value: "line-art" },
	{ label: "低多边形", value: "low-poly" },
	{ label: "建模复合物", value: "modeling-compound" },
	{ label: "霓虹朋克", value: "neon-punk" },
	{ label: "折纸风格", value: "origami" },
	{ label: "摄影风格", value: "photographic" },
	{ label: "像素艺术", value: "pixel-art" },
	{ label: "3D模型", value: "3d-model" },
	{ label: "瓦片纹理", value: "tile-texture" },
]
// 宽高尺寸,需要添加尺寸选项直接更改下面数组即可
const size = [512, 768, 1024]
// 表单数据
const formData = reactive({
	prompt: "",
	style: "",
	width: 1024,
	height: 1024,
	cfgScale: 7,
})

// 表单验证规则
const rules = {
	prompt: [
		{ required: true, message: "请输入提示词", trigger: "blur" },
		{ min: 3, message: "提示词至少需要3个字符", trigger: "blur" },
	],
}
const generatePicture = async function () {
	loadingImages.value = true
	const res = await textToImageStore.doTextToImage(formData)
	resultImage.value = res.data
	authStore.updateMyImages()
	loadingImages.value = false
}
</script>
<template>
	<div class="textToImageFunction">
		<h1 class="textToImageTitle">图灵 · 文绘引擎</h1>
		<div class="textInput">
			<p>提示词</p>
			<el-form
				ref="formRef"
				:model="formData"
				:rules="rules"
			>
				<el-form-item
					class="text"
					prop="prompt"
				>
					<el-input
						v-model="formData.prompt"
						:rows="5"
						resize="none"
						type="textarea"
						placeholder="请输入您想要生成的图片描述，例如：一幅美丽的山水画，有高山流水和朝霞"
					/>
				</el-form-item>
			</el-form>

			<p>风格</p>
			<el-select
				class="select"
				v-model="formData.style"
				placeholder="请选择风格"
			>
				<el-option
					v-for="(option, index) in styleOptions"
					:label="option.label"
					:value="option.value"
				></el-option>
			</el-select>

			<p>宽度</p>
			<el-select
				class="select"
				v-model="formData.width"
			>
				<el-option
					v-for="(option, index) in size"
					:value="option"
				></el-option>
			</el-select>

			<p>高度</p>
			<el-select
				class="select"
				v-model="formData.width"
			>
				<el-option
					v-for="(option, index) in size"
					:value="option"
				></el-option>
			</el-select>

			<p>提示词相关性</p>
			<el-slider
				class="step"
				v-model="formData.cfgScale"
				:min="1"
				:max="30"
				:step="0.5"
				show-input
			></el-slider>

			<el-button
				class="button"
				:disabled="!canGenerate"
				:loading="loadingImages"
				type="success"
				@click="generatePicture"
				>一键生成</el-button
			>
		</div>
	</div>
	<div class="textToImageRes">
		<showResultPicture :resultUrl="resultImage" />
	</div>
</template>
<style scoped>
.textToImageFunction {
	position: relative;
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 2px solid #c9e1fa;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
.textToImageRes {
	float: left;
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 1px solid #cbcccb;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
h1.textToImageTitle {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin: 0 20px;
	padding: 15px 0;
	border-bottom: #0cadda 1px dashed;
}
p {
	margin: 20px 20px 5px 20px;
}
.text {
	display: block;
	width: 90%;
	height: 20%;
	margin: auto;
}
.select {
	display: block;
	width: 90%;
	margin: auto;
}
.step {
	display: flex;
	width: 90%;
	align-items: center;
	margin: auto;
}
.button {
	display: block;
	width: 70%;
	margin: 5vh auto;
}
</style>
