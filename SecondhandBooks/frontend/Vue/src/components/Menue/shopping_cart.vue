<script setup>
import { ref, computed } from 'vue';

// 購物車資料，每個商品帶 seller 屬性
var cart = ref([
  { name: '貓咪去旅行', price: 300, seller: '貓賣家' },
  { name: '123木頭人', price: 200, seller: '貓賣家' },
  { name: '我們這一家', price: 250, seller: '狗賣家' }
]);

// 分組：依賣家分組
var groupedCart = computed(() => {
  const groups = {};
  cart.value.forEach(item => {
    if (!groups[item.seller]) groups[item.seller] = [];
    groups[item.seller].push(item);
  });
  return groups;
});

// 每個賣家的小計
function sellerSubtotal(items) {
  return items.reduce((sum, item) => sum + item.price, 0);
}

// 總金額
var total = computed(() => cart.value.reduce((sum, item) => sum + item.price, 0));

// 面交資訊（每個賣家一個物件）
var checkoutInfo = ref({});

// 面交地點選項
const meetingPlaces = [
  '管院前門',
  '文學院前門',
  '理學院前門',
  '醫學院前門'
];

// 面交時間選項
const meetingTimes = [
  '第一節下課',
  '第二節下課',
  '第三節下課',
  '第四節下課',
  '第五節下課',
  '第六節下課',
  '第七節下課',
  '第八節下課'
];

// 控制是否顯示確認頁
var showCheckoutConfirm = ref(false);

// 刪除商品
function removeItem(index) {
  cart.value.splice(index, 1);
}

// 前往結帳
function checkout() {
  Object.keys(groupedCart.value).forEach(seller => {
    if (!checkoutInfo.value[seller]) {
      checkoutInfo.value[seller] = { location: '', date: '', time: '' };
    }
  });
  showCheckoutConfirm.value = true;
}

// 確認結帳
function confirmCheckout() {
  for (const seller of Object.keys(groupedCart.value)) {
    const info = checkoutInfo.value[seller];
    if (!info.location || !info.date || !info.time) {
      alert(`請完整填寫 ${seller} 的面交地點、日期與時間！`);
      return;
    }
  }

  let message = `結帳成功！\n\n`;
  for (const seller of Object.keys(groupedCart.value)) {
    const info = checkoutInfo.value[seller];
    message += `${seller} 小計 $${sellerSubtotal(groupedCart.value[seller])}\n`;
    message += `面交地點：${info.location}\n面交日期：${info.date}\n面交時間：${info.time}\n\n`;
  }
  message += `總金額 $${total.value}`;
  alert(message);

  cart.value = [];
  showCheckoutConfirm.value = false;
  checkoutInfo.value = {};
}
</script>

<template>
  <div class="cart-container">
    <!-- 購物車清單 -->
    <div v-if="!showCheckoutConfirm">
      <h2>購物車內容</h2>

      <div v-for="(item, i) in cart" :key="i" class="cart-item">
        <div class="cart-name">{{ item.name }}（{{ item.seller }}）</div>
        <div class="cart-price">二手價 ${{ item.price }}</div>
        <button class="btn-delete" @click="removeItem(i)">刪除</button>
      </div>

      <div class="cart-summary">
        總金額：${{ total }}
      </div>

      <button class="btn-checkout" @click="checkout">前往結帳</button>
    </div>

    <!-- 訂單確認 -->
    <div v-else>
      <h2>訂單確認</h2>

      <div v-for="(items, seller) in groupedCart" :key="seller" class="seller-block">
        <h3>{{ seller }}</h3>

        <div v-for="(item, i) in items" :key="i" class="cart-item">
          <div class="cart-name">{{ item.name }}</div>
          <div class="cart-price">二手價 ${{ item.price }}</div>
        </div>

        <div class="cart-summary">
          小計：${{ sellerSubtotal(items) }}
        </div>

        <div class="location-input">
          <label>面交地點：</label>
          <select v-model="checkoutInfo[seller].location">
            <option disabled value="">請選擇面交地點</option>
            <option v-for="place in meetingPlaces" :key="place" :value="place">
              {{ place }}
            </option>
          </select>
        </div>

        <div class="location-input">
          <label>面交日期：</label>
          <input type="date" v-model="checkoutInfo[seller].date" />
        </div>

        <div class="location-input">
          <label>面交時間：</label>
          <select v-model="checkoutInfo[seller].time">
            <option disabled value="">請選擇面交時間</option>
            <option v-for="time in meetingTimes" :key="time" :value="time">
              {{ time }}
            </option>
          </select>
        </div>

        <hr />
      </div>

      <div class="cart-summary">
        總金額：${{ total }}
      </div>

      <button class="btn-checkout" @click="confirmCheckout">立即結帳</button>
      <button class="btn-back" @click="showCheckoutConfirm = false">返回上一頁</button>
    </div>
  </div>
</template>

<style scoped>
.cart-container {
  width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: white;
  margin-top: 100px;
}

.cart-item {
  padding: 5px 0;
}

.cart-name {
  font-weight: 500;
}

.cart-price {
  color: gray;
  font-size: 14px;
  margin-top: 3px;
}

.btn-delete {
  color: red;
  margin-top: 5px;
  background: none;
  border: none;
  cursor: pointer;
}

.cart-summary {
  text-align: right;
  font-weight: bold;
  margin-top: 10px;
}

.location-input {
  margin-top: 5px;
}

.location-input label {
  display: block;
  font-size: 14px;
  margin-bottom: 3px;
}

.location-input input,
.location-input select {
  width: 100%;
  padding: 5px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

.btn-checkout {
  width: 100%;
  margin-top: 10px;
  padding: 8px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-checkout:hover {
  background: #0069d9;
}

.btn-back {
  width: 100%;
  margin-top: 10px;
  padding: 8px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-back:hover {
  background: #5a6268;
}

hr {
  margin: 10px 0;
}
</style>
