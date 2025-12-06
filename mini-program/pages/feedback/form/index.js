const api = require('../../../apis/feedback.js');
const companyApi = require('../../../apis/company.js');
const vehicleApi = require('../../../apis/vehicle.js');

Page({
	data: { 
		form: { status: '待处理' },
		role: '',
		isAdmin: false,
		statusIndex: 0,
		companies: [],
		companyIndex: -1,
		companyName: '',
		vehicles: [],
		vehicleIndex: -1,
		vehicleName: ''
	},
	onLoad() {
		const role = wx.getStorageSync('role') || '';
		this.setData({ 
			role,
			isAdmin: role === 'ADMIN'
		});
		this.loadCompanies();
	},
	async loadCompanies() {
		try {
			const companies = await companyApi.list();
			this.setData({ companies: companies || [] });
		} catch (e) {
			wx.showToast({ title: '加载企业列表失败', icon: 'none' });
		}
	},
	async loadVehicles(companyId) {
		try {
			const vehicles = await vehicleApi.listByCompany(companyId);
			this.setData({ vehicles: vehicles || [] });
		} catch (e) {
			wx.showToast({ title: '加载车辆列表失败', icon: 'none' });
		}
	},
	onCompanyChange(e) {
		const index = parseInt(e.detail.value);
		const company = this.data.companies[index];
		if (company) {
			this.setData({ 
				'form.companyId': company.id,
				companyIndex: index,
				companyName: company.name,
				'form.vehicleId': null,
				vehicleIndex: -1,
				vehicleName: ''
			});
			// 加载该企业的车辆列表
			this.loadVehicles(company.id);
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
	onStatusChange(e) {
		const statuses = ['待处理', '处理中', '已处理', '已关闭'];
		const index = parseInt(e.detail.value);
		this.setData({ 
			'form.status': statuses[index],
			statusIndex: index
		});
	},
	i(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
	},
	async submit() {
		const f = this.data.form;
		if (!f.companyId || !f.reportYear || !f.reportMonth) {
			wx.showToast({ title: '请选择企业并填写年月', icon: 'none' });
			return;
		}
		// 非管理员用户不提交政策建议、状态、回复字段
		if (!this.data.isAdmin) {
			delete f.policySuggestions;
			delete f.status;
			delete f.response;
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


