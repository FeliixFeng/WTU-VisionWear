<script setup>
import showResultPicture from "@/components/showResultPicture.vue"
import { ref, reactive, onMounted, computed } from "vue"
import { useLineToImageStore } from "@/store/lineToImageStore.js"
import { useAuthStore } from "@/store/users"
import rawingCanvas from "@/components/rawingCanvas.vue"
defineOptions({
	name: "lineToImage",
})

const authStore = useAuthStore()
const lineToImageStore = useLineToImageStore()
const loadingImages = ref(false)
const sourceImageUrl = ref("")
const resultImage = ref("")

const drawCanvas = (data) => {
	//console.log(data.canvasUrl);
	sourceImageUrl.value = data.canvasUrl;
}

const requestBody = reactive({
	sketchImageURL: "",
	prompt: "",
	rspImgType: "base64",
	config: "",
})
const canGenerate = computed(() => {
	return (
		requestBody.prompt.trim().length >= 3 && sourceImageUrl.value.trim() !== ""
	)
})
const changeSourceImage = (url) => {
	sourceImageUrl.value = url
	requestBody.sketchImageURL = url
}
const generatePicture = async () => {
	loadingImages.value = true
	const res = await lineToImageStore.doLineToImage(requestBody)
	console.log("res:", res)
	resultImage.value = res.data[0]
	authStore.updateMyImages()
	loadingImages.value = false
}
</script>
<template>
	<div class="lineToImageFunction">
		<h1 class="lineToImageTitle">线稿成图</h1>
		<el-tabs type="border-card" style="width: 500px;  margin: 10px auto">
			<el-tab-pane label="上传线稿">
				<div class="fileInput">
					<drag @updateUml="changeSourceImage" />
				</div>
			</el-tab-pane>
			<el-tab-pane label="画板">
				<div class="canvas-container">
					<rawingCanvas @draw="drawCanvas"></rawingCanvas>
				</div>
			</el-tab-pane>
		</el-tabs>

		<el-input v-model="requestBody.prompt" style="width: 500px; margin: 15px 25px" :rows="3" resize="none"
			type="textarea" placeholder="描述您想要的图像风格和内容（必填项,至少三个字）" />
		<p>文件类型</p>
		<el-radio-group v-model="requestBody.rspImgType" style="margin: 0 0 20px 25px">
			<el-radio value="base64" size="large">base64</el-radio>
			<el-radio value="url" size="large">url</el-radio>
		</el-radio-group>
		<el-button class="button" :disabled="!canGenerate" :loading="loadingImages" type="success"
			style="width: 500px; margin: 50px 0 0 25px" @click="generatePicture">一键生成</el-button>
	</div>
	<div class="lineToImageRes">
		<showResultPicture :resultUrl="resultImage" />
	</div>
</template>
<style scoped>
.lineToImageFunction {
	float: left;
	width: 550px;
	height: 750px;
	border-radius: 15px;
	border: 2px solid #c9e1fa;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}

.lineToImageRes {
	float: left;
	width: 550px;
	height: 750px;
	border-radius: 15px;
	border: 1px solid #cbcccb;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	margin-left: 25px;
	background-color: white;
}

h1.lineToImageTitle {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin: 0 20px;
	padding: 15px 0;
	border-bottom: #0cadda 1px dashed;
}

.fileInput {
	width: 100%;
	height: 230px;
}

.canvas-container {
	height: 230px;
	position: relative;
}

p {
	padding-left: 25px;
	margin: 0;
	margin-top: 10px;
}
</style>
