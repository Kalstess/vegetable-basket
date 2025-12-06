const routePointApi = require('../../../apis/routePoint.js');
const vehicleApi = require('../../../apis/vehicle.js');

Page({
	data: {
		form: { vehicleId: '', vehiclePlateNumber: '', routeDate: '' },
		points: [],
		types: ['批发市场','配送中心','超市卖场便利店','餐饮店','商场','菜场','其他'],
		vehicles: [],
		routeId: '',
		vehicleIndex: 0
	},
	onLoad(options) {
		if (options.vehicleId) {
			this.setData({ 'form.vehicleId': options.vehicleId });
		}
		this.loadVehicles();
		this.generateRouteId();
	},
	generateRouteId() {
		const date = new Date();
		const routeId = `ROUTE_${date.getFullYear()}${String(date.getMonth() + 1).padStart(2, '0')}${String(date.getDate()).padStart(2, '0')}_${Date.now()}`;
		this.setData({ routeId });
	},
	async loadVehicles() {
		try {
			const app = getApp();
			let companyId = app.globalData.companyId;
			const role = wx.getStorageSync('role') || '';
			
			// 如果没有 companyId，尝试从用户信息中获取
			if (!companyId) {
				try {
					const userApi = require('../../../apis/user.js');
					const userInfo = await userApi.getCurrentUser();
					if (userInfo && userInfo.company) {
						companyId = userInfo.company.id;
						app.globalData.companyId = companyId;
						wx.setStorageSync('companyId', companyId);
					} else if (userInfo && userInfo.vehicle && userInfo.vehicle.company) {
						// 司机用户从车辆中获取企业ID
						companyId = userInfo.vehicle.company.id;
						app.globalData.companyId = companyId;
						wx.setStorageSync('companyId', companyId);
					}
				} catch (e) {
					console.warn('获取用户信息失败:', e);
				}
			}
			
			// 根据角色加载车辆列表
			let vehicles = [];
			if (role === 'ADMIN') {
				// 管理员可以查看所有车辆
				const allVehicles = await vehicleApi.list();
				vehicles = allVehicles || [];
			} else if (role === 'DRIVER') {
				// 司机用户只能看到自己负责的车辆
				try {
					const userApi = require('../../../apis/user.js');
					const userInfo = await userApi.getCurrentUser();
					if (userInfo && userInfo.vehicle) {
						vehicles = [userInfo.vehicle];
					}
				} catch (e) {
					console.warn('获取司机车辆信息失败:', e);
				}
			} else if (companyId) {
				// 企业用户查看自己企业的车辆
				const res = await vehicleApi.listByCompany(companyId);
				vehicles = res || [];
			}
			
			if (vehicles.length === 0 && role !== 'ADMIN') {
				wx.showToast({ title: '无法获取车辆信息', icon: 'none' });
				setTimeout(() => wx.navigateBack(), 1500);
				return;
			}
			
			this.setData({ vehicles });
			
			// 如果已有选择的车辆ID，设置对应的索引
			if (this.data.form.vehicleId && vehicles.length > 0) {
				const vehicle = vehicles.find(v => v.id === Number(this.data.form.vehicleId));
				if (vehicle) {
					const vehicleIndex = vehicles.findIndex(v => v.id === Number(this.data.form.vehicleId));
					this.setData({ 
						'form.vehiclePlateNumber': vehicle.plateNumber,
						vehicleIndex: vehicleIndex >= 0 ? vehicleIndex : 0
					});
				}
			} else if (role === 'DRIVER' && vehicles.length === 1) {
				// 司机用户自动选择唯一的车辆
				const vehicle = vehicles[0];
				this.setData({
					'form.vehicleId': vehicle.id,
					'form.vehiclePlateNumber': vehicle.plateNumber,
					vehicleIndex: 0
				});
			}
		} catch (e) {
			console.error('加载车辆列表失败', e);
			wx.showToast({ title: '加载车辆列表失败', icon: 'none' });
		}
	},
	selectVehicle(e) {
		const idx = Number(e.detail.value);
		const vehicle = this.data.vehicles[idx];
		if (vehicle) {
			this.setData({
				vehicleIndex: idx,
				'form.vehicleId': vehicle.id,
				'form.vehiclePlateNumber': vehicle.plateNumber
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
	addPoint() {
		const pts = this.data.points.slice();
		pts.push({ pointType: '', typeIndex: 0, address: '', arriveTime: '', description: '' });
		this.setData({ points: pts });
	},
	remove(e) {
		const idx = Number(e.currentTarget.dataset.idx);
		const pts = this.data.points.slice();
		pts.splice(idx, 1);
		// 重新排序
		pts.forEach((p, i) => { p.seq = i + 1; });
		this.setData({ points: pts });
	},
	pickType(e) {
		const idx = Number(e.currentTarget.dataset.idx);
		const tIdx = Number(e.detail.value);
		const pts = this.data.points.slice();
		pts[idx].typeIndex = tIdx;
		pts[idx].pointType = this.data.types[tIdx];
		this.setData({ points: pts });
	},
	pi(e) {
		const idx = Number(e.currentTarget.dataset.idx);
		const k = e.currentTarget.dataset.k;
		const pts = this.data.points.slice();
		pts[idx][k] = e.detail.value;
		this.setData({ points: pts });
	},
	pt(e) {
		const idx = Number(e.currentTarget.dataset.idx);
		const pts = this.data.points.slice();
		pts[idx].arriveTime = e.detail.value;
		this.setData({ points: pts });
	},
	async submit() {
		const f = this.data.form;
		if (!f.vehicleId || !f.routeDate || this.data.points.length === 0) {
			wx.showToast({ title: '请填写车辆/日期/路线点', icon: 'none' });
			return;
		}
		
		// 验证路线点
		for (let i = 0; i < this.data.points.length; i++) {
			const p = this.data.points[i];
			if (!p.pointType || !p.address) {
				wx.showToast({ title: `路线点${i + 1}信息不完整`, icon: 'none' });
				return;
			}
		}
		
		try {
			wx.showLoading({ title: '提交中...' });
			
			// 构建路线点数据
			const routePoints = this.data.points.map((p, idx) => {
				const arriveTime = p.arriveTime ? `${f.routeDate} ${p.arriveTime}:00` : null;
				return {
					vehicle: { id: Number(f.vehicleId) },
					routeDate: f.routeDate,
					routeId: this.data.routeId,
					seq: idx + 1,
					address: p.address,
					pointType: p.pointType,
					arriveTime: arriveTime,
					description: p.description || ''
				};
			});
			
			// 批量创建路线点
			await routePointApi.createBatch(routePoints);
			
			wx.hideLoading();
			wx.showToast({ title: '提交成功', icon: 'success' });
			setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
		} catch (e) {
			wx.hideLoading();
			wx.showToast({ title: e.message || '提交失败', icon: 'none' });
		}
	}
});


