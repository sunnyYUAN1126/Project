<template>
  <div class="container mt-4" >
    <h2 class="text-center fw-bold mb-4">訂單管理</h2>

    <!-- 切換按鈕 -->
    <div class="mb-4 d-flex gap-2 justify-content-center">
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

    <!-- Container for List -->
    <div>     
      <div class="row justify-content-center">
        <div class="col-md-8">
          <h4 class="fw-bold mb-3">{{ currentTab === 'current' ? '目前訂單' : '交易歷史' }}</h4>
          <div v-for="order in (currentTab === 'current' ? currentOrders : historyOrders)" :key="order.id" class="card mb-3 shadow-sm" style="border: 2px solid #ccc; border-radius: 20px; overflow: hidden;">
              <!-- Card Header -->
              <div class="card-header bg-white border-0 pt-2 px-3 d-flex justify-content-between align-items-center">
                 <span class="fw-bold">訂單編號 {{ order.orderNo }}</span>
                 
                 <!-- Status Moved Here -->
                 <div class="text-center">
                      <div v-if="currentTab === 'current'">
                          <select 
                            v-model="order.status" 
                            @change="handleStatusChange(order)" 
                            :disabled="order.status !== '待面交'"
                            class="form-select form-select-sm w-auto mx-auto"
                            style="font-size: 20px; padding: 8px 18px;"
                          >
                            <option>待面交</option>
                            <option>交易完成</option>
                            <option>取消</option>
                          </select>
                      </div>
                      <div v-else>
                          <span class="fw-bold" style="font-size: 20px;"
                            :class="{
                              'text-warning': order.status === '待面交',
                              'text-success': order.status === '交易完成',
                              'text-danger': order.status === '取消'
                            }"
                          >
                             {{ order.status }}
                          </span>
                      </div>
                 </div>

                 <span class="text-muted small">下單日期 {{ order.orderDate }}</span>
              </div>
              
              <!-- Buyer Info -->
              <div class="px-3 py-1 border-bottom small">
                <span class="fw-bold">訂單用戶：</span> {{ order.user }}
              </div>

              <!-- Order Items -->
              <div class="card-body px-3 py-1">
                 <div v-for="(item, index) in order.items" :key="index">
                    <div class="row py-2 align-items-center">
                       
                       <!-- Image -->
                       <div class="col-md-2 col-2">
                          <img :src="item.coverImage || 'https://via.placeholder.com/100x100?text=No+Image'" 
                               class="img-fluid rounded" 
                               style="width: 60px; height: 60px; object-fit: cover;" 
                               alt="Book Cover">
                       </div>

                       <!-- Details -->
                       <div class="col-md-8 col-7">
                          <div class="mb-0 small"><strong>ISBN：</strong> {{ item.isbn }}</div>
                          <div class="mb-0 fw-bold">{{ item.productName }}</div>
                          <div class="text-muted small" style="font-size: 0.85rem;">
                             二手書資訊：
                             <span v-if="item.productNew">{{ item.productNew }}新</span>
                             <span v-if="item.productClassNote">、{{ item.productClassNote }}筆記</span>
                             <span v-if="item.productNote">、{{ item.productNote }}</span>
                          </div>
                       </div>

                       <!-- Unit Price -->
                       <div class="col-md-2 col-3 text-end fw-bold">
                          $ {{ item.price }}
                       </div>
                    </div>
                    <!-- Item Divider (if not last) -->
                    <hr v-if="index < order.items.length - 1" class="my-0 dashed-line">
                 </div>
              </div>

              <!-- Footer Info (Meetup, Status, Total) -->
              <div class="card-footer bg-white border-top px-3 py-2">
                <div class="row align-items-center">
                   <!-- Meetup Info -->
                   <div class="col-md-6 small">
                      <div><strong>面交日期：</strong> {{ order.date }}</div>
                      <div><strong>時間：</strong> {{ order.time }}</div>
                      <div class="mb-0"><strong>面交地點：</strong> {{ order.location }}</div>
                   </div>

                   <!-- Total Price -->
                   <div class="col-md-6 text-end">
                      <span class="text-muted ms-2 small">訂單金額:</span>
                      <span class="fs-5 fw-bold">$ {{ order.amount }}</span>
                   </div>
                </div>
              </div>
          </div>
        </div>
      </div>
      
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
        user: o.buyerName || `買家 ${o.buyerId}`, // Fallback if name is missing
        items: o.items, // Keep full items list
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
  
  if (newStatus === '交易完成' || newStatus === '取消') {
    const confirmed = confirm(`你確定要將訂單 ${order.orderNo} 設為「${newStatus}」嗎？`);
    if (!confirmed) {
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

<style scoped>
.dashed-line {
    border-top: 1px dashed #ccc;
}
</style>

