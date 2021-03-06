<div class="panel-body chart" style="float: left">
    <canvas id="${name}" class="chart" width="550" height="400"></canvas>
</div>

<script type="text/javascript">


    var div = document.getElementById("${name}");
    var data = {
        labels: [${labels}],
        datasets: [
            {
                label: "${name}",
                fill: false,
                lineTension: 0,
                pointBorderColor: "rgba(255, 0, 0, 0.8)",
                data: [${values}],
                spanGaps: false,
                borderColor: "rgba(255, 0, 0, 0.6)",
            },

            {
                label: "Ideal",
                fill: false,
                lineTension: 0,
                pointBorderColor: "rgba(0, 153, 255, 0.8)",
                data: [${idealValues}],
                spanGaps: false,
                borderColor: "rgba(0, 153, 255, 0.6)",
            },

            {
                label: "Trend",
                fill: false,
                lineTension: 0,
                pointBorderColor: "rgba(255, 0, 0, 0.4)",
                data: [${trendValues}],
                spanGaps: false,
                borderColor: "rgba(255, 0, 0, 0.2)",
            }
        ]
    };

    new Chart(div, {
        type: 'line',
        data: data,
        options: {
            title: {
                display: true,
                text: 'Burn down chart'
            },
            responsive: false,
            tooltips: {
                enabled: false
            },
            hover: {
                animationDuration: 0
            },
            animation: {
                duration: 1,
                onComplete: function () {
                    var chartInstance = this.chart,
                            ctx = chartInstance.ctx;
                    ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, Chart.defaults.global.defaultFontStyle, Chart.defaults.global.defaultFontFamily);
                    ctx.textAlign = 'center';
                    ctx.textBaseline = 'bottom';
                    ctx.fillStyle = 'grey';

                    this.data.datasets.forEach(function (dataset, i) {
                        var meta = chartInstance.controller.getDatasetMeta(i);
                        if (dataset.label != 'Ideal' && dataset.label != 'Trend')
                            meta.data.forEach(function (bar, index) {
                                var data = dataset.data[index];
                                ctx.fillText(data, bar._model.x, bar._model.y - 5);
                            });
                    });
                }
            }
        }
    });


</script>
