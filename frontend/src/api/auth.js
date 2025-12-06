import request from './request'

/**
 * 认证API
 */
export const authApi = {
  // 用户登录
  login(username, password) {
    return request.post('/auth/login', {
      username,
      password
    })
  },

  // 验证token
  validateToken(token) {
    return request.get('/auth/validate', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  }
}

