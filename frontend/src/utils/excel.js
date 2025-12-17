import * as XLSX from 'xlsx'

/**
 * 导出数据到Excel
 * @param {Array} data - 要导出的数据数组
 * @param {Array} columns - 列配置 [{key: 'field', label: '列名', formatter: (val) => val}]
 * @param {String} filename - 文件名
 */
export function exportToExcel(data, columns, filename = 'export.xlsx') {
  // 准备表头
  const headers = columns.map(col => col.label)
  
  // 准备数据行
  const rows = data.map(item => {
    return columns.map(col => {
      const value = item[col.key]
      if (col.formatter) {
        return col.formatter(value, item)
      }
      return value ?? ''
    })
  })
  
  // 创建工作簿
  const wb = XLSX.utils.book_new()
  
  // 创建工作表
  const ws = XLSX.utils.aoa_to_sheet([headers, ...rows])
  
  // 设置列宽
  const colWidths = columns.map(() => ({ wch: 15 }))
  ws['!cols'] = colWidths
  
  // 添加工作表到工作簿
  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
  
  // 导出文件
  XLSX.writeFile(wb, filename)
}

/**
 * 从Excel文件读取数据
 * @param {File} file - Excel文件
 * @returns {Promise<Array>} 解析后的数据数组
 */
export function importFromExcel(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (e) => {
      try {
        const data = new Uint8Array(e.target.result)
        const workbook = XLSX.read(data, { type: 'array' })
        
        // 读取第一个工作表
        const firstSheetName = workbook.SheetNames[0]
        const worksheet = workbook.Sheets[firstSheetName]
        
        // 转换为JSON数组
        const jsonData = XLSX.utils.sheet_to_json(worksheet, {
          header: 1, // 使用数组格式，第一行是表头
          defval: null // 空单元格返回null
        })
        
        if (jsonData.length < 3) {
          reject(new Error('Excel文件至少需要包含表头、说明行和数据行'))
          return
        }
        
        // 第一行是表头
        const headers = jsonData[0]
        // 第二行是（必填）/（可选）说明，跳过
        // 从第三行开始是数据
        const rows = jsonData.slice(2)
        
        // 转换为对象数组
        const result = rows.map(row => {
          const obj = {}
          headers.forEach((header, index) => {
            if (header) {
              obj[header] = row[index] ?? null
            }
          })
          return obj
        })
        
        resolve(result)
      } catch (error) {
        reject(new Error('解析Excel文件失败: ' + error.message))
      }
    }
    
    reader.onerror = () => {
      reject(new Error('读取文件失败'))
    }
    
    reader.readAsArrayBuffer(file)
  })
}

/**
 * 创建Excel模板
 * @param {Array} columns - 列配置 [{key: 'field', label: '列名', required: true, example: '示例值'}]
 * @param {String} filename - 文件名
 */
export function createExcelTemplate(columns, filename = 'template.xlsx') {
  // 表头行
  const headers = columns.map(col => col.label)
  
  // 说明行（必填项标记）
  const requiredRow = columns.map(col => col.required ? '(必填)' : '(可选)')
  
  // 示例行
  const exampleRow = columns.map(col => col.example ?? '')
  
  // 创建工作簿
  const wb = XLSX.utils.book_new()
  
  // 创建工作表
  const ws = XLSX.utils.aoa_to_sheet([headers, requiredRow, exampleRow])
  
  // 设置列宽
  const colWidths = columns.map(() => ({ wch: 20 }))
  ws['!cols'] = colWidths
  
  // 添加工作表到工作簿
  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
  
  // 导出文件
  XLSX.writeFile(wb, filename)
}

