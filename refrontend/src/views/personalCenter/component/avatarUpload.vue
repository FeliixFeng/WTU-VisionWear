<script setup>
import { ref, onMounted, defineProps, defineEmits } from "vue"
import { ElMessage } from "element-plus"

const props = defineProps({
	currentAvatar: {
		type: String,
		default: "",
	},
})

const emit = defineEmits(["updateAvatar"])

const showInputImg = ref(false)
const inputRef = ref()
const file = ref()
const inputUrl = ref("")
const resultUrl = ref("")
const imgCvs = ref()
const preCvs = ref()
let dragging = false
let rafId = null
const coord = {
	x: 0,
	y: 0,
}
// 拖拽偏移量
const offset = {
	x: 0,
	y: 0,
}
let picture = null

class Picture {
	constructor() {
		this.img = new Image()
		this.img.src = inputUrl.value
		this.n = 0.15 //缩放系数
		this.x = 0
		this.y = 0
		this.ready = new Promise((resolve) => {
			this.img.onload = () => {
				this.width = this.img.width
				this.height = this.img.height
				this.initialize()
				resolve(this) // 加载完成后通知外部
			}
		})
	}
	// 初始化数据
	initialize() {
		if (this.width > this.height) {
			if (this.width > 350) {
				const scale = 350 / this.width
				this.height = scale * this.height
				this.width = 350
			}
		} else {
			if (this.height > 350) {
				const scale = 350 / this.height
				this.width = scale * this.width
				this.height = 350
			}
		}
		this.updataCoord()
	}
	// 更新顶点坐标
	updataCoord() {
		this.x = (350 - this.width) / 2
		this.y = (350 - this.height) / 2
	}
	draw() {
		const ctx = imgCvs.value.getContext("2d")
		// 画图前需要清空画布
		ctx.clearRect(0, 0, 350, 350)
		ctx.drawImage(this.img, this.x, this.y, this.width, this.height)
		//蒙层（裁剪区域）
		ctx.strokeStyle = "rgba(255,255,255,0.6)"
		ctx.strokeRect(100, 100, 150, 150)
		//预览图绘制
		this.extractImg()
	}
	// 图片放大
	magnify() {
		this.width = (this.n + 1) * this.width
		this.height = (this.n + 1) * this.height
		this.updataCoord()
		this.draw()
	}
	// 图片缩小
	shrink() {
		this.width = (1 - this.n) * this.width
		this.height = (1 - this.n) * this.height
		this.updataCoord()
		this.draw()
	}
	// 图片拖拽
	dragPicture() {
		this.x += offset.x
		this.y += offset.y
		this.draw()
	}
	//提取蒙层里的图片并预览
	extractImg() {
		const ctx = imgCvs.value.getContext("2d")
		const preCtx = preCvs.value.getContext("2d")
		const imageData = ctx.getImageData(100, 100, 150, 150)
		preCtx.putImageData(imageData, 0, 0)
		resultUrl.value = preCvs.value.toDataURL("image/png")
	}
}

const magnify = () => {
	if (!inputUrl.value || !picture) return
	picture.magnify()
}

const shrink = () => {
	if (!inputUrl.value || !picture) return
	picture.shrink()
}

const startDraPic = (e) => {
	coord.x = e.offsetX
	coord.y = e.offsetY
	dragging = true
}

const onDragPic = (e) => {
	if (!inputUrl.value || !picture || !dragging) return
	// 限制为60祯，避免每次移动都重绘图片导致过于卡顿
	if (!rafId) {
		rafId = requestAnimationFrame(() => {
			const newX = e.offsetX
			const newY = e.offsetY
			offset.x = newX - coord.x
			offset.y = newY - coord.y
			coord.x = newX
			coord.y = newY
			picture.dragPicture()
			rafId = null // 清除标记，准备下一次
		})
	}
}

const stopDraPic = () => {
	dragging = false
	offset.x = 0
	offset.y = 0
}

const inputImg = () => {
	inputRef.value.click()
}

const fileChange = async (e) => {
	const files = e.target.files
	if (files && files.length > 0) {
		file.value = files[0]
		inputUrl.value = URL.createObjectURL(file.value)
		picture = new Picture()
		await picture.ready
		picture.draw()
		imgCvs.value.style.cursor = "grab"
	}
}
const close = () => {
	showInputImg.value = false
	URL.revokeObjectURL(inputUrl.value)
	inputUrl.value = null
	imgCvs.value.style.cursor = "auto"
	const ctx = imgCvs.value.getContext("2d")
	const preCtx = preCvs.value.getContext("2d")
	ctx.clearRect(0, 0, 350, 350)
	preCtx.clearRect(0, 0, 150, 150)
	inputRef.value.value = null
}
const save = () => {
	emit("updateAvatar", resultUrl.value)
	close()
}
</script>
<template>
	<div class="headPortrait">
		<div class="showAvatar">
			<i
				class="iconfont icon-shangchuan"
				@click="showInputImg = true"
			></i>
		</div>
		<div
			class="inputMask"
			v-show="showInputImg"
		>
			<div class="inputBox">
				<div class="tailor">
					<canvas
						@mousedown="startDraPic"
						@mousemove="onDragPic"
						@mouseup="stopDraPic"
						@mouseleave="stopDraPic"
						ref="imgCvs"
						class="myImg"
						width="350"
						height="350"
					></canvas>
					<input
						ref="inputRef"
						type="file"
						accept="image/*"
						@change="fileChange"
					/>
					<el-button
						style="margin-left: 130px"
						@click="inputImg"
						>头像上传</el-button
					>
					<i
						class="iconfont icon-fangda"
						@click="magnify"
					></i>
					<i
						class="iconfont icon-suoxiao"
						@click="shrink"
					></i>
				</div>
				<div class="preview">
					<canvas
						ref="preCvs"
						class="previewImg"
						width="150"
						height="150"
					></canvas>
				</div>
				<el-button
					type="success"
					@click="close"
					style="margin: 10px 10px 0 620px; width: 100px"
				>
					取消
				</el-button>
				<el-button
					type="success"
					@click="save"
					style="width: 100px; margin-top: 10px"
				>
					保存
				</el-button>
			</div>
		</div>
	</div>
</template>

<style scoped>
.showAvatar {
	position: relative;
	width: 150px;
	height: 150px;
	/* background-color: green; */
	margin: auto;
	border-radius: 50%;
	box-shadow: 0 0 10px 5px rgba(1, 1, 1, 0.15);
}
i.icon-shangchuan {
	position: absolute;
	font-size: 40px;
	bottom: 0px;
	right: 5px;
	color: rgb(219, 224, 220);
	cursor: pointer;
}
i.icon-shangchuan:hover {
	color: aqua;
}
.headPortrait .inputMask {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 999;
	background-color: rgba(0, 0, 0, 0.5);
}
.inputBox {
	background-color: white;
	width: 880px;
	height: 500px;
	margin: auto;
	margin-top: 100px;
	border-radius: 10px;
}
.tailor {
	display: inline-block;
	width: 440px;
	height: 440px;
	vertical-align: top;
}
.preview {
	display: inline-block;
	width: 440px;
	height: 440px;
	vertical-align: top;
}
.myImg {
	display: block;
	margin: 0 auto;
	margin-top: 30px;
	box-shadow: 0 0 10px 1px rgba(1, 1, 1, 0.15);
}
.previewImg {
	display: block;
	margin: 0 auto;
	margin-top: 120px;
	border-radius: 50%;
	box-shadow: 0 0 10px 1px rgba(1, 1, 1, 0.15);
}
input {
	position: absolute;
	pointer-events: none;
	opacity: 0;
}
i.icon-fangda {
	display: inline-block;
	margin: 50px 20px;
	margin-top: 20px;
	cursor: pointer;
}
i.icon-fangda:hover {
	color: #096fe4;
}
i.icon-suoxiao {
	cursor: pointer;
}
i.icon-suoxiao:hover {
	color: #096fe4;
}
</style>
