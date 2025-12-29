<template>
  <div class="employeePage">
    <div class="employee-header">
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
          <!-- 薪资编号输入项 -->
          <el-form-item label="薪资编号" class="search-form-item" label-width="80px">
            <el-input v-model="queryParams.salaryId" placeholder="请输入薪资编号" clearable class="search-input"
              :prefix-icon="Money" />
          </el-form-item>

          <!-- 员工姓名输入项 -->
          <el-form-item label="员工姓名" class="search-form-item" label-width="80px">
            <el-input v-model="queryParams.name" placeholder="请输入员工姓名" clearable class="search-input"
              :prefix-icon="UserFilled" />
          </el-form-item>

          <!-- 所属部门下拉项（复用员工页面的部门下拉逻辑） -->
          <el-form-item label="所属部门" class="search-form-item" label-width="80px">
            <el-select v-model="queryParams.deptName" multiple collapse-tags placeholder="请选择所属部门" class="search-input"
              :prefix-icon="HomeFilled">
              <el-option v-for="item in deptOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
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

      <!-- 薪资表格 -->
      <div class="table-container">
        <el-table ref="tableRef" :data="salaryList" style="width: 100%" :loading="loading" cell-class-name="table-cell"
          header-cell-class-name="table-header-cell" @row-click="handleRowClick">
          <!-- 所有列添加 align="center" 实现文字居中 -->
          <el-table-column type="selection" width="55" align="center" />

          <el-table-column prop="salaryId" sortable label="薪资编号" width="120" align="center" />
          <el-table-column prop="name" sortable label="员工姓名" width="150" align="center" />
          <el-table-column prop="deptName" sortable label="所属部门" width="150" align="center" />

          <el-table-column prop="base" sortable label="基础工资" width="150" align="center">
            <template #default="scope">
              {{ scope.row.base ? scope.row.base.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>

          <el-table-column prop="subsidy" sortable label="补贴" width="150" align="center">
            <template #default="scope">
              {{ scope.row.subsidy ? scope.row.subsidy.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>

          <el-table-column prop="fine" sortable label="罚金" width="150" align="center">
            <template #default="scope">
              {{ scope.row.fine ? scope.row.fine.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>

          <el-table-column prop="total" sortable label="总工资" width="150" align="center">
            <template #default="scope">
              {{ scope.row.total ? scope.row.total.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>

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

      <!-- 分页组件（复用员工页面样式） -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
          :page-sizes="[8, 16, 32, 40]" size="small" background layout="total, sizes, prev, pager, next, jumper"
          :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"
          class="custom-pagination" />
      </div>

    </div>

    <!-- 编辑 / 新增薪资对话框（复用员工页面弹窗样式） -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑薪资' : '新增薪资'" width="700px" destroy-on-close
      :close-on-press-escape="false" class="emp-dialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" size="default"
        class="emp-form custom-emp-form">
        <el-row :gutter="20" class="form-content-row">
          <!-- 左列 -->
          <el-col :span="12">
            <el-form-item label="薪资编号" prop="salaryId">
              <el-input v-model="formData.salaryId" placeholder="请输入薪资编号" clearable :prefix-icon="Money"
                :disabled="isEdit" />
            </el-form-item>

            <el-form-item label="员工编号" prop="empId">
              <el-input v-model="formData.empId" placeholder="请输入员工编号" clearable :prefix-icon="UserFilled"  :disabled="isEdit" />
            </el-form-item>

            <el-form-item label="基础工资" prop="base">
              <el-input v-model.number="formData.base" placeholder="请输入基础工资" clearable type="number" step="0.01"
                :prefix-icon="Money" />
            </el-form-item>
          </el-col>

          <!-- 右列 -->
          <el-col :span="12">

            <el-form-item label="补贴" prop="subsidy">
              <el-input v-model.number="formData.subsidy" placeholder="请输入补贴金额" clearable type="number" step="0.01"
                :prefix-icon="Money" />
            </el-form-item>

            <el-form-item label="罚金" prop="fine">
              <el-input v-model.number="formData.fine" placeholder="请输入罚金金额" clearable type="number" step="0.01"
                :prefix-icon="Money" />
            </el-form-item>

            <!-- 总工资：计算字段，仅展示 -->
            <el-form-item label="总工资" prop="total">
              <el-input v-model="computedTotal" placeholder="自动计算" clearable disabled :prefix-icon="Money" />
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
import { ref, reactive, onMounted, nextTick, watch, computed, h } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 薪资相关接口
import { listSalary, getSalary, addSalary, delSalary, updateSalary } from '@/api/salary'
// 复用员工页面的部门下拉接口
import { getDeptOptions } from '@/api/department'
// 图标：复用员工页面图标 + 新增Money图标
import { UserFilled, HomeFilled, Search, Refresh, Delete, Edit, Plus, Warning, Money } from '@element-plus/icons-vue'

// ========== 响应式数据定义（对齐员工页面结构） ==========
const queryParams = reactive({
  pageNum: 1,
  pageSize: 8,
  salaryId: undefined,  // 薪资编号
  name: undefined,   // 员工姓名
  deptName: []          // 所属部门（多选）
})

const salaryList = ref([])
const total = ref(0)
const deptOptions = ref([]) // 部门下拉选项（复用员工页面逻辑）
const tableRef = ref(null)
const loading = ref(false)

// ========== 新增/编辑弹窗共用数据（对齐员工页面结构） ==========
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = reactive({
  salaryId: '',        // 薪资编号
  empId: '',           // 员工编号
  name: '',         // 员工姓名
  deptName: '',        // 所属部门
  base: 0,            // 基础工资
  subsidy: 0,         // 补贴
  fine: 0,            // 罚金
  total: ''            // 总工资（计算字段）
})

// 计算总工资（自动计算）
const computedTotal = computed(() => {
  const base = Number(formData.base) || 0
  const subsidy = Number(formData.subsidy) || 0
  const fine = Number(formData.fine) || 0
  return (base + subsidy - fine).toFixed(2)
})

// 表单验证规则（对齐员工页面验证风格）
const formRules = reactive({
  salaryId: [
    { required: true, message: '请输入薪资编号', trigger: 'blur' }
  ],
  empId: [
    { required: true, message: '请输入员工编号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' }
  ],
  deptName: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  base: [
    { required: true, message: '请输入基础工资', trigger: 'blur' },
    { type: 'number', min: 0, message: '基础工资不能为负数', trigger: 'blur' }
  ],
  subsidy: [
    { type: 'number', min: 0, message: '补贴不能为负数', trigger: 'blur' }
  ],
  fine: [
    { type: 'number', min: 0, message: '罚金不能为负数', trigger: 'blur' }
  ],
  total: [
    { required: false }
  ]
})

// ========== 生命周期（对齐员工页面） ==========
onMounted(() => {
  getList()
  getDeptOptionsList() // 优先加载部门下拉选项
})

// ========== 核心方法（完全复用员工页面逻辑结构） ==========
// 获取部门下拉列表（复用员工页面接口）
const getDeptOptionsList = async () => {
  try {
    const response = await getDeptOptions()
    deptOptions.value = response.data || []
  } catch (error) {
    ElMessage.error('获取部门列表失败，下拉选项加载异常')
    console.error('部门下拉列表请求失败：', error)
    deptOptions.value = []
  }
}

// 获取薪资列表
const getList = async () => {
  loading.value = true
  try {
    const response = await listSalary(queryParams)
    salaryList.value = response.data.rows || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取薪资列表失败，请重试')
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
  queryParams.salaryId = undefined
  queryParams.name = undefined
  queryParams.deptName = []
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

// 删除按钮（完全复用员工页面的单个/批量删除逻辑）
const handleDelete = async (target) => {
  let salaryIds = []
  let deleteType = ''

  if (Array.isArray(target)) {
    if (target.length === 0) {
      ElMessage.warning('请先选择要删除的薪资记录！')
      return
    }
    salaryIds = target.map(row => row.salaryId)
    deleteType = 'batch'
  } else {
    salaryIds = [target.salaryId]
    deleteType = 'single'
  }

  try {
    const confirmText = deleteType === 'single'
      ? '确定删除该薪资记录吗？删除后不可恢复！'
      : `确定删除选中的 ${salaryIds.length} 条薪资记录吗？删除后不可恢复！`

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

    loading.value = true
    const response = await delSalary(salaryIds)

    if (response.code === 200) {
      const successText = deleteType === 'single'
        ? '删除薪资记录成功！'
        : `成功删除 ${salaryIds.length} 条薪资记录！`
      ElMessage.success(successText)
      getList()
    } else {
      const errorText = deleteType === 'single'
        ? '删除薪资记录失败，请重试！'
        : '部分薪资记录删除失败，请检查后重试！'
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

// 行点击选中逻辑（复用员工页面）
const handleRowClick = (row, column, event) => {
  if (column.label === '操作') return
  tableRef.value.toggleRowSelection(row)
}

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
    const response = await getSalary(row.salaryId)
    if (response.code === 200) {
      const salaryInfo = response.data
      formData.salaryId = salaryInfo.salaryId || ''
      formData.empId = salaryInfo.empId || ''
      formData.name = salaryInfo.name || ''
      formData.deptName = salaryInfo.deptName || ''
      formData.base = salaryInfo.base || ''
      formData.subsidy = salaryInfo.subsidy || ''
      formData.fine = salaryInfo.fine || ''
    }
  } catch (error) {
    ElMessage.error('获取薪资详情失败，无法编辑')
    console.error('编辑回显失败：', error)
  }
}

// 重置表单（复用员工页面逻辑）
const resetForm = () => {
  Object.assign(formData, {
    salaryId: '',
    empId: '',
    name: '',
    deptName: '',
    base: '',
    subsidy: '',
    fine: ''
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

// 提交表单（对齐员工页面的验证逻辑）
const handleSubmit = async () => {
  if (!formRef.value) return

  let isValid = false;
  try {
    await new Promise((resolve, reject) => {
      formRef.value.validate((valid, invalidFields) => {
        if (valid) {
          isValid = true;
          resolve();
        } else {
          reject(new Error('表单校验失败'));
        }
      });
    });

    let response
    const submitData = { ...formData }
    delete submitData.total // 总工资为计算字段，不提交

    if (isEdit.value) {
      response = await updateSalary(submitData)
    } else {
      response = await addSalary(submitData)
    }

    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '编辑薪资成功！' : '新增薪资成功！')
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(response.msg || (isEdit.value ? '编辑薪资失败' : '新增薪资失败'))
    }
  } catch (error) {
    ElMessage.error('请完善必填字段后提交！')
    console.error('表单验证失败：', error)
  }
}
</script>

<!-- 完全复用员工页面的样式，仅适配薪资业务的少量调整 -->
<style lang="less" scoped>
.employeePage {
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

  :deep(.el-table) {
    border-radius: 6px;
    overflow: hidden;
    font-size: 14px;

    .table-header-cell {
      background: #f5f7fa;
      color: #A5A2A5;
      font-weight: 600;
      font-size: 15px;
      text-align: center;
      height: 50px;
    }

    .table-cell {
      padding: 14px 10px;
      font-size: 14px;
      border-color: #e8eaec;
      text-align: center;
      color: #575457;
    }

    .el-table__row {
      transition: background-color 0.5s ease;
      position: relative;
      z-index: 1;

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

  .operation {
    display: flex;
    align-items: center;
    justify-content: center;

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
:deep(.emp-dialog) {
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid #f0f2f5;

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

  .el-dialog__body {
    padding: 24px;
    background-color: #ffffff;
  }

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
:deep(.custom-emp-form) {
  .el-form-item {
    margin-bottom: 20px;

    .el-form-item__label {
      font-size: 14px;
      font-weight: 500;
      color: #374151;
      padding-right: 12px;
    }

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