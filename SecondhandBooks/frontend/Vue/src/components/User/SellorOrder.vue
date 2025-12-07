<template>
  <div class="container mt-4" >
    <!-- 切換按鈕 -->
    <!-- 切換按鈕 -->
    <div class="mb-4 d-flex gap-2 my-5">
      <button
        @click="currentTab = 'current'"
        :class="currentTab === 'current' ? 'btn btn-dark' : 'btn btn-outline-dark'"
      >
        目前訂單
      </button>
      <button
        @click="currentTab = 'history'"
        :class="currentTab === 'history' ? 'btn btn-dark' : 'btn btn-outline-dark'"
      >
        交易歷史
      </button>
    </div>

    <!-- 目前訂單 -->
    <div v-show="currentTab === 'current'" class="table-responsive mb-4">
      <h3 class="fw-bold mb-4">目前訂單</h3>
      <table class="table table-striped table-hover table-bordered text-center align-middle">
        <thead class="table-light">
          <tr>
            <th>訂單編號</th>
            <th>訂單用戶</th>
            <th>書籍名稱</th>
            <th>isbn</th>
            <th>訂單金額</th>
            <th>面交地點</th>
            <th>面交日期</th>
            <th>面交時間</th>
            <th>訂單狀態</th>
            <th>下單日期</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in currentOrders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>{{ order.user }}</td>
            <td>{{ order.bookName }}</td>
            <td>{{ order.isbn }}</td>
            <td>{{ order.amount }}</td>
            <td>{{ order.location }}</td>
            <td>{{ order.date }}</td>
            <td>{{ order.time }}</td>
            <td>
              <select 
                v-model="order.status" 
                @change="handleStatusChange(order)" 
                :disabled="order.status !== '代面交'"
                class="form-select form-select-sm"
              >
                <option>代面交</option>
                <option>交易完成</option>
                <option>取消</option>
              </select>
            </td>
            <td>{{ order.orderDate }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 歷史訂單 -->
    <div v-show="currentTab === 'history'" class="table-responsive">
      <h3 class="fw-bold mb-4">交易歷史</h3>
      <table class="table table-striped table-hover table-bordered text-center align-middle">
        <thead class="table-light">
          <tr>
            <th>訂單編號</th>
            <th>訂單用戶</th>
            <th>書籍名稱</th>
            <th>isbn</th>
            <th>訂單金額</th>
            <th>面交地點</th>
            <th>面交日期</th>
            <th>面交時間</th>
            <th>訂單狀態</th>
            <th>下單日期</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in historyOrders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>{{ order.user }}</td>
            <td>{{ order.bookName }}</td>
            <td>{{ order.isbn }}</td>
            <td>{{ order.amount }}</td>
            <td>{{ order.location }}</td>
            <td>{{ order.date }}</td>
            <td>{{ order.time }}</td>
            <td>
              <span 
                :class="{
                  'text-warning': order.status === '代面交',
                  'text-success': order.status === '交易完成',
                  'text-danger': order.status === '取消'
                }"
                class="fw-bold"
              >
                {{ order.status }}
              </span>
            </td>
            <td>{{ order.orderDate }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';

const currentTab = ref('current'); // current / history

const currentOrders = reactive([
  { id: 1, orderNo: 'No.1', user: 'seam', bookName: '資料庫系統概論', isbn: '9789865023456', amount: 350, location: '台北車站', date: '2025/11/10', time: '上午10:00', status: '代面交', orderDate: '2025/10/25' },
  { id: 2, orderNo: 'No.2', user: 'luna', bookName: 'Java 程式設計', isbn: '9789864349281', amount: 420, location: '中山捷運站', date: '2025/11/12', time: '下午2:00', status: '代面交', orderDate: '2025/10/28' },
  { id: 3, orderNo: 'No.3', user: 'sunny', bookName: 'Python 入門指南', isbn: '9789863471120', amount: 280, location: '板橋車站', date: '2025/11/15', time: '下午4:00', status: '代面交', orderDate: '2025/10/30' },
]);

const historyOrders = reactive([
  { id: 4, orderNo: 'No.4', user: 'mimi', bookName: '計算機概論', isbn: '9789863124989', amount: 300, location: '淡水捷運站', date: '2025/09/10', time: '下午3:00', status: '交易完成', orderDate: '2025/09/01' },
  { id: 5, orderNo: 'No.5', user: 'alex', bookName: '網頁設計 HTML+CSS', isbn: '9789861234567', amount: 250, location: '士林夜市入口', date: '2025/09/15', time: '晚上7:00', status: '交易完成', orderDate: '2025/09/05' },
  { id: 6, orderNo: 'No.6', user: 'ruby', bookName: '作業系統 OS', isbn: '9789863128741', amount: 400, location: '新竹火車站', date: '2025/09/20', time: '上午11:30', status: '交易完成', orderDate: '2025/09/10' },
  { id: 7, orderNo: 'No.7', user: 'tommy', bookName: '資料結構（新版）', isbn: '9789863217894', amount: 380, location: '台大校門口', date: '2025/10/05', time: '下午5:00', status: '取消', orderDate: '2025/09/28' },
  { id: 8, orderNo: 'No.8', user: 'cindy', bookName: '統計學入門', isbn: '9789861123341', amount: 300, location: '高雄巨蛋', date: '2025/10/08', time: '下午1:00', status: '取消', orderDate: '2025/09/29' },
  { id: 9, orderNo: 'No.9', user: 'enzo', bookName: '演算法（進階版）', isbn: '9789863125587', amount: 500, location: '台中火車站', date: '2025/10/10', time: '下午6:00', status: '取消', orderDate: '2025/10/01' },
]);

function handleStatusChange(order) {
  if (order.status === '交易完成' || order.status === '取消') {
    const confirmed = confirm(`你確定要將訂單 ${order.orderNo} 設為「${order.status}」嗎？`);
    if (!confirmed) {
      order.status = '代面交';
      return;
    }
    currentOrders.splice(currentOrders.indexOf(order), 1);
    historyOrders.push(order);
  }
}
</script>
