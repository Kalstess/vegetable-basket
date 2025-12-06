const { request } = require('../utils/request.js');

function list() {
	return request({ url: '/vehicles' });
}
function get(id) {
	return request({ url: `/vehicles/${id}` });
}
function listByCompany(companyId) {
	return request({ url: `/vehicles/company/${companyId}` });
}
function listActiveByCompany(companyId) {
	return request({ url: `/vehicles/company/${companyId}/active` });
}
function create(data) {
	return request({ url: '/vehicles', method: 'POST', data });
}
function update(id, data) {
	return request({ url: `/vehicles/${id}`, method: 'PUT', data });
}

module.exports = { list, get, listByCompany, listActiveByCompany, create, update };


