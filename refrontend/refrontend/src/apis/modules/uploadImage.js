import service from "../service.js"
import { MyMessage } from "@/utils/util.toast.js"

/**
 * 上传图片
 * @param {FormData} formData - 包含文件的表单数据
 * @returns {Promise} - 请求结果
 */
export function uploadImage(formData) {
	return service.post("/image/upload", formData).then((res) => {
		// 检查响应状态并显示相应的消息
		if (!res.status) {
			MyMessage.error(res.origin.msg)
		} else {
			MyMessage.success("图片上传成功")
		}
		return res
	})
}
