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
            <th>isbn</th>
            <th>書籍名稱</th>
            <th>金額細項</th>
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
              <select 
                v-model="order.status" 
                @change="handleStatusChange(order)" 
                :disabled="order.status !== '待面交'"
                class="form-select form-select-sm"
              >
                <option>待面交</option>
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
            <th>isbn</th>
            <th>書籍名稱</th>
            <th>金額細項</th>
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
                  'text-warning': order.status === '待面交',

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
import { reactive, ref, onMounted } from 'vue';

const currentTab = ref('current'); // current / history

const currentOrders = ref([]);
const historyOrders = ref([]);

async function fetchOrders() {
  try {
    const response = await fetch('http://localhost:8080/api/orders', {
      credentials: 'include'
    });
    if (!response.ok) throw new Error("Failed to fetch orders");
    const data = await response.json();
    
    // Clear arrays
    currentOrders.value = [];
    historyOrders.value = [];

    // Map and split
    data.selling.forEach(o => {
      const order = {
        id: o.orderId,
        orderNo: `No.${o.orderId}`,
        user: `買家 ${o.buyerId}`, // Placeholder for buyer name
        bookNames: o.items.map(i => i.productName),
        isbns: o.items.map(i => i.isbn),
        prices: o.items.map(i => i.price),
        amount: o.totalPrice,
        location: o.meetupLocation,
        date: o.meetupDate,
        time: o.meetupTime,
        status: o.status,
        orderDate: new Date(o.createdAt).toLocaleDateString()
      };
      
      if (order.status === '待面交') {
        currentOrders.value.push(order);
      } else {
        historyOrders.value.push(order);
      }
    });

  } catch (err) {
    console.error(err);
  }
}

async function handleStatusChange(order) {
  const newStatus = order.status;
  // Note: order.status is already updated by v-model when this triggers if I don't prevent it.
  // Actually, v-model updates it. So `order.status` is the NEW value.
  // But I need to confirm first.
  
  // Wait, if I use v-model, the value changes before I can confirm.
  // Better to use :value and @change with manual update or just confirm AFTER change (and revert if cancelled).
  
  // Revert logic is tricky without previous value.
  // I'll assume the user selected a target status.
  
  if (newStatus === '交易完成' || newStatus === '取消') {
    const confirmed = confirm(`你確定要將訂單 ${order.orderNo} 設為「${newStatus}」嗎？`);
    if (!confirmed) {
        // Revert to '待面交' (assuming it was capable of being changed from there)
       order.status = '待面交';
       return;
    }
    
    try {
        let url = '';
        if (newStatus === '交易完成') {
            url = `http://localhost:8080/api/orders/${order.id}/complete`;
        } else if (newStatus === '取消') {
            url = `http://localhost:8080/api/orders/${order.id}/cancel`;
        }
        
        const response = await fetch(url, {
            method: 'POST',
            credentials: 'include'
        });
        
        if (!response.ok) {
             const errMsg = await response.text();
             throw new Error("Failed to update status: " + errMsg);
        }
        
        alert("狀態更新成功");
        // Move to history
        const index = currentOrders.value.indexOf(order);
        if (index > -1) {
            currentOrders.value.splice(index, 1);
            historyOrders.value.push(order);
        }
        
    } catch (e) {
        console.error(e);
        alert("更新失敗: " + e.message);
        order.status = '待面交'; // Revert
    }
  }
}

onMounted(() => {
    fetchOrders();
});
</script>

