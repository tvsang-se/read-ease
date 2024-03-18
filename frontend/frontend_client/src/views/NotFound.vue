<template>
    <div class="d-flex flex-column">
        404 Không tìm thấy trang !
        <v-btn @click="router.push({ name: 'Home' })">Trở về Trang chủ</v-btn>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter()
</script>