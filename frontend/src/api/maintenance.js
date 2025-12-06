import request from './request'

/**
 * 维护保养API
 */
export const maintenanceApi = {
  // 获取所有维护记录列表
  getAll() {
    return request.get('/maintenance')
  },

  // 根据ID获取维护记录详情
  getById(id) {
    return request.get(`/maintenance/${id}`)
  },

  // 根据车辆ID获取维护记录列表
  getByVehicleId(vehicleId) {
    return request.get(`/maintenance/vehicle/${vehicleId}`)
  },

  // 根据企业ID获取维护记录列表
  getByCompanyId(companyId) {
    return request.get(`/maintenance/company/${companyId}`)
  },

  // 创建维护记录
  create(data) {
    return request.post('/maintenance', data)
  },

  // 更新维护记录
  update(id, data) {
    return request.put(`/maintenance/${id}`, data)
  },

  // 删除维护记录
  delete(id) {
    return request.delete(`/maintenance/${id}`)
  }
}

