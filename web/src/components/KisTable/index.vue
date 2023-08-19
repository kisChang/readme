<!-- 高级表格组件 license by http://eleadmin.com -->
<template>
  <div
    :class="[
      'ele-pro-table ele-bg-white',
      { 'ele-pro-table-fullscreen': tableFullscreen }
    ]"
    :style="{ zIndex: fullZIndex }"
  >

    <el-form v-if="search" :size="searchSize" inline>
      <slot name="search" />
      <el-form-item>
        <el-button type="success" @click="handleSearch" icon="el-icon-search">检索</el-button>
        <el-button type="warning" @click="handleReset" icon="el-icon-refresh">重置</el-button>

      </el-form-item>
    </el-form>

    <!-- 表头工具栏 -->
    <ele-toolbar
      v-if="toolbar"
      :theme="toolsTheme"
      :title="title"
      :sub-title="subTitle"
      :style="toolStyle"
      :class="toolClass"
    >
      <slot name="toolbar">
        <el-button v-if="toolbarAdd" size="mini" type="primary" @click="handleAdd" icon="el-icon-plus">创建</el-button>
      </slot>
      <template slot="action">
        <ele-pro-table-tools
          :columns="columns"
          :size="tableSize"
          :tools="toolkit"
          :columns-sort="columnsSort"
          :fullscreen="tableFullscreen"
          :custom-style="toolkitStyle"
          @reload="onRefresh"
          @update:size="updateSize"
          @update:columns="updateColumns"
          @update:fullscreen="updateFullscreen"
        >
          <slot name="toolkit" />
        </ele-pro-table-tools>
      </template>
    </ele-toolbar>
    <!-- 表格 -->
    <el-table
      ref="table"
      :data="tableData"
      v-loading="tableLoading"
      :height="height"
      :max-height="maxHeight"
      :stripe="stripe"
      :border="border"
      :size="tableSize"
      :fit="fit"
      :show-header="showHeader"
      :highlight-current-row="highlightCurrentRow"
      :current-row-key="currentRowKey"
      :row-class-name="rowClassName"
      :row-style="rowStyle"
      :cell-class-name="cellClassName"
      :cell-style="cellStyle"
      :header-row-class-name="headerRowClassName"
      :header-row-style="headerRowStyle"
      :header-cell-class-name="headerCellClassName"
      :header-cell-style="headerCellStyle"
      :row-key="rowKey"
      :default-expand-all="defaultExpandAll"
      :expand-row-keys="expandRowKeys"
      :default-sort="defaultSort"
      :tooltip-effect="tooltipEffect"
      :show-summary="showSummary"
      :sum-text="sumText"
      :summary-method="summaryMethod"
      :span-method="spanMethod"
      :select-on-indeterminate="selectOnIndeterminate"
      :indent="indent"
      :lazy="lazy"
      :load="tableLoad"
      :tree-props="treeProps"
      @select="select"
      @select-all="selectAll"
      @selection-change="selectionChange"
      @cell-mouse-enter="cellMouseEnter"
      @cell-mouse-leave="cellMouseLeave"
      @cell-click="cellClick"
      @cell-dblclick="cellDblclick"
      @row-click="rowClick"
      @row-contextmenu="rowContextmenu"
      @row-dblclick="rowDblclick"
      @header-click="headerClick"
      @header-contextmenu="headerContextmenu"
      @sort-change="sortChange"
      @filter-change="filterChange"
      @current-change="currentChange"
      @header-dragend="headerDragend"
      @expand-change="expandChange"
      style="width: 100%"
    >
      <ele-pro-table-column
        v-for="col in tableCols"
        :key="col.key"
        :col="col"
        :index="tableIndex"
      >
        <template
          v-for="name in Object.keys($scopedSlots)"
          :slot="name"
          slot-scope="{ row, column, $index }"
        >
          <slot
            :name="name"
            v-bind:row="row"
            v-bind:column="column"
            v-bind:$index="$index"
          />
        </template>
      </ele-pro-table-column>
      <ele-empty slot="empty" :text="errorText || emptyText" />
      <template slot="append">
        <slot name="append" />
      </template>
    </el-table>
    <!-- 分页组件 -->
    <el-pagination
      v-if="needPage"
      :current-page="tablePage"
      :page-size="tableLimit"
      :total="tableTotal"
      :layout="layout"
      :page-sizes="pageSizes"
      :pager-count="pagerCount"
      :popper-class="popperClass"
      :prev-text="prevText"
      :next-text="nextText"
      :hide-on-single-page="hideOnSinglePage"
      :background="true"
      :style="paginationStyle"
      class="ele-pagination-circle"
      @current-change="pageCurrentChange"
      @size-change="pageSizeChange"
    />
  </div>
</template>

<script>
import EleProTableColumn from './components/ele-pro-table-column.vue';
import EleProTableTools from './components/ele-pro-table-tools.vue';
import EleToolbar from './ele-toolbar';
import EleEmpty from './ele-empty';
import { formatTreeData, uuid } from './utils';
// 如果是服务端数据源默认请求参数名称
const DEFAULT_REQUEST = {
  // 页码的参数名称
  pageName: 'page',
  // pageName: 'pageNow',
  // 每页数据量的参数名
  limitName: 'limit',
  // limitName: 'pageSize',
  // 排序字段参数名称
  sortName: 'sort',
  // 排序方式的参数名称
  orderName: 'order'
};
// 如果是服务端数据源默认请求参数名称
const DEFAULT_RESPONSE = {
  // 数据状态的字段名称
  statusName: 'code',
  // 成功的状态码
  statusCode: 0,
  // 信息的字段名称
  msgName: 'message',
  // 数据列表的字段名称
  dataName: 'data.content',
  // 数据总数的字段名称
  countName: 'data.totalElements'
};
// 排序方式的值
const ORDER_VALUE = {
  ascending: 'asc',
  descending: 'desc',
  asc: 'asc',
  desc: 'desc',
};
/* 获取字段的值(支持多层*.*) */
const getFieldValue = (obj, field) => {
  if (typeof field === 'function') {
    return field(obj);
  }
  if (field) {
    let value = obj;
    field.split('.').forEach((f) => {
      value = value ? value[f] : null;
    });
    return value;
  }
};

export default {
  name: 'EleProTable',
  components: {
    EleProTableColumn,
    EleEmpty,
    EleToolbar,
    EleProTableTools
  },
  emits: [
    'done',
    'update:selection',
    'update:current',
    'columns-change',
    'size-change',
    'fullscreen-change',
    'refresh',
    'select',
    'select-all',
    'selection-change',
    'cell-mouse-enter',
    'cell-mouse-leave',
    'cell-click',
    'cell-dblclick',
    'row-click',
    'row-contextmenu',
    'row-dblclick',
    'header-click',
    'header-contextmenu',
    'sort-change',
    'filter-change',
    'current-change',
    'header-dragend',
    'expand-change',
    'handle-search',
    'handle-reset',
    'handle-add',
  ],
  props: {
    // 数据源
    datasource: [String, Array, Function],
    // 如果是服务端数据源设定请求方式
    method: {
      type: String,
      default: 'GET'
    },
    // 如果是服务端数据源设定请求的参数
    where: Object,
    // 如果是服务端数据源设定请求的header
    headers: Object,
    // 如果是服务端数据源设定请求数据类型
    contentType: {
      type: String,
      default: 'application/json'
    },
    // 如果是服务端数据源设定请求参数名称
    request: Object,
    // 如果是服务端数据源设定响应参数名称
    response: Object,
    // 如果是服务端数据源自定义返回格式解析
    parseData: Function,
    // 如果是服务端数据源自定义参数格式解析
    parseParam: Function,
    // 列表选中数据(多选)，支持 .sync 修饰符
    selection: Array,
    // 列表选中数据(单选)，支持 .sync 修饰符
    current: Object,
    // 是否需要分页组件
    needPage: {
      type: Boolean,
      default: true
    },
    // 如果是服务端数据源默认是否请求数据
    initLoad: {
      type: Boolean,
      default: true
    },
    // 表格请求状态
    loading: Boolean,
    // 表格列配置
    columns: {
      type: Array,
      required: true
    },
    // Table 的高度，默认为自动高度。如果 height 为 number 类型，单位 px
    height: [String, Number],
    // Table 的最大高度。合法的值为数字或者单位为 px 的高度
    maxHeight: [String, Number],
    // 是否为斑马纹 table
    stripe: Boolean,
    // 是否带有纵向边框
    border: {
      type: Boolean,
      default: true
    },
    // Table 的尺寸
    size: String,
    // 列的宽度是否自撑开
    fit: {
      type: Boolean,
      default: true
    },
    // 是否显示表头
    showHeader: {
      type: Boolean,
      default: true
    },
    // 是否要高亮当前行
    highlightCurrentRow: Boolean,
    // 当前行的 key，只写属性
    currentRowKey: [String, Number],
    // 行的 className 的回调方法，也可以使用字符串为所有行设置一个固定的 className
    rowClassName: [String, Function],
    // 行的 style 的回调方法，也可以使用一个固定的 Object 为所有行设置一样的 Style
    rowStyle: [Object, Function],
    // 单元格的 className 的回调方法，也可以使用字符串为所有单元格设置一个固定的 className
    cellClassName: [String, Function],
    // 单元格的 style 的回调方法，也可以使用一个固定的 Object 为所有单元格设置一样的 Style
    cellStyle: [Object, Function],
    // 表头行的 className 的回调方法，也可以使用字符串为所有表头行设置一个固定的 className
    headerRowClassName: [String, Function],
    // 表头行的 style 的回调方法，也可以使用一个固定的 Object 为所有表头行设置一样的 Style
    headerRowStyle: [Object, Function],
    // 表头单元格的 className 的回调方法，也可以使用字符串为所有表头单元格设置一个固定的 className
    headerCellClassName: [String, Function],
    // 表头单元格的 style 的回调方法，也可以使用一个固定的 Object 为所有表头单元格设置一样的 Style
    headerCellStyle: [Object, Function],
    // 行数据的 Key，用来优化 Table 的渲染；在使用 reserve-selection 功能与显示树形数据时，该属性是必填的
    rowKey: [String, Function],
    // 空数据时显示的文本内容
    emptyText: String,
    // 是否默认展开所有行，当 Table 包含展开行存在或者为树形表格时有效
    defaultExpandAll: Boolean,
    // 可以通过该属性设置 Table 目前的展开行，需要设置 row-key 属性才能使用，该属性为展开行的 keys 数组
    expandRowKeys: Array,
    // 默认的排序列的 prop 和顺序。它的prop属性指定默认的排序的列，order指定默认排序的顺序
    defaultSort: Object,
    // tooltip effect 属性
    tooltipEffect: String,
    // 是否在表尾显示合计行
    showSummary: Boolean,
    // 合计行第一列的文本
    sumText: String,
    // 自定义的合计计算方法
    summaryMethod: Function,
    // 合并行或列的计算方法
    spanMethod: Function,
    // 在多选表格中，当仅有部分行被选中时，点击表头的多选框时的行为。若为 true，则选中所有行；若为 false，则取消选择所有行
    selectOnIndeterminate: {
      type: Boolean,
      default: true
    },
    // 展示树形数据时，树节点的缩进
    indent: {
      type: Number,
      default: 16
    },
    // 是否懒加载子节点数据
    lazy: Boolean,
    // 加载子节点数据的函数，lazy 为 true 时生效，函数第二个参数包含了节点的层级信息
    load: Function,
    // 渲染嵌套数据的配置选项
    treeProps: {
      type: Object,
      default() {
        return {
          hasChildren: 'hasChildren',
          children: 'children'
        };
      }
    },
    // 分页组件每页显示条目个数
    pageSize: {
      type: Number,
      default: 10,
      validator(value) {
        return value > 0;
      }
    },
    // 分页组件页码按钮的数量，当总页数超过该值时会折叠
    pagerCount: {
      type: Number,
      default: 5
    },
    // 分页组件默认页码
    currentPage: {
      type: Number,
      default: 1,
      validator(value) {
        return value > 0;
      }
    },
    // 分页组件布局，子组件名用逗号分隔
    layout: {
      type: String,
      default: 'total, sizes, prev, pager, next, jumper'
    },
    // 分页组件每页显示个数选择器的选项设置
    pageSizes: {
      type: Array,
      default() {
        return [10, 20, 30, 40, 50, 100];
      }
    },
    // 分页组件每页显示个数选择器的下拉框类名
    popperClass: String,
    // 分页组件替代图标显示的上一页文字
    prevText: String,
    // 分页组件替代图标显示的下一页文字
    nextText: String,
    // 分页组件只有一页时是否隐藏
    hideOnSinglePage: {
      type: Boolean,
      default: false
    },
    // 表头工具栏主题风格
    toolsTheme: String,
    // 标题
    title: String,
    // 二级标题
    subTitle: String,
    // 表头工具按钮布局
    toolkit: {
      type: Array,
      default() {
        return ['reload', 'size', 'columns', 'fullscreen'];
      }
    },
    // 是否开启列拖拽排序
    columnsSort: {
      type: Boolean,
      default: true
    },
    // 行点击选中
    rowClickChecked: Boolean,
    // 行点击选中当单个选中时互斥
    rowClickCheckedIntelligent: {
      type: Boolean,
      default: true
    },
    // 是否显示顶部工具栏
    search: {
      type: Boolean,
      default: false
    },
    // 是否带有纵向边框
    searchSize: {
      type: String,
      default: 'small'
    },
    // 是否显示顶部工具栏
    toolbar: {
      type: Boolean,
      default: true
    },
    // 是否显示顶部工具栏创建按钮
    toolbarAdd: {
      type: Boolean,
      default: true
    },
    // 顶部工具栏样式
    toolStyle: [Object, String],
    // 自定义顶部工具栏class
    toolClass: String,
    // 顶部工具栏右侧样式
    toolkitStyle: [Object, String],
    // 分页组件样式
    paginationStyle: [Object, String],
    // 表格全屏时的z-index
    fullZIndex: {
      type: Number,
      default: 999
    },
    // 是否自动修正页码
    autoAmendPage: {
      type: Boolean,
      default: true
    },
    // 树形表格懒加载请求参数
    parentIdName: {
      type: String,
      default: 'parentId'
    },
    // 顶级的懒加载请求参数值
    defaultParentId: [String, Number],
    // 树形表格懒加载请求参数值的字段
    parentIdField: String
  },
  data() {
    const isArraySource = Array.isArray(this.datasource);
    const cols = formatTreeData(this.columns, (d) => {
      return Object.assign({}, d, { key: uuid(8) });
    });
    return {
      // 当前显示数据
      tableData: [],
      // 当前在第几页
      tablePage: this.currentPage,
      // 每页显示数量
      tableLimit: this.pageSize,
      // 数据总数量
      tableTotal: isArraySource ? this.datasource?.length : 0,
      // 数据请求状态
      tableLoading: this.loading,
      // 表格列配置
      tableCols: cols,
      // 表格尺寸
      tableSize: this.size,
      // 是否是全屏状态
      tableFullscreen: false,
      // 请求错误后的提示信息
      errorText: '',
      // 当前排序参数
      tableOrders: { ...this.defaultSort },
      // 当前筛选参数
      tableFilters: {},
      // 当前搜索参数
      tableWhere: { ...this.where }
    };
  },
  computed: {
    // 表格索引开始序号
    tableIndex() {
      return (this.tablePage - 1) * this.tableLimit + 1;
    }
  },
  mounted() {
    if (this.initLoad) {
      this.reload();
    }
  },
  methods: {
    handleSearch() {
      this.$emit('handle-search');
    },
    handleReset() {
      this.$emit('handle-reset');
    },
    handleAdd() {
      this.$emit('handle-add');
    },

    /* 获取数据 */
    reload(obj, resolve) {
      if (obj) {
        if (obj.page) {
          this.tablePage = obj.page;
        }
        if (obj.limit) {
          this.tableLimit = obj.limit;
        }
        if (obj.where) {
          this.tableWhere = obj.where;
        }
        if (obj.orders) {
          this.tableOrders = obj.orders;
        }
        if (obj.filters) {
          this.tableFilters = obj.filters;
        }
      }
      this.errorText = '';

      // 直接指定数据
      if (Array.isArray(this.datasource)) {
        this.tableLoading = true;
        const data = [...this.datasource];
        this.tableTotal = data.length;
        // 前端排序
        if (this.tableOrders.prop) {
          data.sort((a, b) => {
            const av = a[this.tableOrders.prop];
            const bv = b[this.tableOrders.prop];
            const desc = this.tableOrders.order === 'descending';
            if (av == bv) {
              return 0;
            } else if (desc) {
              return av < bv ? 1 : -1;
            } else {
              return av < bv ? -1 : 1;
            }
          });
        }
        // 前端分页
        if (this.needPage) {
          const maxPage = Math.ceil(this.tableTotal / this.tableLimit);
          if (maxPage && this.tablePage > maxPage) {
            this.tablePage = maxPage;
          }
          const start = (this.tablePage - 1) * this.tableLimit;
          let end = start + this.tableLimit;
          end = end > this.tableTotal ? this.tableTotal : end;
          this.tableData = data.slice(start, end);
        } else {
          this.tablePage = 1;
          this.tableData = data;
        }
        this.tableLoading = false;
        const result = { data: this.tableData, total: this.tableTotal };
        this.$emit('done', result, this.tablePage, this.tableTotal);
        return;
      }

      // 自定义请求方法
      if (typeof this.datasource === 'function') {
        if (!resolve || !parent) {
          this.tableLoading = true;
        }
        // 排序参数
        const orders = {};
        if (this.tableOrders?.prop) {
          const requestNames = this.getRequestNames();
          orders[requestNames.sortName] = this.tableOrders.prop;
          orders[requestNames.orderName] =
            ORDER_VALUE[this.tableOrders.order];
        }
        Promise.resolve(
          this.datasource(
            {
              page: this.tablePage - 1,
              limit: this.tableLimit,
              where: Object.assign({}, this.tableWhere),
              order: orders,
              filters: Object.assign({}, this.tableFilters),
              parent: obj?.parent
            },
            this.callback
          )
        )
          .then((result) => {
            if (typeof result !== 'undefined') {
              const { data, count } = this.getResponseResult(result);
              this.callback(data, count, obj?.where, obj?.parent, resolve);
            }
          })
          .catch((e) => {
            this.callback(e?.message);
            if (resolve) {
              console.error(e);
            }
          });
        return;
      }

      // 数据源为url方式
      if (typeof this.datasource !== 'string') {
        return;
      }
      // 请求参数
      const params = Object.assign({}, this.tableWhere, this.tableFilters);
      const requestNames = this.getRequestNames();
      // 排序参数
      if (this.tableOrders?.prop) {
        params[requestNames.sortName] = this.tableOrders.prop;
        params[requestNames.orderName] = ORDER_VALUE[this.tableOrders.order];
      }
      // 分页参数
      if (this.needPage) {
        params[requestNames.pageName] = this.tablePage;
        params[requestNames.limitName] = this.tableLimit;
      } else {
        this.tablePage = 1;
      }
      // 懒加载父级id
      if (this.lazy) {
        if (obj?.parent) {
          const name = this.parentIdField || this.rowKey;
          const value = getFieldValue(obj.parent, name);
          params[this.parentIdName] = value;
        } else {
          params[this.parentIdName] = this.defaultParentId;
        }
      }
      if (!resolve || !parent) {
        this.tableLoading = true;
      }
      if (this.parseParam) {
        Promise.resolve(this.parseParam(params))
          .then((p) => {
            this.fetchData(p || {}, obj?.where, obj?.parent, resolve);
          })
          .catch((e) => {
            this.tableLoading = false;
            this.errorText = e?.message ?? 'parseParam返回数据错误';
          });
      } else {
        this.fetchData(params, obj?.where, obj?.parent, resolve);
      }
    },
    /* 自定义请求回调 */
    callback(data, total, where, parent, resolve) {
      this.tableLoading = false;
      if (Array.isArray(data)) {
        if (!resolve) {
          // 自动修正页码
          const amendPage = this.autoAmendPage && this.needPage;
          if (amendPage && !data.length && total) {
            const maxPage = Math.ceil(total / this.tableLimit);
            if (maxPage && this.tablePage > maxPage) {
              this.tablePage = maxPage;
              this.reload({ where: where });
              return;
            }
          }
          // 获取返回的数据
          this.tableData = data;
          this.tableTotal = total || data.length;
        } else {
          resolve(data);
        }
        const res = { data: this.tableData, tota: this.tableTotal };
        this.$emit('done', res, this.tablePage, this.tableTotal, parent);
      } else if (typeof data === 'string') {
        this.errorText = data;
      } else {
        this.errorText = '获取数据失败';
        console.error('datasource返回的数据错误: ', data);
      }
    },
    /* url数据源请求数据 */
    fetchData(params, where, parent, resolve) {
      const httpRequest = this.$http?.request;
      if (typeof httpRequest !== 'function') {
        this.errorText = 'this.$http.request is not defined';
        this.tableLoading = false;
        return;
      }
      const method = this.method?.toUpperCase() || 'GET';
      const option = {
        url: this.datasource,
        method: method,
        headers: Object.assign(
          {
            'content-type': this.contentType
          },
          this.headers
        )
      };
      if (['POST', 'PUT', 'PATCH'].includes(method)) {
        if (this.contentType?.toLowerCase()?.includes('form')) {
          const formData = new FormData();
          for (let key in params) {
            if (!Object.prototype.hasOwnProperty.call(params, key)) {
              continue;
            }
            formData.append(key, params[key]);
          }
          option.data = formData;
        } else {
          option.data = params;
        }
      } else {
        option.params = params;
      }
      httpRequest(option)
        .then((res) => {
          this.tableLoading = false;
          const result = res.data;
          const {
            msg,
            data,
            count,
            success,
            successCode
          } = this.getResponseResult(result);
          if (success) {
            if (!resolve) {
              // 自动修正页码
              const amendPage = this.autoAmendPage && this.needPage;
              if (amendPage && !data?.length && count) {
                const maxPage = Math.ceil(count / this.tableLimit);
                if (maxPage && this.tablePage > maxPage) {
                  this.tablePage = maxPage;
                  this.reload({ where: where });
                  return;
                }
              }
              // 获取返回的数据
              this.tableData = data;
              this.tableTotal = count || data.length;
            } else {
              resolve(data);
            }
            this.$emit(
              'done',
              result,
              this.tablePage,
              this.tableTotal,
              parent
            );
          } else {
            const error = `获取数据失败, 正确的状态码为${successCode}`;
            this.errorText = msg || error;
          }
        })
        .catch((e) => {
          this.tableLoading = false;
          this.errorText = e.message;
        });
    },
    /* 获取请求参数名称 */
    getRequestNames() {
      const globalRequest = this.$ELEADMIN?.request;
      return Object.assign({}, DEFAULT_REQUEST, globalRequest, this.request);
    },
    /* 获取返回数据 */
    getResponseData(data) {
      if (!this.lazy) {
        return data;
      }
      const hcName = this.treeProps?.hasChildren ?? 'hasChildren';
      return data?.map((d) => {
        const temp = Object.assign({}, d);
        temp[hcName] = temp[hcName] ?? true;
        return temp;
      });
    },
    /* 获取返回的结果 */
    getResponseResult(data) {
      const result = this.parseData ? this.parseData(data) : data;
      if (Array.isArray(result)) {
        return {
          code: 0,
          msg: '',
          data: this.getResponseData(result),
          success: true,
          successCode: 0
        };
      }
      const globalResponse = this.$ELEADMIN?.response;
      const responseNames = Object.assign(
        {},
        DEFAULT_RESPONSE,
        globalResponse,
        this.response
      );
      const code = getFieldValue(result, responseNames.statusName);
      return {
        code: code,
        msg: getFieldValue(result, responseNames.msgName),
        data: this.getResponseData(
          getFieldValue(result, responseNames.dataName)
        ),
        count: getFieldValue(result, responseNames.countName),
        success: responseNames.statusCode === code,
        successCode: responseNames.statusCode
      };
    },
    /* 异步树形表格加载方法 */
    tableLoad(row, treeNode, resolve) {
      if (this.load) {
        this.load(row, treeNode, resolve);
      } else {
        this.reload({ parent: row }, resolve);
      }
    },
    /* 分页 pageSize 改变时会触发 */
    pageSizeChange(limit) {
      this.tableLimit = limit;
      this.reload();
    },
    /* 分页 currentPage 改变时会触发 */
    pageCurrentChange(page) {
      this.tablePage = page;
      this.reload();
    },
    /* 当用户手动勾选数据行的 Checkbox 时触发的事件 */
    select(selection, row) {
      this.$emit('select', selection, row);
    },
    /* 当用户手动勾选全选 Checkbox 时触发的事件 */
    selectAll(selection) {
      this.$emit('select-all', selection);
    },
    /* 当选择项发生变化时会触发该事件 */
    selectionChange(selection) {
      this.$emit('update:selection', selection);
      this.$emit('selection-change', selection);
    },
    /* 当单元格 hover 进入时会触发该事件 */
    cellMouseEnter(row, column, cell, event) {
      this.$emit('cell-mouse-enter', row, column, cell, event);
    },
    /* 当单元格 hover 退出时会触发该事件 */
    cellMouseLeave(row, column, cell, event) {
      this.$emit('cell-mouse-leave', row, column, cell, event);
    },
    /* 当某个单元格被点击时会触发该事件 */
    cellClick(row, column, cell, event) {
      this.$emit('cell-click', row, column, cell, event);
    },
    /* 当某个单元格被双击击时会触发该事件 */
    cellDblclick(row, column, cell, event) {
      this.$emit('cell-dblclick', row, column, cell, event);
    },
    /* 当某一行被点击时会触发该事件 */
    rowClick(row, column, event) {
      if (this.rowClickChecked && this.selection) {
        if (
          this.rowClickCheckedIntelligent &&
          (this.selection.length === 0 || this.selection.length === 1)
        ) {
          this.clearSelection();
          this.toggleRowSelection(row, true);
        } else {
          this.toggleRowSelection(row);
        }
      }
      this.$emit('row-click', row, column, event);
    },
    /* 当某一行被鼠标右键点击时会触发该事件 */
    rowContextmenu(row, column, event) {
      this.$emit('row-contextmenu', row, column, event);
    },
    /* 当某一行被双击时会触发该事件 */
    rowDblclick(row, column, event) {
      this.$emit('row-dblclick', row, column, event);
    },
    /* 当某一列的表头被点击时会触发该事件 */
    headerClick(column, event) {
      this.$emit('header-click', column, event);
    },
    /* 当某一列的表头被鼠标右键点击时触发该事件 */
    headerContextmenu(column, event) {
      this.$emit('header-contextmenu', column, event);
    },
    /* 当表格的排序条件发生变化的时候会触发该事件 */
    sortChange({ column, prop, order }) {
      if (this.$listeners['sort-change']) {
        this.$emit('sort-change', { column, prop, order });
      } else {
        this.tableOrders = { prop, order };
        this.reload();
      }
    },
    /* 当表格的筛选条件发生变化的时候会触发该事件，filters的 key 是 column 的 columnKey，value 为用户选择的筛选条件的数组 */
    filterChange(filters) {
      if (this.$listeners['filter-change']) {
        this.$emit('filter-change', filters);
      } else {
        this.tableFilters = filters;
        this.reload();
      }
    },
    /* 当表格的当前行发生变化的时候会触发该事件，如果要高亮当前行，请打开表格的 highlight-current-row 属性 */
    currentChange(currentRow, oldCurrentRow) {
      this.$emit('update:current', currentRow);
      this.$emit('current-change', currentRow, oldCurrentRow);
    },
    /* 当拖动表头改变了列的宽度的时候会触发该事件 */
    headerDragend(newWidth, oldWidth, column, event) {
      this.$emit('header-dragend', newWidth, oldWidth, column, event);
    },
    /* 当用户对某一行展开或者关闭的时候会触发该事件（展开行时，回调的第二个参数为 expandedRows；树形表格时第二参数为 expanded） */
    expandChange(row, expandedRows) {
      this.$emit('expand-change', row, expandedRows);
    },
    /* 用于多选表格，清空用户的选择 */
    clearSelection() {
      this.$refs.table.clearSelection();
    },
    /* 用于多选表格，切换某一行的选中状态，如果使用了第二个参数，则是设置这一行选中与否（selected 为 true 则选中） */
    toggleRowSelection(row, selected) {
      this.$refs.table.toggleRowSelection(row, selected);
    },
    /* 用于多选表格，切换所有行的选中状态 */
    toggleAllSelection() {
      this.$refs.table.toggleAllSelection();
    },
    /* 用于可展开表格与树形表格，切换某一行的展开状态，如果使用了第二个参数，则是设置这一行展开与否（expanded 为 true 则展开） */
    toggleRowExpansion(row, expanded) {
      this.$refs.table.toggleRowExpansion(row, expanded);
    },
    /* 用于单选表格，设定某一行为选中行，如果调用时不加参数，则会取消目前高亮行的选中状态 */
    setCurrentRow(row) {
      this.$refs.table.setCurrentRow(row);
    },
    /* 用于清空排序条件，数据会恢复成未排序的状态 */
    clearSort() {
      this.$refs.table.clearSort();
    },
    /* 不传入参数时用于清空所有过滤条件，数据会恢复成未过滤的状态，也可传入由columnKey组成的数组以清除指定列的过滤条件 */
    clearFilter(columnKey) {
      this.$refs.table.clearFilter(columnKey);
    },
    /* 对 Table 进行重新布局，当 Table 或其祖先元素由隐藏切换为显示时，可能需要调用此方法 */
    doLayout() {
      this.$refs.table.doLayout();
    },
    /* 手动对 Table 进行排序，参数prop属性指定排序列，order指定排序顺序 */
    sort(prop, order) {
      this.$refs.table.sort(prop, order);
    },
    /* 修改表格尺寸 */
    updateSize(value) {
      this.tableSize = value;
      this.$emit('size-change', value);
    },
    /* 修改表格列配置 */
    updateColumns(value) {
      this.tableCols = formatTreeData(value, (d) => {
        return Object.assign({}, d, { key: uuid(8) });
      });
      this.$emit('columns-change', value);
    },
    /* 全屏切换 */
    updateFullscreen(value) {
      this.tableFullscreen = value;
      this.$emit('fullscreen-change', value);
    },
    /* 工具栏刷新按钮点击 */
    onRefresh() {
      if (Array.isArray(this.datasource)) {
        this.$emit('refresh');
      } else {
        this.reload();
      }
    },
    /* 全部展开/折叠 */
    toggleRowExpansionAll(expanded) {
      this.tableData.forEach((d) => {
        this.toggleRowExpansion(d, expanded);
      });
    }
  },
  watch: {
    loading(value) {
      this.tableLoading = value;
    },
    datasource() {
      this.reload();
    },
    size(value) {
      this.tableSize = value;
    },
    where(value) {
      this.tableWhere = { ...value };
    }
  }
};
</script>

<style lang="scss">
@import "element-ui/packages/theme-chalk/src/common/var";
$--background-color-base: #fafafa;
$--table-header-font-color: $--color-text-primary;
$--table-header-background-color: $--background-color-base;

/* 表格全屏样式 */
.ele-pro-table.ele-pro-table-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: auto;
  padding-bottom: 15px;
  box-sizing: border-box;

  .ele-table-tool:not(.ele-table-tool-default) {
    padding-left: 15px;
    padding-right: 15px;
    margin-top: 10px;
  }
}


/* table */
.el-table {
  .caret-wrapper {
    height: 14px;
  }

  .sort-caret.ascending {
    top: -5px;
  }

  .sort-caret.descending {
    bottom: -3px;
  }

  th.gutter {
    display: table-cell !important;
  }

  &.ele-table-default-head th {
    background: none;
  }

  .cell.el-tooltip {
    min-width: auto;
  }

  td.el-table-column--selection .cell,
  th.el-table-column--selection .cell {
    padding-left: 0;
    padding-right: 0;
  }

  thead {
    .el-table__cell{
      background-color: $--table-header-background-color !important;
    }
    .cell {
      color: $--color-text-primary !important;
      font-weight: bold;
    }
  }
}

.el-table__body tr.current-row:hover > td {
  background: $--table-current-row-background-color;
}

.el-table__expand-icon > .el-icon {
  font-weight: bold;
}

.el-table__column-filter-trigger {
  line-height: 16px;
}

.el-table__column-filter-trigger i {
  font-weight: bold;
}

.el-table--border.el-loading-parent--relative {
  border-color: $--border-color-lighter;
}

.el-table--border .el-loading-mask {
  right: 1px;
  bottom: 1px;
}

.el-pagination.ele-pagination-circle {
  .el-pager li,
  .btn-prev,
  .btn-next {
    border-radius: 50%;
    min-width: 28px;
    margin-left: 0;
    margin-right: 0;

    &:not(.active) {
      background: none;
    }
  }
}

@media screen and (max-width: 768px) {
  .el-pagination.ele-pagination,
  .el-pagination.ele-pagination-circle {
    .el-pagination__total,
    .el-pagination__sizes,
    .el-pagination__jump {
      display: none;
    }
  }
}


/* 数据表格头部工具栏 */
.ele-table-tool {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 10px;

  .ele-table-tool-title {
    flex: auto;
    margin-top: 5px;
    margin-bottom: 5px;
  }

  .ele-tool {
    margin: 5px 0 5px auto;

    .ele-tool-item {
      font-size: 18px;
      padding: 0 2px;
      cursor: pointer;

      .el-dropdown > i {
        font-size: 18px;
      }
    }
  }

  &.ele-toolbar-form .ele-table-tool-title {
    margin-top: 0;
    margin-bottom: 0;

    .el-form-item,
    .ele-form-actions {
      margin-top: 5px;
      margin-bottom: 5px;
    }
  }

  &.ele-toolbar-actions .ele-table-tool-title {
    margin-top: 0;
    margin-bottom: 0;

    & > .el-button,
    & > .el-link,
    & > .el-dropdown,
    & > .el-tag,
    & > .ele-action {
      margin-top: 5px;
      margin-bottom: 5px;
    }
  }

  &:not(.ele-table-tool-default):not(.ele-table-tools-none)
  .ele-tool
  .ele-tool-item {
    padding: 6px;
    line-height: 1;
    text-align: center;
    border: 1px solid $--border-color-base;
    border-radius: 50%;
    font-size: 14px;

    .el-dropdown > i {
      font-size: 14px;
    }

    &:hover {
      color: $--color-primary;
      border-color: $--color-primary-light-8;
      background-color: $--color-primary-light-9;

      .el-dropdown > i {
        color: $--color-primary;
      }
    }
  }
}

.ele-action+.el-link, .ele-action+.ele-action {
  margin-left: 10px;
}

// 带背景色
.ele-table-tool-default {
  margin-bottom: 0;
  padding: 5px 15px;
  box-sizing: border-box;
  background: $--table-header-background-color;
  border-top: 1px solid $--border-color-lighter;
  border-left: 1px solid $--border-color-lighter;
  border-right: 1px solid $--border-color-lighter;

  .ele-tool .ele-tool-item {
    font-size: 16px;
    padding: 7px 8px;
    border-radius: 2px;
    border: 1px solid $--border-color-light;
    box-sizing: border-box;
    line-height: 1;

    .el-dropdown > i {
      font-size: 16px;
    }
  }
}


/* divider */
.ele-pro-table {
  /* pagination */
  .el-pagination {
    font-weight: normal;
    text-align: center;
  }

  .el-table + .el-pagination {
    margin-top: 15px;
  }

  .el-divider {
    margin: 0;

    &.el-divider--horizontal {
      background: $--border-color-lighter;
    }

    &.ele-divider-base {
      background: $--border-color-base;
    }

    &.ele-divider-light {
      background: $--border-color-light;
    }

    &.ele-divider-lighter {
      background: $--border-color-lighter;
    }

    &.ele-divider-extra-light {
      background: $--border-color-extra-light;
    }

    &.ele-divider-dashed {
      background: linear-gradient(
          to right,
          $--border-color-base,
          $--border-color-base 8px,
          transparent 8px,
          transparent
      );
      background-size: 16px 100% !important;

      &.el-divider--horizontal {
        background: linear-gradient(
            to right,
            $--border-color-lighter,
            $--border-color-lighter 8px,
            transparent 8px,
            transparent
        );
      }

      &.ele-divider-base {
        background: linear-gradient(
            to right,
            $--border-color-base,
            $--border-color-base 8px,
            transparent 8px,
            transparent
        );
      }

      &.ele-divider-light {
        background: linear-gradient(
            to right,
            $--border-color-light,
            $--border-color-light 8px,
            transparent 8px,
            transparent
        );
      }

      &.ele-divider-extra-light {
        background: linear-gradient(
            to right,
            $--border-color-lighter,
            $--border-color-lighter 8px,
            transparent 8px,
            transparent
        );
      }

      &.ele-divider-extra-light {
        background: linear-gradient(
            to right,
            $--border-color-extra-light,
            $--border-color-extra-light 8px,
            transparent 8px,
            transparent
        );
      }
    }
  }
}
</style>
