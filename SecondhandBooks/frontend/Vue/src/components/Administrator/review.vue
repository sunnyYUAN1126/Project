<template>
  <div class="container mt-4">
    <h2 class="mb-4 fw-bold">賣家上架的二手書審核</h2>
    <div class="table-responsive">
      <table class="table table-hover table-bordered table-striped text-center align-middle">
      <thead class="table-dark">
        <tr>
          <th style="min-width: 100px;">會員帳號</th>
          <th>ISBN</th>
          <th style="min-width: 200px;">書籍資訊</th>
          <th style="min-width: 100px;">分類</th>
          <th style="min-width: 200px;">二手書狀況</th>
          <th>新增日期</th>
          <th style="min-width: 70px;">二手價</th>
          <th style="min-width: 100px;">圖片</th>
          <th style="min-width: 150px;">審核</th>
          <th style="min-width: 150px;">管理員備註</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="(book, index) in books" :key="index">
          <td>{{ book.user }}</td>
          <td>{{ book.isbn }}</td>
          <td class="text-start ps-3">
            <div class="fw-bold mb-1">{{ book.title }}</div>
            <div class="small text-muted">作者：{{ book.author }}</div>
            <div class="small text-muted">出版社：{{ book.publisher }}</div>
          </td>
          <td>{{ book.category }}</td>
          <td class="text-start ps-3">
             <div ><strong>幾成新：</strong>{{ book.condition }}</div>
             <div ><strong>筆記：</strong>{{ book.notes }}</div>
             <div ><strong>描述：</strong>{{ book.status }}</div>
          </td>
          <td>{{ book.date }}</td>
          <td>{{ book.price }}</td>
          <td>
            <div class="d-flex flex-column align-items-center gap-2">
              <img
                v-for="(img, i) in book.images"
                :key="i"
                :src="img"
                alt="book image"
                class="img-thumbnail object-fit-cover"
                style="width: 60px; height: 60px; cursor: pointer;"
                @click="openModal(img)"
              />
            </div>
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
import { ref, onMounted } from "vue"
import axios from "axios"

const books = ref([])

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}/${m}/${d}`
}

const fetchPendingBooks = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/books/pending", {
      withCredentials: true
    })
    // Map backend response using DTO/Entity fields to frontend display fields
    books.value = response.data.map(book => ({
      id: book.productId,
      user: book.sellerAccount,
      isbn: book.isbn,
      title: book.name,
      author: book.author,
      publisher: book.publisher,
      category: book.category,
      condition: book.productNew,
      notes: book.productClassNote, // "有無筆記" mapped to product_class_note or "product_note"?
                                    // Based on Book.java: productClassNote is "product_class_note", productNote is "product_note".
                                    // In review.vue dummy: notes="無筆記", status="良好".
                                    // In Book.java: productClassNote -> "有無筆記" (based on AddBook logic usually)
                                    // Let's assume productClassNote is notes, productNote is status/description.
      status: book.productNote,
      date: formatDate(book.createdAt),
      price: book.price,
      images: book.images || [],
      adminNote: book.adminNote || ""
    }))
  } catch (error) {
    console.error("Error fetching pending books:", error)
    alert("無法取得待審核書籍")
  }
}

onMounted(() => {
  fetchPendingBooks()
})

const approve = async (book) => {
  try {
    const payload = {
      approved: true,
      note: book.adminNote
    }
    await axios.post(`http://localhost:8080/api/books/review/${book.id}`, payload, {
      withCredentials: true
    })
    alert(`已同意${book.user}上架書籍\n書籍ID:${book.id}\nISBN: ${book.isbn}\n書名: ${book.title}`)
    fetchPendingBooks() // Refresh list
  } catch (error) {
    console.error("Error approving book:", error)
    alert("審核失敗")
  }
}

const reject = async (book) => {
  try {
    const payload = {
      approved: false,
      note: book.adminNote
    }
    await axios.post(`http://localhost:8080/api/books/review/${book.id}`, payload, {
      withCredentials: true
    })
    alert(`已拒絕${book.user}上架書籍\n書籍ID:${book.id}\nISBN: ${book.isbn}\n書名: ${book.title}`)
    fetchPendingBooks() // Refresh list
  } catch (error) {
    console.error("Error rejecting book:", error)
    alert("審核失敗")
  }
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
