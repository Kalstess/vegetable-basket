const vehicleStatisticsApi = require('../../apis/vehicleStatistics.js');
const companyApi = require('../../apis/company.js');

Page({
  data: {
    list: [],
    loading: false,
    companies: [],
    companyIndex: -1,
    companyName: '',
    companyId: null,
    statDate: ''
  },
  onLoad() {
    const today = new Date();
    const dateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
    this.setData({ statDate: dateStr });
    this.loadCompanies();
    this.loadList();
  },
  onShow() {
    this.loadList();
  },
  async loadCompanies() {
    try {
      const companies = await companyApi.list();
      this.setData({ companies: companies || [] });
    } catch (e) {
      wx.showToast({ title: '加载企业列表失败', icon: 'none' });
    }
  },
  onCompanyChange(e) {
    const index = parseInt(e.detail.value);
    const company = this.data.companies[index];
    if (company) {
      this.setData({ 
        companyId: company.id,
        companyIndex: index,
        companyName: company.name
      });
    }
  },
  onDateChange(e) {
    this.setData({ statDate: e.detail.value });
  },
  async generate() {
    if (!this.data.companyId) {
      wx.showToast({ title: '请选择企业', icon: 'none' });
      return;
    }
    if (!this.data.statDate) {
      wx.showToast({ title: '请选择统计日期', icon: 'none' });
      return;
    }
    try {
      wx.showLoading({ title: '生成中...' });
      await vehicleStatisticsApi.generate(this.data.companyId, this.data.statDate);
      wx.hideLoading();
      wx.showToast({ title: '生成成功', icon: 'success' });
      this.loadList();
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.message || '生成失败', icon: 'none' });
    }
  },
  async loadList() {
    this.setData({ loading: true });
    try {
      // 车辆统计是管理员功能，后端会自动返回所有企业的统计
      const res = await vehicleStatisticsApi.getAll();
      this.setData({ list: Array.isArray(res) ? res : [] });
    } catch (e) {
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },
  async delete(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条统计记录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '删除中...' });
            await vehicleStatisticsApi.delete(id);
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

