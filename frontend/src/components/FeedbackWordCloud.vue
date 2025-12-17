<template>
  <div ref="wordCloudChart" style="height: 350px; width: 100%;"></div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { feedbackApi } from '@/api'

// 使用与词云插件兼容的 ECharts 入口
import * as echarts from 'echarts/lib/echarts'
import { TooltipComponent, TitleComponent } from 'echarts/components'
echarts.use([TooltipComponent, TitleComponent])
import 'echarts-wordcloud'

const wordCloudChart = ref(null)
let wordCloudChartInstance = null

// 按标点符号分词函数
const segmentByPunctuation = (text) => {
  const words = {}

  // 中文标点符号和空白符
  const punctuationRegex = /[，。、；：！？\n\r\t\s]+/

  // 按标点符号分割文本
  const segments = text.split(punctuationRegex).filter(seg => seg.trim().length > 0)

  segments.forEach(segment => {
    const trimmed = segment.trim()
    // 只处理包含中文的片段
    if (/[\u4e00-\u9fa5]/.test(trimmed)) {
      // 2-20 字的片段作为一个词
      if (trimmed.length >= 2 && trimmed.length <= 20) {
        words[trimmed] = (words[trimmed] || 0) + 1
      } else if (trimmed.length > 20) {
        // 过长的片段拆成 2-4 字的词组
        for (let len = 2; len <= 4 && len <= trimmed.length; len++) {
          for (let i = 0; i <= trimmed.length - len; i++) {
            const word = trimmed.substring(i, i + len)
            if (word.length === len && /[\u4e00-\u9fa5]/.test(word)) {
              words[word] = (words[word] || 0) + 1
            }
          }
        }
      }
    }
  })

  return words
}

// 停用词
const stopWords = new Set([
  '的', '了', '在', '是', '我', '有', '和', '就', '不', '人', '都', '一', '一个', '上', '也',
  '很', '到', '说', '要', '去', '你', '会', '着', '没有', '看', '好', '自己', '这'
])

const loadDataAndRender = async () => {
  if (!wordCloudChartInstance || !wordCloudChart.value) return

  try {
    const data = await feedbackApi.getAll()
    const difficultiesTexts = (data || [])
      .filter(item => item && item.mainOperationalDifficulties && item.mainOperationalDifficulties.trim())
      .map(item => item.mainOperationalDifficulties.trim())

    if (!difficultiesTexts.length) {
      wordCloudChartInstance.setOption({
        title: {
          text: '暂无数据',
          left: 'center',
          top: 'middle',
          textStyle: { color: '#909399', fontSize: 16 }
        },
        series: []
      }, true)
      return
    }

    const allText = difficultiesTexts.join('\n')
    const words = segmentByPunctuation(allText)

    const filteredWords = {}
    Object.keys(words).forEach(key => {
      if (key.length >= 2 && !stopWords.has(key) && words[key] >= 2) {
        filteredWords[key] = words[key]
      }
    })

    const wordCloudData = Object.keys(filteredWords)
      .map(key => ({ name: key, value: filteredWords[key] }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 80)

    if (!wordCloudData.length) {
      wordCloudChartInstance.setOption({
        title: {
          text: '暂无有效数据（需要至少2个字符的词组）',
          left: 'center',
          top: 'middle',
          textStyle: { color: '#909399', fontSize: 14 }
        },
        series: []
      }, true)
      return
    }

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: params => `${params.name}: ${params.value}次`
      },
      series: [{
        type: 'wordCloud',
        gridSize: 8,
        sizeRange: [14, 60],
        rotationRange: [-45, 45],
        rotationStep: 15,
        shape: 'pentagon',
        width: '100%',
        height: '100%',
        drawOutOfBound: false,
        textStyle: {
          fontFamily: 'Microsoft YaHei, Arial, sans-serif',
          fontWeight: 'bold',
          color: () => {
            const colors = [
              '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
              '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#ff9f7f'
            ]
            return colors[Math.floor(Math.random() * colors.length)]
          }
        },
        emphasis: {
          focus: 'self',
          textStyle: { shadowBlur: 10, shadowColor: '#333' }
        },
        data: wordCloudData
      }]
    }

    wordCloudChartInstance.setOption(option, true)
  } catch (error) {
    console.error('加载或渲染词云图失败:', error)
    ElMessage.error('加载词云数据失败')
    wordCloudChartInstance.setOption({
      title: {
        text: '词云图渲染失败',
        subtext: error.message || '未知错误',
        left: 'center',
        top: 'middle',
        textStyle: { color: '#f56c6c', fontSize: 14 },
        subtextStyle: { color: '#909399', fontSize: 12 }
      },
      series: []
    }, true)
  }
}

onMounted(() => {
  nextTick(() => {
    if (wordCloudChart.value) {
      wordCloudChartInstance = echarts.init(wordCloudChart.value)
      loadDataAndRender()
    }
  })
})
</script>


