document.addEventListener('DOMContentLoaded', function ()
{

    var form = document.getElementById('cpd');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate5');
        var edateEl = document.getElementById('edate5');
        var oidEl   = document.getElementById('oid5');
        var error   = document.getElementById('cpd-error-message');

        if (!sdateEl || !edateEl || !oidEl) {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;
        var oid   = oidEl.value;

        /* -----------------------------------
           DATE VALIDATION
        ----------------------------------- */

        if (typeof dateValidator === "function") {
            if (!dateValidator(sdate, edate, error)) {
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
