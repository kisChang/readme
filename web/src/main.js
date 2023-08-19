import Vue from "vue";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false;
Vue.use(ElementUI);

import App from "./App.vue";
import router from "./router";

import "./assets/base.css";

const vueApp = new Vue({
  el: '#app',
  router,
  render: h => h(App)
});
