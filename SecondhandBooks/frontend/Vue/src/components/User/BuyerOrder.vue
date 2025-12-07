<template>
  <div class="container mt-4">
    <h2 class="text-center fw-bold mb-4">我的訂單</h2>
    <div class="table-responsive">
      <table class="table table-bordered table-striped table-hover text-center align-middle">
        <thead class="table-dark">
          <tr>
            <th>訂單編號</th>
            <th>賣家用戶</th>
            <th>書籍名稱</th>
            <th>isbn</th>
            <th>訂單金額</th>
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
            <td>{{ order.bookName }}</td>
            <td>{{ order.isbn }}</td>
            <td>{{ order.amount }}</td>
            <td>{{ order.location }}</td>
            <td>{{ order.date }}</td>
            <td>{{ order.time }}</td>
            <td>
              <span 
                :class="{
                  'badge bg-warning text-dark': order.status === '代面交',
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
                v-if="order.status === '代面交'"   
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
import { reactive } from 'vue';

const orders = reactive([
  { id: 1, orderNo: 'No.1', sellerName: 'seam', bookName: '資料庫系統概論', isbn: '9789865023456', amount: 350, location: '台北車站', date: '2025/11/10', time: '上午10:00', status: '代面交', orderDate: '2025/10/25' },
  { id: 2, orderNo: 'No.2', sellerName: 'luna', bookName: 'Java 程式設計', isbn: '9789864349281', amount: 420, location: '中山捷運站', date: '2025/11/12', time: '下午2:00', status: '代面交', orderDate: '2025/10/28' },
  { id: 3, orderNo: 'No.3', sellerName: 'sunny', bookName: 'Python 入門指南', isbn: '9789863471120', amount: 280, location: '板橋車站', date: '2025/11/15', time: '下午4:00', status: '交易完成', orderDate: '2025/10/30' },
]);

function cancelOrder(order) {
  if (confirm(`確定要取消訂單 ${order.orderNo} 嗎？`)) {
    order.status = '取消';
    // 可在這裡呼叫 API 更新後端
    // axios.post('/api/orders/cancel', { orderId: order.id })
    //   .then(() => console.log('取消成功'))
    //   .catch(err => console.error(err))
  }
}
</script>
