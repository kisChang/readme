import axios from "axios";
import router from "@/router";
import { getToken, setToken, removeToken } from "./token-util";
import { TOKEN_HEADER_NAME } from "@/utils/setting";
import {MessageBox} from "element-ui";

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL,
});

/* 跳转到登录页面 */
const goLogin = function (from) {
  removeToken();
  router.replace("/");
  location.reload()
};

/* 添加请求拦截器 */
service.interceptors.request.use(
  (config) => {
    // 添加token到header
    const token = getToken();
    if (token && config.headers) {
      config.headers.set(TOKEN_HEADER_NAME, token);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/* 添加响应拦截器 */
service.interceptors.response.use((res) => {
  // 登录过期处理
  if (res.data?.code === 403 || res.data?.code === 401) {
    const currentPath = router.currentRoute.path;
    if (currentPath !== "/") {
      MessageBox.alert("登录状态已过期, 请退出重新登录!", "系统提示", {
        confirmButtonText: "重新登录",
        callback: (action) => {
          if (action === "confirm") {
            goLogin(currentPath);
          }
        },
        beforeClose: () => {
          MessageBox.close();
        },
      });
    }
    return Promise.reject(new Error(res.data.msg));
  }
  if (!res.data.state) {
    return Promise.reject(new Error(res.data.msg));
  }
  // token自动续期
  const token = res.headers[TOKEN_HEADER_NAME.toLowerCase()];
  if (token) {
    setToken(token);
  }
  return res.data;
});

export default service;
