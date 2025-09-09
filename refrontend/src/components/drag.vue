<script setup>
import { ref, watch, onMounted } from "vue"
import { uploadImage } from "@/apis/modules/uploadImage.js"

const pictureUrl = ref("")
const upload = ref(null)
const showClose = ref(false)
const drag = ref(null)
const emit = defineEmits(["updateUml"])

watch(pictureUrl, (newUrl, oldUrl) => {
	emit("updateUml", newUrl)
})

const close = () => {
	pictureUrl.value = ""
	if (upload.value) {
		upload.value.value = null
	}
	upload.value = null
}
const handleOver = () => {
	if (pictureUrl.value) {
		showClose.value = true
	}
}
const handleOut = () => {
	showClose.value = false
}
const urlInput = function (e) {
	e.preventDefault()
	if (e.dataTransfer.files.length > 0) {
		const file = e.dataTransfer.files[0]
		pictureUrl.value = URL.createObjectURL(file)
		return
	}
	pictureUrl.value = e.dataTransfer.getData("text/uri-list")
}
const fileInput = async (e) => {
	// 本地的图片需要先上传拿到图片的URL
	if (e.target.files.length > 0) {
		const file = e.target.files[0]
		//拿到网络地址前先用本地地址展示图片
		pictureUrl.value = URL.createObjectURL(file)
		const formData = new FormData()
		formData.append("file", file)
		const res = await uploadImage(formData)
		if (res.status) {
			pictureUrl.value = res.origin.data
		} else {
			pictureUrl.value = ""
		}
	} else {
		pictureUrl.value = ""
	}
}
const clickInput = function (e) {
	if (!pictureUrl.value) {
		upload.value.click()
	}
}
onMounted(() => {
	drag.value.ondragover = (e) => {
		e.preventDefault()
	}
})
</script>
<template>
	<div
		class="drag"
		ref="drag"
		@drop="urlInput"
		@click="clickInput"
		@mouseover="handleOver"
		@mouseout="handleOut"
	>
		<p
			class="callWord"
			v-show="!pictureUrl"
		>
			点击或拖动以上传
		</p>
		<i
			class="iconfont icon-shanchuguanbicha icon"
			@click="close"
			@click.stop="close"
			v-show="showClose"
		></i>
		<input
			class="upload"
			ref="upload"
			type="file"
			@change="fileInput"
			@click.stop
		/>
		<img
			class="showImg"
			v-if="pictureUrl"
			:src="pictureUrl"
		/>
	</div>
</template>
<style scoped>
.drag {
	width: 100%;
	height: 100%;
	margin: 0;
	position: relative;
	overflow: hidden;
	display: flex;
	justify-content: center;
	align-items: center;
	cursor: pointer;
	border-radius: 5%;
}
.drag:hover {
	background-color: #f0f0f0;
}
.callWord {
	position: absolute;
	width: 100%;
	text-align: center;
	color: #4c4c4c;
}
.icon {
	position: absolute;
	z-index: 10;
	right: 3px;
	top: 3px;
	font-size: 20px;
	color: #b0b0b0;
	cursor: pointer;
}
.icon:hover {
	color: #4c4c4c;
}
.upload {
	width: 100%;
	height: 100%;
	opacity: 0;
	cursor: pointer;
}
.showImg {
	position: absolute;
	width: 100%;
	height: 95%;
	object-fit: contain;
	border-radius: 10px;
}
</style>
