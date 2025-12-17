import request from './request'

export const analyticsApi = {
  // 企业运输趋势（2022-2025）
  getCompanyTransportTrends(params) {
    return request.get('/analytics/company/transport-trends', { params })
  },

  // 企业内车辆年度运营统计
  getVehicleYearStats(params) {
    return request.get('/analytics/company/vehicle-year-stats', { params })
  }
}


