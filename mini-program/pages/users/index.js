const { getAll, create, update, delete: deleteUser } = require('../../apis/user.js');
const { getAll: getAllCompanies } = require('../../apis/company.js');
const { getAll: getAllVehicles } = require('../../apis/vehicle.js');

Page({
	data: {
		users: [],
		companies: [],
		vehicles: [],
		loading: false,
		dialogVisible: false,
		isEdit: false,
		currentUser: null,
		companyIndex: -1,
		companyName: '',
		vehicleIndex: -1,
		vehicleName: '',
		formData: {
			username: '',
			password: '',
			nickname: '',
			email: '',
			phone: '',
			role: 'COMPANY',
			companyId: null,
			vehicleId: null,
			isActive: true
		}
	},
	onLoad() {
		// 检查权限
		const role = wx.getStorageSync('role');
		if (role !== 'ADMIN') {
			wx.showToast({ title: '无权限访问', icon: 'none' });
			setTimeout(() => {
				wx.navigateBack();
			}, 1500);
			return;
		}
		this.loadData();
		this.loadCompanies();
		this.loadVehicles();
	},
	async loadData() {
		this.setData({ loading: true });
		try {
			const users = await getAll();
			this.setData({ users: users || [] });
		} catch (e) {
			wx.showToast({ title: e.message || '加载失败', icon: 'none' });
		} finally {
			this.setData({ loading: false });
		}
	},
	async loadCompanies() {
		try {
			const companies = await getAllCompanies();
			this.setData({ companies: companies || [] });
		} catch (e) {}
	},
	async loadVehicles() {
		try {
			const vehicles = await getAllVehicles();
			this.setData({ vehicles: vehicles || [] });
		} catch (e) {}
	},
	getRoleName(role) {
		const roleMap = {
			ADMIN: '管理员',
			COMPANY: '企业用户',
			DRIVER: '司机用户'
		};
		return roleMap[role] || role;
	},
	handleAdd() {
		this.setData({
			dialogVisible: true,
			isEdit: false,
			currentUser: null,
			companyIndex: -1,
			companyName: '',
			vehicleIndex: -1,
			vehicleName: '',
			formData: {
				username: '',
				password: '',
				nickname: '',
				email: '',
				phone: '',
				role: 'COMPANY',
				companyId: null,
				vehicleId: null,
				isActive: true
			}
		});
	},
	handleEdit(e) {
		const user = e.currentTarget.dataset.user;
		const companies = this.data.companies;
		const vehicles = this.data.vehicles;
		let companyIndex = -1;
		let companyName = '';
		let vehicleIndex = -1;
		let vehicleName = '';
		
		if (user.company) {
			companyIndex = companies.findIndex(c => c.id === user.company.id);
			companyName = user.company.name || '';
		}
		if (user.vehicle) {
			vehicleIndex = vehicles.findIndex(v => v.id === user.vehicle.id);
			vehicleName = user.vehicle.plateNumber || '';
		}
		
		this.setData({
			dialogVisible: true,
			isEdit: true,
			currentUser: user,
			companyIndex,
			companyName,
			vehicleIndex,
			vehicleName,
			formData: {
				username: user.username,
				password: '',
				nickname: user.nickname || '',
				email: user.email || '',
				phone: user.phone || '',
				role: user.role,
				companyId: user.company?.id || null,
				vehicleId: user.vehicle?.id || null,
				isActive: user.isActive !== false
			}
		});
	},
	handleDelete(e) {
		const user = e.currentTarget.dataset.user;
		wx.showModal({
			title: '确认删除',
			content: `确定要删除用户"${user.username}"吗？`,
			success: async (res) => {
				if (res.confirm) {
					try {
						await deleteUser(user.id);
						wx.showToast({ title: '删除成功', icon: 'success' });
						this.loadData();
					} catch (e) {
						wx.showToast({ title: e.message || '删除失败', icon: 'none' });
					}
				}
			}
		});
	},
	onFormFieldChange(e) {
		const field = e.currentTarget.dataset.field;
		const value = e.detail.value;
		this.setData({ [`formData.${field}`]: value });
	},
	onRoleChange(e) {
		const index = parseInt(e.detail.value);
		const roles = ['ADMIN', 'COMPANY', 'DRIVER'];
		const role = roles[index];
		this.setData({
			'formData.role': role,
			'formData.companyId': null,
			'formData.vehicleId': null,
			companyIndex: -1,
			companyName: '',
			vehicleIndex: -1,
			vehicleName: ''
		});
		if (role === 'DRIVER') {
			this.loadVehicles();
		}
	},
	onCompanyChange(e) {
		const index = parseInt(e.detail.value);
		const company = this.data.companies[index];
		if (company) {
			this.setData({ 
				'formData.companyId': company.id,
				companyIndex: index,
				companyName: company.name
			});
		}
	},
	onVehicleChange(e) {
		const index = parseInt(e.detail.value);
		const vehicle = this.data.vehicles[index];
		if (vehicle) {
			this.setData({ 
				'formData.vehicleId': vehicle.id,
				vehicleIndex: index,
				vehicleName: vehicle.plateNumber
			});
		}
	},
	onSwitchChange(e) {
		this.setData({ 'formData.isActive': e.detail.value });
	},
	async handleSubmit() {
		const { formData, isEdit } = this.data;
		
		// 验证
		if (!formData.username) {
			wx.showToast({ title: '请输入用户名', icon: 'none' });
			return;
		}
		if (!isEdit && !formData.password) {
			wx.showToast({ title: '请输入密码', icon: 'none' });
			return;
		}
		if (formData.password && formData.password.length < 6) {
			wx.showToast({ title: '密码长度至少6位', icon: 'none' });
			return;
		}
		if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
			wx.showToast({ title: '请输入正确的邮箱地址', icon: 'none' });
			return;
		}
		if (formData.role === 'COMPANY' && !formData.companyId) {
			wx.showToast({ title: '企业用户必须选择关联企业', icon: 'none' });
			return;
		}
		if (formData.role === 'DRIVER' && !formData.vehicleId) {
			wx.showToast({ title: '司机用户必须选择关联车辆', icon: 'none' });
			return;
		}
		
		try {
			const submitData = {
				username: formData.username,
				nickname: formData.nickname,
				email: formData.email,
				phone: formData.phone,
				role: formData.role,
				isActive: formData.isActive
			};
			
			if (formData.password) {
				submitData.password = formData.password;
			}
			
			if (formData.role === 'COMPANY' && formData.companyId) {
				submitData.companyId = formData.companyId;
			}
			if (formData.role === 'DRIVER' && formData.vehicleId) {
				submitData.vehicleId = formData.vehicleId;
			}
			
			if (isEdit) {
				await update(this.data.currentUser.id, submitData);
				wx.showToast({ title: '更新成功', icon: 'success' });
			} else {
				await create(submitData);
				wx.showToast({ title: '创建成功', icon: 'success' });
			}
			
			this.setData({ dialogVisible: false });
			this.loadData();
		} catch (e) {
			wx.showToast({ title: e.message || '操作失败', icon: 'none' });
		}
	},
	closeDialog() {
		this.setData({ dialogVisible: false });
	}
});

