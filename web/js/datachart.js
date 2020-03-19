
function data_chart1(){
    let day_month12 = ['1:00','2:00','3:00','4:00','5:00','6:00','7:00','8:00','9:00','10:00','11:00','12:00'];
    let day_month12_data1 = [0.0, 7.9, 10.0, 16.2, 20.6,26.7, 18.6, 16.2, 20.6, 14.0,8.4, 7.3];
    let daymonth12_data2=[1.6,8.9, 12.0, 18.4, 16.7, 22.7, 27.6, 25.2,22.7, 18.8, 10.0, 6.3];

    let cementelecuse_box = echarts.init(document.getElementById('leftup_box'));
    let cementelecuseoption = {
        color:['#e5e7e4'],
        tooltip: {trigger: 'axis',
            textStyle: {fontSize:10}},
        grid: {
            left: '0%', right:'1%',
            top:'10%', bottom: '1%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: day_month12,
                axisLabel: {color: '#83c7e3',fontSize:10},
                axisLine: {lineStyle: {color: 'rgba(255,255,255,0.3)'}},

            },
        ],
        yAxis: [
            {
                type: 'value',
                splitNumber:5,
                axisLabel: {color: '#83c7e3',fontSize:10},
                axisLine: {lineStyle: {color: 'rgba(255,255,255,0.3)'}},
                splitLine:{lineStyle: {color: 'rgba(255,255,255,0.3)'}},
                splitArea:{
                    show:true,
                    areaStyle:{
                        color:'rgba(26,82,157,0.3)',
                    },
                },
            },
        ],
        series: [
            {
                name:'模拟',
                type:'line',
                smooth:true,
                symbol:"none",
                data:day_month12_data1,
                lineStyle: {normal: {width: 1.5}},
            }
        ]
    };
    cementelecuse_box.setOption(cementelecuseoption);
    window.onresize = cementelecuse_box.resize();
}


function data_chart2(){
    let day_month12 = ['1:00','2:00','3:00','4:00','5:00','6:00','7:00','8:00','9:00','10:00','11:00','12:00'];
    let day_month12_data1 = [0.0, 7.9, 10.0, 16.2, 20.6,26.7, 18.6, 16.2, 20.6, 14.0,8.4, 7.3];
    let daymonth12_data2=[1.6,8.9, 12.0, 18.4, 16.7, 22.7, 27.6, 25.2,22.7, 18.8, 10.0, 6.3];

    let cementelecuse_box = echarts.init(document.getElementById('rightup_box'));
    let cementelecuseoption = {
        color:['#e5e7e4', '#00ff00'],
        tooltip: {trigger: 'axis',
            textStyle: {fontSize:14}},
        grid: {
            left: '0%', right:'1%',
            top:'10%', bottom: '1%',
            containLabel: true
        },
        legend: {
            data:['预测','实际'],
            icon: 'rect',
            itemWidth: 10, itemHeight: 10, itemGap: 8,
            textStyle: {color: '#83c7e3',fontSize:14}
        },
        xAxis: [
            {
                type: 'category',
                data: day_month12,
                axisLabel: {color: '#83c7e3',fontSize:14},
                axisLine: {lineStyle: {color: 'rgba(255,255,255,0.3)'}},

            },
        ],
        yAxis: [
            {
                type: 'value',
                splitNumber:5,
                axisLabel: {color: '#83c7e3',fontSize:14},
                axisLine: {lineStyle: {color: 'rgba(255,255,255,0.3)'}},
                splitLine:{lineStyle: {color: 'rgba(255,255,255,0.3)'}},
                splitArea:{
                    show:true,
                    areaStyle:{
                        color:'rgba(26,82,157,0.3)',
                    },
                },
            },
        ],
        series: [
            {
                name:'预测',
                type:'line',
                smooth:true,
                symbol:"none",
                data:day_month12_data1,
                lineStyle: {normal: {width: 1.5}},
            },
            {
                name:'实际',
                type:'line',
                smooth:true,
                symbol:"none",
                data:daymonth12_data2,
                lineStyle: {normal: {width: 1.5}},
            },
        ]
    };
    cementelecuse_box.setOption(cementelecuseoption);
    window.onresize = cementelecuse_box.resize();
}

function chart_flush() {
    data_chart1();
    data_chart2();
}