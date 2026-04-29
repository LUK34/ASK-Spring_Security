(function () {

// -------------------------------------------------------------------------------------------------------
window.doughnut_chart = function (canvasId, labels, dataArr, colors) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: dataArr,
                    backgroundColor: colors,
                    borderColor: "#ffffff",
                    borderWidth: 1
                }]
            },
            options: {
                cutoutPercentage: 60, // inner hole size
                responsive: true,
                legend: { position: 'bottom' },

                // ⭐ WHITE LABELS INSIDE DOUGHNUT SLICES
                plugins: {
                    datalabels: {
                        color: "#ffffff",     // white numbers
                        anchor: "center",     // place inside slice
                        align: "center",      // perfect center alignment
                        font: {
                            size: 14,
                            weight: "bold"
                        },
                        formatter: function (value) {
                            return value;      // numeric value
                        }
                    }
                }
            }
        });

    } catch (e) { 
        console.log("Error in doughnut_chart: " + e); 
    }
};
// -------------------------------------------------------------------------------------------------------
	
})();