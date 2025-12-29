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
          <!-- 员工姓名输入项 -->
          <el-form-item label="员工姓名" class="search-form-item" label-width="80px">
            <el-input v-model="queryParams.name" placeholder="请输入员工姓名" clearable class="search-input"
              :prefix-icon="UserFilled" />
          </el-form-item>

          <!-- 主管编号输入项 -->
          <el-form-item label="主管编号" class="search-form-item" label-width="80px">
            <el-input v-model="queryParams.managerId" placeholder="请输入主管编号" clearable class="search-input"
              :prefix-icon="Avatar" />
          </el-form-item>

          <!-- 部门名称输入项 -->
          <el-form-item label="所属部门" class="search-form-item" label-width="80px">
            <el-select v-model="queryParams.deptName" multiple collapse-tags placeholder="请选择所属部门" class="search-input"
              :prefix-icon="HomeFilled">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
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

      <!-- 员工表格 -->
      <div class="table-container">
        <el-table ref="tableRef" :data="empList" style="width: 100%" :loading="loading" cell-class-name="table-cell"
          header-cell-class-name="table-header-cell" @row-click="handleRowClick">
          <!-- 所有列添加 align="center" 实现文字居中 -->
          <el-table-column type="selection" width="55" align="center" />

          <el-table-column prop="empId" sortable label="员工编号" width="120" align="center" />

          <el-table-column prop="photo" label="照片" width="100" align="center">
            <template #default="scope">
              <el-image :src="scope.row.photo" fit="cover" class="emp-avatar"
                fallback-src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
                preview-src-list="[scope.row.photo]" />
            </template>
          </el-table-column>

          <el-table-column prop="name" sortable label="员工姓名" width="170" align="center" />

          <el-table-column sortable label="性别" width="80" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.gender === '男' ? 'primary' : 'danger'" class="tableTag">
                {{ scope.row.gender }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="deptName" label="所属部门" align="center" />
          <el-table-column prop="job" label="工作岗位" align="center" />

          <el-table-column label="出生日期" width="150" align="center">
            <template #default="scope">
              {{ parseTime(scope.row.birthday, '{y}-{m}-{d}') || '无' }}
            </template>
          </el-table-column>
          <el-table-column label="入职日期" width="150" align="center">
            <template #default="scope">
              {{ parseTime(scope.row.entryday, '{y}-{m}-{d}') || '无' }}
            </template>
          </el-table-column>

          <el-table-column label="主管" align="center">
            <template #default="scope">
              <el-popover effect="light" trigger="hover" placement="top" width="auto">
                <template #default>
                  <div class="manager-info">
                    <div class="info-item">编号: {{ scope.row.managerId || '无' }}</div>
                  </div>
                </template>
                <template #reference>
                  <el-tag type="primary" class="tableTag">{{ scope.row.managerName || '无' }}</el-tag>
                </template>
              </el-popover>
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

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
          :page-sizes="[5, 10, 15, 20]" size="small" background layout="total, sizes, prev, pager, next, jumper"
          :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange"
          class="custom-pagination" />
      </div>

    </div>

    <!-- 编辑 / 添加员工对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '新增员工'" width="700px" destroy-on-close
      :close-on-press-escape="false" class="emp-dialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" size="default"
        class="emp-form custom-emp-form">
        <!-- 顶部居中：员工照片 -->
        <el-form-item label="员工照片" class="photo-form-item">
          <div class="photo-upload-wrapper">
            <!-- 核心：el-tooltip 包裹上传组件 -->
            <el-tooltip content="支持JPG/PNG格式，大小不超过2MB" placement="top-start" effect="dark" popper-class="avatar-tooltip"
              :show-after="100">
              <el-upload name="img" class="avatar-uploader" :action="uploadURL" :show-file-list="false"
                :headers="uploadHeaders" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                <img v-if="formData.photo" :src="formData.photo" class="avatar" alt="员工照片" />
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
            </el-tooltip>
          </div>
        </el-form-item>

        <!-- 下方两列：其他信息 -->
        <el-row :gutter="20" class="form-content-row">
          <!-- 左列 -->
          <el-col :span="12">
            <el-form-item label="员工编号" prop="empId">
              <el-input v-model="formData.empId" placeholder="请输入员工编号" clearable :prefix-icon="UserFilled"
                :disabled="isEdit" />
            </el-form-item>

            <el-form-item label="员工姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入员工姓名" clearable :prefix-icon="UserFilled" />
            </el-form-item>

            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio label="男" class="radio-item">男</el-radio>
                <el-radio label="女" class="radio-item">女</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="所属部门" prop="deptName">
              <el-select v-model="formData.deptName" placeholder="请选择所属部门" clearable :prefix-icon="HomeFilled">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>


          </el-col>

          <!-- 右列 -->
          <el-col :span="12">

            <el-form-item label="工作岗位" prop="job">
              <el-input v-model="formData.job" placeholder="请输入工作岗位" clearable :prefix-icon="Avatar" />
            </el-form-item>

            <el-form-item label="主管编号" prop="managerId">
              <el-input v-model="formData.managerId" placeholder="请输入主管编号" clearable :prefix-icon="Avatar" />
            </el-form-item>

            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker v-model="formData.birthday" type="date" placeholder="请选择出生日期" format="YYYY-MM-DD"
                value-format="YYYY-MM-DD" clearable />
            </el-form-item>

            <el-form-item label="入职日期" prop="entryday">
              <el-date-picker v-model="formData.entryday" type="date" placeholder="请选择入职日期" format="YYYY-MM-DD"
                value-format="YYYY-MM-DD" clearable />
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
import { listEmp, getEmp, addEmp, delEmp, updateEmp } from '@/api/employee'
import { getDeptOptions } from '@/api/department'
import { UserFilled, Avatar, Search, Refresh, Delete, Edit, HomeFilled, Plus, Warning } from '@element-plus/icons-vue'
import { parseTime } from '@/utils/dataUtil'

// ========== 响应式数据定义 ==========
const queryParams = reactive({
  pageNum: 1,
  pageSize: 5,
  name: undefined,
  managerId: undefined,
  deptName: []
})

const empList = ref([])
const total = ref(0)
const options = ref([])
const tableRef = ref(null)
const uploadURL = ref('')
const loading = ref(false)


// ========== 新增/编辑弹窗共用数据 ==========
// 弹窗显示状态
const dialogVisible = ref(false)
// 区分新增/编辑（true=编辑，false=新增）
const isEdit = ref(false)
// 表单Ref
const formRef = ref(null)
// 表单数据（新增/编辑共用）
const formData = reactive({
  empId: '',        // 员工编号
  name: '',         // 员工姓名
  gender: '',       // 性别
  deptName: '',     // 所属部门
  job: '',          // 工作岗位
  birthday: '',     // 出生日期
  entryday: '',     // 入职日期
  managerId: '',    // 主管编号
  photo: ''         // 员工照片
})

// 表单验证规则（共用）
const formRules = reactive({
  empId: [
    { required: true, message: '请输入员工编号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  deptName: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  job: [
    { required: true, message: '请输入工作岗位', trigger: 'blur' }
  ],
  birthday: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  entryday: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ],
  photo: [
    { required: false }
  ],
  managerId: [
    { required: false }
  ]
})

const uploadHeaders = ref({})

// ========== 生命周期 ==========
onMounted(() => {
  getList()
  getOptions() // 优先加载下拉选项
  uploadURL.value = 'http://localhost:9999/api/upload'

  // 核心：从本地缓存获取token，添加到上传请求头
  const token = localStorage.getItem('token') // 假设登录后token存在localStorage
  if (token) {
    uploadHeaders.value = {
      'token': token // 和后端JWT过滤器读取的请求头key一致
    }
  }

})

// ========== 核心方法 ==========
// 获取部门名称下拉列表
const getOptions = async () => {
  try {
    const response = await getDeptOptions()
    // 后端返回的是 [{label: '研发部', value: '研发部'}, ...]，直接赋值
    options.value = response.data || []
  } catch (error) {
    ElMessage.error('获取部门列表失败，下拉选项加载异常')
    console.error('部门下拉列表请求失败：', error)
    // 兜底：加载失败时显示空选项
    options.value = []
  }
}
// 获取员工列表
const getList = async () => {
  loading.value = true
  try {
    const response = await listEmp(queryParams)
    empList.value = response.data.rows || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取员工列表失败，请重试')
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
  queryParams.managerId = undefined
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
// 删除按钮
// 统一删除事件（支持单个删除/批量删除）
/**
 * @param {Object|Array} target - 单个删除传row对象，批量删除传选中的rows数组
 */
const handleDelete = async (target) => {
  // 1. 区分单个/批量删除，提取empIds数组
  let empIds = []
  let deleteType = '' // 标记删除类型：single/batch

  if (Array.isArray(target)) {
    // 批量删除：target是选中的rows数组
    if (target.length === 0) {
      ElMessage.warning('请先选择要删除的员工！')
      return
    }
    empIds = target.map(row => row.empId)
    deleteType = 'batch'
  } else {
    // 单个删除：target是当前行row对象
    empIds = [target.empId]
    deleteType = 'single'
  }

  try {
    // 2. 动态生成确认弹窗内容
    const confirmText = deleteType === 'single'
      ? '确定删除该员工吗？删除后不可恢复！'
      : `确定删除选中的 ${empIds.length} 名员工吗？删除后不可恢复！`

    await ElMessageBox.confirm(
      confirmText,
      deleteType === 'single' ? '删除确认' : '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        // 新增：自定义类名（用于样式穿透）
        customClass: 'delete-confirm-box',
        // 新增：自定义图标（放大+配色）
        icon: h('el-icon', { size: 24, style: { color: '#f56c6c' } }, [h(Warning)]),
        confirmButtonClass: 'delete-confirm-btn',
        cancelButtonClass: 'delete-cancel-btn',
        closeOnClickModal: false,
        // 新增：弹窗宽度适配
        width: deleteType === 'single' ? '420px' : '480px'
      }
    )

    // 3. 调用删除接口（优先批量接口，无则兼容单个接口循环）
    loading.value = true
    let deleteSuccess = true
    let response = null

    response = await delEmp(empIds)

    // 4. 处理删除结果
    if (response.code === 200 && deleteSuccess) {
      const successText = deleteType === 'single'
        ? '删除员工成功！'
        : `成功删除 ${empIds.length} 名员工！`
      ElMessage.success(successText)
      getList() // 刷新列表
    } else {
      const errorText = deleteType === 'single'
        ? '删除员工失败，请重试！'
        : '部分员工删除失败，请检查后重试！'
      ElMessage.error(errorText)
    }

  } catch (error) {
    // 5. 异常处理（用户取消/接口报错）
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
  handleDelete(selectedRows) // 传递数组，触发批量删除
}

// 行点击选中逻辑
/**
 * 行点击事件 - 切换当前行选中状态
 * @param {Object} row 当前点击的行数据
 * @param {Object} column 当前点击的列
 * @param {Event} event 原生事件
 */
const handleRowClick = (row, column, event) => {

  // 可选：排除操作列（点击编辑/删除按钮时不触发行选中）
  if (column.label === '操作') return

  // 切换当前行的选中状态（多选模式）
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
    // 根据员工编号查询详情（需补充接口）
    const response = await getEmp(row.empId)
    if (response.code === 200) {
      // 回显数据
      const empInfo = response.data
      formData.empId = empInfo.empId || ''
      formData.name = empInfo.name || ''
      formData.gender = empInfo.gender || ''
      formData.deptName = empInfo.deptName || ''
      formData.job = empInfo.job || ''
      formData.birthday = empInfo.birthday || ''
      formData.entryday = empInfo.entryday || ''
      formData.managerId = empInfo.managerId || ''
      formData.photo = empInfo.photo || ''
    }
  } catch (error) {
    ElMessage.error('获取员工详情失败，无法编辑')
    console.error('编辑回显失败：', error)
  }
}

// 重置表单（新增/编辑共用）
const resetForm = () => {
  // 先手动还原formData到初始空值（核心：覆盖编辑时的赋值）
  Object.assign(formData, {
    empId: '',        // 员工编号
    name: '',         // 员工姓名
    gender: '',       // 性别
    deptName: '',     // 所属部门
    job: '',          // 工作岗位
    birthday: '',     // 出生日期
    entryday: '',     // 入职日期
    managerId: '',    // 主管编号
    photo: ''         // 员工照片
  })
  // 等待弹窗/表单渲染完成后，再调用resetFields清除验证状态
  nextTick(() => {
    if (formRef.value) {
      formRef.value.resetFields() // 清除验证提示、还原prop字段的表单状态
    }
  })
}

// 监听弹窗关闭事件，重置表单
watch(dialogVisible, (newVal) => {
  if (!newVal) { // 弹窗关闭时
    resetForm()
  }
})

// 头像上传成功回调
const handleAvatarSuccess = (response) => {
  // 假设接口返回 { code: 200, data: '照片URL' }
  if (response.code === 200) {
    formData.photo = response.data
    ElMessage.success('照片上传成功')
  } else {
    ElMessage.error('照片上传失败')
  }
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg' || file.type === 'image/webp'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片！')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
  }
  return isJPG && isLt2M
}

// 提交表单（新增/编辑共用）
const handleSubmit = async () => {
  if (!formRef.value) return

  let isValid = false;
  try {
    // 表单验证
    await new Promise((resolve, reject) => {
      formRef.value.validate((valid, invalidFields) => {
        if (valid) {
          isValid = true;
          resolve(); // 校验通过，继续后续逻辑
        } else {
          reject(new Error('表单校验失败')); // 标记为校验失败
        }
      });
    });
    let response
    if (isEdit.value) {
      // 编辑：调用更新接口
      response = await updateEmp(formData)
    } else {
      // 新增：调用新增接口
      response = await addEmp(formData)
    }
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '编辑员工成功！' : '新增员工成功！')
      // 关闭弹窗
      dialogVisible.value = false
      // 刷新列表
      getList()
    } else {
      ElMessage.error(response.msg || (isEdit.value ? '编辑员工失败' : '新增员工失败'))
    }
  } catch (error) {
    // 表单验证失败
    ElMessage.error('请完善必填字段后提交！')
    console.error('表单验证失败：', error)
  }
}

</script>

<style lang="less" scoped>
.employeePage {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: #FCFAFC;
  padding: 20px;
  overflow-y: auto;
  font-size: 15px;
  /* 基础字体放大 */
  letter-spacing: 1px;
}

// 搜索栏容器
.search-bar-container {
  background: #ffffff;
  padding: 20px 18px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  display: flex; // 新增：横向排列按钮组和表单
  align-items: center; // 垂直居中
  gap: 20px; // 按钮组和表单的间距
  flex-wrap: wrap; // 响应式换行

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
      /* 搜索标签字体从14→15px */
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
      /* 输入框字体放大 */

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

    // hover 效果
    &:hover {
      transform: translateY(-1px);
    }

    // 点击按下效果（复位）
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
    /* 表格基础字体放大 */

    // 表头样式
    .table-header-cell {
      background: #f5f7fa;
      color: #A5A2A5;
      font-weight: 600;
      font-size: 15px;
      /* 表头字体从14→15px */
      text-align: center;
      /* 表头文字强制居中 */
      height: 50px;
    }

    // 单元格样式
    .table-cell {
      padding: 14px 10px;
      /* 单元格内边距适配字体 */
      font-size: 14px;
      /* 单元格字体从13→14px */
      border-color: #e8eaec;
      text-align: center;
      /* 单元格文字强制居中 */
      color: #575457;
    }

    // 行样式（仅保留上下阴影，无左右阴影）
    .el-table__row {
      transition: background-color 0.5s ease;
      position: relative;
      z-index: 1;

      &:hover {
        background-color: #f6f6f7bf;
        color: #303133;
        z-index: 10;

        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15); // 水平偏移=0 → 无左右阴影；垂直偏移+模糊=上下阴影

        // 确保单元格继承行背景（无需给单元格加阴影）
        .table-cell {
          background-color: inherit;
        }
      }

    }

  }

  // 员工头像样式
  .emp-avatar {
    width: 70px;
    height: 70px;
    border-radius: 10px;
    border: 2px solid #f0f2f5;
    transition: all 0.2s ease;

    &:hover {
      transform: scale(1.05);
      border-color: #e4e7ed;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
    /* 弹窗内边距适配字体 */
    font-size: 14px;
    /* 弹窗文字从13→14px */
    text-align: left;
    /* 弹窗内容保持左对齐（更易读） */

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

      // 核心：放大复选框
      .el-checkbox__inner {
        width: 20px; // 原默认是14px，调大
        height: 20px;
        border-radius: 4px; // 可选：调整圆角更协调

        // 调整复选框内“勾”的位置（适配放大后的框）
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

  // 直接穿透Element Plus分页组件
  :deep(.el-pagination) {
    width: 100%;
    gap: 10px;

    // Total + 每页条数
    .el-pagination__total {
      color: #606266;
      font-size: 15px;
      margin-right: 10px;
    }

    .el-pagination__sizes {

      margin-right: auto;


      // 每页条数下拉框
      .el-select .el-select__wrapper {
        border-radius: 6px;
        border: 1px solid #e4e7ed;
        background: #fafafa;
        height: 30px;
        width: 100px;
        padding: 0px 10px;
      }
    }

    // 统一页码/上下页按钮样式
    .el-pager li,
    .btn-prev,
    .btn-next {
      border-radius: 6px;
      margin: 0 3px;
      // padding: 0 12px;
      height: 36px;
      width: 36px;
      line-height: 36px;
      font-size: 15px;
      background: #fff;
      border: 1px solid #d6d7dc;

      // 当前页样式（强制高亮）
      &.is-active {
        background: #409eff;
        color: #ffffff;
        border-color: #409eff;

        // 当前页 hover 时轻微加深背景（可选，提升体验）
        &:hover {
          background: #66b1ff; // 比原主色浅一点
          border-color: #66b1ff;
          color: #fff;
        }
      }

      //  hover效果
      &:hover {
        background: #ecf5ff;
        color: #409eff;
        border-color: #c6e2ff;
      }
    }

    // Go to 输入框 
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
  border-radius: 12px; // 大圆角更柔和
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12); // 层次感阴影
  border: 1px solid #f0f2f5; // 浅边框增强质

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
        transform: rotate(90deg); // 关闭按钮hover旋转
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

// 对话框遮罩层优化（修正层级）
:deep(.el-overlay-dialog) {
  background-color: rgba(249, 250, 251, 0.1);
}

// 表单区域美化
:deep(.custom-emp-form) {
  .el-form-item {
    margin-bottom: 20px;

    &.photo-form-item {
      margin-bottom: 24px;
      text-align: center;
    }

    .el-form-item__label {
      font-size: 14px;
      font-weight: 500;
      color: #374151;
      padding-right: 12px;
    }

    // 输入框/选择器统一样式（提升优先级）
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

    // 单选框样式
    .el-radio {
      margin-right: 20px;

      .el-radio__label {
        font-size: 14px;
        color: #374151;
      }

      .el-radio__inner {
        width: 18px;
        height: 18px;

        &:checked {
          background-color: #409eff;
          border-color: #409eff;
        }
      }
    }
  }

  // 照片上传区域美化
  .photo-upload-wrapper {
    display: inline-block;
    position: relative;

    .avatar-uploader {
      .el-upload {
        width: 120px;
        height: 120px;
        border-radius: 10px; // 圆形上传区
        border: 2px dashed #e5e7eb;
        background-color: #f9fafb;
        transition: all 0.3s ease;

        &:hover {
          border-color: #409eff;
          background-color: #f0f7ff;
        }

        .avatar {
          width: 100%;
          height: 100%;
          border-radius: 10px;
          object-fit: cover;
        }

        .avatar-uploader-icon {
          font-size: 32px;
          color: #9ca3af;
          line-height: 120px;
        }
      }

      .upload-tip {
        margin-top: 12px;
        font-size: 12px;
        color: #9ca3af;
        transition: all 0.2s ease;
        padding: 4px 12px;
        border-radius: 4px;

        &:hover {
          color: #409eff;
          background-color: #f0f7ff;
        }
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
    // padding: 8px 20px;
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

  // 弹窗整体样式
  .el-message-box {
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    border: 1px solid #fef0f0;
    overflow: hidden;

    // 弹窗头部（标题+图标）
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

      // 关闭按钮美化
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

    // 弹窗内容区
    .el-message-box__content {
      padding: 24px;
      font-size: 15px;
      color: #6b7280;
      line-height: 1.6;

      // 警告图标容器
      .el-message-box__icon {
        margin-right: 16px;

        &>.el-icon {
          font-size: 28px;
        }
      }

      // 文字内容
      .el-message-box__message {
        font-size: 15px;
        color: #4b5563;

        &:first-letter {
          font-weight: 500;
        }
      }
    }

    // 弹窗底部按钮区
    .el-message-box__footer {
      padding: 16px 24px;
      background-color: #fafafa;
      border-top: 1px solid #f3f4f6;
      display: flex;
      gap: 12px;
      justify-content: flex-end;

      // 自定义确认按钮
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

      // 自定义取消按钮
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
