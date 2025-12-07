<script setup>
import { ref } from "vue"

// 宣告可以往父層發出事件
const emit = defineEmits(["register-success"])

const username = ref("")
const department = ref("")
const studentId = ref("")
const password = ref("")
const showPassword = ref(false)

async function handleRegister() {
  // 這裡可以先做一些基本檢查
  if (!username.value || !department.value || !studentId.value || !password.value) {
    alert("請填寫完整註冊資料！")
    return
  }

  try {
    const response = await fetch("http://localhost:8080/api/users/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        account: username.value,
        password: password.value,
        studentId: studentId.value,
        department: department.value,
      }),
    })

    if (response.ok) {
      alert("註冊成功！請登入。")
      emit("register-success")
    } else {
      alert("註冊失敗，請稍後再試。")
    }
  } catch (error) {
    console.error("Registration error:", error)
    alert("連線錯誤，請檢查網路或伺服器狀態。")
  }
}
</script>

<template>

<div class="login_container card mx-auto text-center" style="width: 40%;">
  <!-- 標題區 -->
  <div class="startlogin p-4" style="background-color: gray; color: white; border-radius: 5px 5px 0 0;">
    <h2 class="mb-0">註冊新帳號</h2>
  </div>

  <!-- 帳號 -->
  <div class="input-group mb-4 mt-4 mx-auto" style="width: 70%;">
    <span class="input-group-text d-flex justify-content-center align-items-center" style="width: 30%;">帳號</span>
    <input type="text" class="form-control text-center" v-model="username" placeholder="Username">
  </div>

  <!-- 系所 -->
  <div class="input-group mb-4  mx-auto" style="width: 70%;">
    <span class="input-group-text d-flex justify-content-center align-items-center" style="width: 30%;">系所</span>
    <input type="text" class="form-control text-center" v-model="department" placeholder="Department">
  </div>

  <!-- 學號 -->
  <div class="input-group mb-4  mx-auto" style="width: 70%;">
    <span class="input-group-text d-flex justify-content-center align-items-center" style="width: 30%;">學號</span>
    <input type="text" class="form-control text-center" v-model="studentId" placeholder="studentId">
  </div>

  <!-- 密碼 -->
  <div class="input-group mb-4  mx-auto" style="width: 70%;">
    <span class="input-group-text d-flex justify-content-center align-items-center" style="width: 30%;">密碼</span>
    <input :type="showPassword ? 'text' : 'password'" class="form-control text-center" v-model="password" placeholder="Password">
  </div>

  <!-- 顯示密碼 -->
  <div class="mb-4 d-flex justify-content-center align-items-center">
      <input type="checkbox" id="showPwd" v-model="showPassword" class="form-check-input me-2">
      <label for="showPwd" class="form-check-label">顯示密碼</label>
  </div>

  <!-- 註冊 -->
  <div class="mb-4 mt-4 d-flex justify-content-center align-items-center gap-2">
    <button class="btn btn-secondary" @click="handleRegister">註冊</button>
  </div>
</div>
</template>

<style scoped>
*{
    padding: 0;
    margin: 0;
    list-style: none;
}
.login_container{
  margin: 200px;
}


.login_container button{
  width: 50px;
  height: 30px;
  border-radius: 20px;
}
.login_container button:hover{
  color: white;
  background: black;

}




</style>