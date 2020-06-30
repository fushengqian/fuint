import axios from 'axios'

// import qs from 'qs'
import VueCookies from 'vue-cookies'

// 添加一个请求拦截器
axios.interceptors.request.use(config => {
	return config;
}, error => {
	return Promise.reject(error)
});

// 添加一个响应拦截器
axios.interceptors.response.use(response => {
	// 在这里对返回的数据进行处理
	console.log(response)
    if (response.data.code === 1001) {
		let _hash = location.hash.split('#')
		let param = _hash[1].split('?')
		if (param.length > 1 && ['/increase','/increaseDetail'].indexOf(param[0]) === -1) {
			console.log(param)
			let _a = param[1].split('&')
			let obj = {}
			_a.map((el, v) => {
				console.log(el)
				let _b = el.split('=')
				obj[_b[0]] = _b[1]
			})
			console.log(obj)
			location.hash = `#/login?frompath=${param[0].split('/')[1]}&param=${JSON.stringify(obj)}`
		} else {
			location.hash = `#/login`
		}
	}
	return response
}, error => {
	// Do something with response error
	return Promise.reject(error);
});


// axios全局修改的变量
// 请求的url的基础url，将和url拼接请求
axios.defaults.baseURL = '/fuint-coupon'
// axios.defaults.headers.common['Authorization'] = AUTH_TOKEN;
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

let get = async function(url, data) {
	let option = {
		url: url,
		method: 'get',
		data: data,
		timeout: 2000,
		// `headers` are custom headers to be sent
		headers: {'X-Requested-With': 'XMLHttpRequest', 'token': VueCookies.get('storeToken'), 'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'},
		// 是否携带cookie信息
		withCredentials: false,
		// 响应格式
		// 可选项 'arraybuffer', 'blob', 'document', 'json', 'text', 'stream'
		responseType: 'json',
		// 'proxy' defines the hostname and port of the proxy server
		// Use `false` to disable proxies, ignoring environment variables.
		// `auth` indicates that HTTP Basic auth should be used to connect to the proxy, and
		// supplies credentials.
		// This will set an `Proxy-Authorization` header, overwriting any existing
		// `Proxy-Authorization` custom headers you have set using `headers`.
		// 代理
		// proxy: {
		// 	host: '127.0.0.1',
		// 	port: 9000,
		// 	auth: {
		// 		username: 'mikeymike',
		// 		password: 'rapunz3l'
		// 	}
		// },
	};
	try {
		return await axios(option);
	} catch (e) {
		return Promise.reject(e);
	}
}

let post = async function(url, data) {
	let option = {
		url: url,
		method: 'post',
		data: data,
		timeout: 2000,
		// `headers` are custom headers to be sent
		headers: {'X-Requested-With': 'XMLHttpRequest', 'token': VueCookies.get('storeToken'), 'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'},
		// `withCredentials` 表示跨域请求时是否需要使用凭证
		withCredentials: false,
		// 响应格式
		// 可选项 'arraybuffer', 'blob', 'document', 'json', 'text', 'stream'
		responseType: 'json',
		// 'proxy' defines the hostname and port of the proxy server
		// Use `false` to disable proxies, ignoring environment variables.
		// `auth` indicates that HTTP Basic auth should be used to connect to the proxy, and
		// supplies credentials.
		// This will set an `Proxy-Authorization` header, overwriting any existing
		// `Proxy-Authorization` custom headers you have set using `headers`.
		// 代理
		// proxy: {
		// 	host: '127.0.0.1',
		// 	port: 9000,
		// 	auth: {
		// 		username: 'mikeymike',
		// 		password: 'rapunz3l'
		// 	}
		// },
	};
	try {
		return await axios(option);
	} catch (e) {
		return Promise.reject(e);
	}
}
let postWithNoToken = async function(url, data) {
	let option = {
		url: url,
		method: 'post',
		data: data,
		timeout: 2000,
		// `headers` are custom headers to be sent
		headers: {'X-Requested-With': 'XMLHttpRequest', 'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'},
		// `withCredentials` 表示跨域请求时是否需要使用凭证
		withCredentials: false,
		// 响应格式
		// 可选项 'arraybuffer', 'blob', 'document', 'json', 'text', 'stream'
		responseType: 'json',
		// 'proxy' defines the hostname and port of the proxy server
		// Use `false` to disable proxies, ignoring environment variables.
		// `auth` indicates that HTTP Basic auth should be used to connect to the proxy, and
		// supplies credentials.
		// This will set an `Proxy-Authorization` header, overwriting any existing
		// `Proxy-Authorization` custom headers you have set using `headers`.
		// 代理
		// proxy: {
		// 	host: '127.0.0.1',
		// 	port: 9000,
		// 	auth: {
		// 		username: 'mikeymike',
		// 		password: 'rapunz3l'
		// 	}
		// },
	};
	try {
		return await axios(option);
	} catch (e) {
		return Promise.reject(e);
	}
}
export default {
	get: get,
	post: post,
	postWithNoToken: postWithNoToken
}
