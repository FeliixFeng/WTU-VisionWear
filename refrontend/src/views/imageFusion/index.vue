<script setup>
import showResultPicture from "@/components/showResultPicture.vue"
import { useImageFusionStore } from "@/store/imageFusionStore.js"
import { useAuthStore } from "@/store/users"
import { ref, computed } from "vue"
import { ElMessage } from 'element-plus'

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
const progress = ref(0) // 进度条
const showProgress = ref(false) // 是否显示进度条

const canGenerate = computed(() => {
	return sketchUml.value !== "" && referenceUml.value !== ""
})

// 进度条颜色
const progressColor = computed(() => {
	if (progress.value < 30) return '#f56c6c'
	if (progress.value < 70) return '#e6a23c'
	return '#67c23a'
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
		textFeature: text.value,
	}
	loadingImages.value = true
	showProgress.value = true
	progress.value = 0
	resultImageUrl.value = "" // 清空之前的结果
	
	try {
		// 1. 提交融合任务
		const res1 = await imageFusionStore.doImageFusion(formData)
		const jobId = res1.data.jobId
		
		if (!jobId) {
			throw new Error('未获取到任务ID')
		}
		
		ElMessage.success('任务已提交，正在处理中...')
		
		// 2. 轮询获取结果（带进度更新）
		const res2 = await imageFusionStore.getFusionResultUrl(jobId, (currentProgress) => {
			progress.value = currentProgress
		})
		
		// 3. 显示结果
		if (res2 && res2.images && res2.images.length > 0) {
			resultImageUrl.value = res2.images[0].imageUrl
			authStore.updateMyImages()
			ElMessage.success('图片融合完成！')
		} else {
			throw new Error('未获取到生成结果')
		}
	} catch (error) {
		ElMessage.error('图片融合失败: ' + error.message)
		console.error('融合失败:', error)
	} finally {
		loadingImages.value = false
		showProgress.value = false
		progress.value = 0
	}
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
		<!-- 进度条 -->
		<div v-if="showProgress" class="progress-container">
			<el-progress 
				:percentage="progress" 
				:color="progressColor"
				:stroke-width="20"
			>
				<template #default="{ percentage }">
					<span class="progress-text">{{ percentage }}%</span>
				</template>
			</el-progress>
			<p class="progress-tip">正在生成中，请稍候...</p>
		</div>
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
.progress-container {
	margin: 20px 40px;
	padding: 15px;
	background-color: #f5f7fa;
	border-radius: 8px;
}
.progress-text {
	font-size: 14px;
	font-weight: bold;
	color: #333;
}
.progress-tip {
	text-align: center;
	margin-top: 10px;
	font-size: 13px;
	color: #909399;
}
</style>
