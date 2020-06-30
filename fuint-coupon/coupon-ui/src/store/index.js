import Vue from 'vue'
import Vuex from 'vuex'
import addTest from './modules/addTest'
// 修改state时console打印
import createLogger from 'vuex/dist/logger'

Vue.use(Vuex)

const debug = process.env.NODE_ENV !== 'production'

// const state = {
// 	count: 0
// }
const getters = {
	add: state => {
		return state.count + 1;
	}
}


export default new Vuex.Store({
	modules: {
		addTest
	},
	getters,
	// 严格模式，非法修改state时报错
	strict: debug,
	plugins: debug ? [createLogger()] : []
})