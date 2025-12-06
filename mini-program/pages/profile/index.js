const { getCurrentUser, updateProfile } = require('../../apis/user.js');

Page({
	data: {
		formData: {
			username: '',
			nickname: '',
			email: '',
			phone: '',
			role: '',
			password: '',
			company: null,
			vehicle: null
		},
		submitting: false
	},
	onLoad() {
		this.loadProfile();
	},
	async loadProfile() {
		try {
			const data = await getCurrentUser();
			this.setData({
				'formData.username': data.username || '',
				'formData.nickname': data.nickname || '',
				'formData.email': data.email || '',
				'formData.phone': data.phone || '',
				'formData.role': data.role || '',
				'formData.password': '',
				'formData.company': data.company || null,
				'formData.vehicle': data.vehicle || null
			});
		} catch (e) {
			wx.showToast({ title: '加载个人信息失败', icon: 'none' });
		}
	},
	onNicknameChange(e) {
		this.setData({ 'formData.nickname': e.detail.value });
	},
	onEmailChange(e) {
		this.setData({ 'formData.email': e.detail.value });
	},
	onPhoneChange(e) {
		this.setData({ 'formData.phone': e.detail.value });
	},
	onPasswordChange(e) {
		this.setData({ 'formData.password': e.detail.value });
	},
	getRoleName(role) {
		const roleMap = {
			ADMIN: '管理员',
			COMPANY: '企业用户',
			DRIVER: '司机用户'
		};
		return roleMap[role] || role;
	},
	async handleSubmit() {
		const { formData } = this.data;
		
		// 验证邮箱格式
		if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
			wx.showToast({ title: '请输入正确的邮箱地址', icon: 'none' });
			return;
		}
		
		// 验证密码长度
		if (formData.password && formData.password.length < 6) {
			wx.showToast({ title: '密码长度至少6位', icon: 'none' });
			return;
		}
		
		this.setData({ submitting: true });
		try {
			const submitData = {
				nickname: formData.nickname,
				email: formData.email,
				phone: formData.phone
			};
			// 只有填写了新密码才提交
			if (formData.password && formData.password.trim()) {
				submitData.password = formData.password;
			}
			
			await updateProfile(submitData);
			wx.showToast({ title: '保存成功', icon: 'success' });
			
			// 更新本地存储的昵称
			if (submitData.nickname) {
				wx.setStorageSync('nickname', submitData.nickname);
			}
			
			// 重置密码字段
			this.setData({ 'formData.password': '' });
		} catch (e) {
			wx.showToast({ title: e.message || '保存失败', icon: 'none' });
		} finally {
			this.setData({ submitting: false });
		}
	},
	handleReset() {
		this.loadProfile();
	}
});

