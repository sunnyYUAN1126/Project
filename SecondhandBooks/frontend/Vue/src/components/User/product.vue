<template>
  <div class="page container mt-4">
    <h2 class="mb-4">書籍管理</h2>

    <div class="table-responsive">
      <table class="table table-bordered table-striped table-hover text-center align-middle">
        <thead class="table-dark">
          <tr>
            <th>ISBN</th>
            <th style="width: 200px;">書籍資訊</th>
            <th>分類</th>
            <th style="width: 150px;">二手書狀況</th>
            <th @click="setSort('uploadTime')" style="cursor: pointer;">
              新增日期 <i class="bi bi-caret-up-fill" :class="{'text-primary': currentSortField === 'uploadTime', 'text-secondary': currentSortField !== 'uploadTime'}"></i>
            </th>
            <th @click="setSort('price')" style="cursor: pointer;">
              二手價 <i class="bi bi-caret-up-fill" :class="{'text-primary': currentSortField === 'price', 'text-secondary': currentSortField !== 'price'}"></i>
            </th>
            <th>圖片</th>
            <th style="width: 100px;">管理員審核</th>
            <th>管理員備註</th>
            <th>上架狀態</th>
            <th>操作</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="(item, index) in sortedBooks" :key="index">
            <td>{{ item.isbn }}</td>
            <td class="text-start">
              <div class="fw-bold">{{ item.title }}</div>
              <div class="text-muted small">作者：{{ item.author }}</div>
              <div class="text-muted small">出版社：{{ item.publisher }}</div>
            </td>
            <td>{{ item.category }}</td>
            <td class="text-start">
              <div><strong>幾成新：</strong>{{ item.condition }}</div>
              <div><strong>筆記：</strong>{{ item.notes }}</div>
              <div><strong>描述：</strong>{{ item.description }}</div>
            </td>
            <td>{{ item.uploadTime }}</td>
            <td>{{ item.price }}</td>
            <td>
              <div class="d-flex flex-column gap-2 align-items-center">
                <img
                  v-for="(img, i) in item.images || []"
                  :key="i"
                  :src="img"
                  alt="preview"
                  class="img-thumbnail"
                  style="width:60px; height:60px; object-fit:cover; cursor:pointer;"
                  @click="openPreview(img)"
                />
              </div>
            </td>
            <td>
              <span :class="{
                'badge bg-warning text-dark': item.adminStatus === '待審核',
                'badge bg-success': item.adminStatus === '審核通過',
                'badge bg-danger': item.adminStatus === '審核不通過'
              }">
                {{ item.adminStatus }}
              </span>
            </td>
            <td>{{ item.adminNote }}</td>
            <td :class="{ 
              'text-success fw-bold': item.adminStatus === '審核通過', 
              'text-warning fw-bold': item.adminStatus === '待審核', 
              'text-danger fw-bold': item.adminStatus !== '審核通過' && item.adminStatus !== '待審核' 
            }">
              {{ item.adminStatus === "審核通過" ? "上架" : "下架" }}
            </td>
            <td>
              <button class="btn btn-danger btn-sm" @click="deleteBook(index, item.id)">刪除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 圖片放大 -->
    <div v-if="previewImage" class="preview-overlay" @click="closePreview">
      <img :src="previewImage" class="preview-img" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"

const books = ref([])

// Load books on mount
onMounted(async () => {
  try {
    const response = await fetch("http://localhost:8080/api/books/my-books", {
        credentials: 'include'
    });
    if (!response.ok) {
        throw new Error("Failed to fetch my books");
    }
    const data = await response.json();

    books.value = data.map(item => ({
       id: item.productId,
       isbn: item.isbn,
       title: item.name,
       author: item.author,
       publisher: item.publisher,
       category: item.category,
       condition: item.condition,
       notes: item.status, 
       description: item.note,
       uploadTime: item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'N/A',
       price: item.price,
       images: item.images,
       adminStatus: item.adminReview || "待審核",
       adminNote: item.adminNote || "無",
       shelfStatus: item.shelfStatus
    }));

  } catch (err) {
    console.error("Error loading books:", err);
  }
})

const currentSortField = ref(null)

function setSort(field) {
  currentSortField.value = field
}

const sortedBooks = computed(() => {
  const list = books.value.slice()
  
  if (currentSortField.value) {
    list.sort((a, b) => {
      // 針對數值或是日期都還是可以直接比大小 (字串日期 yyyy-mm-dd 可直接比)
      if (a[currentSortField.value] > b[currentSortField.value]) return 1
      if (a[currentSortField.value] < b[currentSortField.value]) return -1
      return 0
    })
  } else {
    // 預設排序：管理員狀態
    list.sort((a, b) => {
      const order = { "審核通過": 0, "待審核": 1, "審核不通過": 2 }
      return (order[a.adminStatus] || 99) - (order[b.adminStatus] || 99)
    })
  }
  return list
})

const previewImage = ref(null)

async function deleteBook(index, bookId) {
  if (confirm("確定要刪除這本書嗎？")) {
    try {
        const response = await fetch(`http://localhost:8080/api/books/${bookId}`, {
            method: "DELETE",
            credentials: 'include'
        });
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || "Failed to delete book");
        }

        books.value.splice(index, 1);
        alert("刪除成功");
    } catch (err) {
        console.error("Delete error:", err);
        alert("刪除失敗: " + err.message);
    }
  }
}

function openPreview(url) {
  previewImage.value = url
}

function closePreview() {
  previewImage.value = null
}
</script>

<style scoped>
.preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  cursor: pointer;
}
.preview-img {
  max-width: 90%;
  max-height: 90%;
  border-radius: 8px;
}
</style>
