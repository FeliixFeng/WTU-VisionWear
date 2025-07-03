<script setup>
import { ref } from "vue"
const isShow = ref(false)
const showDescription = () => {
	isShow.value = !isShow.value
}
</script>
<template>
	<div
		class="user"
		@click="showDescription"
	>
		<i class="iconfont icon-dengluzhanghaodenglu icon"></i>
	</div>
	<div
		class="description"
		v-show="isShow"
	></div>
</template>
<style scoped>
.user {
	float: right;
	height: 100%;
	width: 100px;
	margin-right: 40px;
}
.icon {
	font-size: 35px;
	color: rgb(146, 245, 245);
	cursor: pointer;
}
.description {
	position: absolute;
	top: 55px;
	right: 120px;
	width: 100px;
	height: 150px;
	background-color: red;
	border-radius: 8px;
	z-index: 100;
}
</style>
