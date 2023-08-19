<!-- 空视图组件 license by http://eleadmin.com -->
<template>
  <div :class="['ele-empty', customClass]">
    <div class="ele-empty-icon">
      <slot name="icon">
        <ele-empty-icon />
      </slot>
    </div>
    <div class="ele-empty-text ele-text-placeholder">
      <slot name="text">{{ text }}</slot>
    </div>
    <div class="ele-empty-actions">
      <slot />
    </div>
  </div>
</template>

<script>
  import EleEmptyIcon from './components/ele-empty-icon.vue';

  export default {
    name: 'EleEmpty',
    components: { EleEmptyIcon },
    props: {
      // 提示文本
      text: {
        type: String,
        default: '暂无数据'
      },
      // 自定义class
      customClass: [String, Object]
    }
  };
</script>

<style lang="scss">
  .ele-empty {
    padding: 35px 0;
    position: relative;
    text-align: center;
    line-height: 1;

    .ele-empty-icon {
      font-size: 58px;
      color: hsla(0, 0%, 60%, 0.25);
    }

    .ele-empty-text {
      padding: 6px 0;
    }
  }
</style>
