import request from './request'

export const productTypeApi = {
  // 获取所有产品类型
  getAll() {
    return request.get('/product-types')
  },
  
  // 获取所有启用的产品类型
  getActive() {
    return request.get('/product-types/active')
  },
  
  // 根据ID获取产品类型
  getById(id) {
    return request.get(`/product-types/${id}`)
  },
  
  // 根据类型代码获取产品类型
  getByCode(typeCode) {
    return request.get(`/product-types/code/${typeCode}`)
  },
  
  // 创建产品类型
  create(data) {
    return request.post('/product-types', data)
  },
  
  // 更新产品类型
  update(id, data) {
    return request.put(`/product-types/${id}`, data)
  },
  
  // 删除产品类型
  delete(id) {
    return request.delete(`/product-types/${id}`)
  }
}

