import { useRouter } from "vue-router"
import dbUtils from "utils/util.strotage.js"
import { MyMessage } from "@/utils/util.toast.js"
import { defineStore } from "pinia"
import {
	getUserImages,
	getUserInformation,
	changePassword,
	changeUserInformation,
} from "@/apis/modules/userInformation.js"

export const useUesrInformationStore = defineStore(
	"uesrInformationStore",
	() => {
		async function doGetUserInformation(id) {
			return getUserInformation(id).then((res) => {
				// 检查响应状态并显示相应的消息
				if (!res.status) {
					MyMessage.error(res.origin.msg)
				}
				return res
			})
		}

		async function doGetUserImages() {
			return getUserImages().then((res) => {
				// 检查响应状态并显示相应的消息
				if (!res.status) {
					MyMessage.error(res.origin.msg)
				}
				return res
			})
		}
		return {
			doGetUserInformation,
			doGetUserImages,
		}
	}
)
