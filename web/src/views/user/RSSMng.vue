<template>
  <div>
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

    <kis-table ref="tableAll"
               :columns="columnsFeedSource"
               :datasource="dsAllPage"
               :toolbar-add="false"
               :toolkit="[]">
      <template slot="toolbar">
        <el-input placeholder="请输入名称进行检索" v-model="feedSearch" class="input-with-select" clearable>
          <el-button slot="append" icon="el-icon-search" @click="dsReload"></el-button>
        </el-input>
      </template>
      <template slot="action" slot-scope="{ row }">
        <el-link type="primary" :underline="false" @click="addToMe(row)">添加至我的</el-link>
      </template>
    </kis-table>

    <el-dialog
        title="订阅信息"
        :visible.sync="addView"
        modal-append-to-body
        width="90%">
      <el-form ref="form" :model="feedForm" :rules="rules" label-position="top">
        <el-form-item label="订阅名称" prop="name">
          <el-input v-model="feedForm.name"></el-input>
        </el-form-item>
        <el-form-item label="订阅地址" prop="url">
          <el-input v-model="feedForm.url"></el-input>
        </el-form-item>
        <el-form-item label="订阅描述" prop="descText">
          <el-input type="textarea" :rows="5" :maxlength="300" v-model="feedForm.descText"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addView = false">取 消</el-button>
        <el-button type="primary" @click="addSub">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getUser} from "@/utils/token-util";
import {addFeed, deleteFeed, updateFeed, userFeedPage} from "@/api/user";
import baseTable from "@/components/KisTable/base-table";
import KisTable from "@/components/KisTable";
import {addFeedById, feedPage} from "@/api/user";

export default {
  name: 'RSSMng',
  extends: baseTable,
  components: {KisTable},
  data() {
    return {
      userinfo: {},
      columns: [
        {
          prop: 'name',
          label: '名称',
          showOverflowTooltip: true,
          minWidth: 150
        },
        {
          slot: 'action',
          align: "center",
          label: '操作',
        },
      ],

      feedSearch: '',
      columnsFeedSource: [
        {
          prop: 'name',
          label: '名称',
          showOverflowTooltip: true,
          minWidth: 150
        },
        {
          slot: 'action',
          align: "center",
          label: '操作',
        },
      ],

      addView: false,
      feedForm: {name: null, url: null, descText: null},
      rules: {
        name: [
          {required: true, message: '请输入订阅名称'},
        ],
        url: [
          {required: true, message: '请输入订阅地址'},
        ],
        descText: [
          {required: true, message: '请输入订阅描述信息'},
        ],
      }
    }
  },
  mounted() {
    this.userinfo = getUser() || {nickname: ''}
  },
  methods: {
    dsAllPage({page, limit, where, order}) {
      return feedPage({name: this.feedSearch, ...where, ...order, page, limit})
    },
    dsReload() {
      this.$refs.tableAll.reload()
    },
    $getPage: userFeedPage,
    addSub() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.addView = true
          let func = this.feedForm.feedId ? updateFeed : addFeed
          func(this.feedForm).then(res => {
            this.$message.success(res.msg);
            this.addView = false;
            this.reload()
            this.dsReload()
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
        deleteFeed({userFeedId: row.userFeedId}).then(res => {
          this.$message.success(res.msg);
          this.reload()
        }).catch(err => {
          this.$message.error(err);
        })
      });
    },
    addToMe(row) {
      this.$confirm('确认添加?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        addFeedById(row.feedId).then(res => {
          this.$message.success(res.msg);
          this.dsReload()
          this.reload()
        }).catch(err => {
          this.$message.error(err);
        })
      });
    },
  }
}
</script>
<style scoped lang="scss">

</style>
