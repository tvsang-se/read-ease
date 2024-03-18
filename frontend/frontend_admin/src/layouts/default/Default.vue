<template>
  <v-app>
    <default-bar  class="default-bar"/>

    <default-view />
  </v-app>
</template>

<script setup>
  import DefaultBar from './AppBar.vue'
  import DefaultView from './View.vue'
</script>

<style scoped>
.default-bar {
  z-index: 11111;
}
</style>
