export default {
	//********普通数据持久化*********
	// 存储数据
	set(key, value) {
		if (typeof value === "object") {
			// 如果值是对象或数组，先将其转换为 JSON 字符串再存储
			value = JSON.stringify(value)
		}
		localStorage.setItem(key, value)
	},

	// 获取数据
	get(key) {
		const value = localStorage.getItem(key)
		try {
			// 尝试解析 JSON 字符串
			return JSON.parse(value)
		} catch (error) {
			// 如果解析失败，则返回原始值
			return value
		}
	},

	// 删除指定的数据
	remove(key) {
		localStorage.removeItem(key)
	},

	// 清空所有的数据
	clear() {
		for (let i = 0; i < localStorage.length; i++) {
			const storageKey = localStorage.key(i)
			localStorage.removeItem(storageKey)
		}
	},
}
