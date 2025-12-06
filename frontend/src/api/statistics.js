import request from './request'

/**
 * 统计信息API
 */
export const statisticsApi = {
  // 获取系统统计数据
  getStatistics() {
    return request.get('/statistics')
  }
}

