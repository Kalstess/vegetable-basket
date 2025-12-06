const request = require('../utils/request.js');

module.exports = {
  // 获取所有车辆统计
  getAll() {
    return request.get('/vehicle-statistics');
  },
  
  // 根据ID获取车辆统计
  getById(id) {
    return request.get(`/vehicle-statistics/${id}`);
  },
  
  // 根据企业ID获取车辆统计列表
  getByCompanyId(companyId) {
    return request.get(`/vehicle-statistics/company/${companyId}`);
  },
  
  // 根据企业ID和统计日期获取车辆统计
  getByCompanyIdAndDate(companyId, statDate) {
    return request.get(`/vehicle-statistics/company/${companyId}/date`, { params: { statDate } });
  },
  
  // 创建车辆统计
  create(data) {
    return request.post('/vehicle-statistics', data);
  },
  
	// 根据企业车辆自动生成统计数据
	generate(companyId, statDate) {
		return request.post('/vehicle-statistics/generate', null, { params: { companyId, statDate } });
	},
  
  // 更新车辆统计
  update(id, data) {
    return request.put(`/vehicle-statistics/${id}`, data);
  },
  
  // 删除车辆统计
  delete(id) {
    return request.delete(`/vehicle-statistics/${id}`);
  }
};

