<template>
  <div style="text-align: center;">
    <span>{{ msg }}</span>
  </div>
</template>

<script>
import {wxLoginToDevice} from "@/api/user";

export default {
  name: "WxLoginToDevice",
  data() {
    return {
      msg: '正在登录设备，加载中...'
    }
  },
  mounted() {
    this.loginFunc()
  },
  methods: {
    loginFunc() {
      const retParam = new URLSearchParams(location.search)
      if (retParam.get('code')) {
        let param = {
          deviceCode: retParam.get('key') || this.$route.query.key,
          code: retParam.get('code'),
          state: retParam.get('state')
        }
        wxLoginToDevice(param).then(res => {
          alert("操作结果：" + res.msg)
          this.$router.replace("/")
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
