<script setup>
import { RouterView } from "vue-router"
</script>
<template>
	<main class="body">
		<router-view v-slot="{ Component }">
			<keep-alive>
				<component :is="Component" />
			</keep-alive>
		</router-view>
	</main>
</template>
<style scoped>
main.body {
	/* float: right; */
	/* width: 1400px; */
	display: inline-block;
	/* height: 760px; */
	height: 92vh;
	width: 75vw;
	/* margin: 5px auto; */
	border-radius: 15px;
	/* box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.3); */
	/* 隐藏超出的部分 */
	overflow: hidden;
}
</style>
