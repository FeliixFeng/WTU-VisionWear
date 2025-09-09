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
	async function getFusionResultUrl(jobId) {
		return pollGenerationResult(jobId)
	}

	const pollGenerationResult = async (jobId) => {
		const maxAttempts = 40 // 增加到40次，因为图像融合可能需要更长时间
		const interval = 3000 // 每3秒查询一次
		let lastError = null

		if (!jobId) {
			throw new Error("无效的任务ID")
		}

		console.log(`开始轮询任务结果，任务ID: ${jobId}`)
		ElMessage.info("图片融合任务已提交，正在等待处理...")

		for (let attempt = 0; attempt < maxAttempts; attempt++) {
			try {
				console.log(`轮询尝试 ${attempt + 1}/${maxAttempts}`)

				// 不要每次都显示消息，避免过多弹窗
				if (attempt % 5 === 0 && attempt > 0) {
					ElMessage.info(
						`正在等待图片融合完成 (${attempt + 1}/${maxAttempts})...`
					)
				}

				const response = await getFusionResult(jobId)

				const result = response.data
				console.log("轮询返回结果:", result)

				// 如果成功获取到结果
				// result.code === 1 &&
				// 	result.data &&
				// 	result.data.images &&
				// 	result.data.images.length > 0
				if (result) {
					ElMessage.success("图片融合完成！")
					console.log("生成结果:", result.images)
					return result
				}

				// 如果服务器返回了结果，但没有图片，可能是处理中
				console.log("尚未得到结果，继续轮询...")
				await new Promise((resolve) => setTimeout(resolve, interval))
			} catch (error) {
				// 这里是关键修改：后端在任务未完成时会抛出异常，但这不是真正的错误，而是表示任务仍在队列中
				console.log(
					`轮询出错，但这可能只是表示任务仍在队列中: ${error.message}`
				)

				// 检查错误信息是否与"任务在队列中"相关
				// 注意：后端在任务状态为"ON_QUEUE"时会抛出"查询失败: "的异常
				const errorMsg = error.response?.data?.msg || error.message || ""

				if (errorMsg.includes("查询失败") || errorMsg.includes("ON_QUEUE")) {
					console.log("任务仍在队列中或处理中，继续轮询...")

					// 不要太频繁地显示消息
					if (attempt % 5 === 0) {
						ElMessage.info(
							`任务正在处理中，请耐心等待 (${attempt + 1}/${maxAttempts})...`
						)
					}

					// 继续轮询
					await new Promise((resolve) => setTimeout(resolve, interval))
					continue
				}

				// 其他类型的错误，可能是真正的错误
				lastError = error
				console.error("轮询发生实际错误:", error)

				if (attempt === maxAttempts - 1) {
					// 到达最大尝试次数时才抛出错误
					throw new Error(`获取结果失败: ${error.message}`)
				}

				// 其他情况下继续尝试
				await new Promise((resolve) => setTimeout(resolve, interval))
			}
		}

		// 如果达到最大尝试次数仍未成功
		if (lastError) {
			throw new Error(`获取结果超时: ${lastError.message}`)
		} else {
			throw new Error("获取结果超时")
		}
	}

	return {
		doImageFusion,
		getFusionResultUrl,
	}
})
