// ---------------------------------------------------------------------------------------------------------------
function PieChartCreator_msd(
    ChartLabelElement,
    ChartDataElement,
    ChartNameElement,
    ChartColorElement,
    ChartHoverDescriptionElement,
    ChartUrlLogicElement,
    ChartLangElement
) {

    /* ------------------------------
       LABEL PROCESSING
    ------------------------------ */
    var ChartLabelRaw = ChartLabelElement && ChartLabelElement.value
        ? ChartLabelElement.value
        : "";

    ChartLabelRaw = ChartLabelRaw
        .replace("[", "")
        .replace("]", "")
        .replace(/-MultiSpeciality/g, "");

    var ChartLabel = ChartLabelRaw.split(",");

    for (var i = 0; i < ChartLabel.length; i++) {
        ChartLabel[i] = ChartLabel[i].trim();
    }

    /* ----------------------------------------
       Preserve original EN labels for URL
    ---------------------------------------- */
    var originalEnglishLabels = ChartLabel.slice(); // IE11-safe copy

    /* ------------------------------
       DATA PROCESSING
    ------------------------------ */
    var ChartDataRaw = ChartDataElement && ChartDataElement.value
        ? ChartDataElement.value
        : "";

    ChartDataRaw = ChartDataRaw.replace("[", "").replace("]", "");

    var ChartData = ChartDataRaw.split(",");

    /* ------------------------------
       LANGUAGE TRANSLATION (AR)
    ------------------------------ */
    if (ChartLangElement === 'ar' && typeof LabelTranslationsData !== 'undefined') {
        for (var j = 0; j < ChartLabel.length; j++) {
            if (LabelTranslationsData[ChartLabel[j]]) {
                ChartLabel[j] = LabelTranslationsData[ChartLabel[j]];
            }
        }
    }

    /* ------------------------------
       CHART DATA OBJECT
    ------------------------------ */
    var ChartDataToDisplay = {
        labels: ChartLabel,
        datasets: [{
            data: ChartData,
            backgroundColor: ChartColorElement,
            label: ChartHoverDescriptionElement
        }]
    };

    /* ------------------------------
       CHART OPTIONS
    ------------------------------ */
    var ChartOptions = {
        legend: {
            display: true
        },
        plugins: {
            labels: {
                render: 'value',
                fontColor: 'white',
                fontStyle: 'bolder'
            },
            datalabels: {
                color: 'white',
                fontStyle: 'bolder',
                formatter: function (value) {
                    return value > 0 ? value : '';
                }
            }
        },
        onClick: function (event, element) {
            if (ChartUrlLogicElement !== "" && element.length > 0) {
                var index = element[0]._index;
                var englishLabel = originalEnglishLabels[index];
                var url = ChartUrlLogicElement + encodeURIComponent(englishLabel);
                openWindowCreator(url);
            }
        }
    };

    /* ------------------------------
       CREATE CHART
    ------------------------------ */
    var ctx = document.getElementById(ChartNameElement);

    if (!ctx) {
        if (window.console) {
            console.log("PieChartCreator_msd : Canvas not found -> " + ChartNameElement);
        }
        return;
    }

    new Chart(ctx, {
        type: 'pie',
        data: ChartDataToDisplay,
        options: ChartOptions,
        plugins: [window.ChartDataLabels]
    });
}



// ---------------------------------------------------------------------------------------------------------------

(function () {

    window.pie_chart_general = function (canvasId, labels, dataArr, colors) {

        try {
            var canvas = document.getElementById(canvasId);
            if (!canvas) return;

            canvas.style.width  = "200px";
            canvas.style.height = "200px";

            var ctx = canvas.getContext("2d");

            if (canvas._chartInstance) {
                canvas._chartInstance.destroy();
                canvas._chartInstance = null;
            }

            canvas._chartInstance = new Chart(ctx, {
                type: 'pie',
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
                    responsive: true,
                    maintainAspectRatio: false,
                    animation: false,

                    // 🔽 LEGEND (below chart)
                    legend: {
                        position: 'bottom',
                        labels: {
                            fontColor: "#000000",   // 
                            fontSize: 12,
                            boxWidth: 12
                        }
                    },

                    // 🔽 TOOLTIP (optional)
                    tooltips: {
                        enabled: true,
                        bodyFontColor: "#ffffff",
                        titleFontColor: "#ffffff"
                    },

                    // 🔽 VALUES INSIDE PIE
                    plugins: {
                        datalabels: {
                            color: "#080802",      // ✅ WHITE #ffffff
                            font: {
                                size: 18, //12
                                weight: "bold"
                            },
                            formatter: function (value) {
                                return value;
                            }
                        }
                    }
                }
            });

        } catch (e) {
            console.error("Pie chart error:", e);
        }
    };

})();



// pie_chart_legacy.js  -- IE11 compatible -> old 16 dec 2025

(function () {

// -------------------------------------------------------------------------------------------------------
	window.pie_chart = function (canvasId, labels, dataArr, colors) 
	{
		console.log("Hello from Pie Chart Legacy Function !!!!.")
    try {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;

        var ctx = canvas.getContext("2d");

        return new Chart(ctx, {
            type: 'pie',
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
                responsive: true,
                legend: { position: 'bottom' },
                plugins: {
                    datalabels: {
                        color: "#ffffff",
                        font: { size: 12, weight: "bold" },
                        formatter: function (v) { return v; }
                    }
                }
            }
        });
    } catch (e) { console.log(e); }
};

// -------------------------------------------------------------------------------------------------------


})();



