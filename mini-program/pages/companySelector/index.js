const api = require('../../apis/company.js');

Page({
	data: { items: [] },
	onShow() { this.fetch(); },
	async fetch() {
		try {
			const list = await api.list();
			this.setData({ items: list || [] });
		} catch (e) {
			wx.showToast({ title: e.message || '加载失败', icon: 'none' });
		}
	},
	pick(e) {
		const item = e.currentTarget.dataset.item;
		const app = getApp();
		app.globalData.companyId = item.id;
		wx.showToast({ title: '选择成功', icon: 'success' });
		setTimeout(() => wx.navigateBack({ delta: 1 }), 300);
	}
});


