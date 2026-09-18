document.addEventListener('DOMContentLoaded', function () {

    var form = document.getElementById('edbd');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate3');
        var edateEl = document.getElementById('edate3');
        var oidEl   = document.getElementById('oid3');
        var error   = document.getElementById('edbd-error-message');

        if (!sdateEl || !edateEl || !oidEl) 
        {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;
        var oid   = oidEl.value;

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

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString =
            '?brtSdate=' + encodeURIComponent(sdate) +
            '&brtEdate=' + encodeURIComponent(edate) +
            '&brtOid='   + encodeURIComponent(oid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });
});
