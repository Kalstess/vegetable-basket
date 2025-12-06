const api = require('../../../apis/vehicle.js');

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
	goDetail(e) {
		const id = e.currentTarget.dataset.id;
		wx.navigateTo({ url: `/pages/vehicle/form/index?id=${id}` });
	},
	goCreate() {
		wx.navigateTo({ url: '/pages/vehicle/form/index' });
	}
});


