import request from './request'

/**
 * 反馈管理API
 */
export const feedbackApi = {
  // 获取所有反馈列表
  getAll() {
    return request.get('/feedback')
  },

  // 根据ID获取反馈详情
  getById(id) {
    return request.get(`/feedback/${id}`)
  },

  // 根据企业ID获取反馈列表
  getByCompanyId(companyId) {
    return request.get(`/feedback/company/${companyId}`)
  },

  // 根据状态获取反馈列表
  getByStatus(status) {
    return request.get(`/feedback/status/${status}`)
  },

  // 创建反馈记录
  create(data) {
    return request.post('/feedback', data)
  },

  // 更新反馈记录
  update(id, data) {
    return request.put(`/feedback/${id}`, data)
  },

  // 删除反馈记录
  delete(id) {
    return request.delete(`/feedback/${id}`)
  }
}

