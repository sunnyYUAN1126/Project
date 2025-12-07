<template>
  <div class="page container mt-4">
    <h2 class="mb-4">書籍管理</h2>

    <div class="table-responsive">
      <table class="table table-bordered table-striped table-hover text-center align-middle">
        <thead class="table-dark">
          <tr>
            <th>ISBN</th>
            <th>書名</th>
            <th>分類</th>
            <th>幾成新</th>
            <th>有無筆記</th>
            <th>書況描述</th>
            <th>上架日期</th>
            <th>二手價</th>
            <th>圖片</th>
            <th>管理員審核</th>
            <th>管理員備註</th>
            <th>上架狀態</th>
            <th>操作</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="(item, index) in sortedBooks" :key="index">
            <td>{{ item.isbn }}</td>
            <td>{{ item.title }}</td>
            <td>{{ item.category }}</td>
            <td>{{ item.condition }} 成新</td>
            <td>{{ item.notes }}</td>
            <td>{{ item.description }}</td>
            <td>{{ item.uploadTime }}</td>
            <td>{{ item.price }}</td>
            <td>
              <div class="d-flex gap-2 justify-content-center">
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
              <button class="btn btn-danger btn-sm" @click="deleteBook(index)">刪除</button>
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
import { ref, computed } from "vue"

const books = ref([
  { isbn: "9789865021234", title: "資料庫系統概論", category: "商業與管理類", condition: 9, notes: "無筆記", description: "近全新，無破損", uploadTime: "2025-01-01", price: 250, images: [], adminStatus: "待審核", adminNote: "無" },
  { isbn: "9789574425678", title: "Java 程式設計", category: "社會科學類", condition: 8, notes: "少量筆記", description: "封面微折", uploadTime: "2025-01-15", price: 150, images: [], adminStatus: "審核通過", adminNote: "檢查過封面" },
  { isbn: "9789861234561", title: "Python 程式設計", category: "理工與資訊類", condition: 7, notes: "無筆記", description: "良好", uploadTime: "2025-02-01", price: 200, images: [], adminStatus: "審核不通過", adminNote: "頁面缺頁" },
  { isbn: "9789861234562", title: "微積分", category: "理學類", condition: 9, notes: "少量筆記", description: "近全新", uploadTime: "2025-03-05", price: 300, images: [], adminStatus: "審核通過", adminNote: "封面良好" },
  { isbn: "9789861234563", title: "經濟學原理", category: "商業與管理類", condition: 8, notes: "大量筆記", description: "使用痕跡明顯", uploadTime: "2025-04-10", price: 180, images: [], adminStatus: "待審核", adminNote: "需確認筆記量" },
  { isbn: "9789861234564", title: "英語文法", category: "文學與人文類", condition: 7, notes: "無筆記", description: "封面良好", uploadTime: "2025-05-12", price: 120, images: [], adminStatus: "審核通過", adminNote: "符合上架規範" },
  { isbn: "9789861234565", title: "行為心理學", category: "社會科學類", condition: 6, notes: "少量筆記", description: "一般", uploadTime: "2025-06-01", price: 150, images: [], adminStatus: "審核不通過", adminNote: "缺頁" },
  { isbn: "9789861234566", title: "生物學概論", category: "醫學與健康類", condition: 9, notes: "無筆記", description: "近全新", uploadTime: "2025-07-15", price: 280, images: [], adminStatus: "審核通過", adminNote: "檢查封面及內頁" }
])

const sortedBooks = computed(() => {
  return books.value.slice().sort((a, b) => {
    const order = { "審核通過": 0, "待審核": 1, "審核不通過": 2 }
    return order[a.adminStatus] - order[b.adminStatus]
  })
})

const previewImage = ref(null)

function deleteBook(index) {
  if (confirm("確定要刪除這本書嗎？")) {
    books.value.splice(index, 1)
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
