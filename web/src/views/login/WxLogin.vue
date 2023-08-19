<template>
  <div style="text-align: center;">
    <div>{{ msg }}</div>
    <el-button style="margin-top: 50vh" @click="loginFunc()">手动登录</el-button>
  </div>
</template>

<script>
import {wxLoginRedirect} from "@/api/user";
import {setToken} from "@/utils/token-util";

export default {
  name: "WxLogin",
  data() {
    return {
      msg: '加载中...'
    }
  },
  mounted() {
    if (this.isWechat()) {
      this.msg = "测试中"
    } else {
      this.loginFunc()
    }
  },
  methods: {
    isWechat() {
      return /MicroMessenger/i.test(window.navigator.userAgent);
    },
    loginFunc() {
      const retParam = new URLSearchParams(location.search)
      const wxCode = this.$route.query.code || retParam.get('code')
      if (wxCode) {
        wxLoginRedirect(wxCode, this.$route.query.state).then(res => {
          setToken(res.data.token, true, res.data);
          this.$router.replace("/")
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
