const request = require('../utils/request.js');

module.exports = {
  // 获取所有路线点
  getAll() {
    return request.get('/route-points');
  },
  
  // 根据ID获取路线点
  getById(id) {
    return request.get(`/route-points/${id}`);
  },
  
  // 根据车辆ID获取路线点列表
  getByVehicleId(vehicleId) {
    return request.get(`/route-points/vehicle/${vehicleId}`);
  },
  
  // 根据路线ID获取路线点列表（按顺序）
  getByRouteId(routeId) {
    return request.get(`/route-points/route/${routeId}`);
  },
  
  // 创建路线点
  create(data) {
    return request.post('/route-points', data);
  },
  
  // 批量创建路线点
  createBatch(dataList) {
    return request.post('/route-points/batch', dataList);
  },
  
  // 更新路线点
  update(id, data) {
    return request.put(`/route-points/${id}`, data);
  },
  
  // 删除路线点
  delete(id) {
    return request.delete(`/route-points/${id}`);
  },
  
  // 根据路线ID删除所有路线点
  deleteByRouteId(routeId) {
    return request.delete(`/route-points/route/${routeId}`);
  }
};

