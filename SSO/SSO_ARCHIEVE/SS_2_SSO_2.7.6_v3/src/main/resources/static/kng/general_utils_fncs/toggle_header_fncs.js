// ----------------------------------------------------------------------------------------------------

								// USED
function bindHeaderToggleByPanels(headerId, panelIds) 
{

    var header = document.getElementById(headerId);
    if (!header || !panelIds || panelIds.length === 0) return;

    var isCollapsed = false;

    header.addEventListener("click", function () {

        isCollapsed = !isCollapsed;

        for (var i = 0; i < panelIds.length; i++) {
            var panelId = panelIds[i];
            var panel = document.getElementById(panelId);
            if (!panel) continue;

            if (isCollapsed) {
                // Always hide on collapse
                panel.style.display = "none";
            } else {
                // Restore ONLY panels that had data
                if (GENERAL_PANEL_VISIBILITY[panelId]) {
                    panel.style.display = "block";
                }
            }
        }
    });
}								
								
/*function bindHeaderToggleByPanels(headerId, panelIds)
 {

    if (!headerId || !panelIds || panelIds.length === 0) {
        return;
    }

    var header = document.getElementById(headerId);
    if (!header) return;

    var isCollapsed = false;

    header.addEventListener("click", function () {

        isCollapsed = !isCollapsed;

        for (var i = 0; i < panelIds.length; i++) {
            var panel = document.getElementById(panelIds[i]);
            if (!panel) continue;

            panel.style.display = isCollapsed ? "none" : "block";
        }
    });
}*/


// ----------------------------------------------------------------------------------------------------

						// USED
function bindDynamicOfficeHeaders()
{
    var headers = document.getElementsByClassName("office-header-ie");

    for (var i = 0; i < headers.length; i++)
    {
        var header = headers[i];

        // Skip General_Data_Header (handled separately)
        if (header.id === "General_Data_Header") {
            continue;
        }

        (function(hdr){
            hdr.addEventListener("click", function () {

                var next = hdr.nextSibling;
                while (next && next.nodeType !== 1) {
                    next = next.nextSibling;
                }

                if (!next) return;

                next.style.display =
                    (next.style.display === "none") ? "block" : "none";
            });
        })(header);
    }
}

// ----------------------------------------------------------------------------------------
					//NOT USED

function toggleHeaderFnc(headerId) 
{
	console.log("Hello from toggleHeaderFnc.")
    var header = document.getElementById(headerId);
    if (!header) return;

    header.addEventListener("click", function () 
    {
        var next = header.nextSibling;
        while (next && next.nodeType !== 1) {
            next = next.nextSibling;
        }

        if (!next) return;

        next.style.display =
            (next.style.display === "none") ? "block" : "none";
    });
}

// ----------------------------------------------------------------------------------------------------
						//NOT USED

function bindOfficeHeaderToggle()
{
    var headers = document.getElementsByClassName("office-header-ie");

    for (var i = 0; i < headers.length; i++)
    {
        // ✅ Skip the General header (static one)
        if (headers[i].id === "General_Data_Header") {
            continue;
        }

        headers[i].onclick = function ()
        {
            var officeId = this.getAttribute("data-office-id");

            // ✅ Only bind if it is truly a dynamic office header
            if (!officeId) {
                return;
            }

            var body = document.getElementById("office_body_" + officeId);

            if (!body) {
                return;
            }

            if (body.style.display === "none") {
                body.style.display = "block";
            } else {
                body.style.display = "none";
            }
        };
    }
}

// ----------------------------------------------------------------------------------------------------

					//NOT USED
					
					
function bindOfficeHeaderToggle_idonly()
{
    var header = document.getElementById("General_Data_Header");
    if (!header) return;

    // Find the sibling panel (APPO)
    var sibling = header.nextSibling;
    while (sibling && sibling.nodeType !== 1) 
    {
        sibling = sibling.nextSibling;
    }

    var panelIds = [
        "panel_APPO",
        "panel_SICK",
        "panel_DISPENSE"
    ];

    header.addEventListener("click", function () 
    {
        for (var i = 0; i < panelIds.length; i++) 
        {
            var panel = document.getElementById(panelIds[i]);
            if (!panel) continue;

            // 🚫 Skip sibling to avoid double toggle
            if (panel === sibling) {
                continue;
            }

            panel.style.display =
                (panel.style.display === "none") ? "block" : "none";
        }
    });
}


// ----------------------------------------------------------------------------------------------------