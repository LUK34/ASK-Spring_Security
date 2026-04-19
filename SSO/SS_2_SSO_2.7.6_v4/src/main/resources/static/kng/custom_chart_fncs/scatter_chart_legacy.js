(function () {

// -------------------------------------------------------------------------------------------------------

window.scatter_chart = function (canvasId, pointData, color) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'scatter',
            data: {
                datasets: [{
                    data: pointData,
                    backgroundColor: color
                }]
            },
            options: {
                scales: {
                    xAxes: [{ type: 'linear', position: 'bottom' }]
                }
            }
        });
    } catch (e) { console.log(e); }
};



// -------------------------------------------------------------------------------------------------------



	
})();