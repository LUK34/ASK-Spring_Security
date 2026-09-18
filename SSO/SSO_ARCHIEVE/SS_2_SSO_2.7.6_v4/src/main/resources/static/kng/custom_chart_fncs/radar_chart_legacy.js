(function () {

// -------------------------------------------------------------------------------------------------------

window.radar_chart = function (canvasId, labels, dataArr, color) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'radar',
            data: {
                labels: labels,
                datasets: [{
                    data: dataArr,
                    backgroundColor: color,
                    borderColor: "#000"
                }]
            },
            options: {
                responsive: true,
                legend: { display: false }
            }
        });
    } catch (e) { console.log(e); }
};


// -------------------------------------------------------------------------------------------------------



	
})();