import { defineConfig } from "vite"
import { fileURLToPath, URL } from "node:url"
import vue from "@vitejs/plugin-vue"
import AutoImport from "unplugin-auto-import/vite"
import Components from "unplugin-vue-components/vite"
import { ElementPlusResolver } from "unplugin-vue-components/resolvers"

// https://vite.dev/config/
export default defineConfig({
	plugins: [
		vue(),
		AutoImport({
			resolvers: [ElementPlusResolver()],
		}),
		Components({
			resolvers: [ElementPlusResolver()],
		}),
	],
	resolve: {
		alias: {
			"@": fileURLToPath(new URL("./src", import.meta.url)),
			store: fileURLToPath(new URL("src/store", import.meta.url)),
			utils: fileURLToPath(new URL("src/utils", import.meta.url)),
			// libs: fileURLToPath(new URL("src/libs", import.meta.url)),
		},
	},
	server: {
		port: 9090, // 你想要的端口号
	},
})
