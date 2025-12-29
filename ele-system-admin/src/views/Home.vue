<template>
  <el-row class="home" :gutter="24">
    <!-- 总员工量 -->
    <el-col :span="6">
      <div class="stats-card">
        <div class="stats-card__left">
          <div class="stats-card__title">总员工数</div>
          <div class="stats-card__value-wrap">
            <div class="stats-card__value">{{ countData.employeeCount }}</div>
            <div :class="['stats-card__percent', countData.employeeRate >= 0 ? 'positive' : 'negative']">
              {{ countData.employeeRate >= 0 ? '+' : '' }}{{ countData.employeeRate }}%
            </div>
          </div>
        </div>
        <div class="stats-card__right" style="background-color: #409eff">
          <img src="@/assets/images/员工数.png" alt="员工数" width="35px" height="35px" />
        </div>
      </div>
    </el-col>

    <!-- 部门量 -->
    <el-col :span="6">
      <div class="stats-card">
        <div class="stats-card__left">
          <div class="stats-card__title">总部门数</div>
          <div class="stats-card__value-wrap">
            <div class="stats-card__value">{{ countData.deptCount }}</div>
          </div>
        </div>
        <div class="stats-card__right" style="background-color: #67c23a">
          <img src="@/assets/images/部门数量.png" alt="部门数量" width="35px" height="35px" />
        </div>
      </div>
    </el-col>

    <!-- 薪资量 -->
    <el-col :span="6">
      <div class="stats-card">
        <div class="stats-card__left">
          <div class="stats-card__title">总基础工资</div>
          <div class="stats-card__value-wrap">
            <div class="stats-card__value">{{ countData.totalBase }}</div>
          </div>
        </div>
        <div class="stats-card__right" style="background-color: #f56c6c">
          <img src="@/assets/images/薪资数据.png" alt="薪资数据" width="30px" height="30px" />
        </div>
      </div>
    </el-col>

    <!-- 留言数 -->
    <el-col :span="6">
      <div class="stats-card">
        <div class="stats-card__left">
          <div class="stats-card__title">总罚金</div>
          <div class="stats-card__value-wrap">
            <div class="stats-card__value">{{ countData.totalFine }}</div>
          </div>
        </div>
        <div class="stats-card__right" style="background-color: #909399">
          <img src="@/assets/images/罚金.png" alt="罚金" width="30px" height="30px" />
        </div>
      </div>
    </el-col>

    <!-- 南丁格尔图（部门人数） -->
    <el-col :span="12">
      <div class="chart-card">
        <VChart class="chart" :option="chartOption" autoresize />
      </div>
    </el-col>

    <!-- 横向条形图（部门工资差距） -->
    <el-col :span="12">
      <div class="chart-card">
        <VChart class="chart" :option="salaryGapChartOption" autoresize />
      </div>
    </el-col>

    <!-- 折柱混合图（员工年龄分布） -->
    <el-col :span="24" style="margin-bottom: 20px;">
      <div class="chart-card" style="height: 400px;">
        <VChart class="chart" :option="ageDistChartOption" autoresize />
      </div>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { EmployeeCountData, EmployeeAgeDistData } from '@/api/employee'
import { DepartmentCountData, DeptPersonCountData } from '@/api/department'
import { SalaryCountData, SalaryDeptGapData } from '@/api/salary'

// ---------------------- 引入ECharts模块 ----------------------
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent,
  GridComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([
  PieChart,
  BarChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent,
  GridComponent,
  CanvasRenderer
])

// 响应式数据
const countData = ref({
  employeeCount: 0,
  employeeRate: 0,
  deptCount: 0,
  totalBase: 0,
  totalFine: 0
})

// 南丁格尔图配置（各部门人数）
const chartOption = ref({
  tooltip: {
    trigger: 'item',
    triggerOn: 'mousemove',
    formatter: function (params) {
      return `
        <div style="padding: 6px 8px;">
          <div style="font-weight: bold; margin-bottom: 4px;">${params.seriesName}</div>
          <div>部门：${params.name}</div>
          <div>人数：${params.value} 人</div>
          <div>占比：${params.percent.toFixed(1)}%</div>
        </div>
      `;
    },
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#e6e6e6',
    borderWidth: 1,
    textStyle: {
      fontSize: 14
    }
  },
  legend: {
    top: 'bottom'
  },
  toolbox: {
    show: true,
    feature: {
      mark: { show: true },
      dataView: { show: true, readOnly: false },
      restore: { show: false },
      saveAsImage: { show: true }
    }
  },
  series: [
    {
      name: '各部门人数',
      type: 'pie',
      radius: [30, 210],
      center: ['50%', '50%'],
      roseType: 'area',
      itemStyle: {
        borderRadius: 8
      },
      data: []
    }
  ]
})

// 横向条形图配置（适配真实接口字段）
const salaryGapChartOption = ref({
  title: {
    text: '各部门总工资与公司平均工资差距',
    left: 'center',
    textStyle: { fontSize: 16, fontWeight: 600 }
  },
  tooltip: {
    trigger: 'axis',
    triggerOn: 'mousemove',
    axisPointer: {
      type: 'shadow',
      shadowStyle: {
        color: 'rgba(0, 0, 0, 0.05)'
      }
    },
    formatter: (params) => {
      const { deptName, totalSalary, totalBase, totalBonus, totalFine } = params[0].data;
      const averageSalary = salaryGapChartOption.value._averageSalary;
      return `
        <div style="padding: 10px 12px; min-width: 240px; border: 1px solid #e6e6e6; border-radius: 8px; background: #fff;">
          <div style="font-weight: 600; font-size: 15px; margin-bottom: 8px; color: #333; padding-bottom: 6px; border-bottom: 1px solid #f5f5f5;">
            ${deptName}
          </div>
          <div style="font-size: 14px; line-height: 1.8;">
            <div>总薪资：<span style="color: #409eff; font-weight: 500;">${Number(totalSalary).toLocaleString()}元</span></div>
            <div>总基础工资：<span style="color: #67c23a; font-weight: 500;">${Number(totalBase).toLocaleString()}元</span></div>
            ${totalBonus ? `<div>总奖金：<span style="color: #e6a23c; font-weight: 500;">${Number(totalBonus).toLocaleString()}元</span></div>` : ''}
            ${totalFine ? `<div>总罚款：<span style="color: #f56c6c; font-weight: 500;">${Number(totalFine).toLocaleString()}元</span></div>` : ''}
            <div style="margin-top: 4px; padding-top: 4px; border-top: 1px dashed #f5f5f5;">
              公司平均工资：<span style="color: #909399; font-weight: 500;">${Number(averageSalary).toLocaleString()}元</span>
            </div>
          </div>
        </div>
      `;
    },
    backgroundColor: 'transparent',
    borderColor: 'transparent',
    borderWidth: 0,
    padding: 0,
    textStyle: {
      fontSize: 14,
      color: '#606266'
    },
    enterable: true,
    confine: true
  },
  grid: {
    left: '15%',
    right: '10%',
    top: '15%',
    bottom: '10%'
  },
  xAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: '#e6e6e6' } },
    splitLine: { lineStyle: { color: '#f5f5f5' } },
    axisPointer: {
      label: {
        formatter: (params) => `差距：${Number(params.value).toLocaleString()}元`,
        backgroundColor: 'rgba(0, 0, 0, 0.7)',
        color: '#fff',
        borderRadius: 4,
        padding: [4, 8]
      }
    },
    axisLabel: {
      formatter: (value) => Number(value).toLocaleString() + '元'
    }
  },
  yAxis: {
    type: 'category',
    data: [],
    axisLine: { lineStyle: { color: '#e6e6e6' } },
    axisLabel: { fontSize: 13, color: '#606266' },
    axisTick: { show: false }
  },
  series: [
    {
      name: '工资差距',
      type: 'bar',
      barWidth: 25,
      data: [],
      label: {
        show: true,
        position: 'right',
        formatter: (params) => Number(Math.abs(params.value)).toLocaleString() + '元',
        fontSize: 12,
        color: '#333'
      },
      itemStyle: {
        borderRadius: [0, 4, 4, 0],
        shadowBlur: 3,
        shadowOffsetX: 2,
        shadowColor: 'rgba(0, 0, 0, 0.1)',
        color: function (params) {
          return params.value >= 0 ? '#409eff' : '#f56c6c';
        }
      }
    }
  ]
})

// 折柱混合图（员工年龄分布配置）
const ageDistChartOption = ref({
  title: {
    text: '员工年龄分布（按性别）',
    left: 'center',
    textStyle: { fontSize: 16, fontWeight: 600 }
  },
  tooltip: {
    trigger: 'axis',
    triggerOn: 'mousemove',
    axisPointer: { type: 'shadow' },
    formatter: (params) => {
      // 提取当前年龄段
      const ageRange = params[0].axisValue;
      // 组装tooltip内容
      let tooltipHtml = `<div style="padding: 8px 12px; background: #fff; border-radius: 8px; border: 1px solid #e6e6e6;">`;
      tooltipHtml += `<div style="font-weight: 600; margin-bottom: 6px; color: #333;">${ageRange}</div>`;

      params.forEach(item => {
        let color = '';
        if (item.seriesName === '男性') color = '#409eff';
        else if (item.seriesName === '女性') color = '#f56c6c';
        else color = '#67c23a'; // 总人数

        tooltipHtml += `<div style="font-size: 14px; line-height: 1.6;">
          <span style="display: inline-block; width: 8px; height: 8px; background: ${color}; border-radius: 50%; margin-right: 6px;"></span>
          ${item.seriesName}：<span style="font-weight: 500;">${item.value} 人</span>
        </div>`;
      });

      tooltipHtml += `</div>`;
      return tooltipHtml;
    },
    backgroundColor: 'transparent',
    borderWidth: 0
  },
  legend: {
    top: '10%',
    right: '10%',
    itemStyle: { fontSize: 14 }
  },
  grid: {
    left: '5%',
    right: '5%',
    top: '15%',
    bottom: '8%'
  },
  xAxis: {
    type: 'category',
    data: [], // 年龄段：["18-25", "26-30", "31-35", "36-40", "41-45", "46-50", "50+"]
    axisLine: { lineStyle: { color: '#e6e6e6' } },
    axisLabel: { fontSize: 13, color: '#606266' },
    splitLine: { show: false }
  },
  yAxis: {
    type: 'value',
    name: '人数（人）',
    nameTextStyle: { fontSize: 13, color: '#606266' },
    axisLine: { lineStyle: { color: '#e6e6e6' } },
    splitLine: { lineStyle: { color: '#f5f5f5' } },
    axisLabel: {
      fontSize: 12,
      formatter: (value) => Number(value) + '人'
    }
  },
  series: [
    {
      name: '男性',
      type: 'bar',
      barWidth: 20,
      itemStyle: {
        color: '#409eff',
        borderRadius: [4, 4, 0, 0]
      },
      label: {
        show: true,
        position: 'top',
        fontSize: 12,
        color: '#333',
        formatter: (params) => params.value + '人'
      },
      data: [] // 男性数量
    },
    {
      name: '女性',
      type: 'bar',
      barWidth: 20,
      itemStyle: {
        color: '#f56c6c',
        borderRadius: [4, 4, 0, 0]
      },
      label: {
        show: true,
        position: 'top',
        fontSize: 12,
        color: '#333',
        formatter: (params) => params.value + '人'
      },
      data: [] // 女性数量
    },
    {
      name: '总人数',
      type: 'line',
      zLevel: 10, // 折线层级高于柱状图
      symbol: 'circle', // 拐点为圆形
      symbolSize: 6, // 拐点大小
      lineStyle: {
        color: '#67c23a',
        width: 3
      },
      itemStyle: {
        color: '#67c23a',
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        position: 'top',
        fontSize: 12,
        color: '#67c23a',
        formatter: (params) => params.value + '人'
      },
      data: [] // 总人数（男性+女性）
    }
  ]
})

const loading = ref(false)

// ---------------------- 获取所有数据 ----------------------
const getCountData = async () => {
  try {
    loading.value = true
    // 调用真实接口
    const [empRes, deptRes, salaryRes, deptPersonRes, salaryGapRes, ageDistRes] = await Promise.all([
      EmployeeCountData(),
      DepartmentCountData(),
      SalaryCountData(),
      DeptPersonCountData(),
      SalaryDeptGapData(),
      EmployeeAgeDistData()
    ])

    // 1. 填充基础统计数据
    if (empRes.code === 200) {
      countData.value.employeeCount = empRes.data.employeeCount || 0
      countData.value.employeeRate = empRes.data.employeeRate || 0
    }
    if (deptRes.code === 200) countData.value.deptCount = deptRes.data.deptCount || 0
    if (salaryRes.code === 200) {
      countData.value.totalBase = salaryRes.data.totalBase || 0
      countData.value.totalFine = salaryRes.data.totalFine || 0
    }

    // 2. 填充南丁格尔图数据（部门人数）
    if (deptPersonRes.code === 200 && Array.isArray(deptPersonRes.data)) {
      chartOption.value.series[0].data = deptPersonRes.data
    } else {
      ElMessage.warning('部门人数数据获取失败')
      chartOption.value.series[0].data = [] // 移除示例数据，无数据时置空
    }

    // 3. 填充横向条形图数据（适配真实接口：salaryGapRes.data是数组）
    if (salaryGapRes.code === 200 && Array.isArray(salaryGapRes.data) && salaryGapRes.data.length > 0) {
      // 真实接口中：所有部门的averageSalary是同一个，取第一个即可
      const averageSalary = salaryGapRes.data[0].averageSalary || 0
      const gapList = salaryGapRes.data // 接口data数组直接作为gapList

      // 赋值平均工资
      salaryGapChartOption.value._averageSalary = averageSalary
      // 更新标题
      salaryGapChartOption.value.title.text = `各部门总工资与公司平均工资差距（平均：${Number(averageSalary).toLocaleString()}元）`
      // 填充Y轴部门名称
      salaryGapChartOption.value.yAxis.data = gapList.map(item => item.deptName)
      // 填充条形图数据（映射真实接口字段）
      salaryGapChartOption.value.series[0].data = gapList.map(item => ({
        value: item.value, // 真实接口的差距值是value字段
        deptName: item.deptName,
        totalSalary: item.totalSalary,
        totalBase: item.totalBase,
        totalBonus: item.totalBonus, // 接口返回的字段
        totalFine: item.totalFine    // 接口返回的字段
      }))
    } else {
      ElMessage.warning('部门工资差距数据获取失败或无数据')
      // 无数据时置空，不再显示示例数据
      salaryGapChartOption.value._averageSalary = 0
      salaryGapChartOption.value.title.text = '各部门总工资与公司平均工资差距（暂无数据）'
      salaryGapChartOption.value.yAxis.data = []
      salaryGapChartOption.value.series[0].data = []
    }

    // 填充年龄分布图表数据 
    if (ageDistRes.code === 200 && Array.isArray(ageDistRes.data)) {
      const ageRealData = ageDistRes.data;
      // 填充X轴年龄段
      ageDistChartOption.value.xAxis.data = ageRealData.map(item => item.ageRange);
      // 填充男性数据
      ageDistChartOption.value.series[0].data = ageRealData.map(item => item.male);
      // 填充女性数据
      ageDistChartOption.value.series[1].data = ageRealData.map(item => item.female);
      // 填充总人数数据
      ageDistChartOption.value.series[2].data = ageRealData.map(item => item.total);
    } else {
      ElMessage.warning('员工年龄分布数据获取失败');
      ageDistChartOption.value.xAxis.data = [];
      ageDistChartOption.value.series.forEach(series => series.data = []);
    }

  } catch (error) {
    console.error('数据获取失败：', error)
    ElMessage.error('数据加载失败，请刷新重试')
    // 异常时置空所有图表数据
    chartOption.value.series[0].data = []
    salaryGapChartOption.value.yAxis.data = []
    salaryGapChartOption.value.series[0].data = []
    ageDistChartOption.value.xAxis.data = []
    ageDistChartOption.value.series.forEach(series => series.data = []);
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getCountData()
})
</script>

<style lang="less" scoped>
.home {
  width: 100%;
  height: auto;
  box-sizing: border-box;
  background-color: #FCFAFC;
  padding: 0 20px;

  .el-col {
    margin-top: 20px;
  }
}

.stats-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  height: 80px;
  transition: all 0.5s ease;

  &__left {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  &__title {
    font-size: 14px;
    color: #666;
    font-weight: 400;
  }

  &__value-wrap {
    display: flex;
    align-items: end;
    gap: 8px;
  }

  &__value {
    font-size: 28px;
    font-weight: 700;
    color: #333;
    line-height: 1;
  }

  &__percent {
    font-size: 15px;
    display: flex;
    align-items: center;

    &.positive {
      color: #67c23a;
    }

    &.negative {
      color: #f56c6c;
    }
  }

  &__right {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &:hover {
    box-shadow: 5px 5px 25px rgba(0, 0, 0, 0.15);
  }
}

.chart-card {
  background: #fff;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  height: 500px;
  margin-top: 20px;
  transition: all 0.5s ease;

  &:hover {
    box-shadow: 5px 5px 25px rgba(0, 0, 0, 0.15);
  }
}

.chart {
  width: 100%;
  height: 100%;
}
</style>