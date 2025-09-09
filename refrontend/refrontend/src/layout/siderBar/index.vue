<script setup>
// 左侧菜单列表,需要新增菜单路由直接新建文件，在路由里面添加路径，然后在下面数组里面新增元素
// 菜单的每个选项的name最好在四个字以里，若需要更多字需要调整整个菜单宽度
const iconList = [
	{
		name: "首页",
		icon: "icon-zhuye",
		url: "/home",
	},
	{
		name: "文生图",
		icon: "icon-wenshengtu",
		url: "/textToImage",
	},
	{
		name: "线稿成图",
		icon: "icon-icon_xiangaoshengtu2",
		url: "/lineToImage",
	},
	// {
	// 	name: "局部重绘",
	// 	icon: "icon-jubuzhonghui",
	// 	url: "/partialRedrawing",
	// },
	{
		name: "风格延伸",
		icon: "icon-fenggeqianyi2",
		url: "/styleExtend",
	},
	{
		name: "图片融合",
		icon: "icon-ronghebiao",
		url: "/imageFusion",
	},
	{
		name: "个人中心",
		icon: "icon-gerenzhongxin",
		url: "/personalCenter",
	},
]
const showName = (e) => {
	const element = e.target
	if (element.tagName === "UL") {
		element.classList.add("activeShow")
	}
}
const closeName = (e) => {
	const element = e.target
	if (element.tagName === "UL") {
		element.classList.remove("activeShow")
		element.style.transition = "width 0.5s ease"
	}
}
</script>
<template>
	<div class="nav">
		<div class="sider">
			<ul
				class="menu"
				@mouseover="showName"
				@mouseleave="closeName"
			>
				<li
					v-for="(item, index) in iconList"
					:key="index"
				>
					<router-link
						:to="item.url"
						class="link"
					>
						<i :class="['iconfont', item.icon]"></i>
						<span class="name">{{ item.name }}</span>
					</router-link>
				</li>
			</ul>
		</div>
	</div>
</template>
<style scoped>
.nav {
	display: inline-block;
	position: relative;
	width: 8vw;
	height: 92vh;
}
.sider {
	position: absolute;
	top: 10px;
	left: 15px;
	z-index: 100;
}

.menu {
	width: 50px;
	margin: 0 auto;
	padding: 5px 0;
	background-color: white;
	border-radius: 15px;
	/* 加阴阴 */
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.3);
}

.menu li {
	width: 30px;
	height: 40px;
	line-height: 40px;
	cursor: pointer;
	margin: 5px 10px;
	border-radius: 5px;
	background-color: white;
}

.menu li i {
	font-size: 20px;
	margin-left: 3px;
}

.menu li:hover {
	background-color: #f2f2f2;
}

.name {
	display: none;
}

.activeShow {
	width: 120px;
	transition: width 0.5s ease;
}

.activeShow li {
	width: 100px;
	transition: width 0.5s ease;
	/* 隐藏超出盒子的元素 */
	overflow: hidden;
}

.activeShow .name {
	display: inline-block;
	margin-left: 5px;
}

.link {
	display: inline-block;
	width: 100%;
	height: 100%;
	border-radius: 5px;
}

.router-link-active {
	/* background-color: #a6f774; */
	background-color: #77cbf3;
}
</style>
