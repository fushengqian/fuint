// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import Mint from 'mint-ui'
import { MessageBox } from 'mint-ui'
import 'mint-ui/lib/style.css'
import VueCookies from 'vue-cookies'
Vue.prototype.$MessageBox = MessageBox
Vue.use(VueCookies)
//加载vue-scroll组件
import scroller from 'vue-scroller'
Vue.use(scroller)
Vue.config.productionTip = false
Vue.use(Mint)
/* eslint-disable no-new */
new Vue({
	el: '#app',
	router,
	store,
	components: {
		App
	},
	template: '<App/>'
})