<template>
  <div>
    <router-view v-if="false"></router-view>
  </div>
</template>

<script>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const router = useRouter();
    
    onMounted(() => {
      // 默认重定向到图灵绘境页面
      router.replace('/home/fashion/option1');
    });
  }
};
</script>