<!-- 表格列设置组件 license by http://eleadmin.com -->
<template>
  <ele-tool-item :title="tipTitle" :placement="tipPlacement">
    <el-popover
      popper-class="ele-tool-column"
      placement="bottom-end"
      transition="el-zoom-in-top"
    >
      <i class="el-icon-setting" slot="reference"></i>
      <div class="ele-tool-column-item ele-tool-column-header">
        <div v-if="columnsSort" style="width: 20px"></div>
        <div v-else style="width: 10px"></div>
        <el-checkbox
          v-model="columnIsAllChecked"
          :indeterminate="columnIsIndeterminate"
          @change="onColumnCheckAllChange"
        >
          {{ title }}
        </el-checkbox>
        <el-link type="primary" :underline="false" @click="resetColumnChecked">
          {{ resetText }}
        </el-link>
      </div>
      <el-divider class="ele-divider" />
      <el-checkbox-group
        v-model="columnChecked"
        @change="onColumnCheckedChange"
      >
        <draggable
          animation="300"
          v-model="columnList"
          handle=".ele-tool-column-handle"
          @change="onColumnSortChange"
        >
          <div
            v-for="element in columnList"
            :key="element.key"
            class="ele-tool-column-item"
          >
            <div v-if="columnsSort" class="ele-tool-column-handle">
              <i class="el-icon-menu"></i>
              <i class="el-icon-menu"></i>
            </div>
            <div v-else style="width: 10px"></div>
            <el-checkbox :label="element.key">
              {{
                element.title ||
                  (['index', 'TB_INDEX'].includes(element.key)
                    ? indexText
                    : untitledText)
              }}
            </el-checkbox>
          </div>
        </draggable>
      </el-checkbox-group>
    </el-popover>
  </ele-tool-item>
</template>

<script>
  import draggable from 'vuedraggable';
  import { EleToolItem } from '../ele-toolbar';
  import { eachTreeData } from '../utils';

  /* 获取显示的列配置 */
  const getCheckedColumns = (orgColumns, columns, checked, sort) => {
    const data = [];
    orgColumns.forEach((d) => {
      if (columns.some((c) => c.key === d.columnKey || c.key === d.prop)) {
        if (!checked.includes(d.columnKey) && !checked.includes(d.prop)) {
          return;
        }
      }
      if (d.children) {
        const children = getCheckedColumns(d.children, columns, checked, sort);
        if (children.length) {
          data.push(Object.assign({}, d, { children: children }));
        }
      } else {
        data.push(Object.assign({}, d));
      }
    });
    // 排序
    if (sort) {
      data.sort((a, b) => {
        let ai = columns.findIndex(
          (c) => c.key === a.columnKey || c.key === a.prop
        );
        let bi = columns.findIndex(
          (c) => c.key === b.columnKey || c.key === b.prop
        );
        // 父级列处理
        if (ai === -1 && a.children && a.children.length) {
          ai = columns.findIndex(
            (c) =>
              c.key === a.children[0]?.columnKey || c.key === a.children[0].prop
          );
        }
        if (bi === -1 && b.children && b.children.length) {
          bi = columns.findIndex(
            (c) =>
              c.key === b.children[0].columnKey || c.key === b.children[0].prop
          );
        }
        // 固定列处理
        if (a.fixed === true || a.fixed === 'left') {
          ai -= columns.length;
        } else if (a.fixed === 'right') {
          ai += columns.length;
        }
        if (b.fixed === true || b.fixed === 'left') {
          bi -= columns.length;
        } else if (b.fixed === 'right') {
          bi += columns.length;
        }
        return ai - bi;
      });
    }
    return data;
  };

  export default {
    name: 'EleToolColumns',
    components: { draggable, EleToolItem },
    emits: ['update:columns'],
    props: {
      // tooltip标题
      tipTitle: {
        type: String,
        default: '列设置'
      },
      // tooltip显示位置
      tipPlacement: String,
      // 全选按钮文字
      title: {
        type: String,
        default: '列展示'
      },
      // 重置按钮文字
      resetText: {
        type: String,
        default: '重置'
      },
      // 序号列文字
      indexText: {
        type: String,
        default: '序号'
      },
      // 无标题列文字
      untitledText: {
        type: String,
        default: '未命名'
      },
      // 表格列配置
      columns: {
        type: Array,
        required: true
      },
      // 是否开启列拖拽排序
      columnsSort: {
        type: Boolean,
        default: true
      }
    },
    data() {
      return {
        // 列配置原始数据
        orgColumns: [...this.columns],
        // 列配置下拉列表数据
        columnList: [],
        // 列配置下拉列表选中数据
        columnChecked: [],
        // 列配置下拉列表是否全选
        columnIsAllChecked: false,
        // 列配置下拉列表是否半选
        columnIsIndeterminate: false
      };
    },
    mounted() {
      const columns = this.initColumnList();
      if (!this.columnIsAllChecked) {
        this.$emit('update:columns', columns);
      }
    },
    methods: {
      /* 获取列配置下拉列表数据 */
      initColumnList() {
        const data = [];
        eachTreeData(this.orgColumns, (d) => {
          if (d.children && d.children.length) {
            return;
          }
          if (['selection', 'index', 'expand'].includes(d.type)) {
            return;
          }
          if (d.columnKey || d.prop) {
            data.push({
              key: d.columnKey || d.prop,
              title: d.label,
              show: d.show !== false
            });
          }
        });
        this.columnList = data;
        this.columnChecked = data.filter((d) => d.show).map((d) => d.key);
        this.columnIsAllChecked =
          this.columnList.length > 0 &&
          this.columnList.length === this.columnChecked.length;
        this.columnIsIndeterminate =
          this.columnList.length !== this.columnChecked.length;
        return getCheckedColumns(
          this.orgColumns,
          this.columnList,
          this.columnChecked,
          this.columnsSort
        );
      },
      /* 列配置改变 */
      onColumnCheckedChange() {
        this.columnIsAllChecked =
          this.columnList.length > 0 &&
          this.columnList.length === this.columnChecked.length;
        this.columnIsIndeterminate =
          this.columnList.length !== this.columnChecked.length;
        const columns = getCheckedColumns(
          this.orgColumns,
          this.columnList,
          this.columnChecked,
          this.columnsSort
        );
        this.$emit('update:columns', columns);
      },
      /* 列配置全选/取消全选 */
      onColumnCheckAllChange() {
        if (this.columnIsAllChecked) {
          this.columnChecked = this.columnList.map((d) => d.key);
        } else {
          this.columnChecked = [];
        }
        this.columnIsIndeterminate = false;
        const columns = getCheckedColumns(
          this.orgColumns,
          this.columnList,
          this.columnChecked,
          this.columnsSort
        );
        this.$emit('update:columns', columns);
      },
      /* 重置列配置 */
      resetColumnChecked() {
        const columns = this.initColumnList();
        this.$emit('update:columns', columns);
      },
      /* 列配置拖动改变顺序 */
      onColumnSortChange() {
        const columns = getCheckedColumns(
          this.orgColumns,
          this.columnList,
          this.columnChecked,
          this.columnsSort
        );
        this.$emit('update:columns', columns);
      }
    },
    watch: {
      columns(value) {
        this.orgColumns = [...value];
        const columns = this.initColumnList();
        this.$emit('update:columns', columns);
      }
    }
  };
</script>

<style lang="scss">
  .ele-divider {
    margin: 0 !important;
  }
  /* 列配置 */
  .ele-tool-column {
    &.el-popover {
      padding: 0;
    }

    .ele-tool-column-item {
      display: flex;
      align-items: baseline;
      padding: 4px 16px 4px 4px;
      box-sizing: border-box;

      .el-checkbox {
        flex: 1;
      }

      .el-checkbox__label {
        color: inherit !important;
      }

      .ele-tool-column-handle {
        opacity: 0.8;
        cursor: move;

        .el-icon-_more {
          font-size: 12px;

          & + .el-icon-_more {
            margin: 0 4px 0 -8px;
          }
        }
        .el-icon-menu {
          font-size: 12px;

          & + .el-icon-menu {
            margin: 0 4px 0 -8px;
          }
        }
      }

      &.sortable-chosen {
        background: hsla(0, 0%, 60%, 0.1);
      }

      &.ele-tool-column-header {
        padding: 12px 16px 10px 4px;
        min-width: 180px;
      }
    }

    .el-checkbox-group {
      padding: 5px 0;
      display: block;
    }
  }


</style>
