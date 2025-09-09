<script setup>
import drag from "@/components/drag.vue"
import showResultPicture from "@/components/showResultPicture.vue"

const changePicture = (url) => {
	console.log("Received picture URL:", url)
}
</script>
<template>
	<div class="styleTransferFunction">
		<h1 class="styleTransferTitle">风格迁移</h1>
	</div>
	<div class="styleTransferRes">
		<showResultPicture />
	</div>
</template>

<style scoped>
.styleTransferFunction {
	float: left;
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 2px solid #c9e1fa;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
h1.styleTransferTitle {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin: 0 20px;
	padding: 15px 0;
	border-bottom: #0cadda 1px dashed;
}
.styleTransferRes {
	float: left;
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 1px solid #cbcccb;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
</style>
