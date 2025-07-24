<script setup>
import user from "./components/user.vue"
</script>
<template>
	<el-affix
		position="top"
		:offset="0"
	>
		<div class="head">
			<div class="logo">Vision<span class="logo-x">Wear</span></div>
			<user />
		</div>
	</el-affix>
</template>
<style scoped>
.head {
	display: flex;
	justify-content: space-between;
	height: 7vh;
	width: 100%;
	line-height: 50px;
	background-color: rgb(250, 253, 253);
	font-size: 20px;
	color: #333;
	margin: 0 auto;
	border-radius: 15px 15px 0 0;
	border-bottom: 1px solid #e8e8e8;
	/* box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.05); */
}
.logo {
	height: 100%;
	width: 200px;
	float: left;
	margin-left: 20px;
	/* margin-top: 5px; */
	font-size: 28px;
	font-weight: 700;
	color: #3f8cda;
	white-space: nowrap;
	text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
	letter-spacing: 0.5px;
}

.logo-x {
	color: #e2725b;
	font-weight: 800;
}
</style>
