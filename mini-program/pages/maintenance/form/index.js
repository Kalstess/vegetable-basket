const api = require('../../../apis/maintenance.js');
const vehicleApi = require('../../../apis/vehicle.js');

Page({
	data: {
		form: {},
		vehicles: [],
		vehicleIndex: -1,
		vehicleName: '',
		types: ['日常保养','定期维护','故障维修','年度检测','其他'],
		tIdx: -1
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
	d(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
	},
	pick(e) {
		const idx = Number(e.detail.value);
		this.setData({ tIdx: idx, form: { ...this.data.form, maintType: this.data.types[idx] } });
	},
	async submit() {
		const f = this.data.form;
		if (!f.vehicleId || !f.maintDate) {
			wx.showToast({ title: '请选择车辆并填写日期', icon: 'none' });
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


