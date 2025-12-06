function chooseImageBase64() {
	return new Promise((resolve, reject) => {
		wx.chooseImage({
			count: 1,
			sizeType: ['compressed'],
			sourceType: ['album', 'camera'],
			success(res) {
				const path = res.tempFilePaths[0];
				const fs = wx.getFileSystemManager();
				
				// 使用异步方式读取文件，避免真机上的同步问题
				fs.readFile({
					filePath: path,
					encoding: 'base64',
					success(fileRes) {
						const ext = path.split('.').pop().toLowerCase();
						const mimeType = ext === 'jpg' || ext === 'jpeg' ? 'jpeg' : ext === 'png' ? 'png' : 'jpeg';
						resolve({ 
							base64: `data:image/${mimeType};base64,${fileRes.data}`, 
							path 
						});
					},
					fail(err) {
						console.error('读取图片失败:', err);
						reject(new Error('读取图片失败: ' + (err.errMsg || '未知错误')));
					}
				});
			},
			fail(err) { 
				console.error('选择图片失败:', err);
				reject(new Error('选择图片失败: ' + (err.errMsg || '未知错误'))); 
			}
		});
	});
}

module.exports = { chooseImageBase64 };


