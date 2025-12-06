const api = require('../../../apis/company.js');

Page({
	data: {
		form: {},
		companyTypes: ['国有','民营','其他'],
		typeIndex: -1
	},
	i(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
	},
	d(e) {
		const k = e.currentTarget.dataset.k;
		this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
	},
	onTypePick(e) {
		const idx = Number(e.detail.value);
		this.setData({
			typeIndex: idx,
			form: { ...this.data.form, companyType: this.data.companyTypes[idx] }
		});
	},
	async submit() {
		if (!this.data.form.name) {
			wx.showToast({ title: '请输入企业名称', icon: 'none' });
			return;
		}
		try {
			if (this.data.form.id) {
				await api.update(this.data.form.id, this.data.form);
			} else {
				const saved = await api.create(this.data.form);
				this.setData({ form: saved });
			}
			wx.showToast({ title: '提交成功', icon: 'success' });
			setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
		} catch (e) {
			wx.showToast({ title: e.message || '提交失败', icon: 'none' });
		}
	}
});


