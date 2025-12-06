const deliveryPointApi = require('../../../apis/deliveryPoint.js');

Page({
  data: {
    list: []
  },
  onLoad() {
    this.loadList();
  },
  onShow() {
    this.loadList();
  },
	async loadList() {
		try {
			const app = getApp();
			let companyId = app.globalData.companyId;
			
			// 如果没有 companyId，尝试从用户信息中获取
			if (!companyId) {
				try {
					const userApi = require('../../../apis/user.js');
					const userInfo = await userApi.getCurrentUser();
					if (userInfo && userInfo.company) {
						companyId = userInfo.company.id;
						app.globalData.companyId = companyId;
						wx.setStorageSync('companyId', companyId);
					}
				} catch (e) {
					console.warn('获取用户信息失败:', e);
				}
			}
			
			// 管理员可以查看所有，企业用户和司机用户需要 companyId
			const role = wx.getStorageSync('role') || '';
			if (role !== 'ADMIN' && !companyId) {
				wx.showToast({ title: '无法获取企业信息', icon: 'none' });
				setTimeout(() => wx.navigateBack(), 1500);
				return;
			}
			
			wx.showLoading({ title: '加载中...' });
			let res;
			if (role === 'ADMIN') {
				// 管理员查看所有配送网点
				res = await deliveryPointApi.getAll();
			} else {
				// 企业用户和司机用户查看自己企业的配送网点
				res = await deliveryPointApi.getByCompanyId(companyId);
			}
			wx.hideLoading();
			if (res) {
				this.setData({ list: Array.isArray(res) ? res : [] });
			}
		} catch (e) {
			wx.hideLoading();
			wx.showToast({ title: e.message || '加载失败', icon: 'none' });
		}
	},
  addNew() {
    wx.navigateTo({ url: '/pages/deliveryPoint/form/index' });
  },
  edit(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/deliveryPoint/form/index?id=${id}` });
  },
  async delete(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个配送网点吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '删除中...' });
            await deliveryPointApi.delete(id);
            wx.hideLoading();
            wx.showToast({ title: '删除成功', icon: 'success' });
            this.loadList();
          } catch (e) {
            wx.hideLoading();
            wx.showToast({ title: e.message || '删除失败', icon: 'none' });
          }
        }
      }
    });
  }
});

