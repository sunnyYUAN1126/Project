<template>
  <div class="container mt-4">
    <h2 class="text-center fw-bold mb-4">我的訂單</h2>
    <div class="table-responsive">
      <table class="table table-bordered table-striped table-hover text-center align-middle">
        <thead class="table-dark">
          <tr>
            <th>訂單編號</th>
            <th>賣家用戶</th>
            <th>ISBN</th>
            <th>書籍名稱</th>
            <th>金額細項</th>
            <th>訂單合計</th>
            <th>面交地點</th>
            <th>面交日期</th>
            <th>面交時間</th>
            <th>訂單狀態</th>
            <th>下單日期</th>
            <th>取消訂單</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>{{ order.sellerName }}</td>
            <td class="p-0 align-middle">
              <div v-for="(code, index) in order.isbns" :key="index" class="py-2" :class="{'border-bottom': index < order.isbns.length - 1}">{{ code }}</div>
            </td>
            <td class="p-0 text-start align-middle">
              <div v-for="(name, index) in order.bookNames" :key="index" class="p-2" :class="{'border-bottom': index < order.bookNames.length - 1}">{{ name }}</div>
            </td>
            <td class="p-0 align-middle">
              <div v-for="(price, index) in order.prices" :key="index" class="py-2" :class="{'border-bottom': index < order.prices.length - 1}">{{ price }}</div>
            </td>

            <td>{{ order.amount }}</td>
            <td>{{ order.location }}</td>
            <td>{{ order.date }}</td>
            <td>{{ order.time }}</td>
            <td>
              <span 
                :class="{
                  'badge bg-warning text-dark': order.status === '待面交',

                  'badge bg-success': order.status === '交易完成',
                  'badge bg-danger': order.status === '取消'
                }"
              >
                {{ order.status }}
              </span>
            </td>
            <td>{{ order.orderDate }}</td>
            <td>
              <!-- 只有代面交才顯示 -->
              <button 
                v-if="order.status === '待面交'"   

                @click="cancelOrder(order)" 
                class="btn btn-outline-dark btn-sm"
              >
                取消訂單
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const orders = ref([]);

async function fetchOrders() {
  try {
    const response = await fetch('http://localhost:8080/api/orders', {
      credentials: 'include'
    });
    if (!response.ok) throw new Error("Failed to fetch orders");
    const data = await response.json();
    
    // Map backend DTO to frontend structure
    // Backend DTO: orderId, buyerId, sellerId, meetupLocation, meetupDate, meetupTime, status, createdAt, items (List), totalPrice
    orders.value = data.buying.map(o => ({
      id: o.orderId,
      orderNo: `No.${o.orderId}`,
      sellerName: o.sellerName, // Now using real seller account from DTO
      bookNames: o.items.map(i => i.productName),
      isbns: o.items.map(i => i.isbn),
      prices: o.items.map(i => i.price),
      amount: o.totalPrice,
      location: o.meetupLocation,
      date: o.meetupDate,
      time: o.meetupTime,
      status: o.status,
      orderDate: new Date(o.createdAt).toLocaleDateString()
    }));
  } catch (err) {
    console.error(err);
  }
}

async function cancelOrder(order) {
  if (confirm(`確定要取消訂單 ${order.orderNo} 嗎？`)) {
    try {
      const response = await fetch(`http://localhost:8080/api/orders/${order.id}/cancel`, {
        method: 'POST',
        credentials: 'include'
      });
      if (!response.ok) throw new Error("Failed to cancel");
      
      // Update local status
      order.status = '取消';
      alert("訂單已取消");
    } catch (err) {
      console.error(err);
      alert("取消失敗");
    }
  }
}

onMounted(() => {
  fetchOrders();
});
</script>

