const { getStatistics } = require('../../apis/statistics.js');

Page({
	data: { 
		stats: {},
		role: '',
		nickname: ''
	},
	onShow() { 
		const role = wx.getStorageSync('role') || '';
		const nickname = wx.getStorageSync('nickname') || wx.getStorageSync('username') || '用户';
		this.setData({ role, nickname });
		this.fetchStats(); 
	},
	async fetchStats() {
		try {
			const s = await getStatistics();
			this.setData({ stats: s || {} });
		} catch (e) {}
	},
	// 判断是否为管理员
	isAdmin() {
		return this.data.role === 'ADMIN';
	},
	// 判断是否为企业用户
	isCompany() {
		return this.data.role === 'COMPANY';
	},
	// 判断是否为司机用户
	isDriver() {
		return this.data.role === 'DRIVER';
	},
	goCompany() { wx.navigateTo({ url: '/pages/company/form/index' }); },
	goVehicleList() { wx.navigateTo({ url: '/pages/vehicle/list/index' }); },
	goVehicleStatistics() { wx.navigateTo({ url: '/pages/vehicleStatistics/index' }); },
	goTransport() { wx.navigateTo({ url: '/pages/transport/form/index' }); },
	goDeliveryPoint() { wx.navigateTo({ url: '/pages/deliveryPoint/list/index' }); },
	goRoute() { wx.navigateTo({ url: '/pages/route/form/index' }); },
	goMaintenance() { wx.navigateTo({ url: '/pages/maintenance/form/index' }); },
	goCompliance() { wx.navigateTo({ url: '/pages/compliance/form/index' }); },
	goFeedback() { wx.navigateTo({ url: '/pages/feedback/form/index' }); },
	goProfile() { wx.navigateTo({ url: '/pages/profile/index' }); },
	goUsers() { wx.navigateTo({ url: '/pages/users/index' }); },
	onLogout() {
		wx.showModal({
			title: '提示',
			content: '确定要退出登录吗？',
			success: (res) => {
				if (res.confirm) {
					wx.removeStorageSync('token');
					wx.removeStorageSync('username');
					wx.removeStorageSync('nickname');
					wx.removeStorageSync('role');
					wx.removeStorageSync('companyId');
					wx.removeStorageSync('vehicleId');
					const app = getApp();
					app.globalData.token = '';
					app.globalData.role = '';
					app.globalData.user = null;
					app.globalData.companyId = null;
					app.globalData.vehicleId = null;
					wx.reLaunch({ url: '/pages/login/index' });
				}
			}
		});
	}
});


