<script setup>
import MyHead from "./head/index.vue"
import siderBar from "./siderBar/index.vue"
import mains from "./mains/index.vue"
import MyPicture from "./MyPicture/index.vue"
</script>
<template>
	<div class="container">
		<MyHead />
		<siderBar />
		<mains />
		<MyPicture />
	</div>
</template>
<style scoped>
.container {
	position: relative;
	width: calc(100% - 10px);
	height: 820px;
	margin: 0 auto;
	background-color: #f2f2f2;
	/* background-color: red; */
	border-radius: 15px;
}
</style>
