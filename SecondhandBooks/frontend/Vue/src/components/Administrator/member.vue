<template>
  <div class="member-management container mt-4">
    <div class="card shadow-sm border-0 rounded-4">
      <div class="card-body p-4">
        
        <!-- Header & Search -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h3 class="fw-bold mb-0 text-secondary"><i class="bi bi-people-fill me-2"></i>會員管理</h3>
          <div class="input-group" style="width: 300px;">
            <input type="text" class="form-control" placeholder="Search Account" v-model="searchKeyword" @keyup.enter="fetchUsers">
            <button class="btn btn-primary" type="button" @click="fetchUsers">
              <i class="bi bi-search"></i>
            </button>
          </div>
        </div>

        <!-- Users Table -->
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>User ID</th>
                <th>大頭貼</th>
                <th>帳號</th>
                <th>學號</th>
                <th>系級</th>
                <th>角色</th>
                <th>建立時間</th>
                <th>更新時間</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="users.length === 0">
                <td colspan="10" class="text-center py-4 text-muted">查無會員資料</td>
              </tr>
              <tr v-for="user in users" :key="user.user_id">
                <td>{{ user.user_id }}</td>
                <td>
                  <img :src="user.user_picture || 'https://via.placeholder.com/40'" 
                       class="rounded-circle object-fit-cover border" 
                       width="40" height="40" 
                       alt="User">
                </td>
                <td class="fw-bold text-primary">{{ user.account }}</td>
                <td>{{ user.student_id }}</td>
                <td>{{ user.department }}</td>
                <td>
                  <span class="badge" :class="user.role === '管理員' ? 'bg-danger' : 'bg-success'">
                    {{ user.role }}
                  </span>
                </td>
                <td class="small text-secondary">{{ formatDate(user.created_at) }}</td>
                <td class="small text-secondary">{{ formatDate(user.updated_at) }}</td>
                <td>
                  <button class="btn btn-sm btn-outline-primary me-1" @click="openEditModal(user)">
                    <i class="bi bi-pencil-square"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger" @click="confirmDelete(user)">
                    <i class="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <div v-if="showModal" class="modal fade show d-block" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0">
          <div class="modal-header">
            <h5 class="modal-title fw-bold">編輯會員資料</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="updateUser">
              <div class="mb-3">
                <label class="form-label text-muted small">帳號 (不可修改)</label>
                <input type="text" class="form-control" :value="editingUser.account" disabled>
              </div>
              <div class="mb-3">
                <label class="form-label">學號</label>
                <input type="text" class="form-control" v-model="editingUser.student_id">
              </div>
              <div class="mb-3">
                <label class="form-label">系級</label>
                <input type="text" class="form-control" v-model="editingUser.department">
              </div>
              <div class="mb-3">
                <label class="form-label">角色</label>
                <select class="form-select" v-model="editingUser.role">
                  <option value="會員">會員</option>
                  <option value="管理員">管理員</option>
                </select>
              </div>
              <div class="text-end mt-4">
                <button type="button" class="btn btn-light me-2" @click="closeModal">取消</button>
                <button type="submit" class="btn btn-primary px-4">儲存</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const users = ref([]);
const searchKeyword = ref('');
const showModal = ref(false);
const editingUser = ref({});

const API_BASE_URL = 'http://localhost:8080/api/users';

// Fetch Users
const fetchUsers = async () => {
  try {
    let url = API_BASE_URL;
    if (searchKeyword.value.trim()) {
      url += `?search=${encodeURIComponent(searchKeyword.value.trim())}`;
    }
    const response = await fetch(url);
    if (response.ok) {
      users.value = await response.json();
    } else {
      console.error('Fetch failed');
    }
  } catch (error) {
    console.error('Error:', error);
  }
};

// Update User
const openEditModal = (user) => {
  // Clone user object to avoid direct mutation
  editingUser.value = { ...user };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  editingUser.value = {};
};

const updateUser = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/${editingUser.value.user_id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        studentId: editingUser.value.student_id,
        department: editingUser.value.department,
        role: editingUser.value.role
      })
    });
    
    if (response.ok) {
      alert('更新成功');
      closeModal();
      fetchUsers(); // Refresh list
    } else {
      alert('更新失敗');
    }
  } catch (error) {
    console.error('Error updating:', error);
    alert('發生錯誤');
  }
};

// Delete User
const confirmDelete = async (user) => {
  if (!confirm(`確定要刪除會員 ${user.account} 嗎？此操作無法復原。`)) return;
  
  try {
    const response = await fetch(`${API_BASE_URL}/${user.user_id}`, {
      method: 'DELETE'
    });
    
    if (response.ok) {
      alert('刪除成功');
      fetchUsers();
    } else {
      alert('刪除失敗');
    }
  } catch (error) {
    console.error('Error deleting:', error);
  }
};

// Helper
const formatDate = (isoString) => {
  if (!isoString) return '-';
  return new Date(isoString).toLocaleDateString();
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.table th {
  font-weight: 600;
  color: #555;
  white-space: nowrap;
}
.input-group .form-control:focus {
  border-color: #8da399;
  box-shadow: 0 0 0 0.25rem rgba(141, 163, 153, 0.25);
}
.btn-primary {
  background-color: #8da399;
  border-color: #8da399;
}
.btn-primary:hover {
  background-color: #7a8f86;
  border-color: #7a8f86;
}
.btn-outline-primary {
  color: #8da399;
  border-color: #8da399;
}
.btn-outline-primary:hover {
  background-color: #8da399;
  color: white;
}
</style>
