<script setup>
import SellerOrder from './SellorOrder.vue'
import product from './product.vue'
import AddBook from './AddBook.vue'
import order from './BuyerOrder.vue'

import { ref } from 'vue'
const currentPage = ref('AddBook')

function switchPage(page) {
  currentPage.value = page
}
</script>

<template>
  <div class="buyer_container">
    <!-- 側邊欄，用 Bootstrap nav flex-column 的 style -->
    <nav class="nav flex-column side-nav">

      <h5 class="menu-title">賣書</h5>

      <a class="nav-link" 
         :class="{ active: currentPage === 'AddBook' }"
         @click="switchPage('AddBook')">
         新增書籍
      </a>

      <a class="nav-link"
         :class="{ active: currentPage === 'product' }"
         @click="switchPage('product')">
         書籍管理
      </a>

      <a class="nav-link"
         :class="{ active: currentPage === 'sellerOrder' }"
         @click="switchPage('sellerOrder')">
         訂單管理
      </a>

      <h5 class="menu-title">買書</h5>

      <a class="nav-link"
         :class="{ active: currentPage === 'order' }"
         @click="switchPage('order')">
         訂單查詢
      </a>

    </nav>

    <!-- 右側內容 -->
    <div class="content">
      <order v-show="currentPage === 'order'" />
      <AddBook v-show="currentPage === 'AddBook'" />
      <product v-show="currentPage === 'product'" />
      <SellerOrder v-show="currentPage === 'sellerOrder'" />
    </div>
  </div>
</template>

<style scoped>
* {
  padding: 0;
  margin: 0;
  list-style: none;
}

.buyer_container {
  display: flex;
  margin-top: 100px;
  height: 100%;
}

/* ----- 仿 Bootstrap nav flex-column 風格 ----- */
.side-nav {
  width: 130px;
  height: 100%;
  padding: 20px 10px;
  background: #f8f9fa;
  border-right: 1px solid #ddd;
  border-radius: 10px;
}

.side-nav .menu-title {
  margin: 15px 0 5px;
  font-size: 16px;
  font-weight: bold;
  color: #6c757d;
}

/* nav-link 樣式 */
.side-nav .nav-link {
  font-size: 18px;
  color: #333;
  padding: 12px 5px;
  border-radius: 4px;
  cursor: pointer;
  transition: 0.2s;
}

.side-nav .nav-link:hover {
  background-color: #a1e1ee;
  color: #3349d6;
}

/* active 狀態（你點到的那頁） */
.side-nav .nav-link.active {
  background-color: #c0b5ea;
  font-weight: bold;
  color: #7245e4;
}

/* 右側內容 */
.content {
  flex: 1;
  padding: 20px;
}
</style>
