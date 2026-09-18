(function () {

// -------------------------------------------------------------------------------------------------------

window.polar_area_chart = function (canvasId, labels, dataArr, colors) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'polarArea',
            data: {
                labels: labels,
                datasets: [{
                    data: dataArr,
                    backgroundColor: colors
                }]
            },
            options: { responsive: true }
        });
    } catch (e) { console.log(e); }
};


// -------------------------------------------------------------------------------------------------------



	
})();