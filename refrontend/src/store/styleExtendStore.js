import { styleExtend } from "@/apis/modules/images.js"
import { MyMessage } from "@/utils/util.toast.js"
import { defineStore } from "pinia"

export const useStyleExtendStore = defineStore("styleExtendStore", () => {
	async function doStyleExtend(requestBody) {
		// 准备请求体数据 - 调整为匹配后端API参数
		console.log("requestBody:", requestBody)
		return styleExtend(requestBody).then((res) => {
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
		doStyleExtend,
	}
})

// 图片可能只是一个url地址，可能不是一个文件，暂时没用下面尺寸处理函数
// 图片尺寸处理函数
// const resizeImageIfNeeded = (file) => {
//     return new Promise((resolve) => {
//       const img = new Image()
//       const url = URL.createObjectURL(file)

//       img.onload = () => {
//         URL.revokeObjectURL(url)

//         // 检查尺寸是否已经符合要求
//         for (const dim of SUPPORTED_DIMENSIONS) {
//           if (img.width === dim.width && img.height === dim.height) {
//             console.log('图片尺寸已符合要求:', `${img.width}x${img.height}`)
//             resolve(file) // 直接返回原始文件
//             return
//           }
//         }

//         // 计算原始图片的宽高比
//         const originalRatio = img.width / img.height

//         // 找到最接近原始宽高比的支持尺寸
//         let bestMatch = SUPPORTED_DIMENSIONS[0]
//         let minRatioDifference = Math.abs((bestMatch.width / bestMatch.height) - originalRatio)

//         for (const dim of SUPPORTED_DIMENSIONS) {
//           const ratioDifference = Math.abs((dim.width / dim.height) - originalRatio)
//           if (ratioDifference < minRatioDifference) {
//             minRatioDifference = ratioDifference
//             bestMatch = dim
//           }
//         }

//         // 使用最佳匹配的尺寸
//         const targetWidth = bestMatch.width
//         const targetHeight = bestMatch.height

//         console.log(`选择最接近的尺寸: ${targetWidth}x${targetHeight}，原始比例: ${originalRatio.toFixed(2)}，目标比例: ${(targetWidth/targetHeight).toFixed(2)}`)

//         // 创建Canvas进行调整
//         const canvas = document.createElement('canvas')
//         canvas.width = targetWidth
//         canvas.height = targetHeight
//         const ctx = canvas.getContext('2d')

//         // 绘制并保持宽高比
//         let sx = 0, sy = 0, sWidth = img.width, sHeight = img.height

//         // 计算裁剪区域，保持宽高比并居中
//         if (img.width / img.height > targetWidth / targetHeight) {
//           sWidth = img.height * (targetWidth / targetHeight)
//           sx = (img.width - sWidth) / 2
//         } else {
//           sHeight = img.width * (targetHeight / targetWidth)
//           sy = (img.height - sHeight) / 2
//         }

//         // 在画布上绘制调整后的图像
//         ctx.drawImage(img, sx, sy, sWidth, sHeight, 0, 0, targetWidth, targetHeight)

//         // 转换为Blob
//         canvas.toBlob((blob) => {
//           // 创建新的File对象
//           const resizedFile = new File([blob], file.name, {
//             type: 'image/jpeg',
//             lastModified: Date.now()
//           })

//           console.log(`图片已调整为 ${targetWidth}x${targetHeight}`)
//           resolve(resizedFile)
//         }, 'image/jpeg', 0.92) // 使用JPEG格式，92%质量
//       }

//       img.src = url
//     })
//   }
