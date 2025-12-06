const { login } = require('../../apis/auth.js');

Page({
	data: {
		username: '',
		password: ''
	},
	onUserChange(e) {
		this.setData({ username: e.detail.value });
	},
	onPassChange(e) {
		this.setData({ password: e.detail.value });
	},
	async onLogin() {
		if (!this.data.username || !this.data.password) {
			wx.showToast({ title: '请输入账号和密码', icon: 'none' });
			return;
		}
		if (this.data.password.length < 6) {
			wx.showToast({ title: '密码长度至少6位', icon: 'none' });
			return;
		}
		try {
			const res = await login({ username: this.data.username, password: this.data.password });
			wx.setStorageSync('token', res.token);
			wx.setStorageSync('username', res.username);
			wx.setStorageSync('nickname', res.nickname || res.username);
			wx.setStorageSync('role', res.role);
			const app = getApp();
			app.globalData.token = res.token;
			app.globalData.user = res;
			app.globalData.role = res.role;
			
			// 登录后获取用户完整信息（包括 companyId）
			try {
				const userApi = require('../../apis/user.js');
				const userInfo = await userApi.getCurrentUser();
				if (userInfo && userInfo.company) {
					app.globalData.companyId = userInfo.company.id;
					wx.setStorageSync('companyId', userInfo.company.id);
				}
				if (userInfo && userInfo.vehicle) {
					app.globalData.vehicleId = userInfo.vehicle.id;
					wx.setStorageSync('vehicleId', userInfo.vehicle.id);
				}
			} catch (e) {
				console.warn('获取用户信息失败:', e);
			}
			
			wx.reLaunch({ url: '/pages/home/index' });
		} catch (e) {
			wx.showToast({ title: e.message || '登录失败', icon: 'none' });
		}
	}
});


