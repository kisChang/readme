import axios from "@/utils/request";

export async function wxLogin(url) {
  return axios.get("/open-api/wx_login?url=" + url);
}

export async function wxLoginRedirect(code, state) {
  return axios.post("/open-api/wx_login/redirect_code", { code, state });
}
export async function wxLoginToDevice(params) {
  return axios.post("/open-api/wx_login/wx_redirect_to_device", params);
}

export async function deviceUseWxLogin(key) {
  return axios.post("/open-api/user/getcode_login_wx", { key });
}

export async function addFeed(params) {
  return axios.post("/user-api/feed/addFeed", params);
}

export async function addFeedById(feedId) {
  return axios({
    url: "/user-api/feed/addFeedById",
    method: 'get',
    params: {feedId: feedId}
  });
}

export async function userLoginToDevice(key) {
  return axios({
    url: "/user-api/login_to_device",
    method: 'get',
    params: {key: key}
  });
}

export async function updateFeed(params) {
  return axios.post("/user-api/feed/updateFeed", params);
}

export async function deleteFeed(params) {
  return axios.post("/user-api/feed/deleteFeed", params);
}

export async function userFeedPage(params) {
  return axios({
    url: "/user-api/feed_source/self",
    method: 'get',
    params: {...params}
  });
}

export async function feedPage(params) {
  return axios({
    url: "/user-api/feed_source/all/page",
    method: 'get',
    params: {...params}
  });
}

export async function updateEmail(params) {
  return axios.post("/user-api/email/update", params);
}

export async function deleteEmail(params) {
  return axios.post("/user-api/email/delete", params);
}

export async function userEmailPage(params) {
  return axios({
    url: "/user-api/email/page",
    method: 'get',
    params: {...params}
  });
}

export async function changeUsername(username) {
  return axios({
    url: "/user-api/change_username",
    method: 'get',
    params: {username: username}
  });
}

export async function changePassword(param) {
  return axios.post("/user-api/change_password", param);
}

export async function userLogin(params) {
  return axios.post("/open-api/login", params);
}

export async function userReg(params) {
  return axios.post("/open-api/reg", params);
}
