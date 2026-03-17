(function () {

// -------------------------------------------------------------------------------------------------------

window.bubble_chart = function (canvasId, bubbleData, color) {
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'bubble',
            data: {
                datasets: [{
                    data: bubbleData,
                    backgroundColor: color
                }]
            },
            options: { responsive: true }
        });
    } catch (e) { console.log(e); }
};



// -------------------------------------------------------------------------------------------------------



	
})();