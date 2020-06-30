import Vue from 'vue'
import Router from 'vue-router'
import VueCookies from 'vue-cookies'

Vue.use(Router)
const loginComponent = r => require.ensure([], () => r(require('@/page/login')), 'login_page')
const indexComponent = r => require.ensure([], () => r(require('@/page/index')), 'index_page')
const registerComponent = r => require.ensure([], () => r(require('@/page/register')), 'register_page')
const resultComponent = r => require.ensure([], () => r(require('@/page/result')), 'result_page')
const increaseComponent = r => require.ensure([], () => r(require('@/page/increase')), 'increase_page')
const increaseDetailComponent = r => require.ensure([], () => r(require('@/page/increaseDetail')), 'increaseDetail_page')
const aboutComponent = r => require.ensure([], () => r(require('@/page/about')), 'about_page')

const routes = [{
  path: '/',
  redirect: '/home'
}, {
  path: '/login',
  component: loginComponent,
  name: 'login'
}, {
  path: '/home',
  component: indexComponent,
  name: 'home'
}, {
  path: '/register',
  component: registerComponent,
  name: 'register'
}, {
  path: '/result',
  component: resultComponent,
  name: 'result'
}, {
  path: '/increase',
  component: increaseComponent,
  name: 'increase'
}, {
  path: '/increaseDetail',
  component: increaseDetailComponent,
  name: 'increaseDetail'
}, {
  path: '/about',
  component: aboutComponent,
  name: 'about'
}]
let router = new Router({
	routes
})
router.beforeEach((to, from, next) => {
  if (!VueCookies.get('storeToken') && to.name !== 'login' && to.name !== 'register') {
    if (from.name === 'register') {
      next()
      return false
    }
    if (to.name === 'result') {
      next({path: '/login', query: {'frompath': to.name, 'param': JSON.stringify(to.query)}})
      return false
    }
    next({path: '/login'})
  } else {
    next()
  }
})
export default router
