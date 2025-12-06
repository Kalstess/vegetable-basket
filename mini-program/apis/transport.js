const { request } = require('../utils/request.js');

function create(data) {
	return request({ url: '/transport-stats', method: 'POST', data });
}
function update(id, data) {
	return request({ url: `/transport-stats/${id}`, method: 'PUT', data });
}
function listByVehicle(vehicleId) {
	return request({ url: `/transport-stats/vehicle/${vehicleId}` });
}
function listByCompany(companyId) {
	return request({ url: `/transport-stats/company/${companyId}` });
}

module.exports = { create, update, listByVehicle, listByCompany };


