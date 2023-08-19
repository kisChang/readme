<template>
  <el-card class="box-card">
    <el-form v-if="showLogin" ref="loginForm" :model="regFrom" :rules="fromRules" label-width="4rem">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="regFrom.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="regFrom.password"></el-input>
      </el-form-item>
    </el-form>
    <el-form v-else ref="regForm" :model="regFrom" :rules="fromRules" label-width="4rem">
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="regFrom.nickname"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="regFrom.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="regFrom.password"></el-input>
      </el-form-item>
    </el-form>
    <div v-if="showLogin">
      <el-button type="primary" @click="subFunc" v-loading.fullscreen.lock="fullscreenLoading">登录</el-button>
      <el-button type="primary" @click="showLogin = false">切换至注册</el-button>
    </div>
    <div v-else>
      <el-button type="primary" @click="subFunc" v-loading.fullscreen.lock="fullscreenLoading">立即注册</el-button>
      <el-button type="primary" @click="showLogin = true">切换至登录</el-button>
    </div>
  </el-card>
</template>

<script>
import {getUser, setToken} from "@/utils/token-util";
import {userLogin, userReg} from "@/api/user";

export default {
  name: "LoginReg",
  props: {
    autoRouter: {
      type: Boolean,
      default: true,
    }
  },
  data() {
    return {
      showLogin: true, fullscreenLoading: false,
      regFrom: {nickname: '', username: '', password: '', code: ''},
      fromRules: {
        nickname: [
          {required: true, message: '请输入您的昵称', trigger: 'blur'},
        ],
        username: [
          {required: true, message: '请输入登录账号', trigger: 'blur'},
        ],
        password: [
          {required: true, message: '请输入登录密码', trigger: 'blur'},
        ],
        code: [
          {required: true, message: '请输入验证码', trigger: 'blur'},
        ],
      }
    }
  },
  mounted() {
  },
  methods: {
    subFunc() {
      const form = this.showLogin ? 'loginForm' : 'regForm'
      const func = this.showLogin ? userLogin : userReg
      this.$refs[form].validate((valid) => {
        if (valid) {
          this.fullscreenLoading = true
          func(this.regFrom).then(res => {
            this.$message.success(res.msg);
            setToken(res.data.token, true, res.data);
            if (this.autoRouter) {
              this.$router.replace("/")
            }
            this.$emit("loginSuccess", res.data)
          }).catch(err => {
            this.$message.error(err);
          }).finally(() => {
            this.fullscreenLoading = false
          })
        }
      });
    },
  }
}
</script>

<style scoped lang="scss">
::v-deep .el-form-item__label:before {
  content: '';
  display: none;
}
</style>
