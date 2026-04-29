document.addEventListener('DOMContentLoaded', function () 
{

    /* -----------------------------------
       YEAR FILTER INIT
    ----------------------------------- */

    if (typeof yearFilter === "function") 
    {
        yearFilter('yearid9');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('procyvps');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault(); // always control submit manually

        var oidEl  = document.getElementById('oid9');
        var yearEl = document.getElementById('yearid9');
        var error  = document.getElementById('procyvps-error-message');

        if (!oidEl || !yearEl) 
        {
            return;
        }

        var oid    = oidEl.value;
        var yearid = yearEl.value;

        /* -----------------------------------
           CLINIC VALIDATION
        ----------------------------------- */

        if (typeof clinicValidator === "function") 
        {
            if (!clinicValidator(oid, error)) 
            {
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
