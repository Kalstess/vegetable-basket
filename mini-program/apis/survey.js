const request = require('../utils/request.js');

module.exports = {
	// 获取所有问卷调查列表
	list() {
		return request.get('/survey');
	},
	
	// 根据ID获取问卷调查详情
	getById(id) {
		return request.get(`/survey/${id}`);
	},
	
	// 根据年份获取问卷调查列表
	getByYear(year) {
		return request.get(`/survey/year/${year}`);
	},
	
	// 创建或更新问卷调查（保存为草稿）
	createOrUpdate(data) {
		return request.post('/survey', data);
	},
	
	// 提交问卷调查
	submit(id) {
		return request.post(`/survey/${id}/submit`);
	},
	
	// 删除问卷调查
	delete(id) {
		return request.delete(`/survey/${id}`);
	}
};

