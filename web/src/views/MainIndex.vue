<template>
  <div>
    <el-container>
      <el-main style="height: calc(100vh - 60px)">
        <router-view/>
      </el-main>
      <el-footer>
        <el-tabs v-if="this.userinfo.userId" v-model="defTab" tab-position="bottom" :stretch="true">
          <el-tab-pane name="/">
            <span slot="label" @click="toRouter('/')"><i class="el-icon-s-home"></i></span>
          </el-tab-pane>
          <el-tab-pane name="/rss">
            <span slot="label" @click="toRouter('/rss')"><i class="el-icon-s-promotion"></i></span>
          </el-tab-pane>
          <el-tab-pane name="/mail">
            <span slot="label" @click="toRouter('/mail')"><i class="el-icon-s-marketing"></i></span>
          </el-tab-pane>
        </el-tabs>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import {getUser} from "@/utils/token-util";

export default {
  data() {
    return {
      defTab: '/', userinfo: {nickname: ''},
    }
  },
  mounted() {
    this.userinfo = getUser() || {nickname: ''}
    this.defTab = this.$route.path
  },
  methods: {
    toRouter(path){
      if (this.$route.path !== path) {
        this.$router.replace(path)
      }
    },
  },
};
</script>

<style scoped>
::v-deep .el-tabs__item{
  font-size: 1.5rem;
}
</style>
