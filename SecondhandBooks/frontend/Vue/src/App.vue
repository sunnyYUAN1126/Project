<script setup>
import { ref } from "vue"
import UserLogin from './components/Menue/UserLogin.vue'
import Register from "./components/Menue/register.vue"
import Home from "./components/Menue/home.vue"
import Shopping_cart from "./components/Menue/shopping_cart.vue"
import Buyer_menue from "./components/User/menue.vue"

import Adminmenue from "./components/Administrator/menue.vue"

// 頁面狀態
const currentPage = ref("home")
const homeRef = ref(null)

// 登入狀態
const isLoggedIn = ref(false)        // 一般會員登入
const adminIsLoggedIn = ref(false)   // 管理員登入
const userName = ref("")             // 使用者名稱
const userPicture = ref("")          // 使用者圖片

// ------------------------
// 頁面切換方法
// ------------------------
function showHomePage() { 
  currentPage.value = "home"
  // 重置商品詳細頁，回到商品列表
  setTimeout(() => {
    if (homeRef.value && homeRef.value.$refs.productRef) {
      homeRef.value.$refs.productRef.goBack()
    }
  }, 0)
}
function showLoginPage() { currentPage.value = "login" }
function showRegisterPage() { currentPage.value = "register" }
function showShoppingCartPage() { currentPage.value = "shopping_cart" }
function showMemberAreaPage() { currentPage.value = "Member_Area" }
function showAdministratorAreaPage() { currentPage.value = "AtorAreaPage" }
function showAdminHomePage() { currentPage.value = "adminHome" }

// ------------------------
// 使用者登入/登出
// ------------------------
function handleLoginSuccess(userData) {
  isLoggedIn.value = true
  userName.value = userData.account
  userPicture.value = userData.userPicture || ""
  currentPage.value = "home"
}
async function logout() {
  try {
    await fetch("http://localhost:8080/api/users/logout", {
      method: "POST",
    })
  } catch (error) {
    console.error("Logout error:", error)
  } finally {
    // 無論後端是否成功，前端都執行登出
    isLoggedIn.value = false
    userName.value = ""
    currentPage.value = "home"
  }
}

// ------------------------
// 管理員登入/登出
// ------------------------
function handleAdminLoginSuccess() {
  adminIsLoggedIn.value = true
  currentPage.value = "adminHome"
}
function adminLogout() {
  adminIsLoggedIn.value = false
  currentPage.value = "home"
}
</script>

<template>
  <div class="App_container">
    <!-- Navbar -->
    <nav class="menu navbar  navbar-expand-lg fixed-top">
      <div class="logo" @click="showHomePage">二手書網站 <i class="bi bi-book"></i> </div>
      <ul>
        <li @click="showShoppingCartPage">購物車 <i class="bi bi-cart4"></i></li>

        <!-- 管理員登入時 -->
        <template v-if="adminIsLoggedIn">
          <li @click="adminLogout">管理員登出</li>
          <li @click="showAdminHomePage">管理員後台</li>
        </template>

        <!-- 一般會員登入時 -->
        <template v-else-if="isLoggedIn">
          <li @click="logout">登出</li>
          <li @click="showMemberAreaPage" class="d-flex align-items-center">
            <img v-if="userPicture" :src="userPicture" alt="User" style="width: 30px; height: 30px; border-radius: 50%; margin-right: 8px; object-fit: cover;">
            <div v-else style="width: 30px; height: 30px; border-radius: 50%; margin-right: 8px; background-color: lightblue; display: flex; justify-content: center; align-items: center;">
              <i class="bi bi-person-fill" style="color: white;"></i>
            </div>
            {{ userName }}
          </li>
        </template>

        <!-- 都沒登入 -->
        <template v-else>
          <li @click="showLoginPage">登入</li>
        </template>


      </ul>
    </nav>

    <!-- 頁面內容 -->
    <Home ref="homeRef" v-if="currentPage === 'home'" />
    <Shopping_cart v-if="currentPage === 'shopping_cart'" />
    <Buyer_menue v-if="currentPage === 'Member_Area'" />

    <Adminmenue v-if="currentPage === 'adminHome'" />
    <UserLogin v-else-if="currentPage === 'login'" @login-success="handleLoginSuccess" @admin-login-success="handleAdminLoginSuccess" @login-Register="currentPage = 'register'" />
    <Register v-else-if="currentPage === 'register'" @register-success="currentPage = 'login'" />
  </div>
</template>

<style scoped>
* {
  padding: 0;
  margin: 0;
  list-style: none;
}

.menu {
  background: rgb(212, 211, 211);
  padding: 0 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.App_container .logo {
  color: black;
  font-size: 50px;
  padding: 10px 0px;
  cursor: pointer;
}
.App_container .logo:hover,
.App_container li:hover {
  color: rgb(123, 150, 173);
  font-weight: bold;
}

.App_container ul {
  display: flex;
  gap: 10px;
}


</style>
