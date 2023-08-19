/** 表格工具栏组件 license by http://eleadmin.com */
import EleToolbar from './src/main.vue';
import EleToolItem from './src/components/ele-tool-item.vue';

EleToolbar.install = function(Vue) {
  Vue.component(EleToolbar.name, EleToolbar);
  Vue.component(EleToolItem.name, EleToolItem);
};

export default EleToolbar;

export { EleToolItem };
