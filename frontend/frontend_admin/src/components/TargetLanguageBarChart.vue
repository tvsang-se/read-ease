<template>
    <div class="text-center font-weight-medium text-high-emphasis pa-1 pt-0">
        <span>Biểu đồ thống kê những ngôn ngữ được chọn dịch sang bởi người dùng [{{
            useUserStore().user.targetLanguageCount.length }}]</span>
    </div>
    <div>
        <Bar id="my-chart-id" :options="chartOptions" :data="chartData" />
    </div>
</template>
  
<script setup>
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'
import { languagesObj } from "../etc/languages";

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

import { ref, watch, computed } from 'vue'
import { useUserStore } from '../store/user'

const chartOptions = ref({})
const chartData = computed(() => {
    // alert("hi")
    const labels = useUserStore().user.targetLanguageCount.map(lang => { return languagesObj[lang.targetLanguage] })
    const data = useUserStore().user.targetLanguageCount.map(lang => { return lang.count })

    const chartColors = getRandomColorArray(data.length)
    const borderColor = chartColors[0]
    const backgroundColor = chartColors[1]
    const tempt = computed(() => {
        let t = data.map((d, i) => {
            return {
                label: labels[i],
                data: [d],
                borderColor: borderColor[i],
                backgroundColor: backgroundColor[i],
                borderWidth: 1
            }
        })
        return t;
    })
    // console.log(tempt.value)
    // chartData.value.labels = ['']
    // chartData.value.datasets = tempt.value
    return {
        labels: [''],
        datasets: tempt.value
    }
})


chartOptions.value = {
    indexAxis: 'y',
    responsive: true,
    plugins: {
        legend: {
            position: 'top',
        },
        // title: {
        //     display: true,
        //     text: 'Biểu đồ thống kê số ngôn ngữ được chọn dịch sang'
        // }
    },
    scales: {
        y: {
            display: true,
            title: {
                display: true,
                text: 'Ngôn ngữ'
            },
        }
    },
    maintainAspectRatio: false
}


function makeColor(colorNum, colors) {
    if (colors < 1) colors = 1;
    // defaults to one color - avoid divide by zero
    return colorNum * (360 / colors) % 360;
}

function getRandomColor() {
    let letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function getRandomColorArray(n) {
    let colorBorderArray = [];
    let colorBackgroundArray = [];
    for (let i = 0; i < n; i++) {
        let color = "hsla( " + makeColor(i, n) + ", 100%,50%,";
        colorBorderArray.push(color + '1)');
        colorBackgroundArray.push(color + '0.18)')
    }
    return [colorBorderArray, colorBackgroundArray];
}



</script>