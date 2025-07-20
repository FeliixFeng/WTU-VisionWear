import service from "../service.js"

// 通过id获取url
export const getImageUrl = (imageId) => {
	return service.get("/image/get-image", { imageId })
}
// 文生图接口
export const textToImage = (data) => {
	return service.post("image/doubao/text-to-image", data)
}
// 图片融合接口,返回jobId
export const imageFusion = (data) => {
	return service.post("/image/image-fusion", data)
}
//图片融合接口，返回融合结果
export const getFusionResult = (jobId) => {
	return service.get("/image/image-fusion/result", { jobId })
}
// 图生图（风格延伸）接口
export const styleExtend = (data) => {
	return service.post("/image/doubao/image-to-image", data)
}
// 线稿成图接口
export const lineToImage = (data) => {
	return service.post("/image/sketch-to-image", data)
}
