/**
 * token操作封装
 */
import { TOKEN_STORE_NAME, USER_STORE_NAME } from './setting';

/**
 * 获取缓存的token
 * @returns {string}
 */
export function getToken() {
  const token = getCookie(TOKEN_STORE_NAME) || localStorage.getItem(TOKEN_STORE_NAME);
  if (!token) {
    return sessionStorage.getItem(TOKEN_STORE_NAME);
  }
  return token;
}

/**
 * 获取缓存的token
 * @returns {string}
 */
export function getUser() {
  const user = localStorage.getItem(USER_STORE_NAME);
  if (!user) {
    return JSON.parse(sessionStorage.getItem(USER_STORE_NAME));
  }
  return JSON.parse(user);
}


export function getCookie(cname){
  let name = cname + "=";
  let ca = document.cookie.split(';');
  for (let i = 0; i < ca.length; i++) {
    let c = ca[i].trim();
    if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
  }
  return null;
}

export function setCookie(name, value, exDays) {
  let d = new Date();
  d.setTime(d.getTime() + (exDays * 24 * 60 * 60 * 1000));
  // @ts-ignore
  let expires = "expires=" + d.toGMTString();
  document.cookie = name + "=" + value + "; " + expires;
}

/**
 * 缓存token
 * @param token token
 * @param remember 是否永久存储
 */
export function setToken(token, remember, user) {
  removeToken();
  if (token) {
    if (remember) {
      localStorage.setItem(TOKEN_STORE_NAME, token);
      localStorage.setItem(USER_STORE_NAME, JSON.stringify(user));
      setCookie(TOKEN_STORE_NAME, token, 30);
    } else {
      sessionStorage.setItem(TOKEN_STORE_NAME, token);
      sessionStorage.setItem(USER_STORE_NAME, JSON.stringify(user));
    }
  }
}

/**
 * 缓存token
 * @param user token
 * @param remember 是否永久存储
 */
export function setUser(user, remember) {
  localStorage.removeItem(USER_STORE_NAME);
  sessionStorage.removeItem(USER_STORE_NAME);
  if (user) {
    if (remember) {
      localStorage.setItem(USER_STORE_NAME, JSON.stringify(user));
    } else {
      sessionStorage.setItem(USER_STORE_NAME, JSON.stringify(user));
    }
  }
}

/**
 * 移除缓存的token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_STORE_NAME);
  localStorage.removeItem(USER_STORE_NAME);
  sessionStorage.removeItem(TOKEN_STORE_NAME);
  sessionStorage.removeItem(USER_STORE_NAME);
  setCookie(TOKEN_STORE_NAME, null, 0);
}
