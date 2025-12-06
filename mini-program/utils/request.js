const { baseURL } = require('../config/index.js');

function request(options) {
	const app = getApp();
	const headers = options.header || {};
	const token = app?.globalData?.token || wx.getStorageSync('token') || '';
	if (token) {
		headers['Authorization'] = `Bearer ${token}`;
	}
	
	// 处理查询参数
	let url = `${baseURL}${options.url}`;
	if (options.params && Object.keys(options.params).length > 0) {
		const queryString = Object.keys(options.params)
			.map(key => `${encodeURIComponent(key)}=${encodeURIComponent(options.params[key])}`)
			.join('&');
		url += (url.includes('?') ? '&' : '?') + queryString;
	}
	
	return new Promise((resolve, reject) => {
		wx.request({
			url: url,
			method: options.method || 'GET',
			data: options.data || {},
			header: {
				'Content-Type': 'application/json',
				...headers
			},
			success: (res) => {
				const { statusCode, data } = res;
				if (statusCode >= 200 && statusCode < 300) {
					if (data && typeof data === 'object' && 'code' in data) {
						if (data.code === 200) {
							resolve(data.data);
						} else {
							reject(new Error(data.message || '请求失败'));
						}
					} else {
						resolve(data);
					}
				} else {
					reject(new Error((data && data.message) || `HTTP ${statusCode}`));
				}
			},
			fail: (err) => reject(err)
		});
	});
}

module.exports = {
	request,
	get(url, params) {
		return request({ url, method: 'GET', params });
	},
	post(url, data, options = {}) {
		return request({ url, method: 'POST', data, ...options });
	},
	put(url, data) {
		return request({ url, method: 'PUT', data });
	},
	delete(url) {
		return request({ url, method: 'DELETE' });
	}
};


