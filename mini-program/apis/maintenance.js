const { request } = require('../utils/request.js');

function create(data) {
	return request({ url: '/maintenance', method: 'POST', data });
}
function update(id, data) {
	return request({ url: `/maintenance/${id}`, method: 'PUT', data });
}
function listByVehicle(vehicleId) {
	return request({ url: `/maintenance/vehicle/${vehicleId}` });
}
function listByCompany(companyId) {
	return request({ url: `/maintenance/company/${companyId}` });
}

module.exports = { create, update, listByVehicle, listByCompany };


