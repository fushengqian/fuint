// promise测试函数，模拟异步请求
let promiseFunction = function() {
	return new Promise(function(resolve, reject) {
		setTimeout(function() {
			let num = Math.ceil(Math.random() * 10);
			if (num > 5) {
				resolve(num)
			} else {
				reject({
					message: '数字小于5',
					number: num
				})
			}
		}, 2000);

	})
}
const addTest = {
	state: {
		count: 0
	},
	mutations: {
		increase: (state, payload) => {
			state.count += payload.n;
		},
		minus: (state, payload) => {
			state.count -= payload;
		}
	},
	actions: {
		testActions({
			commit
		}) {
			promiseFunction().then(function(data) {
				commit('increase', {
					n: data
				});
			}).catch(function(response) {
				console.log(response.message + ": " + response.number);
			})

		}
	}
}

export default addTest;