const { request } = require('../utils/request.js');

function list() {
	return request({ url: '/companies' });
}
function get(id) {
	return request({ url: `/companies/${id}` });
}
function create(data) {
	return request({ url: '/companies', method: 'POST', data });
}
function update(id, data) {
	return request({ url: `/companies/${id}`, method: 'PUT', data });
}
function search(keyword) {
	return request({ url: `/companies/search?keyword=${encodeURIComponent(keyword)}` });
}

module.exports = { list, get, create, update, search };


