<!-- 表头工具栏按钮 license by http://eleadmin.com -->
<template>
  <div class="ele-tool-item ele-action" @click="onClick">
    <el-tooltip :content="title" :disabled="disabled" :placement="placement">
      <slot>
        <i :class="icon"></i>
      </slot>
    </el-tooltip>
  </div>
</template>

<script>
  export default {
    name: 'EleToolItem',
    emits: ['click'],
    props: {
      // tooltip title
      title: String,
      // tooltip placement
      placement: String,
      // tooltip disabled
      disabled: Boolean,
      // 图标
      icon: String
    },
    methods: {
      onClick(e) {
        this.$emit('click', e);
      }
    }
  };
</script>
