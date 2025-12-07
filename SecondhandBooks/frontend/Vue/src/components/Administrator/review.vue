<template>
  <div class="container mt-4">
    <h2 class="mb-4 fw-bold">賣家上架的二手書審核</h2>
    <div class="table-responsive">
      <table class="table table-hover table-bordered table-striped text-center align-middle">
      <thead class="table-dark">
        <tr>
          <th>會員帳號</th>
          <th>ISBN</th>
          <th>書名</th>
          <th>書籍作者</th>
          <th>書籍出版社</th>
          <th>分類</th>
          <th>幾成新</th>
          <th>有無筆記</th>
          <th>書況描述</th>
          <th>上架日期</th>
          <th>二手價</th>
          <th>圖片</th>
          <th>審核</th>
          <th>管理員備註</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="(book, index) in books" :key="index">
          <td>{{ book.user }}</td>
          <td>{{ book.isbn }}</td>
          <td>{{ book.title }}</td>
          <td>{{ book.author }}</td>
          <td>{{ book.publisher }}</td>
          <td>{{ book.category }}</td>
          <td>{{ book.condition }}</td>
          <td>{{ book.notes }}</td>
          <td>{{ book.status }}</td>
          <td>{{ book.date }}</td>
          <td>{{ book.price }}</td>
          <td class="d-flex justify-content-center gap-2">
            <img
              v-for="(img, i) in book.images"
              :key="i"
              :src="img"
              alt="book image"
              class="img-thumbnail object-fit-cover"
              style="width: 60px; height: 60px; cursor: pointer;"
              @click="openModal(img)"
            />
          </td>
          <td>
            <button class="btn btn-success btn-sm me-2" @click="approve(book)">同意</button>
            <button class="btn btn-danger btn-sm" @click="reject(book)">不同意</button>
          </td>
          <td>
            <input type="text" v-model="book.adminNote" placeholder="備註" class="form-control form-control-sm" />
          </td>
        </tr>
      </tbody>
      </table>
    </div>

    <!-- Image Modal -->
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
  </div>
</template>

<script setup>
import { ref } from "vue"

const books = ref([
  {
    user: "user001",
    isbn: "9789861234560",
    title: "資料庫系統概論",
    author: "Date, C. J.",
    publisher: "Pearson",
    category: "商業與管理類",
    condition: "九成新",
    notes: "無筆記",
    status: "良好",
    date: "2025/11/18",
    price: "$250",
    images: [
      "https://via.placeholder.com/60x60?text=封面1",
      "https://via.placeholder.com/60x60?text=封面2",
      "https://via.placeholder.com/60x60?text=封面3"
    ],
    adminNote: ""
  },
  {
    user: "user002",
    isbn: "9789866543210",
    title: "Java 程式設計",
    author: "Liang, Y. Daniel",
    publisher: "Pearson",
    category: "社會科學類",
    condition: "七成新",
    notes: "有筆記",
    status: "普通",
    date: "2025/11/17",
    price: "$180",
    images: [
      "https://via.placeholder.com/60x60?text=封面1",
      "https://via.placeholder.com/60x60?text=封面2"
    ],
    adminNote: ""
  }
])

const approve = (book) => {
  alert(`已同意：${book.user} 的書籍上架`)
}

const reject = (book) => {
  alert(`已拒絕：${book.user} 的書籍上架`)
}

// Modal logic
const showModal = ref(false)
const currentImage = ref("")

const openModal = (img) => {
  currentImage.value = img
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  currentImage.value = ""
}
</script>
