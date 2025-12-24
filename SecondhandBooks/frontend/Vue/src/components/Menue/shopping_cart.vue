<script setup>
import { ref, computed, onMounted, watch } from 'vue';

const props = defineProps({
  userId: {
    type: [Number, String],
    default: null
  }
});

// 購物車資料
var cart = ref([]);

// 抓取購物車資料
async function fetchCart() {
  if (!props.userId) {
    cart.value = [];
    return;
  }
  try {
    const response = await fetch(`http://localhost:8080/api/cart/${props.userId}`, {
        credentials: 'include'
    });
    if (!response.ok) {
        throw new Error("Failed to fetch cart");
    }
    const data = await response.json();
    
    // 轉換成前端需要的格式 (API return CartItemDTO)
    // DTO fields: cartId, productId, productName, productPrice, sellerName, coverImage, sellerId
    cart.value = data.map(item => ({
      cartId: item.cartId,
      productId: item.productId,
      name: item.productName,
      price: item.productPrice,
      seller: item.sellerName || '未知賣家',
      sellerId: item.sellerId, // New field
      coverImage: item.coverImage
    }));
  } catch (error) {
    console.error("Fetch cart error:", error);
    // alert("無法取得購物車資料");
  }
}

// 監聽 userId 改變 (例如重新登入)
watch(() => props.userId, (newVal) => {
  if (newVal) {
    fetchCart();
  } else {
    cart.value = [];
  }
}, { immediate: true });

// 分組：依賣家分組 (Use Seller Name as key for display, assume unique or acceptable)
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
async function removeItem(index, cartId) {
  if (!confirm("確定要刪除嗎？")) return;
  
  try {
    const response = await fetch(`http://localhost:8080/api/cart/${cartId}`, {
        method: "DELETE",
        credentials: 'include'
    });
    if (!response.ok) {
        throw new Error("Failed to remove item");
    }
    
    // 成功後從本地陣列移除
    const cartIndex = cart.value.findIndex(c => c.cartId === cartId);
    if (cartIndex !== -1) {
      cart.value.splice(cartIndex, 1);
    }
  } catch (error) {
    console.error("Remove item error:", error);
    alert("刪除失敗");
  }
}

// 前往結帳
function checkout() {
  if (cart.value.length === 0) {
    alert("購物車是空的！");
    return;
  }
  Object.keys(groupedCart.value).forEach(seller => {
    if (!checkoutInfo.value[seller]) {
      checkoutInfo.value[seller] = { location: '', date: '', time: '' };
    }
  });
  showCheckoutConfirm.value = true;
}

// 確認結帳
async function confirmCheckout() {
  for (const seller of Object.keys(groupedCart.value)) {
    const info = checkoutInfo.value[seller];
    if (!info.location || !info.date || !info.time) {
      alert(`請完整填寫 ${seller} 的面交地點、日期與時間！`);
      return;
    }
  }

  // Iterate over each seller group and create order
  try {
      for (const sellerName of Object.keys(groupedCart.value)) {
          const items = groupedCart.value[sellerName];
          if (items.length === 0) continue;
          
          const sellerId = items[0].sellerId;
          const info = checkoutInfo.value[sellerName];
          
          const request = {
              sellerId: sellerId,
              meetupLocation: info.location,
              meetupDate: info.date,
              meetupTime: info.time
          };
          
          const response = await fetch('http://localhost:8080/api/orders/checkout', {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify(request),
              credentials: 'include'
          });
          
          if (!response.ok) {
              const errMsg = await response.text();
              throw new Error(`Failed to checkout for seller ${sellerName}: ${errMsg}`);
          }
      }
      
      alert("結帳成功！");
      cart.value = [];
      showCheckoutConfirm.value = false;
      checkoutInfo.value = {};
      
  } catch (e) {
      console.error(e);
      alert("結帳發生錯誤: " + e.message);
  }
}

</script>

<template>
  <div class="cart-container">
    <div v-if="!userId" class="text-center">
      <p>請先登入以檢視購物車</p>
    </div>

    <template v-else>
      <!-- 購物車清單 -->
      <div v-if="!showCheckoutConfirm">
        <h2>購物車內容</h2>
        <div v-if="cart.length === 0" class="text-muted">目前的購物車是空的</div>

        <div v-for="(item, i) in cart" :key="item.cartId" class="cart-item d-flex justify-content-between align-items-center border-bottom py-2">
          <div>
            <div class="cart-name">{{ item.name }} <span class="text-muted" style="font-size: 0.9em;">({{ item.seller }})</span></div>
            <div class="cart-price">二手價 ${{ item.price }}</div>
          </div>
          <button class="btn-delete" @click="removeItem(i, item.cartId)">刪除</button>
        </div>

        <div class="cart-summary" v-if="cart.length > 0">
          總金額：${{ total }}
        </div>

        <button class="btn-checkout" @click="checkout" v-if="cart.length > 0">前往結帳</button>
      </div>

      <!-- 訂單確認 -->
      <div v-else>
        <h2>訂單確認</h2>

        <div v-for="(items, seller) in groupedCart" :key="seller" class="seller-block mb-4 p-3 border rounded">
          <h3 class="h5 mb-3 border-bottom pb-2">賣家:{{ seller }}</h3>

          <div v-for="(item, i) in items" :key="item.cartId" class="cart-item mb-2">
            <div class="d-flex justify-content-between">
               <span>{{ item.name }}</span>
               <span>${{ item.price }}</span>
            </div>
          </div>

          <div class="cart-summary mt-2">
            小計：${{ sellerSubtotal(items) }}
          </div>

          <div class="location-input mt-3">
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
        </div>

        <div class="cart-summary mb-3 p-2 bg-light rounded">
          總金額：${{ total }}
        </div>

        <button class="btn-checkout mb-2" @click="confirmCheckout">立即結帳</button>
        <button class="btn-back" @click="showCheckoutConfirm = false">返回上一頁</button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.cart-container {
  width: 100%;
  max-width: 600px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: white;
  margin-top: 100px;
}

.cart-item {
  /* padding: 5px 0; */
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
  background: none;
  border: 1px solid red;
  border-radius: 5px;
  padding: 2px 8px;
  cursor: pointer;
  font-size: 14px;
}
.btn-delete:hover {
  background-color: #fff0f0;
}

.cart-summary {
  text-align: right;
  font-weight: bold;
  margin-top: 10px;
  font-size: 1.1em;
}

.location-input {
  margin-top: 10px;
}

.location-input label {
  display: block;
  font-size: 14px;
  margin-bottom: 3px;
  font-weight: bold;
}

.location-input input,
.location-input select {
  width: 100%;
  padding: 8px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

.btn-checkout {
  width: 100%;
  margin-top: 15px;
  padding: 10px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  font-weight: bold;
}

.btn-checkout:hover {
  background: #218838;
}

.btn-back {
  width: 100%;
  margin-top: 10px;
  padding: 10px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-back:hover {
  background: #5a6268;
}

.text-muted {
  color: #6c757d;
}
</style>

