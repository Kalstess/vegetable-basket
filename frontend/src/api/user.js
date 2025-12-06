import request from './request'

export const userApi = {
  // 获取所有用户（仅管理员）
  getAll() {
    return request.get('/users')
  },

  // 根据ID获取用户
  getById(id) {
    return request.get(`/users/${id}`)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return request.get('/users/profile')
  },

  // 创建用户（仅管理员）
  create(data) {
    return request.post('/users', data)
  },

  // 更新用户（管理员可更新所有，普通用户只能更新自己）
  update(id, data) {
    return request.put(`/users/${id}`, data)
  },

  // 更新当前用户个人信息
  updateProfile(data) {
    return request.put('/users/profile', data)
  },

  // 删除用户（仅管理员）
  delete(id) {
    return request.delete(`/users/${id}`)
  }
}

