<script setup>
import showResultPicture from "@/components/showResultPicture.vue"
</script>
<template>
	<div class="partialRedrawingFunction">
		<h1 class="partialRedrawingTitle">局部重绘</h1>
	</div>
	<div class="partialRedrawingRes">
		<showResultPicture />
	</div>
</template>
<style scoped>
.partialRedrawingFunction {
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 2px solid #c9e1fa;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
.partialRedrawingRes {
	width: 36vw;
	height: 91vh;
	border-radius: 15px;
	border: 1px solid #cbcccb;
	box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.1);
	margin: 5px;
	background-color: white;
}
h1.partialRedrawingTitle {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin: 0 20px;
	padding: 15px 0;
	border-bottom: #0cadda 1px dashed;
}
</style>
