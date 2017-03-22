<div class="panel-body chart" style="float: left">
    <canvas id="${name}-pie" class="chart" width="550" height="400"></canvas>
</div>

<script type="text/javascript">

    var pieDiv = document.getElementById("${name}-pie");
    new Chart(pieDiv, {
        type: 'doughnut',
        data: {
            labels: [${statuses}],
            datasets: [
                {
                    data: [${hoursByStatus}],
                    backgroundColor: [
                        "#b3b3cc",
                        "#ff5c33",
                        "#ff66d9",
                        "#6699ff",
                        "#bf8040",
                        "#cccc00",
                        "#ffad33",
                        "#53c653",
                        "#59b300",
                        "#00e600"
                    ]

                }]
        }, options: {
            title: {
                display: true,
                text: 'Hours/ticket status'
            }, showAllTooltips: true, responsive: false
        }
    });

</script>