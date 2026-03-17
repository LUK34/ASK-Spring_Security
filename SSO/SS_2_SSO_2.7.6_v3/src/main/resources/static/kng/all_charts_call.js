/* =======================================================
   GROUP DATA BY OFFICE CODE
   ======================================================= */
function groupByOffice(dataList) {

    var grouped = {};

    if (!dataList || dataList.length === 0) {
        return grouped;
    }

    for (var i = 0; i < dataList.length; i++) {

        var row = dataList[i];

        if (!row || !row.officeCode) {
            continue;
        }

        var office = row.officeCode;

        if (!grouped[office]) {
            grouped[office] = {
                labels: [],
                values: [],
                colors: []
            };
        }

        grouped[office].labels.push(row.patType);
        grouped[office].values.push(row.patCount);
        grouped[office].colors.push(row.colorValue);
    }

    return grouped;
}

/* =======================================================
   RENDER PIE CHARTS FOR ONE DATASET
   RETURNS true IF AT LEAST ONE CHART IS RENDERED
   ======================================================= */
function renderChartsForDataset(dataList) {

    var grouped = groupByOffice(dataList);
    var renderedAnyChart = false;

    for (var officeCode in grouped) {

        if (!grouped.hasOwnProperty(officeCode)) {
            continue;
        }

        var canvasId = "chart_" + officeCode;
        var canvas = document.getElementById(canvasId);

        if (!canvas) {
            continue;
        }

        pie_chart_general(
            canvasId,
            grouped[officeCode].labels,
            grouped[officeCode].values,
            grouped[officeCode].colors
        );

        renderedAnyChart = true;
    }

    return renderedAnyChart;
}

/* =======================================================
   MAIN LOAD HANDLER
   ======================================================= */
function onAllChartsLoad() {

    console.log("all_charts_call.js loaded");

    var hasGeneralData = false;

    /* ---------------- Appointment ---------------- */
    var appoPanel = document.getElementById("panel_APPO");
    GENERAL_PANEL_VISIBILITY["panel_APPO"] = false;
    if (window.APPO_CHART_DATA && window.APPO_CHART_DATA.length > 0) 
    {
        if (renderChartsForDataset(window.APPO_CHART_DATA)) 
        {
            hasGeneralData = true;
            GENERAL_PANEL_VISIBILITY["panel_APPO"] = true;
        }
    } 
    else 
    {
         if (appoPanel) 
         {
        	appoPanel.style.display = "none";
    	}
    }

    /* ---------------- Sick Leave ---------------- */
    var sickPanel = document.getElementById("panel_SICK");
    GENERAL_PANEL_VISIBILITY["panel_SICK"] = false;
    if (window.SICK_CHART_DATA && window.SICK_CHART_DATA.length > 0) 
    {
        if (renderChartsForDataset(window.SICK_CHART_DATA)) 
        {
            hasGeneralData = true;
            GENERAL_PANEL_VISIBILITY["panel_SICK"] = true;
        }
    } 
    else 
    {
        
        if (sickPanel) 
        {
            sickPanel.style.display = "none";
        }
    }

    /* ---------------- Dispense ---------------- */
    var dispPanel = document.getElementById("panel_DISPENSE");
    GENERAL_PANEL_VISIBILITY["panel_DISPENSE"] = false;
    if (window.DISP_CHART_DATA && window.DISP_CHART_DATA.length > 0) 
    {
        if (renderChartsForDataset(window.DISP_CHART_DATA)) 
        {
            hasGeneralData = true;
            GENERAL_PANEL_VISIBILITY["panel_DISPENSE"] = true;
        }
    } 
    else 
    {
        if (dispPanel) 
        {
            dispPanel.style.display = "none"; 
        }
    }
    
     /* ---------------- Civil Id ---------------- */
    var civilPanel = document.getElementById("panel_CIVIL");
    GENERAL_PANEL_VISIBILITY["panel_CIVIL"] = false;
    if (window.CIVIL_CHART_DATA && window.CIVIL_CHART_DATA.length > 0) 
    {
        if (renderChartsForDataset(window.CIVIL_CHART_DATA)) 
        {
            hasGeneralData = true;
            GENERAL_PANEL_VISIBILITY["panel_CIVIL"] = true;
        }
    } 
    else 
    {
        
        if (civilPanel) 
        {
            civilPanel.style.display = "none";  
        }
    }
    
    

    /* ---------------- Hide entire General section if empty ---------------- */
    var generalSection = document.getElementById("generalDataSection");
    if (generalSection && !hasGeneralData) 
    {
        generalSection.style.display = "none";
    }
}

/* =======================================================
   SAFE LOAD ATTACH (IE11 + legacy friendly)
   ======================================================= */
if (window.addEventListener) {
    window.addEventListener("load", onAllChartsLoad, false);
} else if (window.attachEvent) {
    window.attachEvent("onload", onAllChartsLoad); // old IE
} else {
    window.onload = onAllChartsLoad;
}
