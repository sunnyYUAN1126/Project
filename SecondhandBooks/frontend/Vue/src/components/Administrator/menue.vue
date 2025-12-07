<script setup>
import review from './review.vue'

import { ref } from 'vue'
const currentPage = ref('review')

function switchPage(page) {
  currentPage.value = page
}
</script>

<template>
  <div class="buyer_container">

    <!-- ⬅️ 側邊欄：Bootstrap nav flex-column 風格 -->
    <nav class="nav flex-column side-nav">

      <h5 class="menu-title">管理員</h5>

      <a class="nav-link"
         :class="{ active: currentPage === 'review' }"
         @click="switchPage('review')">
        書籍審核
      </a>

    </nav>

    <!-- 主要內容 -->
    <div class="content">
      <review v-show="currentPage === 'review'" />
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
}

/* ----- 側邊欄：Bootstrap nav flex-column 風格 ----- */
.side-nav {
  width: 140px;
  height: 100%;
  padding: 20px 10px;
  background: #f8f9fa;
  border-right: 1px solid #ddd;
  border-radius: 10px;
}

.menu-title {
  margin-bottom: 12px;
  font-size: 17px;
  font-weight: bold;
  color: #6c757d;
}

/* nav-link 樣式 */
.side-nav .nav-link {
  font-size: 18px;
  color: #333;
  padding: 12px 5px;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s;
}

.side-nav .nav-link:hover {
  background-color: #ffe0f0;
  color: #d63384;
}

/* active：當前頁面 */
.side-nav .nav-link.active {
  background-color: #ffd6ef;
  color: #c2185b;
  font-weight: bold;
}

/* 右側內容 */
.content {
  flex: 1;
  padding: 20px;
}
</style>
