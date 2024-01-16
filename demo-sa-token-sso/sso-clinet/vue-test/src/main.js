import Vue from 'vue'
import App from './App.vue'
//导入VueRouter插件
import VueRouter from  'vue-router'
//引入router
import router from './router'


import {request} from  "@/http/http"


Vue.config.productionTip = false;
Vue.prototype.$http = request

//使用路由插件
Vue.use(VueRouter)

new Vue({
  render: h => h(App),
  router:router,
}).$mount('#app')
