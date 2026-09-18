(function () {

// -------------------------------------------------------------------------------------------------------
window.bar_line_chart = function (canvasId, labels, barData, lineData) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [
                    {
                        type: 'bar',
                        label: 'Bar',
                        data: barData,
                        backgroundColor: "#4CAF50",
                        borderColor: "#ffffff",
                        borderWidth: 1,
                        datalabels: {
                            color: "#ffffff",
                            anchor: "center",   // label inside bar
                            align: "center",
                            font: {
                                size: 14,
                                weight: "bold"
                            },
                            formatter: function (value) {
                                return value;
                            }
                        }
                    },
                    {
                        type: 'line',
                        label: 'Line',
                        data: lineData,
                        borderColor: "#0000FF",
                        backgroundColor: "rgba(0,0,0,0)",
                        fill: false,
                        pointBackgroundColor: "#0000FF",
                        pointBorderColor: "#ffffff",
                        pointRadius: 5,
                        datalabels: {
                            color: "#ffffff",
                            anchor: "end",     // label above point
                            align: "top",
                            font: {
                                size: 14,
                                weight: "bold"
                            },
                            formatter: function (value) {
                                return value;
                            }
                        }
                    }
                ]
            },

            options: {
                responsive: true,
                legend: {
                    position: "bottom",
                    labels: { fontSize: 12 }
                },

                plugins: {
                    datalabels: { display: true }  // ensure plugin runs globally
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

    } catch (e) { 
        console.log("Error in bar_line_chart: " + e); 
    }
};
// -------------------------------------------------------------------------------------------------------


	
})();