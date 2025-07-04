<template>
  <div class="drawing-container">
    <canvas ref="canvas" @mousedown="startDrawing" @mousemove="draw" @mouseup="stopDrawing"
      @mouseleave="stopDrawing"></canvas>

    <div class="controls">
      <div class="brush-controls">
        <label>画笔大小:</label>
        <input type="range" min="1" max="50" v-model="brushSize">
        <label>画笔颜色:</label>
        <input type="color" v-model="brushColor">
      </div>

      <div class="action-buttons">
        <button @click="clearCanvas">清空画布</button>
        <button @click="saveAsImage">保存为图片</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const isDrawing = ref(false);
const lastX = ref(0);
const lastY = ref(0);
const brushSize = ref(5);
const brushColor = ref('#000000');
const canvas = ref(null);
const ctx = ref(null);

const resizeCanvas = () => {
  canvas.value.width = canvas.value.offsetWidth;
  canvas.value.height = canvas.value.offsetHeight;
  clearCanvas();
};

const getCanvasCoordinates = (e) => {
  const rect = canvas.value.getBoundingClientRect();
  return {
    x: e.clientX - rect.left,
    y: e.clientY - rect.top
  };
};

const startDrawing = (e) => {
  isDrawing.value = true;
  const { x, y } = getCanvasCoordinates(e);
  [lastX.value, lastY.value] = [x, y];
};

const draw = (e) => {
  if (!isDrawing.value) return;

  const { x, y } = getCanvasCoordinates(e);

  ctx.value.beginPath();
  ctx.value.moveTo(lastX.value, lastY.value);
  ctx.value.lineTo(x, y);
  ctx.value.lineWidth = brushSize.value;
  ctx.value.lineCap = 'round';
  ctx.value.strokeStyle = brushColor.value;
  ctx.value.stroke();

  [lastX.value, lastY.value] = [x, y];
};

const stopDrawing = () => {
  isDrawing.value = false;
};

const clearCanvas = () => {
  ctx.value.fillStyle = '#ffffff';
  ctx.value.fillRect(0, 0, canvas.value.width, canvas.value.height);
};

const saveAsImage = () => {
  const link = document.createElement('a');
  link.download = 'drawing.png';
  link.href = canvas.value.toDataURL('image/png');
  link.click();
};

onMounted(() => {
  ctx.value = canvas.value.getContext('2d');
  resizeCanvas();

  // Use ResizeObserver for better resizing
  const resizeObserver = new ResizeObserver(() => {
    resizeCanvas();
  });
  resizeObserver.observe(canvas.value);

  // Cleanup observer on unmount
  onBeforeUnmount(() => {
    resizeObserver.disconnect();
  });
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCanvas);
});
</script>

<style scoped>
.drawing-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-height: 80vh;
  max-width: 100vw;
  height: 500px;
  overflow: hidden;
}

canvas {
  flex: 1;
  border: 1px solid #ccc;
  background-color: white;
  cursor: crosshair;
  min-height: 300px;
  max-width: 100%;
}

.controls {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.brush-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

button {
  padding: 8px 16px;
  min-width: 100px;
  font-size: 14px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
