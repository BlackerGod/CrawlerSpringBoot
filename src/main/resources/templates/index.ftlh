<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>我的Github趋势</title>
</head>
<body onload="run();">

<!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
<div id="main" style="width: 100%;height:600px;"></div>

<script src="/js/echarts.min.js"></script>
<script src="/js/jquery.min.js"></script>
<script type="text/javascript">
    function run () {
        console.log("开始")
        var data = ${projectList}
        var projectNames= [];
        var stars = [];
        for (var i = 0; i < data.length; i++){
            var project = data[i];
            projectNames.push(project.name);
            stars.push(project.starcount);
        }
        drawStars(projectNames,stars);
    }


    function drawStars(projectNames,stars) {
        var myChart = echarts.init(document.getElementById('main'));
        var option = {
            title: {
                text: 'Star 天榜'
            },
            tooltip: {},
            legend: {
                data:['Star']
            },
            xAxis: {
                //data: ["衬衫","羊毛衫","雪纺衫","裤子","高跟鞋","袜子"]
                data:projectNames
            },
            yAxis: {},
            series: [{
                name: 'Star',
                type: 'bar',//柱状图
                //data: [5, 20, 36, 10, 10, 20]
                data:stars
            }],
            dataZoom: [
                {   // 这个dataZoom组件，默认控制x轴。
                    type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
                    start: 0,      // 左边在 10% 的位置。
                    end: 10         // 右边在 60% 的位置。
                },
                {   // 这个dataZoom组件，也控制x轴。
                    type: 'inside', // 这个 dataZoom 组件是 inside 型 dataZoom 组件
                    start: 0,      // 左边在 10% 的位置。
                    end: 10         // 右边在 60% 的位置。
                }
            ],
        };
        myChart.setOption(option);
    }
</script>
</body>
</html>