document.addEventListener('DOMContentLoaded', function () 
{

    /* -----------------------------------
       KWTI / NKWTI FILTER INIT
    ----------------------------------- */

    if (typeof kwtNkwtFilter === "function") {
        kwtNkwtFilter('kwtid11');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('sv');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate11');
        var edateEl = document.getElementById('edate11');
        var oidEl   = document.getElementById('oid11');
        var kwtEl   = document.getElementById('kwtid11');
        var error   = document.getElementById('sv-error-message');

        if (!sdateEl || !edateEl || !oidEl || !kwtEl) 
        {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;
        var oid   = oidEl.value;
        var kwtid = kwtEl.value;

        /* -----------------------------------
           DATE VALIDATION
        ----------------------------------- */

        if (typeof dateValidator === "function") 
        {
            if (!dateValidator(sdate, edate, error)) 
            {
                return;
            }
        }

        /* Safe console logging (optional) */
        if (window.console && console.log) 
        {
            console.log(sdate, edate, oid, kwtid);
        }

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString =
            '?brtSdate='      + encodeURIComponent(sdate) +
            '&brtEdate='      + encodeURIComponent(edate) +
            '&brtOid='        + encodeURIComponent(oid) +
            '&brtKwtNkwtid='  + encodeURIComponent(kwtid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
