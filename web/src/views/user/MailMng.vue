<template>
  <div>
    <div>邮件接收地址：<a :href="'mailto:' + userinfo.username + '@kischang.top'">{{userinfo.username}}@kischang.top</a></div>
    <div style="margin: 10px;text-align: center;">
      <el-button type="primary"  @click="changeUserView = true">修改用户名</el-button>
    </div>
    <kis-table ref="table"
               :columns="columns"
               :datasource="datasource"
               :toolbar-add="true"
               :toolkit="['reload']"
               @handle-add="addView = true">
      <template slot="action" slot-scope="{ row }">
        <el-link type="primary" :underline="false" @click="editRss(row)">编辑</el-link>
        <el-link type="danger" style="margin-left: 10px"
                 :underline="false" @click="deleteRss(row)">删除</el-link>
      </template>
    </kis-table>

    <el-dialog
        title="邮箱信息"
        :visible.sync="addView"
        modal-append-to-body
        width="90%">
      <el-form ref="form" :model="feedForm" :rules="rules" label-position="top">
        <el-form-item label="邮箱地址" prop="mail">
          <el-input v-model="feedForm.mail"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addView = false">取 消</el-button>
        <el-button type="primary" @click="addSub">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
        title="修改用户名"
        :visible.sync="changeUserView"
        modal-append-to-body
        width="90%">
      <el-form ref="form" label-position="top">
        <el-form-item label="新用户名" prop="toUsername">
          <el-input v-model="toUsername"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="changeUserView = false">取 消</el-button>
        <el-button type="primary" @click="updateUsername">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getUser, setToken} from "@/utils/token-util";
import {changeUsername, deleteEmail, updateEmail, userEmailPage} from "@/api/user";
import baseTable from "@/components/KisTable/base-table";
import KisTable from "@/components/KisTable";

export default {
  name: 'EmailMng',
  extends: baseTable,
  components: {KisTable},
  data() {
    return {
      userinfo: {},
      columns: [
        {
          prop: 'mail',
          label: '信任邮箱地址',
          showOverflowTooltip: true,
          minWidth: 150
        },
        {
          slot: 'action',
          align: "center",
          label: '操作',
        },
      ],

      changeUserView: false, toUsername: null,

      addView: false,
      feedForm: {mail: null},
      rules: {
        mail: [
          {required: true, message: '请输入邮箱地址'},
        ],
      }
    }
  },
  mounted() {
    this.userinfo = getUser() || {nickname: ''}
  },
  methods: {
    $getPage: userEmailPage,
    addSub() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.addView = true
          updateEmail(this.feedForm).then(res => {
            this.$message.success(res.msg);
            this.addView = false;
            this.reload()
          }).catch(err => {
            this.$message.error(err);
          })
        }
      });
    },
    editRss(row) {
      this.feedForm = row
      this.addView = true
    },
    deleteRss(row) {
      this.$confirm('确认删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEmail({userFeedId: row.userFeedId}).then(res => {
          this.$message.success(res.msg);
        }).catch(err => {
          this.$message.error(err);
        })
      });
    },

    updateUsername() {
      changeUsername(this.toUsername).then(res => {
        this.$message.success(res.msg);
        setToken(res.data.token, true, res.data);
        location.reload()
      }).catch(err => {
        this.$message.error(err);
      })
    },

  }
}
</script>
<style scoped lang="scss">

</style>
