import routers from "./routers.js"
import { createRouter, createWebHashHistory } from "vue-router"
import NProgress from "nprogress" //切换路由会显示进度条
import "nprogress/nprogress.css"
import dbUtils from "utils/util.strotage.js"

NProgress.configure({ showSpinner: false }) // NProgress Configuration
const router = createRouter({
	history: createWebHashHistory(),
	routes: routers,
	// 刷新时，滚动条位置还原
	scrollBehavior: () => ({ left: 0, top: 0 }),
})

/**
 * 检查路由对象是否具有权限
 * @param {Array} perms - 权限列表
 * @param {Object} route - 路由对象
 * @returns {boolean} - 是否具有权限
 */
// function hasPermission(perms, route) {
//     if (perms.includes('*')) return true
//     if (route.meta && route.meta.perms) {
//         // 如果路由对象定义了 meta 属性或者定义 meta.perms 属性，那么就根据权限值来判断是否具有权限
//         return perms.some(perm => route.meta.perms.includes(perm))
//     } else {
//         // 如果路由对象没有定义 meta 属性或者没有定义 meta.perms 属性，那么默认认为具有权限，返回 true。
//         return true
//     }
// }

router.beforeEach(async (to, from, next) => {
	NProgress.start()
	const isLoggedIn = dbUtils.get("accessToken")
	if (isLoggedIn) {
		if (to.name === "login") {
			NProgress.done()
			return next("/")
		} else {
			NProgress.done()
			return next()
		}
	} else {
		if (to.name === "login") {
			NProgress.done()
			return next()
		} else {
			dbUtils.clear()
			NProgress.done()
			return next({ name: "login" })
		}
	}
})

router.afterEach((to, from) => {
	NProgress.done()
	// window.document.title = to.meta.title
})
export default router
