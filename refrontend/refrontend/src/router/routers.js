const constantRouters = [
	{
		path: "/",
		component: () => import("@/layout/index.vue"),
		// 重回定向到/home
		redirect: "/home",
		name: "Layout",
		children: [
			{
				path: "/textToImage",
				component: () => import("@/views/textToImage/index.vue"),
				name: "textToImage",
			},
			{
				path: "/lineToImage",
				component: () => import("@/views/lineToImage/index.vue"),
				name: "lineToImage",
			},
			// {
			// 	path: "/partialRedrawing",
			// 	component: () => import("@/views/partialRedrawing/index.vue"),
			// 	name: "partialRedrawing",
			// },
			{
				path: "/imageFusion",
				component: () => import("@/views/imageFusion/index.vue"),
				name: "imageFusion",
			},
			{
				path: "/styleExtend",
				component: () => import("@/views/styleExtend/index.vue"),
				name: "styleExtend",
			},
			{
				path: "/home",
				component: () => import("@/views/home/index.vue"),
				name: "home",
			},
		],
	},
	{
		path: "/login",
		component: () => import("@/views/login/introPage.vue"),
		name: "login",
	},
	{
		path: "/personalCenter",
		component: () => import("@/views/personalCenter/index.vue"),
		name: "personalCenter",
	},
]

export default [...constantRouters]
