import service from "../service.js"
import { MyMessage } from "@/utils/util.toast.js"

export const getMyImages = () => {
	return service.get("/user/getAllImage").then((res) => {
		if (!res.status) {
			MyMessage.error(res.origin.msg)
		}
		return res
	})
}
