import { imageFusion, getFusionResult } from "@/apis/modules/images.js"
import { MyMessage } from "@/utils/util.toast.js"
import { defineStore } from "pinia"

export const useImageFusionStore = defineStore("imageFusionStore", () => {
	async function doImageFusion(formData) {
		// 创建符合后端ImageFusionDTO格式的请求体
		const requestBody = {
			imageUrlList: formData.urlList,
			dimensions: "SQUARE", // 使用默认值，可以根据需要修改
			mode: "relax", // 使用默认值，可以根据需要修改
			hookUrl: "", // 不使用回调
			textFeature: formData.Feature, // 添加设计特征
		}
		console.log("requestBody", requestBody)
		return imageFusion(requestBody).then((res) => {
			// 检查响应状态并显示相应的消息
			if (!res.status) {
				MyMessage.error(res.origin.msg)
			} else {
				MyMessage.success(res.origin.msg)
			}
			return res
		})
	}
	async function getFusionResultUrl(jobId, progressCallback) {
		return pollGenerationResult(jobId, progressCallback)
	}

	const pollGenerationResult = async (jobId, progressCallback) => {
		const maxAttempts = 60 // 最多轮询60次
		const interval = 2000 // 每2秒查询一次

		if (!jobId) {
			throw new Error("无效的任务ID")
		}

		console.log(`开始轮询任务结果，任务ID: ${jobId}`)

		for (let attempt = 0; attempt < maxAttempts; attempt++) {
			try {
				console.log(`轮询尝试 ${attempt + 1}/${maxAttempts}`)

				const response = await getFusionResult(jobId)
				const result = response.data

				console.log("轮询返回结果:", result)

				// 更新进度
				if (result.progress !== undefined && progressCallback) {
					progressCallback(result.progress)
				}

				// 检查是否完成（有图片结果）
				if (result.images && result.images.length > 0) {
					console.log("图片融合完成，生成结果:", result.images)
					if (progressCallback) {
						progressCallback(100) // 确保进度条显示100%
					}
					return result
				}

			// 如果进度小于100，继续轮询
			if (result.progress !== undefined && result.progress < 100) {
				console.log(`当前进度: ${result.progress}%，继续轮询...`)
				await new Promise((resolve) => setTimeout(resolve, interval))
				continue
			}

			// 进度100但没有图片，或者没有进度信息，继续轮询
			console.log("等待任务处理中，继续轮询...")
			await new Promise((resolve) => setTimeout(resolve, interval))
			continue

			} catch (error) {
				console.error("轮询发生错误:", error)
				
				// 如果是网络错误或其他错误，继续尝试
				if (attempt < maxAttempts - 1) {
					await new Promise((resolve) => setTimeout(resolve, interval))
					continue
				}
				
				// 最后一次尝试失败，抛出错误
				throw new Error(`获取结果失败: ${error.message}`)
			}
		}

		// 如果达到最大尝试次数仍未成功
		throw new Error("获取结果超时，请稍后重试")
	}

	return {
		doImageFusion,
		getFusionResultUrl,
	}
})
