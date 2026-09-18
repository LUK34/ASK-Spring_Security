// charts_call.js (IE11 SAFE + VERIFIED)

(function () {

    console.log("=== charts_call.js LOADED ===");

    function waitForCanvases(callback) {

        console.log(" Waiting for canvas elements...");

        var attempts = 0;
        var maxAttempts = 30;

        var timer = setInterval(function () {

            attempts++;

            var canvases = document.getElementsByTagName("canvas");

            console.log(
                "🔁 Attempt:", attempts,
                "| Canvas count:", canvases ? canvases.length : 0
            );

            if (canvases && canvases.length > 0) {
                clearInterval(timer);
                console.log("✅ Canvases detected:", canvases.length);
                callback();
            }

            if (attempts >= maxAttempts) {
                clearInterval(timer);
                console.warn("❌ No canvases detected after waiting");
            }

        }, 100);
    }

    function renderCharts() {

        console.log("=== renderCharts START ===");

        if (!window.CHART_DATA) {
            console.error("❌ CHART_DATA is undefined");
            return;
        }

        if (window.CHART_DATA.length === 0) {
            console.warn("⚠️ CHART_DATA is empty");
            return;
        }

        if (typeof pie_chart_general !== "function") {
            console.error("❌ pie_chart_general() not found");
            return;
        }

        var grouped = {};

        for (var i = 0; i < window.CHART_DATA.length; i++) {

            var r = window.CHART_DATA[i];
            var canvasId = "chart_" + r.officeCode;
            var canvas = document.getElementById(canvasId);

            if (!canvas) {
                console.warn("⛔ Canvas NOT FOUND:", canvasId);
                continue;
            }

            if (!grouped[canvasId]) {
                grouped[canvasId] = { labels: [], values: [], colors: [] };
            }

            grouped[canvasId].labels.push(r.patType);
            grouped[canvasId].values.push(parseInt(r.patCount, 10));
            grouped[canvasId].colors.push(r.colorValue);
        }

        for (var id in grouped) {
            if (!grouped.hasOwnProperty(id)) continue;

            pie_chart_general(
                id,
                grouped[id].labels,
                grouped[id].values,
                grouped[id].colors
            );
        }

        console.log("=== renderCharts END ===");
    }

    // ✅ safest start point for IE11
    window.onload = function () {
        waitForCanvases(renderCharts);
    };

})();



/*
$(document).ready(function () {

    if (!window.CHART_DATA || window.CHART_DATA.length === 0) {
        console.warn("No chart data");
        return;
    }

    var grouped = {};

    for (var i = 0; i < window.CHART_DATA.length; i++) {
        var r = window.CHART_DATA[i];

        if (!grouped[r.officeCode]) {
            grouped[r.officeCode] = {
                labels: [],
                values: [],
                colors: []
            };
        }

        grouped[r.officeCode].labels.push(r.patType);
        grouped[r.officeCode].values.push(r.patCount);
        grouped[r.officeCode].colors.push(r.colorValue);
    }

    for (var canvasId in grouped) {
        if (!grouped.hasOwnProperty(canvasId)) continue;

        if (!document.getElementById(canvasId)) {
            console.warn("Canvas not found:", canvasId);
            continue;
        }

        pie_chart(
            canvasId,
            grouped[canvasId].labels,
            grouped[canvasId].values,
            grouped[canvasId].colors
        );
    }
});
*/


  
  /*  $(document).ready(function () 
  {	
		pie_chart("family_pie_chart_9", ["Father", "Person", "Sons"],[10, 20, 30],["#000000","#008000", "#0000FF"]);
		pie_chart("family_pie_chart_10", ["Father", "Person", "Sons"],[10, 20, 30],["#000000","#008000", "#0000FF"]);
		pie_chart("family_pie_chart_11", ["Father", "Person", "Sons"],[10, 20, 30],["#000000","#008000", "#0000FF"]);
  });*/
  
  /*
      $(document).ready(function () 
      {

        var labels = ["Father", "Person", "Sons"];
        var data   = [10, 20, 30];
        var colors = ["#000000", "#008000", "#0000FF"];

        if (!chartCanvasIds || chartCanvasIds.length === 0) 
        {
            console.log("No charts to render");
            return;
        }

        for (var i = 0; i < chartCanvasIds.length; i++) {

            var canvasId = chartCanvasIds[i];

            if (canvasId && document.getElementById(canvasId)) {
                pie_chart(canvasId, labels, data, colors);
            }
        }

    });
  
  */
    
//--------------------------------------------------------------------------------------------------------------------------
