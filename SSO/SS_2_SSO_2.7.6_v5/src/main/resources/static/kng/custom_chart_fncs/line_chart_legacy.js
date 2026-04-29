(function () {

// -------------------------------------------------------------------------------------------------------

window.line_chart = function (canvasId, labels, dataArr, lineColor) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    data: dataArr,
                    borderColor: lineColor,
                    backgroundColor: "rgba(0,0,0,0)",
                    fill: false,
                    pointBackgroundColor: lineColor,
                    pointBorderColor: "#ffffff",
                    pointRadius: 5
                }]
            },
            options: {
                responsive: true,
                legend: { display: false },

                // ⭐ ADD WHITE LABELS HERE
                plugins: {
                    datalabels: {
                        color: "#ffffff",
                        align: "top",
                        anchor: "end",
                        font: {
                            size: 12,
                            weight: "bold"
                        },
                        formatter: function (value) {
                            return value;  // show numeric label
                        }
                    }
                }
            }
        });

    } catch (e) { 
        console.log("Error in line_chart: " + e); 
    }
};









/*window.line_chart = function (canvasId, labels, dataArr, lineColor) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    data: dataArr,
                    borderColor: lineColor,
                    backgroundColor: "rgba(0,0,0,0)",
                    fill: false
                }]
            },
            options: {
                responsive: true,
                legend: { display: false }
            }
        });
    } catch (e) { console.log(e); }
};
*/

// -------------------------------------------------------------------------------------------------------



	
})();