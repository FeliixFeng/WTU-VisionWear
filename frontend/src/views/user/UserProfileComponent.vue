<template>
  <div>
    <slot></slot>
    <div class="user-icon-area" @click="toggleUserPanel" ref="iconArea">
      <span class="iconfont icon-denglu1 user-icon" aria-label="用户菜单"></span>

      <transition name="fade">
        <div class="user-panel-float" v-show="showUserPanel" @click.stop>
          <div class="user-info">
            <div class="avatar">
              <img src="https://img.icons8.com/ios-filled/50/user.png" alt="用户头像" />
            </div>
            <div class="user-text">
              <div class="phone">151****9603</div>
              <div class="user-id">用户9603</div>
            </div>
            <div class="switch" @click="switchTeam">⟳ 切换团队</div>
          </div>

          <div class="package-box">
            <div class="version-row">
              <span class="version">免费版</span>
              <span class="upgrade-tag">升级</span>
            </div>

            <div class="bar-label">算力（每月更新）<span>0/50</span></div>
            <div class="bar-bg"><div class="bar-fill" style="width: 0%;"></div></div>

            <div class="bar-label">作图体验点（每月更新）<span>0/200</span></div>
            <div class="bar-bg"><div class="bar-fill" style="width: 0%;"></div></div>

            <div class="package-tip">购买套餐获得更多算力，解锁更多权益</div>
            <button class="buy-btn" @click="buyPackage">立即购买套餐 ⚡</button>
          </div>

          <div class="user-actions">
            <div class="action-item" @click="createTeam"><i class="action-icon">🎨</i> 创建团队</div>
            <div class="action-item" @click="goToWebsite"><i class="action-icon">🏠</i> 前往官网</div>
            <div class="action-item" @click="buyPackage"><i class="action-icon">🛒</i> 购买套餐</div>
            <div class="action-item" @click="goToSettings">
              <i class="action-icon">⚙️</i> 个人设置
              <span class="blue">绑定微信快速登录</span>
            </div>
            <div class="action-item" @click="goToOrders"><i class="action-icon">📦</i> 订单中心</div>
            <div class="action-item" @click="resetPassword"><i class="action-icon">🔑</i> 重置密码</div>
            <div class="action-item logout" @click="logout"><i class="action-icon">🚪</i> 退出登录</div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import '../../styles/fontClass/iconfont.css'

export default {
  name: 'UserPanel',
  data() {
    return {
      showUserPanel: false,
      activeFunction: '',
    };
  },
  methods: {
    toggleUserPanel() {
      this.showUserPanel = !this.showUserPanel;
    },
    handleClickOutside(e) {
      const panel = this.$el.querySelector('.user-panel-float');
      const iconArea = this.$refs.iconArea;

      if (panel && iconArea && !panel.contains(e.target) && !iconArea.contains(e.target)) {
        this.showUserPanel = false;
      }
    },
    switchTeam() {
      // 切换团队逻辑
      this.$emit('switch-team');
    },
    createTeam() {
      // 创建团队逻辑
      this.$emit('create-team');
    },
    goToWebsite() {
      // 前往官网逻辑
      window.open('https://yourwebsite.com', '_blank');
    },
    buyPackage() {
      // 购买套餐逻辑
      this.$router.push('/packages');
    },
    goToSettings() {
      // 个人设置逻辑
      this.$router.push('/settings');
    },
    goToOrders() {
      // 订单中心逻辑
      this.$router.push('/orders');
    },
    resetPassword() {
      // 重置密码逻辑
      this.$router.push('/reset-password');
    },
    logout() {
      // 实现登出功能，并重定向到首页
      localStorage.removeItem('token'); // 删除token
      sessionStorage.clear(); // 清除会话数据

      // 触发登出事件，允许父组件响应
      this.$emit('logout');

      // 重定向到首页
      this.$router.push('/');
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside);
  },
};
</script>

<style scoped>
.user-icon-area {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.user-icon {
  font-size: 28px;
  margin-right: 100px;
  transition: color 0.2s ease;
}

.user-icon:hover {
  color: #5a8bff;
}

.user-panel-float {
  position: absolute;
  top: 40px;
  right: 0;
  width: 300px;
  background: #fff;
  border: 1px solid #eaeaea;
  padding: 20px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  z-index: 100;
  border-radius: 12px;
}

/* 添加过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar img {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.user-text {
  flex: 1;
  margin-left: 12px;
}

.user-text .phone {
  font-weight: 600;
  font-size: 15px;
  color: #333;
}

.user-id {
  color: #999;
  font-size: 13px;
  margin-top: 2px;
}

.switch {
  color: #5a8bff;
  font-size: 13px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.switch:hover {
  background-color: #f0f5ff;
}

.package-box {
  background-color: #f9f9f9;
  padding: 16px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.version-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.version {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.upgrade-tag {
  font-size: 12px;
  background-color: #f0f5ff;
  color: #5a8bff;
  padding: 2px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.bar-label {
  font-size: 13px;
  color: #333;
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  margin-bottom: 6px;
}

.bar-bg {
  background: #eaeaea;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #7f5eff, #5a8bff);
  width: 0%;
  transition: width 0.5s ease;
}

.package-tip {
  font-size: 13px;
  color: #666;
  margin: 14px 0 10px;
}

.buy-btn {
  background: linear-gradient(90deg, #7f5eff, #5a8bff);
  border: none;
  border-radius: 6px;
  color: white;
  width: 100%;
  padding: 10px 0;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.buy-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(90, 139, 255, 0.3);
}

.user-actions {
  border-top: 1px solid #eaeaea;
  padding-top: 10px;
}

.action-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333;
  padding: 10px 0;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
  padding-left: 10px;
}

.action-item:hover {
  background-color: #f5f7fa;
}

.action-icon {
  margin-right: 10px;
  display: inline-block;
  width: 20px;
  text-align: center;
  font-style: normal;
}

.blue {
  color: #007aff;
  margin-left: 5px;
  font-size: 12px;
}

.logout {
  color: #ff4d4f;
  margin-top: 4px;
}

@media (max-width: 768px) {
  .user-panel-float {
    width: 280px;
    right: -50px;
  }

  .user-icon {
    margin-right: 50px;
  }
}
</style>