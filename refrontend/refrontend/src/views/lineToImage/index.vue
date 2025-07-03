<script setup>
import showResultPicture from "@/components/showResultPicture.vue"
</script>
<template>
	<div class="lineToImageFunction">
		<h1 class="lineToImageTitle">线稿成图</h1>
	</div>
	<div class="lineToImageRes">
		<showResultPicture />
	</div>
</template>
<style scoped>
.lineToImageFunction {
	float: left;
	width: 550px;
	height: 750px;
	border-radius: 15px;
	border: 2px solid #c9e1fa;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
.lineToImageRes {
	float: left;
	width: 550px;
	height: 750px;
	border-radius: 15px;
	border: 1px solid #cbcccb;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	margin-left: 25px;
	background-color: white;
}
h1.lineToImageTitle {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin: 0 20px;
	padding: 15px 0;
	border-bottom: #0cadda 1px dashed;
}
</style>
