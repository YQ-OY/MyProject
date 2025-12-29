import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

function initState() {
  return {
    isCollapse: false, // 折叠状态
  }
}

export const useAllDataStore = defineStore('allData', () => {
  // 1. 状态（响应式）
  const state = ref(initState())


  const toggleCollapse = () => {
    state.value.isCollapse = !state.value.isCollapse
  }


  // 导出属性）
  return {
    state,
    toggleCollapse   
  }
})