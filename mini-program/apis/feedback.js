const { request } = require('../utils/request.js');

function create(data) {
	return request({ url: '/feedback', method: 'POST', data });
}
function update(id, data) {
	return request({ url: `/feedback/${id}`, method: 'PUT', data });
}
function listByCompany(companyId) {
	return request({ url: `/feedback/company/${companyId}` });
}

module.exports = { create, update, listByCompany };


