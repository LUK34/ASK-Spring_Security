(function () {

// -------------------------------------------------------------------------------------------------------
window.horizontal_bar_chart = function (canvasId, labels, dataArr, barColor) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'horizontalBar',
            data: {
                labels: labels,
                datasets: [{
                    data: dataArr,
                    backgroundColor: barColor,
                    borderColor: "#ffffff",
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                legend: { display: false },

                // ⭐ WHITE LABELS INSIDE BAR (same as vertical bar)
                plugins: {
                    datalabels: {
                        color: "#ffffff",
                        anchor: "center",   // position label inside the bar
                        align: "center",    // horizontally & vertically center it
                        font: {
                            size: 14,
                            weight: "bold"
                        },
                        formatter: function (value) {
                            return value;    // numeric value
                        }
                    }
                },

                // X-axis: value axis
                scales: {
                    xAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }],
                    // Y-axis: category labels
                    yAxes: [{
                        ticks: {
                            fontColor: "#000"
                        }
                    }]
                }
            }
        });

    } catch (e) {
        console.log("Error creating horizontal_bar_chart: " + e);
    }
};
// -------------------------------------------------------------------------------------------------------




	
})();