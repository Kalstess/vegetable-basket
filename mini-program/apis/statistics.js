const { request } = require('../utils/request.js');

function getStatistics() {
	return request({ url: '/statistics' });
}

module.exports = { getStatistics };


