<script setup>
import { useRouter } from "vue-router"
const router = useRouter()

const list = [
	{
		title: "图灵绘境",
		icon: "icon-wenshengtu",
		description: "基于AI生成时尚图像",
		url: "/textToImage",
	},
	{
		title: "风格延伸",
		icon: "icon-fenggeqianyi2",
		description: "将您的图片转换为不同风格",
		url: "/styleExtend",
	},
	{
		title: "图片融合",
		icon: "icon-ronghebiao",
		description: "将两张图片融合在一起",
		url: "/imageFusion",
	},
	{
		title: "线稿成图",
		icon: "icon-icon_xiangaoshengtu2",
		description: "将线稿快速转化为完整图像",
		url: "/lineToImage",
	},
]
</script>
<template>
	<div class="homeContainer">
		<h1>欢迎使用 VisionWear</h1>
		<p class="des">AI定义时尚，科技塑造未来</p>
		<p class="funcNav">功能导航</p>
		<ul class="navList">
			<li v-for="(item, index) in list">
				<div
					class="card"
					@click="router.push(item.url)"
				>
					<i :class="['iconfont', item.icon]"></i>
					<p class="title">{{ item.title }}</p>
					<p class="description">{{ item.description }}</p>
				</div>
			</li>
		</ul>
		<p class="star">开始使用</p>
		<p class="addition">请从左侧菜单选择功能或点击上方卡片直接访问</p>
	</div>
</template>
<style scoped>
.homeContainer {
	margin-top: 5px;
	width: 75vw;
	height: 91vh;
	padding: 30px;
	background-color: white;
	border-radius: 15px;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
}
h1 {
	font-size: 28px;
	color: #409eff;
	margin-bottom: 8px;
}
.des {
	font-size: 16px;
	color: #909399;
	margin-bottom: 30px;
}
.funcNav {
	font-size: 20px;
	margin-bottom: 20px;
	color: #303133;
	font-weight: 500;
	border-bottom: 1px solid #ebeef5;
	padding-bottom: 10px;
}
.navList {
	display: flex;
	justify-content: space-around;
}
li i {
	font-size: 35px;
	color: #409eff;
}
.card {
	width: 200px;
	height: 200px;
	cursor: pointer;
	background-color: #f5f7fa;
	padding: 25px 15px;
	text-align: center;
	transition: 0.3s;
}
.card:hover {
	box-shadow: 0 0 2px 2px rgba(27, 37, 139, 0.1);
	transform: scale(1.1);
}
.card .description {
	font-size: 14px;
	color: #909399;
}
.star {
	font-size: 20px;
	margin: 30px 0 10px 0;
	color: #303133;
	font-weight: 500;
	border-bottom: 1px solid #ebeef5;
	padding-bottom: 10px;
}
.addition {
	margin: 0;
	font-size: 14px;
	color: #6d7075;
}
</style>
