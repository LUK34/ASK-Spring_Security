
document.addEventListener('DOMContentLoaded', function () 
{

    /* -----------------------------------
       YEAR FILTER INIT
    ----------------------------------- */

    if (typeof yearFilter === "function") 
    {
        yearFilter('yearid7');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('pyvps');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var oidEl   = document.getElementById('oid7');
        var yearEl  = document.getElementById('yearid7');
        var error   = document.getElementById('pyvps-error-message');

        if (!oidEl || !yearEl) {
            return;
        }

        var oid    = oidEl.value;
        var yearid = yearEl.value;

        /* -----------------------------------
           CLINIC VALIDATION
        ----------------------------------- */

        if (typeof clinicValidator === "function") {
            if (!clinicValidator(oid, error)) {
                return;
            }
        }

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString =
            '?brtOid='    + encodeURIComponent(oid) +
            '&brtYearid=' + encodeURIComponent(yearid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
