<template>
  <div class="container mt-4">
    <h2 class="text-center fw-bold mb-4">我的訂單</h2>
    
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div v-for="order in orders" :key="order.id" class="card mb-3 shadow-sm" style="border: 2px solid #ccc; border-radius: 20px; overflow: hidden;">
          <!-- Card Header -->
          <div class="card-header bg-white border-0 pt-2 px-3 d-flex justify-content-between align-items-center">
             <span class="fw-bold">訂單編號 {{ order.orderNo }}</span>
             
             <!-- Status Moved Here -->
             <span class="fw-bold" style="font-size: 25px;"
                :class="{
                  'text-warning': order.status === '待面交',
                  'text-success': order.status === '交易完成',
                  'text-danger': order.status === '取消'
                }"
              >
                 {{ order.status }}
              </span>

             <span class="text-muted small">下單日期 {{ order.orderDate }}</span>
          </div>
          
          <!-- Seller Info -->
          <div class="px-3 py-1 border-bottom small">
            <span class="fw-bold">賣家用戶：</span> {{ order.sellerName }}
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
                         <span v-if="item.productNew">{{ item.productNew }}</span>
                         <span v-if="item.productClassNote">、{{ item.productClassNote }}</span>
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
                  <span class="text-muted ms-2 small">訂單金額</span>
                  <span class="fs-5 fw-bold ms-1">$ {{ order.amount }}</span>
               </div>
            </div>

            <div class="text-end mt-2">
                <button v-if="order.status === '待面交'" @click="cancelOrder(order)" class="btn btn-outline-danger btn-sm" style="font-size: 20px; padding: 2px 8px;">取消訂單</button>
            </div>
          
          </div>
        </div>
      </div>
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
    
    orders.value = data.buying.map(o => ({
      id: o.orderId,
      orderNo: `No.${o.orderId}`,
      sellerName: o.sellerName,
      items: o.items, // Pass full items list with details
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

<style scoped>
.dashed-line {
    border-top: 1px dashed #ccc;
}
</style>

