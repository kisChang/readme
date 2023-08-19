<template>
  <div class="login-back">
    <h1 class="page__title">
      <img src="/images/logo.png" alt="LoGo" height="70">
    </h1>
    <login-reg :auto-router="true"/>
  </div>
</template>

<script>
import {getUser} from "@/utils/token-util";
import LoginReg from "@/views/components/LoginReg.vue";

export default {
  name: "LoginRegPage",
  components: { LoginReg },
  data() {
    return {
    }
  },
  mounted() {
    this.userinfo = getUser() || {nickname: ''}
    if (this.userinfo.username) {
      this.$message.success(this.userinfo.username + "，欢迎登录。")
      this.$router.replace("/")
    }
  },
  methods: {
  }
}
</script>

<style scoped lang="scss">
.login-back {
  height: 100vh;
  background: #f2f2f2;

  ::v-deep .box-card {
    width: 90vw;
    max-width: 300px;
    margin: 50px auto auto;
  }
}
.page__title {
  text-align: center;
  padding-top: 100px;
}
</style>
