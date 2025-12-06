import request from './request'

/**
 * 企业管理API
 */
export const companyApi = {
  // 获取所有企业列表
  getAll() {
    return request.get('/companies')
  },

  // 根据ID获取企业详情
  getById(id) {
    return request.get(`/companies/${id}`)
  },

  // 搜索企业
  search(keyword) {
    return request.get('/companies/search', {
      params: { keyword }
    })
  },

  // 创建企业
  create(data) {
    return request.post('/companies', data)
  },

  // 更新企业
  update(id, data) {
    return request.put(`/companies/${id}`, data)
  },

  // 删除企业
  delete(id) {
    return request.delete(`/companies/${id}`)
  }
}

