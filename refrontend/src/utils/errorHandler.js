// 迁移登录功能时一起迁移过来的
/**
 * 错误级别定义
 */
export const ErrorLevel = {
	INFO: "info",
	WARNING: "warning",
	ERROR: "error",
}

/**
 * 错误类型定义
 */
export const ErrorType = {
	NETWORK: "network", // 网络连接错误
	SERVER: "server", // 服务器错误(5xx)
	CLIENT: "client", // 客户端错误(4xx)
	AUTH: "auth", // 认证/授权错误
	VALIDATION: "validation", // 表单验证错误
	BUSINESS: "business", // 业务逻辑错误
	UNKNOWN: "unknown", // 未知错误
}

/**
 * 格式化并显示API错误
 * @param {Error} error - 错误对象
 * @param {string} customMessage - 自定义错误消息(可选)
 * @param {ErrorLevel} level - 错误级别(可选，默认为 'error')
 * @returns {string} 格式化后的错误消息
 */
export function handleApiError(
	error,
	customMessage = "",
	level = ErrorLevel.ERROR
) {
	let errorMessage = customMessage || "操作失败"
	let errorType = ErrorType.UNKNOWN
	let errorDetails = ""

	console.error("API错误:", error)

	if (error.response) {
		// 服务器响应了，但状态码不是2xx
		const { status, data } = error.response
		errorType = status >= 500 ? ErrorType.SERVER : ErrorType.CLIENT

		if (status === 401 || status === 403) {
			errorType = ErrorType.AUTH
			errorMessage = "登录已过期或没有权限，请重新登录"

			// 清除本地存储的认证信息
			localStorage.removeItem("accessToken")
			localStorage.removeItem("refreshToken")
			localStorage.removeItem("userName")
			localStorage.removeItem("userId")

			// 延迟跳转到登录页面
			setTimeout(() => {
				window.location.href = "/#/login"
			}, 1500)
		} else if (status === 400) {
			errorType = ErrorType.VALIDATION
			errorMessage = data?.msg || "请求参数错误"
		} else if (status === 404) {
			errorMessage = "请求的资源不存在"
		} else if (status >= 500) {
			errorMessage = "服务器错误，请稍后重试"
		}

		// 添加服务器返回的错误消息作为详情
		if (data && data.msg) {
			errorDetails = data.msg
		}
	} else if (error.request) {
		// 请求已发出，但没有收到响应
		errorType = ErrorType.NETWORK
		errorMessage = "网络连接问题，服务器没有响应"
	} else {
		// 请求设置时出错
		errorMessage = error.message || "请求出错"
	}

	// 构建完整错误消息
	const fullMessage = errorDetails
		? `${errorMessage}: ${errorDetails}`
		: errorMessage

	// 根据错误级别显示不同的提示
	switch (level) {
		case ErrorLevel.INFO:
			ElMessage.info(fullMessage)
			break
		case ErrorLevel.WARNING:
			ElMessage.warning(fullMessage)
			break
		case ErrorLevel.ERROR:
		default:
			ElMessage.error({
				message: fullMessage,
				duration: 5000,
				showClose: true,
			})

			// 对于严重错误，还可以使用通知方式
			if (errorType === ErrorType.SERVER || errorType === ErrorType.AUTH) {
				ElNotification({
					title: "错误",
					message: fullMessage,
					type: "error",
					duration: 4500,
					position: "top-right",
				})
			}
			break
	}

	// 返回错误消息，方便调用者进一步处理
	return {
		message: fullMessage,
		type: errorType,
		level,
		details: errorDetails,
	}
}

/**
 * 处理业务逻辑错误(后端返回code!==1的情况)
 * @param {Object} response - 后端返回的响应对象
 * @param {string} defaultMessage - 默认错误消息
 * @param {ErrorLevel} level - 错误级别
 * @returns {Object|null} 错误信息对象或null(成功时)
 */
export function handleBusinessError(
	response,
	defaultMessage = "操作失败",
	level = ErrorLevel.ERROR
) {
	// 检查响应是否存在且有data字段
	if (!response || !response.data) {
		ElMessage.error("无效的响应数据")
		return { message: "无效的响应数据", type: ErrorType.UNKNOWN, level }
	}

	const { code, msg, data } = response.data

	// 如果code为1，表示业务处理成功
	if (code === 1) {
		return null
	}

	// 业务处理失败
	const errorMessage = msg || defaultMessage

	switch (level) {
		case ErrorLevel.INFO:
			ElMessage.info(errorMessage)
			break
		case ErrorLevel.WARNING:
			ElMessage.warning(errorMessage)
			break
		case ErrorLevel.ERROR:
		default:
			ElMessage.error({
				message: errorMessage,
				duration: 5000,
				showClose: true,
			})
			break
	}

	return {
		message: errorMessage,
		type: ErrorType.BUSINESS,
		level,
		details: data,
	}
}

/**
 * 显示成功消息
 * @param {string} message - 成功消息
 * @param {boolean} useNotification - 是否使用通知方式显示
 */
export function showSuccess(message = "操作成功", useNotification = false) {
	if (useNotification) {
		ElNotification({
			title: "成功",
			message,
			type: "success",
			duration: 3000,
			position: "bottom-right",
		})
	} else {
		ElMessage.success({
			message,
			duration: 3000,
			showClose: true,
		})
	}
}

// 默认导出所有函数
export default {
	handleApiError,
	handleBusinessError,
	showSuccess,
	ErrorLevel,
	ErrorType,
}
