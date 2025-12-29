<template>
  <div class="deptPage">
    <div class="dept-header">
      <!-- 搜索栏 -->
      <div class="search-bar-container">

        <!-- 增加 删除 -->
        <div class="operate-group">
          <el-button size="default" type="success" :icon="Plus" @click="handleAdd" class="add-btn">
            新增
          </el-button>
          <el-button size="default" type="danger" :icon="Delete" @click="handleBatchDeleteTrigger"
            class="batch-delete-btn">
            删除
          </el-button>
        </div>

        <el-form inline :model="queryParams" class="search-form" size="default">
          <!-- 部门名称输入项 -->
          <el-form-item label="部门名称" class="search-form-item" label-width="80px">
            <el-input v-model="queryParams.name" placeholder="请输入部门名称" clearable class="search-input"
              :prefix-icon="OfficeBuilding" />
          </el-form-item>

          <!-- 主管编号输入项 -->
          <el-form-item label="主管编号" class="search-form-item" label-width="80px">
            <el-input v-model="queryParams.managerEmpId" placeholder="请输入主管编号" clearable class="search-input"
              :prefix-icon="UserFilled" />
          </el-form-item>

          <!-- 操作按钮组 -->
          <el-form-item class="search-form-item operate-group">
            <el-button type="primary" :icon="Search" @click="handleQuery" class="search-btn">
              搜索
            </el-button>
            <el-button :icon="Refresh" @click="resetQuery" class="reset-btn">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 部门表格 -->
      <div class="table-container">
        <el-table ref="tableRef" :data="deptList" style="width: 100%" :loading="loading" cell-class-name="table-cell"
          header-cell-class-name="table-header-cell" @row-click="handleRowClick">
          <!-- 所有列添加 align="center" 实现文字居中 -->
          <el-table-column type="selection" width="55" align="center" />

          <el-table-column prop="deptId" sortable label="部门编号" width="120" align="center" />

          <el-table-column prop="name" sortable label="部门名称" width="170" align="center" />

          <el-table-column label="部门主管" align="center">
            <template #default="scope">
              <el-popover effect="light" trigger="hover" placement="top" width="auto">
                <template #default>
                  <div class="manager-info">
                    <div class="info-item">员工编号: {{ scope.row.managerEmpId || '无' }}</div>
                  </div>
                </template>
                <template #reference>
                  <el-tag type="primary" class="tableTag">{{ scope.row.managerName || '无' }}</el-tag>
                </template>
              </el-popover>
            </template>
          </el-table-column>

          <el-table-column prop="count" sortable label="部门人数" width="170" align="center" />

          <el-table-column prop="description" label="部门描述" min-width="200" align="center" />

          <el-table-column fixed-right label="操作" min-width="120">
            <template #default="scope">
              <div class="operation">
                <el-button link type="primary" @click="handleEdit(scope.row)" class="edit-btn"
                  :icon="Edit">编辑</el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)" class="delete-btn"
                  :icon="Delete">删除</el-button>
              </div>
            </template>
          </el-table-column>

        </el-table>
      </div>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
          :page-sizes="[5, 10, 15, 20]" size="small" background layout="total, sizes, prev, pager, next, jumper"
          :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"
          class="custom-pagination" />
      </div>

    </div>

    <!-- 编辑 / 新增部门对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '新增部门'" width="600px" destroy-on-close
      :close-on-press-escape="false" class="dept-dialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" size="default"
        class="dept-form custom-dept-form">
        <el-row class="form-content-row">
          <!-- 单列布局：span=24 包含所有表单项 -->
          <el-col :span="24">
            <el-form-item label="部门编号" prop="deptId">
              <el-input v-model="formData.deptId" placeholder="请输入部门编号" clearable :prefix-icon="OfficeBuilding"
                :disabled="isEdit" />
            </el-form-item>

            <el-form-item label="部门名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入部门名称" clearable :prefix-icon="OfficeBuilding" />
            </el-form-item>

            <el-form-item label="主管编号" prop="managerEmpId">
              <el-input v-model="formData.managerEmpId" placeholder="请输入主管编号" clearable :prefix-icon="UserFilled" />
            </el-form-item>

            <el-form-item label="部门描述" prop="description">
              <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入部门描述" clearable />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="default" @click="resetForm" v-if="!isEdit">重置</el-button>
          <el-button size="default" type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '提交' }}</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch, h } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 替换为部门相关接口（需根据实际后端接口调整）
import { listDept, getDept, addDept, delDept, updateDept } from '@/api/department'
import { UserFilled, OfficeBuilding, Search, Refresh, Delete, Edit, Plus, Warning } from '@element-plus/icons-vue'

// ========== 响应式数据定义 ==========
const queryParams = reactive({
  pageNum: 1,
  pageSize: 5,
  name: undefined,
  managerEmpId: undefined
})

const deptList = ref([])
const total = ref(0)
const tableRef = ref(null)
const loading = ref(false)

// ========== 新增/编辑弹窗共用数据 ==========
// 弹窗显示状态
const dialogVisible = ref(false)
// 区分新增/编辑（true=编辑，false=新增）
const isEdit = ref(false)
// 表单Ref
const formRef = ref(null)
// 表单数据（新增/编辑共用）- 适配DEPT表结构
const formData = reactive({
  deptId: '',        // 部门编号
  name: '',          // 部门名称
  managerEmpId: '',  // 主管编号
  description: ''    // 部门描述
})

// 表单验证规则（共用）
const formRules = reactive({
  deptId: [
    { required: true, message: '请输入部门编号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  managerEmpId: [
    { required: false, message: '请输入主管编号', trigger: 'blur' }
  ],
  description: [
    { required: false, message: '请输入部门描述', trigger: 'blur' }
  ]
})

// ========== 生命周期 ==========
onMounted(() => {
  getList()
})

// ========== 核心方法 ==========

// 获取部门列表
const getList = async () => {
  loading.value = true
  try {
    const response = await listDept(queryParams)
    deptList.value = response.data.rows || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取部门列表失败，请重试')
    console.error('列表请求失败：', error)
  } finally {
    loading.value = false
  }
}

// 搜索按钮
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮
const resetQuery = () => {
  queryParams.name = undefined
  queryParams.managerEmpId = undefined
  queryParams.pageNum = 1
  queryParams.pageSize = 5
  getList()
}

// pageSize改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 删除按钮（支持单个/批量删除）
const handleDelete = async (target) => {
  // 1. 区分单个/批量删除，提取deptIds数组
  let deptIds = []
  let deleteType = ''

  if (Array.isArray(target)) {
    if (target.length === 0) {
      ElMessage.warning('请先选择要删除的部门！')
      return
    }
    deptIds = target.map(row => row.deptId)
    deleteType = 'batch'
  } else {
    deptIds = [target.deptId]
    deleteType = 'single'
  }

  try {
    // 2. 确认弹窗
    const confirmText = deleteType === 'single'
      ? '确定删除该部门吗？删除后不可恢复！'
      : `确定删除选中的 ${deptIds.length} 个部门吗？删除后不可恢复！`

    await ElMessageBox.confirm(
      confirmText,
      deleteType === 'single' ? '删除确认' : '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'delete-confirm-box',
        icon: h('el-icon', { size: 24, style: { color: '#f56c6c' } }, [h(Warning)]),
        confirmButtonClass: 'delete-confirm-btn',
        cancelButtonClass: 'delete-cancel-btn',
        closeOnClickModal: false,
        width: deleteType === 'single' ? '420px' : '480px'
      }
    )

    // 3. 调用删除接口
    loading.value = true
    const response = await delDept(deptIds)

    // 4. 处理结果
    if (response.code === 200) {
      const successText = deleteType === 'single'
        ? '删除部门成功！'
        : `成功删除 ${deptIds.length} 个部门！`
      ElMessage.success(successText)
      getList()
    } else {
      const errorText = deleteType === 'single'
        ? '删除部门失败，请重试！'
        : '部分部门删除失败，请检查后重试！'
      ElMessage.error(errorText)
    }

  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${deleteType === 'single' ? '单个' : '批量'}删除失败：`, error)
      ElMessage.error('删除操作异常，请重试！')
    } else {
      ElMessage.info('已取消删除')
    }
  } finally {
    loading.value = false
  }
}

// 批量删除按钮
const handleBatchDeleteTrigger = () => {
  const selectedRows = tableRef.value?.getSelectionRows() || [];
  handleDelete(selectedRows)
}

// 行点击选中逻辑
const handleRowClick = (row, column, event) => {
  if (column.label === '操作') return
  tableRef.value.toggleRowSelection(row)
}

// ========== 新增弹窗相关方法 ==========
// 打开新增弹窗
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 打开编辑弹窗
const handleEdit = async (row) => {
  isEdit.value = true
  dialogVisible.value = true
  try {
    const response = await getDept(row.deptId)
    if (response.code === 200) {
      const deptInfo = response.data
      formData.deptId = deptInfo.deptId || ''
      formData.name = deptInfo.name || ''
      formData.managerEmpId = deptInfo.managerEmpId || ''
      formData.description = deptInfo.description || ''
    }
  } catch (error) {
    ElMessage.error('获取部门详情失败，无法编辑')
    console.error('编辑回显失败：', error)
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    deptId: '',
    name: '',
    managerEmpId: '',
    description: ''
  })
  nextTick(() => {
    if (formRef.value) {
      formRef.value.resetFields()
    }
  })
}

// 监听弹窗关闭重置表单
watch(dialogVisible, (newVal) => {
  if (!newVal) {
    resetForm()
  }
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await new Promise((resolve, reject) => {
      formRef.value.validate((valid) => {
        valid ? resolve() : reject(new Error('表单校验失败'))
      })
    })

    let response
    if (isEdit.value) {
      response = await updateDept(formData)
    } else {
      response = await addDept(formData)
    }

    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '编辑部门成功！' : '新增部门成功！')
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(response.msg || (isEdit.value ? '编辑部门失败' : '新增部门失败'))
    }
  } catch (error) {
    ElMessage.error('请完善必填字段后提交！')
    console.error('表单验证失败：', error)
  }
}
</script>

<style lang="less" scoped>
.deptPage {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: #FCFAFC;
  padding: 20px;
  overflow-y: auto;
  font-size: 15px;
  letter-spacing: 1px;
}

// 搜索栏容器
.search-bar-container {
  background: #ffffff;
  padding: 20px 18px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;

  &:hover {
    box-shadow: 5px 5px 25px rgba(0, 0, 0, 0.15);
  }
}

// 搜索表单
.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;

  .search-form-item {
    margin-bottom: 0;

    :deep(.el-form-item__label) {
      color: #606266;
      font-weight: 500;
      font-size: 15px;
    }
  }

  .search-input {
    width: 200px;

    :deep(.el-input__wrapper) {
      border-radius: 6px;
      border: 1px solid #e4e7ed;
      box-shadow: none;
      background-color: #fafafa;
      transition: all 0.2s ease;
      font-size: 15px;

      &:hover {
        border-color: #c0c4cc;
        background-color: #f9f9f9;
      }

      &:focus-within {
        border-color: #409eff;
        background-color: #ffffff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
      }

      .el-input__clear {
        color: #909399;

        &:hover {
          color: #f56c6c;
        }
      }

      .el-input__prefix {
        color: #909399;
        margin-right: 4px;
      }
    }
  }
}

// 按钮组
.operate-group {
  display: flex;
  gap: 8px;

  .search-btn,
  .reset-btn,
  .add-btn,
  .batch-delete-btn {
    font-size: 15px;
    border-radius: 6px;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-1px);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

// 表格容器
.table-container {
  margin-top: 20px;
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 5px 5px 25px rgba(0, 0, 0, 0.15);
  }

  // 表格整体美化
  :deep(.el-table) {
    border-radius: 6px;
    overflow: hidden;
    font-size: 14px;

    // 表头样式
    .table-header-cell {
      background: #f5f7fa;
      color: #A5A2A5;
      font-weight: 600;
      font-size: 15px;
      text-align: center;
      height: 50px;
    }

    // 单元格样式
    .table-cell {
      padding: 14px 10px;
      font-size: 14px;
      border-color: #e8eaec;
      text-align: center;
      color: #575457;
    }

    // 行样式
    .el-table__row {
      transition: background-color 0.5s ease;
      position: relative;
      z-index: 1;
      height: 80px;

      &:hover {
        background-color: #f6f6f7bf;
        color: #303133;
        z-index: 10;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);

        .table-cell {
          background-color: inherit;
        }
      }
    }
  }

  // 主管标签样式
  .tableTag {
    font-size: 13px;
    padding: 5px 10px;
    height: 25px;
  }

  // 主管信息弹窗
  .manager-info {
    padding: 10px 14px;
    font-size: 14px;
    text-align: left;

    .info-item {
      padding: 5px 0;
    }
  }

  .operation {
    display: flex;
    align-items: center;
    justify-content: center;

    // 操作按钮样式
    .edit-btn,
    .delete-btn {
      height: 35px;
      width: 70px;
      font-size: 15px;
      border-radius: 7px;
    }

    .edit-btn {
      color: #409eff;

      &:hover {
        color: #66b1ff;
        background: #ecf5ff;
      }
    }

    .delete-btn {
      color: #f56c6c;

      &:hover {
        color: #f78989;
        background: #fef0f0;
      }
    }
  }

  // 表头复选框样式
  :deep(.el-table-column--selection) {
    .el-checkbox {
      --el-checkbox-input-hover-border-color: #409eff;

      &:hover {
        .el-checkbox__inner {
          border-color: #409eff;
        }
      }

      .el-checkbox__inner {
        width: 20px;
        height: 20px;
        border-radius: 4px;

        &::after {
          align-items: center;
          height: 9px;
          width: 6px;
        }
      }
    }
  }
}

// 分页容器美化
.pagination-container {
  margin-top: 20px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 5px 5px 25px rgba(0, 0, 0, 0.15);
  }

  // 分页组件样式
  :deep(.el-pagination) {
    width: 100%;
    gap: 10px;

    .el-pagination__total {
      color: #606266;
      font-size: 15px;
      margin-right: 10px;
    }

    .el-pagination__sizes {
      margin-right: auto;

      .el-select .el-select__wrapper {
        border-radius: 6px;
        border: 1px solid #e4e7ed;
        background: #fafafa;
        height: 30px;
        width: 100px;
        padding: 0px 10px;
      }
    }

    .el-pager li,
    .btn-prev,
    .btn-next {
      border-radius: 6px;
      margin: 0 3px;
      height: 36px;
      width: 36px;
      line-height: 36px;
      font-size: 15px;
      background: #fff;
      border: 1px solid #d6d7dc;

      &.is-active {
        background: #409eff;
        color: #ffffff;
        border-color: #409eff;

        &:hover {
          background: #66b1ff;
          border-color: #66b1ff;
          color: #fff;
        }
      }

      &:hover {
        background: #ecf5ff;
        color: #409eff;
        border-color: #c6e2ff;
      }
    }

    .el-pagination__jump {
      margin-left: auto;

      .el-pagination__goto,
      .el-pagination__classifier {
        color: #606266;
        font-size: 15px;
      }

      .el-input__wrapper {
        border-radius: 6px;
        border: 1px solid #e4e7ed;
        background: #fafafa;
        height: 30px;
        font-size: 15px;
        width: 80px;
      }
    }
  }
}

// 对话框整体美化
:deep(.dept-dialog) {
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid #f0f2f5;

  // 标题栏美化
  .el-dialog__header {
    padding: 20px 24px 16px;
    background-color: #fcfdfe;
    border-bottom: 1px solid #c5c6c8;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
    }

    .el-dialog__close {
      font-size: 18px;
      color: #9ca3af;
      transition: all 0.2s ease;

      &:hover {
        color: #409eff;
        transform: rotate(90deg);
      }
    }
  }

  // 内容区域
  .el-dialog__body {
    padding: 24px;
    background-color: #ffffff;
  }

  // 底部区域
  .el-dialog__footer {
    padding: 16px 24px 20px;
    background-color: #fcfdfe;
    border-top: 1px solid #f0f2f5;
  }
}

// 对话框遮罩层优化
:deep(.el-overlay-dialog) {
  background-color: rgba(249, 250, 251, 0.1);
}

// 表单区域美化
:deep(.custom-dept-form) {
  .el-form-item {
    margin-bottom: 20px;

    .el-form-item__label {
      font-size: 14px;
      font-weight: 500;
      color: #374151;
      padding-right: 12px;
    }

    // 输入框/选择器统一样式
    .el-input__wrapper,
    .el-select__wrapper,
    .el-date-editor .el-input__wrapper {
      border-radius: 8px;
      border: 1px solid #e5e7eb;
      box-shadow: none;
      padding: 0 12px;
      height: 40px;
      font-size: 14px;

      &:hover {
        border-color: #c4c8cf;
      }

      &:focus-within {
        border-color: #409eff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
      }
    }
  }
}

// 底部按钮栏美化
:deep(.dialog-footer) {
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  .el-button {
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 5px rgba(0, 0, 0, 0.08);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

// 自定义删除确认弹窗美化
:deep(.delete-confirm-box) {
  .el-message-box {
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    border: 1px solid #fef0f0;
    overflow: hidden;

    .el-message-box__header {
      background-color: #fef0f0;
      padding: 16px 24px;
      border-bottom: 1px solid #fee2e2;

      .el-message-box__title {
        font-size: 16px;
        font-weight: 600;
        color: #c41d1d;
        margin-left: 8px;
      }

      .el-message-box__headerbtn {
        color: #9ca3af;
        font-size: 18px;

        &:hover {
          color: #f56c6c;
          transform: rotate(90deg);
          transition: all 0.3s ease;
        }
      }
    }

    .el-message-box__content {
      padding: 24px;
      font-size: 15px;
      color: #6b7280;
      line-height: 1.6;

      .el-message-box__icon {
        margin-right: 16px;

        &>.el-icon {
          font-size: 28px;
        }
      }

      .el-message-box__message {
        font-size: 15px;
        color: #4b5563;

        &:first-letter {
          font-weight: 500;
        }
      }
    }

    .el-message-box__footer {
      padding: 16px 24px;
      background-color: #fafafa;
      border-top: 1px solid #f3f4f6;
      display: flex;
      gap: 12px;
      justify-content: flex-end;

      .delete-confirm-btn {
        background-color: #f56c6c;
        border-color: #f56c6c;
        color: #fff;
        padding: 8px 24px;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.2s ease;

        &:hover {
          background-color: #f78989;
          border-color: #f78989;
          transform: translateY(-2px);
          box-shadow: 0 4px 8px rgba(245, 108, 108, 0.2);
        }

        &:active {
          transform: translateY(0);
        }
      }

      .delete-cancel-btn {
        background-color: #fff;
        border-color: #d1d5db;
        color: #6b7280;
        padding: 8px 24px;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.2s ease;

        &:hover {
          background-color: #f9fafb;
          border-color: #9ca3af;
          color: #4b5563;
          transform: translateY(-2px);
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
        }

        &:active {
          transform: translateY(0);
        }
      }
    }
  }
}
</style>