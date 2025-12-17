import request from './request'
import publicRequest from './publicRequest'

/**
 * 企业自助注册与审批 API
 */
export const companyRegistrationApi = {
  // 企业自助注册（公开接口，不需要token）
  register(data) {
    return publicRequest.post('/company-registration/register', data)
  },

  // 商务委审批通过
  approve(companyId, remark, existingCompanyId) {
    const params = { remark: remark || '' }
    if (existingCompanyId) {
      params.existingCompanyId = existingCompanyId
    }
    return request.post(`/company-registration/${companyId}/approve`, null, { params })
  },

  // 商务委审批驳回
  reject(companyId, remark) {
    return request.post(`/company-registration/${companyId}/reject`, null, {
      params: { remark: remark || '' }
    })
  }
}


