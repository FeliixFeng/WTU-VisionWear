import { textToImage } from "@/apis/modules/images.js"
import { MyMessage } from "@/utils/util.toast.js"
import { defineStore } from "pinia"

export const useTextToImageStore = defineStore("textToImageStore", () => {
	async function doTextToImage(formData) {
		// 准备请求体数据 - 调整为匹配后端API参数
		const requestBody = {
			prompt: formData.prompt,
			req_key: "high_aes_general_v30l_zt2i",
			width: formData.width,
			height: formData.height,
			// 如果prompt较短，开启文本扩写
			use_pre_llm: formData.prompt.length < 10,
			// 添加scale参数，从cfgScale映射到后端的scale范围(1-10)
			scale: Math.min(10, Math.max(1, formData.cfgScale / 3)),
		}
		// 如果选择了风格且不是默认风格，将风格添加到提示词中
		if (formData.style) {
			requestBody.prompt += `, ${formData.style} style`
		}
		return textToImage(requestBody).then((res) => {
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
		doTextToImage,
	}
})
