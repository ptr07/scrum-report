<div class="panel-body chart" style="float: left">
    <canvas id="${name}-flow" width="550" height="400"></canvas>
</div>

<script type="text/javascript">


    var barDiv = document.getElementById("${name}-flow");

    new Chart(barDiv, {
        type: 'bar',
        data: {
            labels: [${labels}],
            datasets: [
            <#list statusValues as item>
                {
                    label: "${item.name()}",
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
                text: 'Flow chart'
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