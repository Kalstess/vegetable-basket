const api = require('../../../apis/transport.js');
const vehicleApi = require('../../../apis/vehicle.js');

Page({
	data: {
		form: {},
		vehicles: [],
		vehicleDisplayNames: [],
		vehicleIndex: -1,
		vehicleName: '',
		types: ['蔬菜','肉类','禽蛋','水产','水果','饮料','炒货','其他'],
		checkedTypes: {}
	},
	onLoad() {
		this.loadVehicles();
	},
	async loadVehicles() {
		try {
			const vehicles = await vehicleApi.list();
			const vehicleDisplayNames = vehicles.map(v => 
				`${v.plateNumber}${v.companyName ? ' - ' + v.companyName : ''}`
			);
			this.setData({ 
				vehicles: vehicles || [],
				vehicleDisplayNames: vehicleDisplayNames || []
			});
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
				vehicleName: this.data.vehicleDisplayNames[index]
			});
		}
	},
	i(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
	},
	onTypes(e) {
		const sel = e.detail.value || [];
		const map = {};
		sel.forEach(v => map[v] = true);
		this.setData({ checkedTypes: map, form: { ...this.data.form, productTypes: sel } });
	},
	async submit() {
		const f = this.data.form;
		if (!f.vehicleId || !f.statYear || !f.statMonth) {
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


