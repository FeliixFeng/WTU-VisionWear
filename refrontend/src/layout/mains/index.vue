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
	display: flex;
	justify-content: space-between;
}
</style>
