import service from "../service.js"
import { MyMessage } from "@/utils/util.toast.js"

export const authLogin = (data) => {
	return service.post("/auth/login", data).then((res) => {
		// 登陆成功弹出成功信息，失败弹出失败信息
		if (!res.status) {
			MyMessage.error(res.origin.msg)
		} else {
			MyMessage.success(res.origin.msg)
		}
		return res
	})
}

/**
 * 用户退出登录
 */
export const authLogout = () => {
	return service.post("/auth/logout")
}