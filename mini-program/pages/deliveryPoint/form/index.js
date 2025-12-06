const deliveryPointApi = require('../../../apis/deliveryPoint.js');
const vehicleApi = require('../../../apis/vehicle.js');

Page({
  data: {
    id: null,
    form: {
      name: '',
      pointType: '',
      address: '',
      vehicleId: null,
      vehiclePlateNumber: '',
      contactPerson: '',
      contactPhone: ''
    },
    types: ['批发市场', '配送中心', '超市卖场便利店', '餐饮店', '商场', '菜场', '其他'],
    typeIndex: 0,
    vehicles: [],
    vehicleIndex: 0
  },
  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadDetail();
    }
    this.loadVehicles();
  },
  async loadDetail() {
    try {
      wx.showLoading({ title: '加载中...' });
      const res = await deliveryPointApi.getById(this.data.id);
      wx.hideLoading();
      if (res.code === 200 && res.data) {
        const data = res.data;
        const typeIndex = this.data.types.indexOf(data.pointType);
        this.setData({
          'form.name': data.name || '',
          'form.pointType': data.pointType || '',
          'form.address': data.address || '',
          'form.vehicleId': data.vehicleId,
          'form.vehiclePlateNumber': data.vehiclePlateNumber || '',
          'form.contactPerson': data.contactPerson || '',
          'form.contactPhone': data.contactPhone || '',
          typeIndex: typeIndex >= 0 ? typeIndex : 0
        });
        if (data.vehicleId) {
          const vehicleIdx = this.data.vehicles.findIndex(v => v.id === data.vehicleId);
          if (vehicleIdx >= 0) {
            this.setData({ vehicleIndex: vehicleIdx });
          }
        }
      }
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.message || '加载失败', icon: 'none' });
    }
  },
  async loadVehicles() {
    try {
      const app = getApp();
      const companyId = app.globalData.companyId;
      if (!companyId) {
        return;
      }
      const res = await vehicleApi.getByCompanyId(companyId);
      if (res.code === 200 && res.data) {
        this.setData({ vehicles: res.data });
        if (this.data.id) {
          this.loadDetail();
        }
      }
    } catch (e) {
      console.error('加载车辆列表失败', e);
    }
  },
  i(e) {
    const k = e.currentTarget.dataset.k;
    this.setData({ [`form.${k}`]: e.detail.value });
  },
  pickType(e) {
    const idx = Number(e.detail.value);
    this.setData({
      typeIndex: idx,
      'form.pointType': this.data.types[idx]
    });
  },
  selectVehicle(e) {
    const idx = Number(e.detail.value);
    if (idx === 0 && this.data.vehicles.length === 0) {
      return;
    }
    if (idx >= 0 && idx < this.data.vehicles.length) {
      const vehicle = this.data.vehicles[idx];
      this.setData({
        vehicleIndex: idx,
        'form.vehicleId': vehicle.id,
        'form.vehiclePlateNumber': vehicle.plateNumber
      });
    } else {
      this.setData({
        vehicleIndex: 0,
        'form.vehicleId': null,
        'form.vehiclePlateNumber': ''
      });
    }
  },
  async submit() {
    const f = this.data.form;
    if (!f.name || !f.pointType) {
      wx.showToast({ title: '请填写网点名称和类型', icon: 'none' });
      return;
    }
    try {
      wx.showLoading({ title: '提交中...' });
      const app = getApp();
      const companyId = app.globalData.companyId;
      
      const data = {
        company: { id: Number(companyId) },
        name: f.name,
        pointType: f.pointType,
        address: f.address || '',
        contactPerson: f.contactPerson || '',
        contactPhone: f.contactPhone || '',
        isActive: true
      };
      
      if (f.vehicleId) {
        data.vehicle = { id: Number(f.vehicleId) };
      }
      
      if (this.data.id) {
        await deliveryPointApi.update(this.data.id, data);
      } else {
        await deliveryPointApi.create(data);
      }
      
      wx.hideLoading();
      wx.showToast({ title: '提交成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 600);
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.message || '提交失败', icon: 'none' });
    }
  }
});

