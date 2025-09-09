class Message {
	info(duration, content = "提示信息") {
		return this.showMessage("", content, duration)
	}

	success(duration, content = "成功信息") {
		return this.showMessage("success", content, duration)
	}

	warning(duration, content = "警告信息") {
		return this.showMessage("warning", content, duration)
	}

	error(duration, content = "错误信息") {
		return this.showMessage("error", content, duration)
	}

	showMessage(type, content, duration) {
		return ElMessage({
			message: duration,
			type: type,
		})
	}
}

class Notification {
	info(description, message = "提示", position = "top-right") {
		return this.showNotification("info", description, message, position)
	}

	success(description, message = "成功", position = "top-right") {
		return this.showNotification("success", description, message, position)
	}

	warning(description, message = "警告", position = "top-right") {
		return this.showNotification("warning", description, message, position)
	}

	error(description, message = "错误", position = "top-right") {
		return this.showNotification("error", description, message, position)
	}

	showNotification(type, description, message, position = "top-right") {
		return ElNotification({
			title: message,
			message: description,
			type: type,
			position,
		})
	}
}

export const MyMessage = new Message()
export const MyNotification = new Notification()
