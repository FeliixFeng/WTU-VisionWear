<script setup>
import showResultPicture from "@/components/showResultPicture.vue"
import { useImageFusionStore } from "@/store/imageFusionStore.js"
import { useAuthStore } from "@/store/users"
import { ref, computed } from "vue"

defineOptions({
	name: "imageFusion",
})

const authStore = useAuthStore()
const imageFusionStore = useImageFusionStore()
const sketchUml = ref("")
const referenceUml = ref("")
const text = ref("")
const resultImageUrl = ref("")
const loadingImages = ref(false)

const canGenerate = computed(() => {
	return sketchUml.value !== "" && referenceUml.value !== ""
})
const changeSketch = (url) => {
	sketchUml.value = url
}
const changeReference = (url) => {
	referenceUml.value = url
}
const fusion = async () => {
	const formData = {
		urlList: [sketchUml.value, referenceUml.value],
		textFeature: text,
	}
	loadingImages.value = true
	const res1 = await imageFusionStore.doImageFusion(formData)
	const jobId = res1.data.jobId
	const res2 = await imageFusionStore.getFusionResultUrl(jobId)
	resultImageUrl.value = res2.images[0].imageUrl
	authStore.updateMyImages()
	loadingImages.value = false
}
</script>
<template>
	<div class="imageFusionFunction">
		<h1 class="imageFusionTitle">图片融合</h1>
		<p>款式图</p>
		<div class="sketch">
			<drag @updateUml="changeSketch" />
		</div>
		<p>参考图</p>
		<div class="reference">
			<drag @updateUml="changeReference" />
		</div>
		<p>设计特征</p>
		<div class="feature">
			<el-input
				v-model="text"
				:rows="4"
				resize="none"
				type="textarea"
				placeholder="请输入对设计的描述或提示词提高生成准度"
			/>
		</div>
		<el-button
			class="button"
			:disabled="!canGenerate"
			:loading="loadingImages"
			@click="fusion"
			type="success"
			>一键生成</el-button
		>
	</div>
	<div class="imageFusionRes">
		<showResultPicture :resultUrl="resultImageUrl" />
	</div>
</template>
<style scoped>
.imageFusionFunction {
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 2px solid #c9e1fa;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
.imageFusionRes {
	float: left;
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 1px solid #cbcccb;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
h1.imageFusionTitle {
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
.sketch {
	margin: 10px 0 20px 80px;
	width: 70%;
	height: 18%;
	border-radius: 5%;
	border: #65d6f5 1px dashed;
}
.reference {
	margin: 10px 0 20px 80px;
	width: 70%;
	height: 18%;
	border-radius: 5%;
	border: #65d6f5 1px dashed;
}
.feature {
	margin: 10px 0 20px 80px;
	width: 70%;
	height: 16%;
}
.button {
	display: block;
	width: 60%;
	margin: 0 auto;
}
</style>
