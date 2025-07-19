import service from "../service.js"

// 获取用户信息
export const getUserInformation = (id) => {
	return service.get(`/user/${id}`)
}
// 修改密码
export const changePassword = (data) => {
	return service.post("/user/changePassword", data)
}
// 修改用户信息
export const changeUserInformation = (data) => {
	return service.post("/user/changeInfo", data)
}
// 获取我的图片
export const getUserImages = (userId) => {
	return service.get("/user/getAllImage", { userId })
}
