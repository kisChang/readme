<!-- 表格默认工具按钮组件 license by http://eleadmin.com -->
<template>
  <div class="ele-tool ele-space" :style="customStyle">
    <slot />
    <template v-for="tool in tools">
      <!-- 刷新 -->
      <ele-tool-item
        v-if="tool === 'reload'"
        :key="tool"
        :placement="tipsPlacement"
        icon="el-icon-refresh-right"
        title="刷新"
        @click="reload"
      />
      <!-- 密度设置 -->
      <ele-tool-item
        v-if="tool === 'size'"
        :key="tool"
        :placement="tipsPlacement"
        title="密度设置"
      >
        <el-dropdown
          trigger="click"
          placement="bottom-end"
          @command="updateSize"
        >
          <i class="el-icon-s-fold"></i>
          <el-dropdown-menu slot="dropdown" style="min-width: 80px">
            <el-dropdown-item command="large">
              <span :class="{ 'ele-text-primary': size === 'large' }">
                宽松
              </span>
            </el-dropdown-item>
            <el-dropdown-item command="medium">
              <span :class="{ 'ele-text-primary': size === 'medium' }">
                默认
              </span>
            </el-dropdown-item>
            <el-dropdown-item command="small">
              <span :class="{ 'ele-text-primary': size === 'small' }">
                中等
              </span>
            </el-dropdown-item>
            <el-dropdown-item command="mini">
              <span :class="{ 'ele-text-primary': size === 'mini' }">
                紧凑
              </span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </ele-tool-item>
      <!-- 列设置 -->
      <ele-tool-columns
        v-if="tool === 'columns'"
        :key="tool"
        :columns="columns"
        :columns-sort="columnsSort"
        :tip-placement="tipsPlacement"
        tip-title="列展示"
        title="列展示"
        reset-text="重置"
        index-text="columnsOption.index"
        untitled-text="无标题"
        @update:columns="updateColumns"
      />
      <!-- 全屏切换 -->
      <ele-tool-item
        v-if="tool === 'fullscreen'"
        :key="tool"
        :placement="tipsPlacement"
        title="全屏"
        :icon="fullscreen ? 'el-icon-full-screen' : 'el-icon-full-screen'"
        @click="toggleFullscreen"
      />
    </template>
  </div>
</template>

<script>
  import { EleToolItem } from '../ele-toolbar';
  import EleToolColumns from './ele-tool-columns.vue';

  export default {
    name: 'EleProTableTools',
    components: { EleToolItem, EleToolColumns },
    emits: ['reload', 'update:size', 'update:columns', 'update:fullscreen'],
    props: {
      // 表格尺寸
      size: {
        type: String,
        default: 'medium'
      },
      // 表格列配置
      columns: {
        type: Array,
        default() {
          return [];
        }
      },
      // 工具按钮布局
      tools: {
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
      // 是否是全屏状态
      fullscreen: Boolean,
      // 自定义样式
      customStyle: Object
    },
    computed: {
      // tooltips的方向
      tipsPlacement() {
        return this.fullscreen ? 'bottom' : 'top';
      }
    },
    methods: {
      /* 刷新表格 */
      reload() {
        this.$emit('reload');
      },
      /* 修改表格尺寸 */
      updateSize(command) {
        this.$emit('update:size', command);
      },
      /* 修改columns */
      updateColumns(value) {
        this.$emit('update:columns', value);
      },
      /* 全屏切换 */
      toggleFullscreen() {
        this.$emit('update:fullscreen', !this.fullscreen);
      }
    }
  };
</script>

<style lang="scss">
.ele-space {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
}
</style>
