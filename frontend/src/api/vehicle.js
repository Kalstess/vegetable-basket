import request from './request'

/**
 * 车辆管理API
 */
export const vehicleApi = {
  // 获取所有车辆列表
  getAll() {
    return request.get('/vehicles')
  },

  // 根据ID获取车辆详情
  getById(id) {
    return request.get(`/vehicles/${id}`)
  },

  // 根据企业ID获取车辆列表
  getByCompanyId(companyId) {
    return request.get(`/vehicles/company/${companyId}`)
  },

  // 根据企业ID获取有效车辆列表
  getActiveByCompanyId(companyId, isActive = true) {
    return request.get(`/vehicles/company/${companyId}/active`, {
      params: { isActive }
    })
  },

  // 创建车辆
  create(data) {
    return request.post('/vehicles', data)
  },

  // 更新车辆
  update(id, data) {
    return request.put(`/vehicles/${id}`, data)
  },

  // 更新车辆状态
  updateStatus(id, isActive) {
    return request.put(`/vehicles/${id}/status`, null, {
      params: { isActive }
    })
  },

  // 删除车辆
  delete(id) {
    return request.delete(`/vehicles/${id}`)
  }
}

