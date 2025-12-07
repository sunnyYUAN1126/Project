<script setup>
import { ref, provide } from 'vue';
import product from './products.vue'; // 注意路徑是否正確

// 透過 ref 取得子組件實例
const productRef = ref(null);

// 不需要 defineExpose productRef，因為通常是父組件要用子組件方法
// 如果這是 App.vue 的子組件，則不需要 expose 給誰看，自己用就好

// 定義切換分類的函式
const selectCategory = (category) => {
  if (productRef.value) {
    productRef.value.filterByCategory(category);
  }
}

// 搜尋功能 (模擬)
const searchQuery = ref('');
const handleSearch = () => {
    console.log("搜尋:", searchQuery.value);
    // 未來可以在這裡呼叫 productRef.value.filterBySearch(...)
}
</script>

<template>
  <nav class="navbar bg-body-tertiary navbar-expand-lg bg-body-tertiary fixed-top second-nav">
    <div class="container-fluid">
      <a class="navbar-brand" href="#" @click.prevent="selectCategory('ALL')">二手書城</a>

      <ul class="nav nav-pills">
        <li class="nav-item">
          <a class="nav-link" href="#" @click.prevent="selectCategory('ALL')">ALL</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#" @click.prevent="selectCategory('文學類')">文學類</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#" @click.prevent="selectCategory('社會科學類')">社會科學類</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#" @click.prevent="selectCategory('商業管理類')">商業管理類</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#" @click.prevent="selectCategory('理工資訊類')">理工資訊類</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#" @click.prevent="selectCategory('醫學健康類')">醫學健康類</a>
        </li>
      </ul>          
    
      <form class="d-flex" role="search" @submit.prevent="handleSearch">
        <input v-model="searchQuery" class="form-control me-2" type="search" placeholder="Search" aria-label="Search"/>
        <button class="btn btn-outline-success" type="submit">
          <i class="bi bi-search"></i>
        </button>
      </form>

    </div>
  </nav>

  <product ref="productRef"/>

</template>

<style scoped>
.nav-link {
  color: rgb(0, 0, 0) !important;
  cursor: pointer; /* 讓滑鼠變成手指 */
}
.active{
  background: rgb(216, 210, 211) !important;
}
.nav-link:hover {
  background-color: rgb(216, 210, 211) !important; 
  color: rgb(0, 0, 0) !important;
  font-weight: bold;
}
.second-nav {
  top: 80px;  
}
</style>