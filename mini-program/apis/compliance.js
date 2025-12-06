const { request } = require('../utils/request.js');

function create(data) {
	return request({ url: '/compliance', method: 'POST', data });
}
function update(id, data) {
	return request({ url: `/compliance/${id}`, method: 'PUT', data });
}
function listByVehicle(vehicleId) {
	return request({ url: `/compliance/vehicle/${vehicleId}` });
}
function listByCompany(companyId) {
	return request({ url: `/compliance/company/${companyId}` });
}

module.exports = { create, update, listByVehicle, listByCompany };


