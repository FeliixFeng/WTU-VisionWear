import { authLogin } from "@/apis/modules/login.js"
import { useRouter } from "vue-router"
import dbUtils from "utils/util.strotage.js"
import { getMyImages } from "@/apis/modules/getMyImages"
import { defineStore } from "pinia"
import { register } from "@/apis/modules/weChat"
import { ref } from "vue"

export const useAuthStore = defineStore("users", () => {
	const userImages = ref([])
	let router = useRouter()
	async function logout() {
		// 执行退出登录逻辑，例如清除用户凭证和重置用户状态等
		dbUtils.clear()
		// 导航到登录页或其他适当的页面
		await router.replace("/login")
	}
	async function login(form) {
		try {
			const loginInfo = await authLogin(form)
			dbUtils.clear()
			// 存储 accessToken 和 refreshToken
			dbUtils.set("accessToken", loginInfo.data.accessToken)
			dbUtils.set("refreshToken", loginInfo.data.refreshToken)
			const userData = loginInfo.data
			dbUtils.set("userData", userData)
			// 导航到登录页或其他适当的页面
			await router.push({ path: "/" })
			return { userData }
		} catch (err) {
			console.log(err)
		}
	}
	async function DoRegister(userData) {
		return register(userData).then((res) => {
			if (!res.status) {
				MyMessage.error(res.origin.msg)
			} else {
				MyMessage.success(res.origin.msg)
			}
		})
	}
	async function updateMyImages() {
		const response = await getMyImages()
		userImages.value = response.data
	}
	return {
		DoRegister,
		userImages,
		logout,
		login,
		updateMyImages,
	}
})
