const { request } = require('../utils/request.js');

function activeProductTypes() {
	return request({ url: '/product-types/active' });
}

module.exports = { activeProductTypes };


