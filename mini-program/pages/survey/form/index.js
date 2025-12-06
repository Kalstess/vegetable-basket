const api = require('../../../apis/survey.js');
const companyApi = require('../../../apis/company.js');
const app = getApp();

Page({
	data: {
		form: {},
		companies: [],
		companyDisplayNames: [],
		companyIndex: -1,
		companyName: '',
		currentStep: 0,
		totalSteps: 6,
		// 选项数据
		deliveryCustomerTypes: ['电商平台', '连锁商超', '餐饮及中央厨房', '批发市场', '食品加工与生产企业', '食堂学校机关企业', '个人团购自提点等', '其他'],
		checkedCustomerTypes: {},
		standardEquipmentTypes: ['标准周转箱', '标准周转筐', '标准托盘', '标准笼车', '没有使用标准化载具', '其他'],
		checkedEquipmentTypes: {},
		gpsPlatformTypes: ['全国道路货运车辆公共监管与服务平台简称全国货运平台', '上海主副食品市场运行调控系统', '企业自建或第三方车队管理系统TMS', '甲方货主或物流平台的运力协同系统', '上海交通局执法支队监管平台', '未接入任何平台', '其他'],
		checkedGpsPlatformTypes: {},
		operationalProblems: [
			{ type: '企业经营下滑无法重新更换新车', order: 1 },
			{ type: '高峰时段限行无法进城', order: 2 },
			{ type: '限行区外缺少合法卸货点', order: 3 },
			{ type: '社区商超地下停车场限高禁入', order: 4 },
			{ type: '卸货车位被社会车占用', order: 5 },
			{ type: '交警城管处罚频繁', order: 6 },
			{ type: '新能源续航不足冬季掉电', order: 7 },
			{ type: '新能源充电桩少', order: 8 },
			{ type: '冷藏车购置维护成本高', order: 9 },
			{ type: '司机短缺工资上涨', order: 10 },
			{ type: '客户账期长现金流压力', order: 11 },
			{ type: '回程空驶率高', order: 12 },
			{ type: '无法进行尾板箱体分割等改装', order: 13 },
			{ type: '其他', order: 14 }
		]
	},
	onLoad(options) {
		this.loadCompanies();
		if (options.id) {
			this.loadData(options.id);
		} else {
			this.initForm();
		}
	},
	initForm() {
		this.setData({
			form: {
				surveyYear: new Date().getFullYear(),
				deliveryCustomerTypes: [],
				annualTransport2022: null,
				annualTransport2023: null,
				annualTransport2024: null,
				annualTransport2025: null,
				futureVehiclePlan: null,
				revenue2025: null,
				regularBasketVehicles2025: 0,
				coldBasketVehicles2025: 0,
				regularOtherVehicles2025: 0,
				coldOtherVehicles2025: 0,
				basketTripComparison: null,
				mainProductType: null,
				secondaryProductType: null,
				standardEquipmentTypes: [],
				gpsSystemType: null,
				gpsPlatformTypes: [],
				hasColdBasketVehicle: false,
				coldVehicleMultiTemp: null,
				tempRecordingDevice: null,
				avgLoadingRate: null,
				basketVehicleImportance: null,
				otherImportance: null,
				operationalProblems: this.data.operationalProblems
			}
		});
	},
	async loadCompanies() {
		try {
			const companies = await companyApi.list();
			const companyDisplayNames = companies.map(c => c.name);
			this.setData({ 
				companies: companies || [],
				companyDisplayNames: companyDisplayNames || []
			});
		} catch (e) {
			wx.showToast({ title: '加载企业列表失败', icon: 'none' });
		}
	},
	async loadData(id) {
		try {
			const data = await api.getById(id);
			this.setData({
				form: data,
				companyName: data.companyName,
				'form.companyId': data.companyId
			});
			// 设置多选状态
			if (data.deliveryCustomerTypes) {
				const map = {};
				data.deliveryCustomerTypes.forEach(v => map[v] = true);
				this.setData({ checkedCustomerTypes: map });
			}
			if (data.standardEquipmentTypes) {
				const map = {};
				data.standardEquipmentTypes.forEach(v => map[v] = true);
				this.setData({ checkedEquipmentTypes: map });
			}
			if (data.gpsPlatformTypes) {
				const map = {};
				data.gpsPlatformTypes.forEach(v => map[v] = true);
				this.setData({ checkedGpsPlatformTypes: map });
			}
		} catch (e) {
			wx.showToast({ title: '加载数据失败', icon: 'none' });
		}
	},
	onCompanyChange(e) {
		const index = parseInt(e.detail.value);
		const company = this.data.companies[index];
		if (company) {
			this.setData({ 
				'form.companyId': company.id,
				companyIndex: index,
				companyName: this.data.companyDisplayNames[index]
			});
		}
	},
	i(e) {
		const k = e.currentTarget.dataset.k;
		const value = e.detail.value;
		this.setData({ [`form.${k}`]: value });
	},
	onCustomerTypesChange(e) {
		const sel = e.detail.value || [];
		if (sel.length > 3) {
			wx.showToast({ title: '最多选择3项', icon: 'none' });
			return;
		}
		const map = {};
		sel.forEach(v => map[v] = true);
		this.setData({ checkedCustomerTypes: map, 'form.deliveryCustomerTypes': sel });
	},
	onEquipmentTypesChange(e) {
		const sel = e.detail.value || [];
		const map = {};
		sel.forEach(v => map[v] = true);
		this.setData({ checkedEquipmentTypes: map, 'form.standardEquipmentTypes': sel });
	},
	onGpsPlatformTypesChange(e) {
		const sel = e.detail.value || [];
		const map = {};
		sel.forEach(v => map[v] = true);
		this.setData({ checkedGpsPlatformTypes: map, 'form.gpsPlatformTypes': sel });
	},
	onSwitchChange(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ [`form.${k}`]: e.detail.value });
	},
	nextStep() {
		if (this.data.currentStep < this.data.totalSteps - 1) {
			this.setData({ currentStep: this.data.currentStep + 1 });
		}
	},
	prevStep() {
		if (this.data.currentStep > 0) {
			this.setData({ currentStep: this.data.currentStep - 1 });
		}
	},
	async save() {
		const f = this.data.form;
		if (!f.companyId || !f.surveyYear) {
			wx.showToast({ title: '请选择企业和填写年份', icon: 'none' });
			return;
		}
		try {
			// 处理运营问题排序
			const problems = this.data.operationalProblems.map((p, index) => ({
				problemType: p.type,
				sortOrder: index + 1,
				otherDescription: p.type === '其他' ? f.otherProblemDescription : null
			}));
			f.operationalProblems = problems;
			
			await api.createOrUpdate(f);
			wx.showToast({ title: '保存成功', icon: 'success' });
			setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
		} catch (e) {
			wx.showToast({ title: e.message || '保存失败', icon: 'none' });
		}
	},
	async submit() {
		const f = this.data.form;
		if (!f.companyId || !f.surveyYear) {
			wx.showToast({ title: '请选择企业和填写年份', icon: 'none' });
			return;
		}
		try {
			// 先保存
			await this.save();
			// 再提交
			if (f.id) {
				await api.submit(f.id);
				wx.showToast({ title: '提交成功', icon: 'success' });
				setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
			}
		} catch (e) {
			wx.showToast({ title: e.message || '提交失败', icon: 'none' });
		}
	},
	moveProblemUp(e) {
		const index = parseInt(e.currentTarget.dataset.index);
		if (index > 0) {
			const problems = this.data.operationalProblems;
			const temp = problems[index];
			problems[index] = problems[index - 1];
			problems[index - 1] = temp;
			problems.forEach((p, i) => p.order = i + 1);
			this.setData({ operationalProblems: problems });
		}
	},
	moveProblemDown(e) {
		const index = parseInt(e.currentTarget.dataset.index);
		if (index < this.data.operationalProblems.length - 1) {
			const problems = this.data.operationalProblems;
			const temp = problems[index];
			problems[index] = problems[index + 1];
			problems[index + 1] = temp;
			problems.forEach((p, i) => p.order = i + 1);
			this.setData({ operationalProblems: problems });
		}
	}
});

