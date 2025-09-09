<script setup>
import showResultPicture from "@/components/showResultPicture.vue"
import { ref, reactive, onMounted, computed } from "vue"
import { useStyleExtendStore } from "@/store/styleExtendStore.js"
import { useAuthStore } from "@/store/users"

defineOptions({
	name: "styleExtend",
})

const authStore = useAuthStore()
const styleExtendStore = useStyleExtendStore()
const resultImage = ref("")
const formRef = ref(null)
const loadingImages = ref(false)
const sourceImageUrl = ref("")
const canGenerate = computed(() => {
	return (
		formData.prompt.trim().length >= 3 && sourceImageUrl.value.trim() !== ""
	)
})

// 风格选项,需要添加选项直接修改下面数组即可
const styleOptions = [
	{ label: "默认", value: "" },
	{ label: "3D模型", value: "3d-model" },
	{ label: "模拟胶片", value: "analog-film" },
	{ label: "动漫", value: "anime" },
	{ label: "电影", value: "cinematic" },
	{ label: "漫画书", value: "comic-book" },
	{ label: "数字艺术", value: "digital-art" },
	{ label: "增强", value: "enhance" },
	{ label: "奇幻艺术", value: "fantasy-art" },
	{ label: "等距", value: "isometric" },
	{ label: "线稿", value: "line-art" },
	{ label: "低多边形", value: "low-poly" },
	{ label: "建模化合物", value: "modeling-compound" },
	{ label: "霓虹朋克", value: "neon-punk" },
	{ label: "折纸", value: "origami" },
	{ label: "摄影", value: "photographic" },
	{ label: "像素艺术", value: "pixel-art" },
	{ label: "瓷砖纹理", value: "tile-texture" },
]
// 参考模式选项
const referenceMode = [
	{ label: "提示词模式", value: "creative" },
	{ label: "全参考模式", value: "reference" },
	{ label: "人物参考模式", value: "reference_char" },
]

// 图片尺寸
const sizes = [
	{ width: 512, height: 512, label: "512 x 512" },
	{ width: 768, height: 768, label: "768 x 768" },
	{ width: 1024, height: 1024, label: "1024 x 1024" },
	{ width: 1328, height: 1328, label: "1328 x 1328" },
]

// 表单验证规则
const rules = {
	prompt: [
		{ required: true, message: "请输入提示词", trigger: "blur" },
		{ min: 3, message: "提示词至少需要3个字符", trigger: "blur" },
	],
}

// 表单数据，与DTO结构对应
const formData = reactive({
	prompt: "",
	gpen: 0.4, // 高清处理效果
	genMode: "creative", // 参考模式
	style: "", // 风格（会添加到prompt中）
	seed: "-1", // 随机种子
	size: sizes[2], // 图像尺寸
})

const changeSourceImage = (url) => {
	sourceImageUrl.value = url
}
const generatePicture = async function () {
	const requestBody = {
		req_key: "i2i_portrait_photo", // 固定值
		image_input: sourceImageUrl.value, // 源图像URL
		prompt: prompt, // 包含风格的提示词
		width: formData.size.width,
		height: formData.size.height,
		gpen: formData.gpen,
		skin: 0.3, // 使用默认值
		skin_unifi: 0, // 使用默认值
		gen_mode: formData.genMode,
		seed: formData.seed,
	}
	loadingImages.value = true
	const res = await styleExtendStore.doStyleExtend(formData)
	resultImage.value = res.data[0]
	authStore.updateMyImages()
	loadingImages.value = false
}
</script>
<template>
	<div class="styleExtendFunction">
		<h1 class="styleExtendTitle">风格延伸</h1>
		<div class="DataInput">
			<p>源图像</p>
			<div class="sourceImage select">
				<drag @updateUml="changeSourceImage" />
			</div>
			<p>样式预设</p>
			<el-select
				class="select"
				v-model="formData.style"
				placeholder="请选择预设样式"
			>
				<el-option
					v-for="(option, index) in styleOptions"
					:label="option.label"
					:value="option.value"
				></el-option>
			</el-select>
			<p>参考模式</p>
			<el-select
				class="select"
				v-model="formData.genMode"
				placeholder="请选择参考模式"
			>
				<el-option
					v-for="(option, index) in referenceMode"
					:label="option.label"
					:value="option.value"
				></el-option>
			</el-select>

			<p>提示词</p>
			<el-form
				ref="formRef"
				:model="formData"
				:rules="rules"
			>
				<el-form-item
					class="select"
					prop="prompt"
				>
					<el-input
						v-model="formData.prompt"
						:rows="3"
						resize="none"
						type="textarea"
						placeholder="描述您想要的图像风格和内容（必填项,越详细越好）"
					/>
				</el-form-item>
			</el-form>
			<p>高清处理效果</p>
			<p>图像尺寸</p>
			<div class="line">
				<el-slider
					class="dispose"
					v-model="formData.gpen"
					:min="0"
					:max="1"
					:step="0.01"
					:format-tooltip="(value) => value.toFixed(2)"
				></el-slider>
				<div class="slider-value">{{ formData.gpen.toFixed(2) }}</div>
				<el-select
					class="size"
					:value-key="'label'"
					v-model="formData.size"
					placeholder="选择图像尺寸"
				>
					<el-option
						v-for="(option, index) in sizes"
						:label="option.label"
						:value="option"
					/>
				</el-select>
			</div>
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
	<div class="styleExtendRes">
		<!-- <showResultPicture  /> -->
		<showResultPicture :resultUrl="resultImage" />
	</div>
</template>
<style scoped>
.styleExtendFunction {
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 2px solid #c9e1fa;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
.styleExtendRes {
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 1px solid #cbcccb;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
h1.styleExtendTitle {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin: 0 20px;
	padding: 15px 0;
	border-bottom: #0cadda 1px dashed;
}
.sourceImage {
	margin: 10px 0 20px 80px;
	height: 18vh;
	border-radius: 5%;
	border: #65d6f5 1px dashed;
}
.DataInput {
	position: relative;
}
.slider-value {
	width: 50px;
	color: var(--el-text-color-secondary);
	font-size: 12px;
}
.select {
	display: block;
	margin: auto;
	width: 80%;
}
p {
	margin: 20px 20px 5px 20px;
}
.line {
	display: flex;
	justify-content: space-around;
	align-items: center;
}
.DataInput p:nth-last-of-type(-n + 2) {
	display: inline-block;
	margin: 20px 0 5px 20px;
}
.DataInput p:last-of-type {
	margin-left: 35%;
}
.dispose {
	width: 40%;
}
.size {
	width: 40%;
}
.button {
	display: block;
	width: 60%;
	margin: 3vh auto;
}
</style>
