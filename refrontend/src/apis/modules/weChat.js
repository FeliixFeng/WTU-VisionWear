import service from "../service.js"

export const sendVerificationCodes = (data) => {
	return service.post("/auth/send-verification-code", data)
}

export const forgotPassword = (data) => {
	return service.post("/auth/forgot-password", data)
}
export const register = (data) => {
	return service.post("/auth/register", data)
}
