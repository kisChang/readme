<script>
export default {
  data() {
    return {
      // 表格列配置
      columns: [],
      // 表格选中数据
      selection: [],
      // 当前编辑数据
      current: null,
      // 是否显示编辑弹窗
      showEdit: false,

      loading: false,

      // 检索条件
      where: {}, DEF_WHERE: {}
    }
  },
  beforeMount() {
    this.$nextTick(() => {
      this.where = Object.assign({}, this.DEF_WHERE)
    })
  },
  methods: {
    $getPage(params) {
      this.loading = true
      return new Promise((resolve, reject) => {
        reject({message: '方法 "$getPage" 未定义！'} )
      }).finally(() => {
        this.loading = false
      })
    },
    $remove() {
      return new Promise((resolve, reject) => {
        reject({message: '方法 "$remove" 未定义！'} )
      })
    },
    /* 表格数据源 */
    datasource({page, limit, where, order}) {
      //where = Object.assign(where, this.where)
      return this.$getPage({...where, ...order, page, limit})
    },
    /* 刷新表格 */
    reload(where) {
      this.$refs.table.reload({
        page: 1,
        where: where
      });
    },
    change(e) {
      this.$forceUpdate()
    },

    handleSearch() {
      this.reload(this.where)
    },
    handleReset() {
      this.where = Object.assign({}, this.DEF_WHERE)
      this.handleSearch()
    },
    handleDelete(row) {
      this.$confirm('此操作将删除数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        this.$remove({id: row.id}).then(res => {
          if (res.stat){
            this.$message.success(res.msg)
          }else {
            this.$message.error(res.msg)
          }
        }).finally(() => {
          this.loading = false
          this.reload()
        })
      })
    },
  }
}
</script>
