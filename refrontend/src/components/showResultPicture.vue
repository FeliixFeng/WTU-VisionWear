<script setup>
const propos = defineProps({
	resultUrl: {
		type: String,
		required: false,
	},
})
</script>
<template>
	<div class="showResultPicture">
		<h1 class="resultTitle">生成结果</h1>
		<div class="showArea">
			<el-empty
				v-show="!propos.resultUrl"
				description="暂无生成结果"
			></el-empty>
			<el-image
				v-show="propos.resultUrl"
				:src="propos.resultUrl"
			></el-image>
		</div>
	</div>
</template>
<style scoped>
.showResultPicture {
	width: 100%;
	height: 100%;
	overflow: hidden;
}
h1.resultTitle {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin: 0 20px;
	padding: 15px 0;
	border-bottom: #0cadda 1px dashed;
}
.showArea {
	width: 100%;
	height: 100%;
	padding: 20px 15px;
}
</style>
