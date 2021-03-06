<div class="panel-body chart" style="float: left">
    <canvas id="${name}-worklog" class="chart" width="550" height="400"></canvas>
</div>

<script type="text/javascript">
    var worklogDiv = document.getElementById("${name}-worklog");

    new Chart(worklogDiv, {
        type: 'bar',
        data: {
            labels: [${projectsNames}],
            datasets: [
            <#list workLogProjectsValues as item>
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
                text: 'Work log / project'
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