import { lineToImage } from "@/apis/modules/images.js"
import { MyMessage } from "@/utils/util.toast.js"
import { defineStore } from "pinia"

export const useLineToImageStore = defineStore("lineToImageStore", () => {
	async function doLineToImage(requestBody) {
		// 准备请求体数据 - 调整为匹配后端API参数
		console.log("requestBody:", requestBody)
		return lineToImage(requestBody).then((res) => {
			// 检查响应状态并显示相应的消息
			if (!res.status) {
				MyMessage.error(res.origin.msg)
			} else {
				MyMessage.success(res.origin.msg)
			}
			return res
		})
	}
	return {
		doLineToImage,
	}
})
