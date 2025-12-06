const api = require('../../../apis/compliance.js');
const vehicleApi = require('../../../apis/vehicle.js');

Page({
	data: { 
		form: {},
		vehicles: [],
		vehicleIndex: -1,
		vehicleName: ''
	},
	onLoad() {
		this.loadVehicles();
	},
	async loadVehicles() {
		try {
			const vehicles = await vehicleApi.list();
			this.setData({ vehicles: vehicles || [] });
		} catch (e) {
			wx.showToast({ title: '加载车辆列表失败', icon: 'none' });
		}
	},
	onVehicleChange(e) {
		const index = parseInt(e.detail.value);
		const vehicle = this.data.vehicles[index];
		if (vehicle) {
			this.setData({ 
				'form.vehicleId': vehicle.id,
				vehicleIndex: index,
				vehicleName: vehicle.plateNumber
			});
		}
	},
	i(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
	},
	onSwitch(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
	},
	async submit() {
		const f = this.data.form;
		if (!f.vehicleId || !f.reportYear || !f.reportMonth) {
			wx.showToast({ title: '请选择车辆并填写年月', icon: 'none' });
			return;
		}
		try {
			await api.create(f);
			wx.showToast({ title: '提交成功', icon: 'success' });
			setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
		} catch (e) {
			wx.showToast({ title: e.message || '提交失败', icon: 'none' });
		}
	}
});


