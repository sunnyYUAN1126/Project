<script setup>
import { ref, onMounted } from 'vue'

// ==========================================
// 狀態變數
// ==========================================
const uniqueBookList = ref([]);
const currentCategory = ref('ALL');

// 控制頁面狀態
const showDetail = ref(false)
const selectedBookInfo = ref(null) // 存書籍基本資訊
const selectedBookSellers = ref([]) // 存該書的所有賣家列表

// 放大圖片控制
const showModal = ref(false)
const currentImage = ref("")

// ==========================================
// API 呼叫
// ==========================================
const API_BASE_URL = 'http://localhost:8080/api/books';

// 取得書籍列表 (依分類)
const fetchBooks = async (category) => {
  try {
    const url = category === 'ALL' 
      ? `${API_BASE_URL}/unique` 
      : `${API_BASE_URL}/unique?category=${encodeURIComponent(category)}`;
      
    const response = await fetch(url);
    if (!response.ok) throw new Error('Network response was not ok');
    uniqueBookList.value = await response.json();
  } catch (error) {
    console.error('Error fetching books:', error);
    uniqueBookList.value = [];
  }
};

// 取得特定書籍的所有賣家列表
const fetchListings = async (isbn, name) => {
  try {
    // 優先使用 ISBN 查詢，如果沒有 ISBN 則使用書名
    const identifier = isbn || name;
    if (!identifier) return;

    const url = `${API_BASE_URL}/listings/${encodeURIComponent(identifier)}`;
    const response = await fetch(url);
    if (!response.ok) throw new Error('Network response was not ok');
    selectedBookSellers.value = await response.json();
  } catch (error) {
    console.error('Error fetching listings:', error);
    selectedBookSellers.value = [];
  }
};

// ==========================================
// 互動邏輯
// ==========================================

function openModal(img) {
  currentImage.value = processImageUrl(img)
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  currentImage.value = ""
}

// 點擊商品卡片：查看詳細
function viewDetail(book) {
  selectedBookInfo.value = book;
  showDetail.value = true;
  // 呼叫 API 取得賣家列表
  fetchListings(book.isbn, book.name);
}

// 返回列表頁
function goBack() {
  selectedBookInfo.value = null;
  selectedBookSellers.value = [];
  showDetail.value = false;
}

// 接收外部 (Home.vue) 傳來的分類切換請求
function filterByCategory(categoryName) {
  currentCategory.value = categoryName;
  // 切換分類時，如果正開著詳細頁，建議切回列表
  goBack();
  // 重新抓取資料
  fetchBooks(categoryName);
}

// 初始化
onMounted(() => {
  fetchBooks('ALL');
});

// 處理圖片 URL
const processImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/300';
  
  // 如果是已經是 http 開頭，直接回傳
  if (url.startsWith('http')) return url;
  
  // 處理本地路徑 C:\1.projectJava\mysqldatabase\1-1.jpg
  // 轉換為 http://localhost:8080/images/1-1.jpg
  
  let newUrl = url;
  const prefix = 'C:\\1.projectJava\\mysqldatabase\\';
  
  // Case insensitive check for prefix
  if (newUrl.toLowerCase().startsWith(prefix.toLowerCase())) {
      newUrl = newUrl.substring(prefix.length);
  } else if (newUrl.includes('\\')) {
      // Fallback: just take the filename if it looks like a windows path
      newUrl = newUrl.split('\\').pop();
  }
  
  // Replace any remaining backslashes with forward slashes
  newUrl = newUrl.replace(/\\/g, '/');
  
  // Ensure no leading slash
  if (newUrl.startsWith('/')) {
      newUrl = newUrl.substring(1);
  }

  return `http://localhost:8080/images/${newUrl}`;
};

// 暴露方法給父組件
defineExpose({
  goBack,
  filterByCategory
})
</script>

<template>
  <div class="page-background">
    <div class="content-wrapper">
      <div v-if="!showDetail" class="apple row row-cols-1 row-cols-md-3 g-4">
        <div v-if="uniqueBookList.length === 0" class="col-12 text-center">
            <h3>此分類暫無書籍</h3>
        </div>

        <div class="col" v-for="book in uniqueBookList" :key="book.isbn">
          <div class="card" @click="viewDetail(book)" style="cursor:pointer">
            <div style="text-align: center;">
              <img class="imgall" :src="processImageUrl(book.coverImage)" alt="商品圖片" style="height: 200px; width: 100%; object-fit: contain; ">
            </div>
            <div class="card-body">
              <h5 class="card-title">{{ book.name }}</h5>
              <p class="card-text text-muted">{{ book.author }}</p>
              <div class="d-flex justify-content-between align-items-center px-2">
                <span class="badge bg-secondary">{{ book.category }}</span>
                <span class="text-danger fw-bold">${{ book.minPrice }} 起</span>
              </div>
              <p class="card-text mt-2">總庫存: {{ book.totalStock }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="card2 mx-auto my-3 p-4 shadow-lg" style="width: 80%; background: white; border-radius: 20px;">
        <button class="btn btn-outline-secondary mb-3" @click="goBack" style="position: fixed; bottom: 30px; right: 30px; z-index: 1000; box-shadow: 0 4px 6px rgba(0,0,0,0.1);"><i class="bi bi-arrow-up-left-circle"></i> 返回列表</button>

        <div class="d-flex gap-4 align-items-start">
          <div style="width: 40%; flex-shrink: 0; text-align: center;">
            <img :src="processImageUrl(selectedBookInfo.coverImage)" alt="商品圖片" class="img-fluid rounded shadow-sm" style="max-height: 400px; object-fit: contain;">
          </div>
          <div class="flex-grow-1">
            <div class="card-body h-100 d-flex flex-column justify-content-center">
              <h2 class="card-title fw-bold mb-3" style="color: #333;">{{ selectedBookInfo.name }}</h2>
              <div class="fs-5 text-muted mb-2">作者: <span class="text-dark">{{ selectedBookInfo.author }}</span></div>
              <div class="fs-5 text-muted mb-2">出版社: <span class="text-dark">{{ selectedBookInfo.publisher }}</span></div>
              <div class="fs-5 text-muted mb-2">ISBN: <span class="text-dark">{{ selectedBookInfo.isbn }}</span></div>
              <div class="fs-5 text-muted mt-3">全站總庫存: <span class="badge bg-success">{{ selectedBookInfo.totalStock }}</span></div>
            </div>
          </div>
        </div>

        <div class="pt-5">
          <h4 class="mb-3 border-bottom pb-2">賣家列表</h4>
          <table class="table table-hover align-middle">
            <thead>
              <tr>
                <th>賣家</th>
                <th>書況等級</th>
                <th>筆記狀況</th>
                <th>賣家備註</th>
                <th>上架日期</th>
                <th>價格</th>
                <th>商品實拍</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, index) in selectedBookSellers" :key="product.productId">
                <td>{{ product.sellerName }}</td>
                <td><span class="badge bg-info text-dark">{{ product.condition }}</span></td>
                <td>{{ product.status }}</td>
                <td style="max-width: 150px;" class="text-truncate" :title="product.note">{{ product.note || '無' }}</td>
                <td>{{ product.createdAt ? new Date(product.createdAt).toLocaleDateString() : 'N/A' }}</td>
                <td class="fw-bold text-danger">{{ product.price }}元</td>

                <td>
                  <div style="display: flex; gap: 5px;">
                    <img
                      v-for="(img, i) in product.images"
                      :key="i"
                      :src="processImageUrl(img)"
                      class="img-thumbnail object-fit-cover"
                      style="width: 50px; height: 50px; cursor:pointer;"
                      @click="openModal(img)"
                    >
                    <span v-if="!product.images || product.images.length === 0" class="text-muted small">無圖片</span>
                  </div>
                </td>

                <td>
                  <button class="btn btn-primary btn-sm">
                    <i class="bi bi-cart4"></i> 加入購物車
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showModal" class="modal fade show" tabindex="-1" style="display: block; background-color: rgba(0,0,0,0.5);" @click.self="closeModal">
    <div class="modal-dialog modal-dialog-centered modal-md">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">圖片預覽</h5>
          <button type="button" class="btn-close" @click="closeModal"></button>
        </div>
        <div class="modal-body text-center">
          <img :src="currentImage" class="img-fluid" alt="Enlarged view">
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 樣式保持原樣，微調一些間距 */
.imgall {
  width: 100%;
  height: 300px;
  background: rgb(255, 254, 254);
  border-radius: 30px 30px 0 0;
}
.card{
  border-radius: 30px;
  transition: transform 0.2s;
}
.card:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.card-body {
  background: rgb(255, 255, 255);
  border-radius: 0 0 30px 30px;
  padding: 20px;
}

.card-text {
  margin: 5px 0;
}

.card2 {
  padding-top: 20px;
}
.page-background {
  background-color: #f0f2f5; 
  min-height: 100vh;
  padding: 120px 0 50px 0;
}
.content-wrapper {
  background-color: rgba(255, 255, 255, 0.5);
  width: 85%;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  border-radius: 10px;
}
.row{
  padding: 20px;
}
</style>