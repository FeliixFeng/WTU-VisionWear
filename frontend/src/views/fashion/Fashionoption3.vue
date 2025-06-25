<script setup>
import { onMounted,ref } from 'vue';
import {getValidToken} from "../../utils/auth.js";
import request from "../../main.js";

const pictures= ref([])

const getAllImage = async () => {
    try {
    // 获取token
        const token = getValidToken()
        if (!token) {
        throw new Error('未登录，请先登录！')
        }

        // 使用axios发送请求
        const response = await request({
            method: 'get',
            // url: `/user/getAllImage`
            url: `/user/getMaterial`
        });
        const imageResult = response.data
        console.log('获取图片URL响应:', response.data)

        //注意这里是code等于1成功
        if (imageResult.code !== 1 || !imageResult.data) {
        throw new Error(imageResult.msg || '获取图片链接失败，请稍后重试')
        }

        // 返回图片URL
        return imageResult.data
    } catch (error) {
            console.error('获取图片URL出错:', error)
            throw error
    }
}

onMounted(async () => {
    pictures.value = await getAllImage()
})

</script>

<template>
  <div class="showPictures">
    <el-image class="images" 
    v-for="(item,index) in pictures" 
    :src="item"
    lazy="true"
    :zoom-rate="1.2"
    :max-scale="7"
    :min-scale="0.2"
    :preview-src-list="pictures"
    :preview-teleported="true"
    :initial-index="index"
    show-progress
    fit="cover"
    />
  </div>
</template>

<style scoped>
.showPictures {
  display: flex;
  flex-wrap: wrap;
}
.images {
  width: 260px;
  height: 300px;
  margin: 8px;
  box-sizing: border-box;
  object-fit: cover;
  border: 2px solid rgb(168, 229, 243);
}
.images:hover {
  transform: scale(1.05);
  transition: transform 0.3s;
  border: 2px solid rgb(80, 218, 128);
}
</style>