import Vue from "vue";
import VueRouter from "vue-router";
import {getUser} from "@/utils/token-util";

import("../views/user/RSSMng.vue")

Vue.use(VueRouter);

const router = new VueRouter({
  mode: "hash",
  base: "/",
  routes: [
    {
      path: "/",
      name: "home",
      component: () => import("../views/MainIndex.vue"),
      children: [
        {
          path: "",
          name: "rss",
          component: () => import("../views/user/Index.vue"),
        },
        {
          path: "rss",
          name: "rss",
          component: () => import("../views/user/RSSMng.vue"),
          meta: { auth: true }
        },
        {
          path: "mail",
          name: "mail",
          component: () => import("../views/user/MailMng.vue"),
          meta: { auth: true }
        },
      ]
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/login/LoginRegPage.vue"),
    },
    {
      path: "/login/device_qrcode",
      name: "device_qrcode",
      component: () => import("../views/login/DeviceCode.vue"),
    },
    {
      path: "/login/wx_redirect",
      name: "wx_login",
      component: () => import("../views/login/WxLogin.vue"),
    },
    {
      path: "/login/wx_redirect_to_device",
      name: "wx_redirect_to_device",
      component: () => import("../views/login/WxLoginToDevice.vue"),
    },
  ],
});


router.beforeEach((to, from, next) => {
  // 验证登录
  if (to.matched.some(m => m.meta.auth)) {
    if (getUser()) {
      next()
    } else {
      // 首页有登录
      next('/');
    }
  } else {
    next()
  }
})

export default router;
