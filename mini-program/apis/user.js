const { request } = require('../utils/request.js');

function getAll() {
	return request({ url: '/users', method: 'GET' });
}

function getById(id) {
	return request({ url: `/users/${id}`, method: 'GET' });
}

function getCurrentUser() {
	return request({ url: '/users/profile', method: 'GET' });
}

function create(data) {
	return request({ url: '/users', method: 'POST', data });
}

function update(id, data) {
	return request({ url: `/users/${id}`, method: 'PUT', data });
}

function updateProfile(data) {
	return request({ url: '/users/profile', method: 'PUT', data });
}

function deleteUser(id) {
	return request({ url: `/users/${id}`, method: 'DELETE' });
}

module.exports = {
	getAll,
	getById,
	getCurrentUser,
	create,
	update,
	updateProfile,
	delete: deleteUser
};

