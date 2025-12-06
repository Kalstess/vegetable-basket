import request from './request'

/**
 * 合规管理API
 */
export const complianceApi = {
  // 获取所有合规记录列表
  getAll() {
    return request.get('/compliance')
  },

  // 根据ID获取合规记录详情
  getById(id) {
    return request.get(`/compliance/${id}`)
  },

  // 根据车辆ID获取合规记录列表
  getByVehicleId(vehicleId) {
    return request.get(`/compliance/vehicle/${vehicleId}`)
  },

  // 根据企业ID获取合规记录列表
  getByCompanyId(companyId) {
    return request.get(`/compliance/company/${companyId}`)
  },

  // 创建合规记录
  create(data) {
    return request.post('/compliance', data)
  },

  // 更新合规记录
  update(id, data) {
    return request.put(`/compliance/${id}`, data)
  },

  // 删除合规记录
  delete(id) {
    return request.delete(`/compliance/${id}`)
  }
}

