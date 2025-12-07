<script setup>
import { ref, onMounted } from "vue"

// 登入事件
const emit = defineEmits(["admin-login-success"])

// 帳號密碼
const username = ref("")
const password = ref("")

function handleLogin() {
  if (username.value && password.value) {
    emit("admin-login-success") // 通知父層登入成功
  } else {
    alert("請輸入帳號和密碼！")
  }
}

// 存背景圖片
const catImg = ref("")

// 頁面載入抓貓咪
onMounted(async () => {
  try {
    const res = await fetch('https://api.thecatapi.com/v1/images/search', {
      headers: {
        'x-api-key': 'live_AOITW0dIvWcHI7Vdu2FCkLf0zUiX5KpQkD2rMEGkz309q4cIgODvoVlJAAKFAc8L'
      }
    })
    const data = await res.json()
    catImg.value = data[0].url
  } catch (err) {
    console.error('抓貓咪圖片失敗', err)
  }
})
</script>

<template>
  <div class="backgroundALL">
    <div class="login_page">
        <!-- 左邊貓咪區 -->
        <div class="cat_side" :style="{
          backgroundImage: catImg ? `url(${catImg})` : 'none',
          backgroundSize: 'contain',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          backgroundColor: '#bbbbbb69'
        }">
        </div>

        <!-- 右邊登入區 -->
        <div class="login_side">
          <div class="login_container card text-center">
            <div class="startlogin p-4">
              <h2 class="mb-0">管理員登入</h2>
            </div>

            <div class="input-group mb-4 mt-4 mx-auto" style="width: 70%;">
              <span class="input-group-text d-flex justify-content-center align-items-center" style="width: 20%;">帳號</span>
              <input type="text" class="form-control text-center" v-model="username" placeholder="Username">
            </div>

            <div class="input-group mb-4 mx-auto" style="width: 70%;">
              <span class="input-group-text d-flex justify-content-center align-items-center" style="width: 20%;">密碼</span>
              <input type="password" class="form-control text-center" v-model="password" placeholder="Password">
            </div>

            <div class="mb-4 mt-4">
              <button class="btn btn-primary" @click="handleLogin">登入</button>
            </div>
          </div>
        </div>
      </div>
  </div>
  
</template>

<style scoped>
.backgroundALL{
  margin-top: 90px;
}

.login_page {
  display: flex;
  min-height: 100vh;
  width: 100%;
}

/* 左邊貓咪區 */
.cat_side {
  flex: 1;
}

/* 右邊登入區 */
.login_side {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5; /* 避免透明 */
}

/* 登入框 */
.login_container {
  width: 80%;
  max-width: 800px;
  padding: 20px;
  border-radius: 10px;
}

.startlogin {
  background-color: gray;
  color: white;
  border-radius: 5px 5px 0 0;
  font-weight: bold;
}

.login_container button{
  width: 80px;
  height: 30px;
  border-radius: 20px;

  /* 垂直水平置中文字 這超酷到底為甚麼~*/
  display: flex;
  justify-content: center; /* 水平置中 */
  align-items: center;     /* 垂直置中 */
  margin: 0 auto;
}
.login_container button:hover{
  color: white;
  background: black;
}
</style>
