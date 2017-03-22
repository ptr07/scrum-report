<div class="panel-body chart" style="float: left">
    <canvas id="${name}-start" class="chart" width="550" height="400"></canvas>
</div>


<script type="text/javascript">
    var startDiv = document.getElementById("${name}-start");

    new Chart(startDiv, {
        type: 'bar',
        data: {
            labels: [${projectsNames}],
            datasets: [
            <#list projectsValues as item>
                {
                    label: "${item.kind()}",
                    backgroundColor: "${item.color()}",
                    borderWidth: 1,
                    stacked: true,
                    data: [${item.valuesString()}],
                },
            </#list>]
        },

        options: {
            title: {
                display: true,
                text: 'Projects chart'
            },
            scales: {
                xAxes: [{
                    barPercentage: 0.9,
                    categoryPercentage: 0.9,
                    stacked: true
                }],
                yAxes: [{
                    stacked: true
                }]
            }
        }
    });
</script>