<script setup>
import MyHead from "./head/index.vue"
import siderBar from "./siderBar/index.vue"
import mains from "./mains/index.vue"
import MyPicture from "./MyPicture/index.vue"
</script>
<template>
	<div class="container">
		<MyHead />
		<div class="myBody">
			<siderBar />
			<mains />
			<MyPicture />
		</div>
	</div>
</template>
<style scoped>
.container {
	position: relative;
	width: calc(100% - 10px);
	height: 100vh;
	margin: 0 auto;
	background-color: #f2f2f2;
	border-radius: 15px;
}
.myBody {
	display: flex;
	justify-content: space-between;
}
</style>
