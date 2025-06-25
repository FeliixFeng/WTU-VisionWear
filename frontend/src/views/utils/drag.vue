<script setup>
import { ref, onMounted } from 'vue'
import { image } from "../../api"
const pictureUrl = ref('')
const upload = ref(null)
const inp = ref(null)
const emit = defineEmits(['update','click'])

const fileInput = async (e) => {
  const file = e.target.files[0]
  if (!file) {
    return
  }
  const formData = new FormData()
  formData.append('file', file)
  const response = await image.uploadImage(formData)
  console.log('上传响应:', response)

  // 使用业务错误处理工具检查响应
  //const error = handleBusinessError(response, '图片上传失败');
  //if (error) return; // 如果有错误，handleBusinessError已经显示了错误消息

  // 没有错误，继续处理成功情况
  pictureUrl.value = response.data.data
  console.log('上传成功:', pictureUrl.value)
  //pictureUrl.value = URL.createObjectURL(e.target.files[0])
  emit('update', pictureUrl.value, 'update')
}

const urlInput = function (e) {
  e.preventDefault()
  if (e.dataTransfer.files.length > 0) {
    const file = e.dataTransfer.files[0]
    pictureUrl.value = URL.createObjectURL(file)
    emit('update', pictureUrl.value, 'update')
    return
  }
  pictureUrl.value = e.dataTransfer.getData('text/uri-list')
  emit('update', pictureUrl.value, 'update')
}

const clickInput = function (e) {
  inp.value.click()
}

const clear = function () {
  emit('update', pictureUrl.value, 'clear')
  pictureUrl.value = ''
  inp.value.value = ''
}
onMounted(() => {
  upload.value.ondragover = e => {
    e.preventDefault()
  }
})
</script>

<template>
  <p>
    <slot></slot>
    *
    <el-button type="round" @click="clear">清空</el-button>
  </p>
  <div class="upload" ref="upload" @drop="urlInput" @click="clickInput" v-show="!pictureUrl">
    <p v-if="!pictureUrl">点击或拖动以上传</p>
    <input type="file" class="inp" ref="inp" @change="fileInput" @click.stop/>
  </div>
  <div class="showPicture" v-if="pictureUrl" @click="()=>{emit('click', pictureUrl)}">
    <img :src="pictureUrl"/>
  </div>
</template>

<style scoped>
.upload {
  position: relative;
  width: 100%;
  height: 100px;
  border: 1px dashed #ccc;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.upload:hover {
  border: 1px dashed #86a3e6;
}

.inp {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
}

.showPicture {
  width: 100%;
  height: 125px;
  border: 1px dashed #86a3e6;
}

.showPicture img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
</style>