import request from './request'

/**
 * 运输统计API
 */
export const transportApi = {
  // 获取所有运输统计列表
  getAll() {
    return request.get('/transport-stats')
  },

  // 根据ID获取运输统计详情
  getById(id) {
    return request.get(`/transport-stats/${id}`)
  },

  // 根据车辆ID获取运输统计列表
  getByVehicleId(vehicleId) {
    return request.get(`/transport-stats/vehicle/${vehicleId}`)
  },

  // 根据企业ID获取运输统计列表
  getByCompanyId(companyId) {
    return request.get(`/transport-stats/company/${companyId}`)
  },

  // 创建运输统计记录
  create(data) {
    return request.post('/transport-stats', data)
  },

  // 更新运输统计记录
  update(id, data) {
    return request.put(`/transport-stats/${id}`, data)
  },

  // 删除运输统计记录
  delete(id) {
    return request.delete(`/transport-stats/${id}`)
  }
}

