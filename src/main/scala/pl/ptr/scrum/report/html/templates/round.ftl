<div class="panel-body pie chart" style="float: left">
    <canvas class="pie chart" id="${name}-pie-all" width="550" height="300"></canvas>
</div>

<div class="panel-body pie chart" style="float: left">
    <canvas class="pie chart" id="${name}-pie-done" width="550" height="300"></canvas>
</div>

<div class="panel-body pie chart" style="float: left">
    <canvas class="pie chart" id="${name}-pie-log" width="550" height="300"></canvas>
</div>


<script type="text/javascript">
    var allDiv = document.getElementById("${name}-pie-all");
    var allPieChart = new Chart(allDiv, {
        type: 'doughnut',
        data: {
            labels: [${taskTypesNames}],
            datasets: [
                {
                    data: [${taskTypesValues}],
                    backgroundColor: [
                    ${taskTypesColors}]

                }]
        }, options: {
            title: {
                display: true,
                text: 'All estimated tickets'
            }, showAllTooltips: true, responsive: false
        }
    });


</script>


<script type="text/javascript">
    var doneDiv = document.getElementById("${name}-pie-done");
    new Chart(doneDiv, {
        type: 'doughnut',
        data: {
            labels: [${taskTypesNames}],
            datasets: [
                {
                    data: [${doneHoursByType}],
                    backgroundColor: [
                    ${taskTypesColors}]

                }]
        }, options: {
            title: {
                display: true,
                text: 'Done tickets / ticket type'
            }, showAllTooltips: true, responsive: false
        }
    });


</script>


<script type="text/javascript">
    var logDiv = document.getElementById("${name}-pie-log");
    new Chart(logDiv, {
        type: 'doughnut',
        data: {
            labels: [${taskTypesNames}],
            datasets: [
                {
                    data: [${workLogByType}],
                    backgroundColor: [
                    ${taskTypesColors}]

                }]
        }, options: {
            title: {
                display: true,
                text: 'Logged work / ticket type'
            }, showAllTooltips: true, responsive: false
        }
    });


</script>