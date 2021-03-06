<div class="panel-body chart" style="float: left">
    <canvas id="${name}-done" class="chart" width="550" height="400"></canvas>
</div>

<script type="text/javascript">
    var doneDiv = document.getElementById("${name}-done");

    new Chart(doneDiv, {
        type: 'bar',
        data: {
            labels: [${projectsNames}],
            datasets: [
            <#list doneProjectsValues as item>
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
                text: 'Done tickets / project'
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