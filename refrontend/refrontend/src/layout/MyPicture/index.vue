<script setup>
import { ref, onMounted, watch, onBeforeMount } from "vue"
import { useAuthStore } from "@/store/users"

const authStore = useAuthStore()
const userImages = ref(authStore.userImages)

//监听我的图片是否有变化，及时更新右侧列表
watch(
	() => authStore.userImages,
	(newVal) => {
		userImages.value = newVal
	},
	{ immediate: true }
)
onBeforeMount(() => {
	authStore.updateMyImages()
})
const handleDragStart = (event, imageUrl) => {
	event.dataTransfer.setData("text/uri-list", imageUrl)
	event.dataTransfer.effectAllowed = "copy"
}
</script>
<template>
	<div class="MyPicture">
		<el-aside
			width="200px"
			class="image-library"
		>
			<div class="library-header">
				<h3>我的图片</h3>
				<span class="image-count">{{ userImages.length }}</span>
			</div>
			<div class="library-content">
				<div
					v-if="userImages.length === 0"
					class="empty-library"
				>
					<el-empty description="暂无图片" />
				</div>
				<div
					v-else
					class="image-list"
				>
					<div
						v-for="(image, index) in userImages"
						:key="index"
						class="image-item"
						draggable="true"
						@dragstart="handleDragStart($event, image)"
					>
						<img
							:src="image"
							:alt="`用户图片${index + 1}`"
						/>
						<div class="image-overlay">
							<span>拖拽使用</span>
						</div>
					</div>
				</div>
			</div>
		</el-aside>
	</div>
</template>
<style scoped>
.MyPicture {
	display: inline-block;
	float: right;
	margin-top: 5px;
	/* position: absolute; */
	bottom: 10px;
	right: 0;
	/* width: 15%; */
	width: 15vw;
	/* height: 750px; */
	height: 91vh;
	background-color: white;
	border-radius: 15px;
	border: 1px solid #cbcccb;
}
.image-library {
	background: white;
	border-radius: 12px;
	padding: 24px;
	width: 100%;
	height: 100%;
	/* margin-left: 15px; */
	border: 1px solid #e8e8e8;
}

.library-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	border-bottom: 1px solid #f0f0f0;
	margin-bottom: 20px;
	padding-bottom: 12px;
}

.library-header h3 {
	margin: 0;
	color: #333;
	font-weight: 500;
}

.image-count {
	color: #999;
	font-size: 13px;
	background: #f5f5f5;
	padding: 2px 8px;
	border-radius: 10px;
}

/* .library-content {
} */

.empty-library {
	height: 300px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.image-list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.image-item {
	position: relative;
	border: 1px solid #e8e8e8;
	border-radius: 8px;
	overflow: hidden;
	cursor: grab;
	transition: all 0.2s ease;
	background: #fafafa;
	aspect-ratio: 1/1;
}

.image-item:hover {
	border-color: #409eff;
	transform: translateX(2px);
	box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.image-item:active {
	cursor: grabbing;
}

.image-item img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	object-position: center;
	display: block;
}

.image-overlay {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.6);
	color: white;
	display: flex;
	align-items: center;
	justify-content: center;
	opacity: 0;
	transition: opacity 0.2s ease;
	font-size: 12px;
	text-align: center;
}

.image-item:hover .image-overlay {
	opacity: 1;
}

/* 滚动条样式 */
/* .result-content::-webkit-scrollbar,
.library-content::-webkit-scrollbar {
	width: 4px;
}

.result-content::-webkit-scrollbar-track,
.library-content::-webkit-scrollbar-track {
	background: transparent;
}

.result-content::-webkit-scrollbar-thumb,
.library-content::-webkit-scrollbar-thumb {
	background: #d0d0d0;
	border-radius: 2px;
}

.result-content::-webkit-scrollbar-thumb:hover,
.library-content::-webkit-scrollbar-thumb:hover {
	background: #b0b0b0;
} */
</style>
