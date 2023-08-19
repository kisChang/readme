<template>
  <div style="text-align: center;margin-top: 50vh;">
    <el-row>
      <el-button type="primary" @click="useWxLogin()">微信登录</el-button>

      <el-button v-if="userinfo.userId"
                 type="primary"
                 @click="useNowUserLogin()">
        使用（{{userinfo.username}}）登录
      </el-button>

      <el-button type="primary" @click="loginDialogVisible = true">
        登录{{ userinfo.userId ? "其他" :  "我的" }}账号
      </el-button>

    </el-row>
    <el-dialog
        title=""
        :visible.sync="loginDialogVisible"
        width="100%">
      <login-reg :auto-router="false" @loginSuccess="useNowUserLogin" />
    </el-dialog>
  </div>
</template>

<script>
import {getUser} from "@/utils/token-util";
import {deviceUseWxLogin, userLoginToDevice} from "@/api/user";
import LoginReg from "@/views/components/LoginReg.vue";

export default {
  name: "DeviceCode",
  components: { LoginReg },
  data() {
    return {
      key: null,

      loginDialogVisible: false,
      userinfo: {nickname: ''},
    }
  },
  mounted() {
    this.key = this.$route.query.key
    this.userinfo = getUser() || {nickname: ''}
  },
  methods: {
    isWechat() {
      return /MicroMessenger/i.test(window.navigator.userAgent);
    },
    useWxLogin(){
      deviceUseWxLogin(this.key).then(res => {
        location.href = res.data
      })
    },
    useNowUserLogin() {
      userLoginToDevice(this.key).then(res => {
        this.$message.success(res.msg)
        this.$router.replace("/")
      })
    },
  }
}
</script>

<style scoped>

</style>
