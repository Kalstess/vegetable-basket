const { request } = require('../utils/request.js');

function create(data) {
	return request({ url: '/reports', method: 'POST', data });
}

module.exports = { create };


