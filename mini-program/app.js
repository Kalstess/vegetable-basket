App({
	onLaunch() {
		const token = wx.getStorageSync('token') || '';
		const role = wx.getStorageSync('role') || '';
		const companyId = wx.getStorageSync('companyId') || null;
		const vehicleId = wx.getStorageSync('vehicleId') || null;
		this.globalData.token = token;
		this.globalData.role = role;
		this.globalData.companyId = companyId;
		this.globalData.vehicleId = vehicleId;
	},
	globalData: {
		token: '',
		user: null,
		role: '',
		companyId: null,
		vehicleId: null
	}
});


