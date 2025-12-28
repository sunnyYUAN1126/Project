<script setup>
import { ref, onMounted, computed, inject } from 'vue'

// ==========================================
// 狀態變數
// ==========================================
const uniqueBookList = ref([]);
const currentCategory = ref('ALL');

// 分頁狀態
// 分頁狀態
const currentPage = ref(1);
const totalPages = ref(1);
const totalItems = ref(0);

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
const fetchBooks = async (category, page = 1) => {
  try {
    // 後端已經固定一頁為 6 個
    let url = `${API_BASE_URL}/unique?page=${page}`;
    if (category !== 'ALL') {
      url += `&category=${encodeURIComponent(category)}`;
    }
      
    const response = await fetch(url);
    if (!response.ok) throw new Error('Network response was not ok');
    
    // 解析 PageResult 結構
    const data = await response.json();
    uniqueBookList.value = data.list;
    currentPage.value = data.currentPage;
    totalPages.value = data.totalPages;
    totalItems.value = data.totalCount;
    
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
// 排序邏輯
// ==========================================
const currentSortField = ref(null)
const currentSortOrder = ref('asc') // 'asc' or 'desc'

function setSort(field) {
  if (currentSortField.value === field) {
    // 同欄位點擊，切換排序方向
    currentSortOrder.value = currentSortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    // 不同欄位，預設由小到大
    currentSortField.value = field
    currentSortOrder.value = 'asc'
  }
}

// 根據 selectedBookSellers 產生排序後的清單
const sortedProductList = computed(() => {
  // 複製一份陣列以免改動到原始資料
  const list = selectedBookSellers.value.slice()
  
  // 書況對應數值表 (一成 ~ 九成, 全新)
  const conditionMap = {
    '一成': 1, '二成': 2, '三成': 3,
    '四成': 4, '五成': 5, '六成': 6,
    '七成': 7, '八成': 8, '九成': 9
  };

  if (currentSortField.value) {
    list.sort((a, b) => {
      let valA = a[currentSortField.value]
      let valB = b[currentSortField.value]
      
      // 針對價格欄位轉為數字比較
      if (currentSortField.value === 'price') {
        valA = Number(valA)
        valB = Number(valB)
      } else if (currentSortField.value === 'condition') {
        // 針對書況，使用 mapping 轉成數字
        valA = conditionMap[valA] || 0
        valB = conditionMap[valB] || 0
      } else {
        // 其他欄位處理 null 或 undefined
        if (valA === null || valA === undefined) valA = ''
        if (valB === null || valB === undefined) valB = ''
      }

      if (valA === valB) return 0
      
      const result = valA > valB ? 1 : -1
      return currentSortOrder.value === 'asc' ? result : -result
    })
  }
  return list
})

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
  currentPage.value = 1; // 切換分類時重置頁碼
  // 切換分類時，如果正開著詳細頁，建議切回列表
  goBack();
  // 重新抓取資料
  fetchBooks(categoryName, 1);
}

// 換頁
function changePage(page) {
  if (page < 1 || page > totalPages.value) return;
  fetchBooks(currentCategory.value, page);
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// 初始化
onMounted(() => {
  fetchBooks('ALL', 1);
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

// 關鍵字搜尋書籍
const searchBooks = async (query) => {
  if (!query || query.trim() === '') {
    // 如果搜尋字串為空，重置回全部
    fetchBooks('ALL', 1);
    return;
  }
  
  try {
    // 移除 size 參數
    const url = `${API_BASE_URL}/search?query=${encodeURIComponent(query)}&page=1`;
    const response = await fetch(url);
    if (!response.ok) throw new Error('Network response was not ok');
    
    const data = await response.json();
    uniqueBookList.value = data.list;
    currentPage.value = data.currentPage;
    totalPages.value = data.totalPages;
    totalItems.value = data.totalCount;
    
    // 搜尋時關閉詳細頁
    goBack();
  } catch (error) {
    console.error('Error searching books:', error);
    uniqueBookList.value = [];
  }
};

const injectedUserId = inject('userId');

async function addToCart(product) {
  if (!injectedUserId || !injectedUserId.value) {
    alert("請先登入才能加入購物車");
    return;
  }
  
  try {
    // product.productId is the specific item ID
    const response = await fetch('http://localhost:8080/api/cart/add', {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ buyerId: injectedUserId.value, productId: product.productId }),
        credentials: 'include'
    });
    if (!response.ok) {
        throw new Error("Failed to add to cart");
    }
    alert("加入購物車成功！");
  } catch (error) {
    console.error("Add to cart error:", error);
    alert("加入購物車失敗");
  }
}

// 暴露方法給父組件
defineExpose({
  goBack,
  filterByCategory,
  searchBooks
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

        <!-- Pagination Controls -->
        <div v-if="totalPages >= 1" class="col-12 mt-5 mb-3 d-flex justify-content-center w-100">
            <nav aria-label="Page navigation example">
                <ul class="pagination mb-0">
                    <li class="page-item" :class="{ disabled: currentPage === 1 }">
                        <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)" aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>
                    
                    <li v-for="p in totalPages" :key="p" class="page-item" :class="{ active: p === currentPage }">
                        <a class="page-link" href="#" @click.prevent="changePage(p)">{{ p }}</a>
                    </li>
                    
                    <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                        <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)" aria-label="Next">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>
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
                <th @click="setSort('condition')" style="cursor: pointer;">
                  書況等級 
                  <i class="bi" 
                     :class="{
                       'bi-caret-up-fill': currentSortField === 'condition' && currentSortOrder === 'asc',
                       'bi-caret-down-fill': currentSortField === 'condition' && currentSortOrder === 'desc',
                       'bi-caret-up': currentSortField !== 'condition'
                     }"
                     :style="{ color: currentSortField === 'condition' ? '#8da399' : '#ccc' }">
                  </i>
                </th>
                <th>筆記狀況</th>
                <th>賣家備註</th>
                <th>
                  上架日期
                </th>
                <th @click="setSort('price')" style="cursor: pointer;">
                  價格 
                  <i class="bi" 
                     :class="{
                       'bi-caret-up-fill': currentSortField === 'price' && currentSortOrder === 'asc',
                       'bi-caret-down-fill': currentSortField === 'price' && currentSortOrder === 'desc',
                       'bi-caret-up': currentSortField !== 'price'
                     }"
                     :style="{ color: currentSortField === 'price' ? '#8da399' : '#ccc' }">
                  </i>
                </th>
                <th>商品實拍</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, index) in sortedProductList" :key="product.productId">
                <td>{{ product.sellerName }}</td>
                <td><span class="badge bg-info text-dark">{{ product.condition }}新</span></td>
                <td>{{ product.status }}</td>
                <td style="max-width: 200px; white-space: pre-wrap;">{{ product.note || '無' }}</td>
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
                  <button class="btn btn-primary btn-sm" 
                          :disabled="injectedUserId && product.sellerId === injectedUserId"
                          :class="{ 'btn-secondary': injectedUserId && product.sellerId === injectedUserId }"
                          @click="addToCart(product)">
                    <template v-if="injectedUserId && product.sellerId === injectedUserId">
                      本人商品 <i class="bi bi-cart-x"></i>
                    </template>

                    <template v-else>
                      加入 <i class="bi bi-cart4"></i>
                    </template>
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
  width: 80%;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  border-radius: 10px;
}
.row {
  padding: 30px;
}

/* Custom Pagination Styles */
.page-item .page-link {
  border-radius: 50% !important;
  margin: 0 6px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdcdc; /* Lighter border */
  color: #6c757d; /* Muted text */
  background-color: #f8f9fa; /* Very light gray back */
  transition: all 0.3s ease;
}

.page-item.active .page-link {
  background-color: #8da399; /* Morandi Sage/Green-Grey */
  border-color: #8da399;
  color: white;
  box-shadow: 0 4px 6px rgba(141, 163, 153, 0.4);
}

.page-item .page-link:hover {
  background-color: #dbe4eb; /* Soft muted blue-gray for hover */
  color: #6c757d;
  border-color: #dbe4eb;
  transform: translateY(-2px);
}

.page-item.active .page-link:hover {
  background-color: #7a8f86; /* Darker sage for active hover */
  border-color: #7a8f86;
  color: white;
}
</style>