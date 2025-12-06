const api = require('../../../apis/vehicle.js');
const companyApi = require('../../../apis/company.js');
const { chooseImageBase64 } = require('../../../utils/image.js');
const reportApi = require('../../../apis/report.js');

Page({
	data: {
		id: null,
		form: {},
		companies: [],
		companyIndex: -1,
		companyName: '',
		vehicleCategories: ['菜篮子工程车','非菜篮子工程车'],
		vehicleTypes: ['普通','冷藏'],
		plateColors: ['蓝牌','黄牌'],
		vcIdx: -1,
		vtIdx: -1,
		pcIdx: -1,
		dlPreview: ''
	},
	onLoad(q) {
		this.loadCompanies();
		if (q && q.id) {
			this.setData({ id: Number(q.id) });
			this.fetch(Number(q.id));
		}
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
				'form.companyId': company.id,
				companyIndex: index,
				companyName: company.name
			});
		}
	},
	async fetch(id) {
		try {
			const data = await api.get(id);
			const companies = this.data.companies;
			let companyIndex = -1;
			let companyName = '';
			if (data.companyId) {
				companyIndex = companies.findIndex(c => c.id === data.companyId);
				if (companyIndex >= 0) {
					companyName = companies[companyIndex].name;
				}
			}
			this.setData({
				form: data,
				companyIndex,
				companyName,
				vcIdx: this.data.vehicleCategories.indexOf(data.vehicleCategory || ''),
				vtIdx: this.data.vehicleTypes.indexOf(data.vehicleType || ''),
				pcIdx: this.data.plateColors.indexOf(data.colorPlate || '')
			});
		} catch (e) {
			wx.showToast({ title: e.message || '加载失败', icon: 'none' });
		}
	},
	async pickDL() {
		try {
			const { base64, path } = await chooseImageBase64();
			this.setData({
				dlPreview: path,
				form: { ...this.data.form, drivingLicenseData: this.data.form.drivingLicenseData || '' }
			});
			// 作为补充：把图片以base64写入报送记录，便于后台查看原件
			try {
				const app = getApp();
				const companyId = this.data.form.companyId || app.globalData.companyId;
				if (companyId) {
					await reportApi.create({
						company: { id: Number(companyId) },
						submittedBy: (app.globalData.user && app.globalData.user.username) || 'miniapp',
						submitType: '车辆信息',
						rawPayload: JSON.stringify({ type: '驾驶证图片', base64, plateNumber: this.data.form.plateNumber || '' })
					});
				}
			} catch (_) {}
		} catch (e) {
			wx.showToast({ title: '图片选择失败', icon: 'none' });
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
	pickVC(e) {
		const idx = Number(e.detail.value);
		this.setData({ vcIdx: idx, form: { ...this.data.form, vehicleCategory: this.data.vehicleCategories[idx] } });
	},
	pickVT(e) {
		const idx = Number(e.detail.value);
		this.setData({ vtIdx: idx, form: { ...this.data.form, vehicleType: this.data.vehicleTypes[idx] } });
	},
	pickPC(e) {
		const idx = Number(e.detail.value);
		this.setData({ pcIdx: idx, form: { ...this.data.form, colorPlate: this.data.plateColors[idx] } });
	},
	async submit() {
		const form = { ...this.data.form };
		// DTO 需要 companyId -> company 对象或字段，后端VehicleDTO接收companyId字段
		if (!form.companyId || !form.plateNumber) {
			wx.showToast({ title: '请选择企业并填写车牌号', icon: 'none' });
			return;
		}
		try {
			if (this.data.id) {
				await api.update(this.data.id, form);
			} else {
				const saved = await api.create(form);
				this.setData({ id: saved.id, form: saved });
			}
			wx.showToast({ title: '保存成功', icon: 'success' });
			setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
		} catch (e) {
			wx.showToast({ title: e.message || '保存失败', icon: 'none' });
		}
	}
});


