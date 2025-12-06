const request = require('../utils/request.js');

module.exports = {
  // 获取所有配送网点
  getAll() {
    return request.get('/delivery-points');
  },
  
  // 根据ID获取配送网点
  getById(id) {
    return request.get(`/delivery-points/${id}`);
  },
  
  // 根据企业ID获取配送网点列表
  getByCompanyId(companyId) {
    return request.get(`/delivery-points/company/${companyId}`);
  },
  
  // 根据企业ID获取有效配送网点列表
  getActiveByCompanyId(companyId, isActive = true) {
    return request.get(`/delivery-points/company/${companyId}/active`, { params: { isActive } });
  },
  
  // 根据车辆ID获取配送网点列表
  getByVehicleId(vehicleId) {
    return request.get(`/delivery-points/vehicle/${vehicleId}`);
  },
  
  // 创建配送网点
  create(data) {
    return request.post('/delivery-points', data);
  },
  
  // 更新配送网点
  update(id, data) {
    return request.put(`/delivery-points/${id}`, data);
  },
  
  // 删除配送网点
  delete(id) {
    return request.delete(`/delivery-points/${id}`);
  }
};

