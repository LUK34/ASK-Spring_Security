(function () {

// -------------------------------------------------------------------------------------------------------
window.vertical_bar_chart = function (canvasId, labels, dataArr, barColor) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'bar',
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

                // ⭐ FIXED WHITE LABELS — now clearly visible
                plugins: {
                    datalabels: {
                        color: "#ffffff",
                        anchor: "center",   // inside bar
                        align: "center",    // perfect center alignment
                        font: {
                            size: 14,
                            weight: "bold"
                        },
                        formatter: function (value) {
                            return value;
                        }
                    }
                },

                scales: {
                    yAxes: [{
                        ticks: { beginAtZero: true }
                    }],
                    xAxes: [{
                        ticks: { fontColor: "#000" }
                    }]
                }
            }
        });

    } catch (e) { console.log(e); }
};


// -------------------------------------------------------------------------------------------------------

})();