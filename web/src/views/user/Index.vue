<template>
  <div>
    <div class="page__hd">
      <h1 class="page__title">
        <img src="/static/images/logo.png" alt="LoGo" height="70">
      </h1>
      <p class="page__desc">一个纯粹的Kindle RSS 阅读工具</p>
      <el-button type="primary" @click="toRead">进入阅读端</el-button>

      <div style="margin-top: 10%">
        <template v-if="this.userinfo.userId">
          <p class="page__desc">欢迎您，{{ userinfo.nickname }}</p>
          <el-button type="primary" @click="toLogout">退出登录</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="toWxLogin">一个不能用的微信登录</el-button>
          <el-button type="primary" @click="toLoginReg">登录/注册</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import {getUser, removeToken} from "@/utils/token-util";
import {wxLogin} from "@/api/user";

export default {
  data() {
    return {
      userinfo: {}
    }
  },
  mounted() {
    this.userinfo = getUser() || {nickname: ''}
  },
  methods: {
    toRead() {
      location.href = "/kindle/index.html"
      location.hash = ""
    },
    toLogout() {
      removeToken()
      location.reload()
    },
    toLoginReg() {
      this.$router.replace("/login")
    },
    toWxLogin() {
      wxLogin("/login/wx_redirect").then((res) => {
        if (res.state) {
          location.href = res.data
        }
      });
    },
  }
}
</script>


<style scoped>
.page__hd {
  margin-top: 10vh;
  text-align: center;
}
</style>
