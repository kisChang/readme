<template>
  <div class="ele-table-tool ele-table-tool-default">
    <div class="ele-table-tool-title ele-cell">
      <div v-if="title || subTitle" style="margin-right: 8px">
        <h6 v-if="title">{{ title }}</h6>
        <div v-if="subTitle">{{ subTitle }}</div>
      </div>
      <div class="ele-cell-content" style="margin-left: 0">
        <slot />
      </div>
    </div>
    <slot name="action" />
  </div>
</template>

<script>
  export default {
    name: 'EleToolbar',
    props: {
      // 主题风格
      theme: {
        type: String,
        default: 'default',
        validator(value) {
          return ['none', 'default'].includes(value);
        }
      },
      // 标题
      title: String,
      // 二级标题
      subTitle: String
    }
  };
</script>
