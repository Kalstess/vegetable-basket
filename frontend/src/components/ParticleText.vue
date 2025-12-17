<template>
  <canvas ref="canvasRef" class="particle-canvas"></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const canvasRef = ref(null)
let animationId = null
let particles = []
let mouse = { x: 0, y: 0 }
let textPixels = []

const props = defineProps({
  text: {
    type: String,
    default: 'vegetable'
  },
  fontSize: {
    type: Number,
    default: 120
  },
  particleSize: {
    type: Number,
    default: 3
  },
  particleDistance: {
    type: Number,
    default: 80
  },
  particleSpeed: {
    type: Number,
    default: 0.5
  }
})

const initCanvas = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  const dpr = window.devicePixelRatio || 1
  
  // 设置canvas尺寸
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  
  // 创建离屏canvas用于绘制文字
  const textCanvas = document.createElement('canvas')
  const textCtx = textCanvas.getContext('2d')
  textCanvas.width = canvas.width
  textCanvas.height = canvas.height
  
  // 绘制文字
  textCtx.fillStyle = '#ffffff'
  textCtx.font = `bold ${props.fontSize}px Arial`
  textCtx.textAlign = 'center'
  textCtx.textBaseline = 'middle'
  // 文字位置：屏幕上方，为登录框留出空间
  const textY = canvas.height / 2 - 200
  textCtx.fillText(
    props.text,
    canvas.width / 2,
    textY
  )
  
  // 获取文字像素数据
  const imageData = textCtx.getImageData(0, 0, textCanvas.width, textCanvas.height)
  textPixels = []
  
  // 采样像素点（降低密度，中文字符需要更密集的采样）
  const gap = 3
  for (let y = 0; y < imageData.height; y += gap) {
    for (let x = 0; x < imageData.width; x += gap) {
      const index = (y * imageData.width + x) * 4
      const alpha = imageData.data[index + 3]
      if (alpha > 128) {
        textPixels.push({ x, y })
      }
    }
  }
  
  // 创建粒子
  particles = textPixels.map(pixel => {
    const baseX = pixel.x
    const baseY = pixel.y
    return {
      baseX,
      baseY,
      x: baseX,
      y: baseY,
      vx: 0,
      vy: 0,
      size: props.particleSize + Math.random() * 2
    }
  })
  
  // 鼠标移动事件（使用全局事件，因为canvas设置了pointer-events: none）
  const handleMouseMove = (e) => {
    mouse.x = e.clientX
    mouse.y = e.clientY
  }
  
  window.addEventListener('mousemove', handleMouseMove)
  
  // 动画循环
  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    
    particles.forEach(particle => {
      // 计算鼠标距离
      const dx = mouse.x - particle.x
      const dy = mouse.y - particle.y
      const distance = Math.sqrt(dx * dx + dy * dy)
      const minDistance = props.particleDistance
      
      // 如果鼠标靠近，粒子远离
      if (distance < minDistance) {
        const angle = Math.atan2(dy, dx)
        const force = (minDistance - distance) / minDistance
        particle.vx -= Math.cos(angle) * force * props.particleSpeed
        particle.vy -= Math.sin(angle) * force * props.particleSpeed
      }
      
      // 粒子回到原位的力（弹性效果）
      const returnDx = particle.baseX - particle.x
      const returnDy = particle.baseY - particle.y
      const returnDistance = Math.sqrt(returnDx * returnDx + returnDy * returnDy)
      if (returnDistance > 0.1) {
        particle.vx += returnDx * 0.03
        particle.vy += returnDy * 0.03
      }
      
      // 应用速度
      particle.x += particle.vx
      particle.y += particle.vy
      
      // 阻尼（让粒子运动更平滑）
      particle.vx *= 0.88
      particle.vy *= 0.88
      
      // 绘制粒子（带光晕效果）
      const gradient = ctx.createRadialGradient(
        particle.x, particle.y, 0,
        particle.x, particle.y, particle.size * 3
      )
      gradient.addColorStop(0, 'rgba(255, 255, 255, 1)')
      gradient.addColorStop(0.5, 'rgba(255, 255, 255, 0.6)')
      gradient.addColorStop(1, 'rgba(255, 255, 255, 0)')
      
      ctx.beginPath()
      ctx.arc(particle.x, particle.y, particle.size * 3, 0, Math.PI * 2)
      ctx.fillStyle = gradient
      ctx.fill()
      
      // 绘制核心粒子
      ctx.beginPath()
      ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
      ctx.fillStyle = 'rgba(255, 255, 255, 0.95)'
      ctx.fill()
    })
    
    animationId = requestAnimationFrame(animate)
  }
  
  animate()
  
  // 清理函数
  return () => {
    window.removeEventListener('mousemove', handleMouseMove)
    if (animationId) {
      cancelAnimationFrame(animationId)
    }
  }
}

let cleanup = null
let resizeHandler = null

onMounted(() => {
  cleanup = initCanvas()
  
  // 窗口大小改变时重新初始化
  resizeHandler = () => {
    if (cleanup) cleanup()
    cleanup = initCanvas()
  }
  
  window.addEventListener('resize', resizeHandler)
})

onUnmounted(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  if (cleanup) {
    cleanup()
  }
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
})
</script>

<style scoped>
.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}
</style>

