<template>
  <div class="container mt-5">
    <div class="card p-4 shadow-sm">
      <h2 class="mb-4 text-center">新增書籍</h2>

      <form @submit.prevent="submitBook">
        <!-- ISBN -->
        <div class="mb-3">
          <label class="form-label">ISBN：</label>
          <input class="form-control" v-model="form.isbn" required />
        </div>

        <!-- 書名 -->
        <div class="mb-3">
          <label class="form-label">書名：</label>
          <input class="form-control" v-model="form.title" required />
        </div>

        <!-- 新增：書籍作者 -->
        <div class="mb-3">
          <label class="form-label">書籍作者：</label>
          <input class="form-control" v-model="form.author" required />
        </div>

        <!-- 新增：書籍出版社 -->
        <div class="mb-3">
          <label class="form-label">書籍出版社：</label>
          <input class="form-control" v-model="form.publisher" required />
        </div>

        <!-- 🔥 新增：分類 -->
        <div class="mb-3">
          <label class="form-label">分類：</label>
          <select class="form-select bg-light-gray" v-model="form.category" required>
            <option disabled value="">請選擇分類</option>
            <option value="文學與人文類">文學與人文類</option>
            <option value="社會科學類">社會科學類</option>
            <option value="商業與管理類">商業與管理類</option>
            <option value="理學類">理工與資訊類</option>
            <option value="醫學與健康類">醫學與健康類</option>
          </select>
        </div>

        <!-- 成新 & 筆記 -->
        <div class="row mb-3">
          <div class="col">
            <label class="form-label">幾成新：</label>
            <select class="form-select bg-light-gray" v-model="form.condition">
              <option value="9">9成新</option>
              <option value="8">8成新</option>
              <option value="7">7成新</option>
              <option value="6">6成新</option>
              <option value="5">5成新</option>
              <option value="4">4成新</option>
              <option value="3">3成新</option>
              <option value="2">2成新</option>
              <option value="1">1成新</option>
            </select>
          </div>
          <div class="col">
            <label class="form-label">是否有筆記：</label>
            <select class="form-select bg-light-gray" v-model="form.notes">
              <option value="none">無筆記</option>
              <option value="few">少量筆記</option>
              <option value="many">大量筆記</option>
            </select>
          </div>
        </div>

        <!-- 書況描述 -->
        <div class="mb-3">
          <label class="form-label">書況描述：</label>
          <textarea class="form-control" v-model="form.description" rows="3" />
        </div>

        <!-- 二手價 -->
        <div class="mb-3">
          <label class="form-label">二手價：</label>
          <input class="form-control" type="number" v-model="form.price" min="0" />
        </div>

        <!-- 上架日期 & 數量 -->
        <div class="row mb-3">
          <div class="col">
            <label class="form-label">上架日期：</label>
            <input type="date" class="form-control" v-model="form.uploadTime" disabled />
          </div>
          <div class="col">
            <label class="form-label">數量：</label>
            <input class="form-control" type="number" v-model="form.quantity" disabled />
          </div>
        </div>

        <!-- 圖片上傳 -->
        <div class="mb-3">
          <label class="form-label">上傳圖片（至少1張，最多4張）：</label>
          <input type="file" @change="handleFiles" accept="image/*" multiple />
        </div>

        <!-- 圖片預覽 -->
        <div class="mb-3 d-flex gap-3 flex-wrap">
          <div
            v-for="(img, index) in imagePreviews"
            :key="index"
            class="position-relative"
          >
            <img
              :src="img"
              alt="preview"
              class="border rounded"
              style="width:120px; height:120px; object-fit:cover;"
            />
            <button
              type="button"
              @click="removeImage(index)"
              class="btn btn-sm btn-danger position-absolute"
              style="top:0; right:0;"
            >
              ×
            </button>
          </div>
        </div>

        <!-- 提交 -->
        <div class="text-center">
          <button class="btn btn-primary px-5">新增書籍</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"

const today = new Date().toISOString().slice(0, 10)

const form = reactive({
  isbn: "",
  title: "",
  author: "",       // ✅ 新增：書籍作者
  publisher: "",    // ✅ 新增：書籍出版社
  category: "",     // ✅ 新增：分類
  condition: "",
  notes: "",
  description: "",
  uploadTime: today,
  price: "",
  quantity: 1
})

const images = ref([])
const imagePreviews = ref([])

function handleFiles(e) {
  const files = Array.from(e.target.files)
  for (let file of files) {
    if (images.value.length >= 4) {
      alert("最多只能上傳4張圖片！")
      break
    }
    images.value.push(file)
    const reader = new FileReader()
    reader.onload = (event) => {
      imagePreviews.value.push(event.target.result)
    }
    reader.readAsDataURL(file)
  }
}

function removeImage(index) {
  images.value.splice(index, 1)
  imagePreviews.value.splice(index, 1)
}

function submitBook() {
  if (!form.category) {
    alert("請選擇分類！")
    return
  }

  if (images.value.length === 0) {
    alert("請至少上傳一張圖片！")
    return
  }

  console.log("送出書籍資料：", form)
  console.log("圖片檔案：", images.value)
  alert("新增成功！（假的）")
}
</script>

<style scoped>
.card {
  max-width: 700px;
  margin: 0 auto;
  border-radius: 12px;
}
.position-relative {
  position: relative;
}
.bg-light-gray {
  background-color: #f2f2f2;
}
</style>
