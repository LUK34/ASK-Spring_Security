document.addEventListener('DOMContentLoaded', function () 
{

    /* -----------------------------------
       MONTH & YEAR FILTER INIT
    ----------------------------------- */

    if (typeof monthFilter === "function") 
    {
        monthFilter('monthid8');
    }

    if (typeof yearFilter === "function")
    {
        yearFilter('yearid8');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('pmvps');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault(); // always control navigation manually

        var oidEl   = document.getElementById('oid8');
        var monthEl = document.getElementById('monthid8');
        var yearEl  = document.getElementById('yearid8');

        if (!oidEl || !monthEl || !yearEl) 
        {
            return;
        }

        var oid     = oidEl.value;
        var monthid = monthEl.value;
        var yearid  = yearEl.value;

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString =
            '?brtOid='     + encodeURIComponent(oid) +
            '&brtMonthid=' + encodeURIComponent(monthid) +
            '&brtYearid='  + encodeURIComponent(yearid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
