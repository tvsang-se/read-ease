<template>
    <div class="text-center font-weight-medium text-high-emphasis">
        <span>Biểu đồ thống kê số tài khoản được tạo mới theo</span>
        <div class="select-dropdown">
            <select v-model="time">
                <option v-for="(option, index) in timeOptions" :value="option.value" :key="index">{{ option.title }}
                </option>
            </select>
        </div>
        <div class="select-dropdown mr-2">
            <select v-model="year">
                <option v-for="(option, index) in listYear" :value="option.value" :key="index">{{ option.title }}
                </option>
            </select>
        </div>
        [{{ totalUser }}]
    </div>
    <Line id="my-chart-id" :options="chartOptions" :data="chartData" :key="keyComponent" />
</template>
  
<script setup>
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
} from 'chart.js'
import { Line } from 'vue-chartjs'

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
)

// import {
//   Chart as ChartJS,
//   Title,
//   Tooltip,
//   Legend,
//   BarElement,
//   CategoryScale,
//   LinearScale
// } from 'chart.js'
// import { Bar } from 'vue-chartjs'
// import * as chartConfig from './chartConfig.js'

// ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

import { ref, watch, computed } from 'vue'
import { useUserStore } from '../store/user'
import Admin2Service from '../services/admin2.service';

const chartData = ref({})
const chartOptions = ref({})
const time = ref('date')
const year = ref(0)
const keyComponent = ref(0)
const listYear = ref([])

listYear.value = useUserStore().user.listYear.map(year => { return { 'title': year, value: year } })
listYear.value.push({ title: 'Tất cả', value: 0 })
const timeOptions = ref([])
timeOptions.value = [{ value: 'date', title: "ngày" }, { value: 'week', title: "tuần" }, { value: 'month', title: "tháng" }, { value: 'year', title: "năm" }]

chartData.value.labels = useUserStore().user.userCreatedByDate.labels //['2017','2018', '2019', '2020', '2021', '2022', '2023','2017','2018', '2019', '2020', '2021', '2022', '2023','2017','2018', '2019', '2020', '2021', '2022', '2023','2017','2018', '2019', '2020', '2021', '2022', '2023','2017','2018', '2019', '2020', '2021', '2022', '2023','2017','2018', '2019', '2020', '2021', '2022', '2023','2017','2018', '2019', '2020', '2021', '2022', '2023','2017','2018', '2019', '2020', '2021', '2022', '2023']
chartData.value.datasets = [{
    label: "Số tài khoản được tạo mới",
    data: useUserStore().user.userCreatedByDate.data, //[169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10,169,24, 30, 190, 30, 140, 10],
    borderColor: "#00adef",
    backgroundColor: "#00adef40",
    // borderWidth: 1.8
    fill: false,
    cubicInterpolationMode: 'monotone',
    tension: 0.9,
    borderWidth: 1,
    pointStyle: false
}]

const totalUser = computed(() => {
    let sum = 0, length = chartData.value.datasets[0].data.length
    for(let i=0; i<length; i++) {
        sum += chartData.value.datasets[0].data[i]
    }
    return sum
})

watch(time, (val) => {
    getData()
})

watch(year, (val) => {
    getData()
})

chartOptions.value = {
    // indexAxis: 'y',
    responsive: true,
    plugins: {
        legend: {
            position: 'top',
        },
        //   title: {
        //     display: true,
        //     text: 'Biểu đồ thống kê tài khoản được tạo mới theo'
        //   }
    },
    interaction: {
        intersect: false,
    },
    scales: {
        x: {
            display: true,
        },
        y: {
            display: true,
            suggestedMin: 0,
            suggestedMax: 6
        }
    }
}

async function getData() {
    try {
        let res
        if (year.value !== 0)
            res = await Admin2Service.getStatisticCreatedUsersByYear(time.value, year.value)
        else
            res = await Admin2Service.getStatisticCreatedUsers(time.value)

        chartData.value.labels = res.data.labels
        chartData.value.datasets[0].data = res.data.data
        keyComponent.value += 1
        // console.log(res)
    } catch (error) {
        console.log(error)
    }

}


// setTimeout(() => {
//     year.value = 2023
// },100)

</script>

<style scoped>
/* .timeOptions {
    border: 1.2px solid #000;
    padding: 1px 8px;
    text-align: center;
    font-weight: 700;
    font-size: 1.2em;
    margin-left: 8px;
} */

.select-dropdown,
.select-dropdown * {
    margin: 0;
    padding: 0;
    position: relative;
    box-sizing: border-box;
    text-align: center;
    font-weight: 600 !important;
    font-size: 1.1rem !important;
}

.select-dropdown {
    margin-left: 8px;
    position: relative;
    background-color: #E6E6E6;
    border-radius: 4px;
    display: inline-block;
}

.select-dropdown select {
    text-align: center;
    font-size: 1rem;
    font-weight: normal;
    max-width: 100%;
    padding: 2px 22px 2px 6px;
    border: none;
    background-color: transparent;
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
}

.select-dropdown select:active,
.select-dropdown select:focus {
    outline: none;
    box-shadow: none;
}

.select-dropdown:after {
    content: "";
    position: absolute;
    top: 50%;
    right: 8px;
    width: 0;
    height: 0;
    margin-top: -2px;
    border-top: 5px solid #aaa;
    border-right: 5px solid transparent;
    border-left: 5px solid transparent;
}
</style>