const constantRouters = [{
		path: "/",
		component: () => import("@/layout/index.vue"),
		// 重回定向到/textToImage
		redirect: "/textToImage",
		name: "Layout",
		children: [{
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
				path: "/styleTransfer",
				component: () => import("@/views/styleTransfer/index.vue"),
				name: "styleTransfer",
			},
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
		],
	},
	{
		path: "/login",
		component: () => import("@/views/login/introPage.vue"),
		name: "login",
	},
]

export default [...constantRouters]