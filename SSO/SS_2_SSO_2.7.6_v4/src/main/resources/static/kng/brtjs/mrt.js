document.addEventListener('DOMContentLoaded', function ()
{

    var form = document.getElementById('mrt');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('mrt_sdate');
        var edateEl = document.getElementById('mrt_edate');
        var error   = document.getElementById('mrt-error-message');

        if (!sdateEl || !edateEl) 
        {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;

        /* -----------------------------------
           DATE VALIDATION
        ----------------------------------- */

        if (typeof dateValidator === "function") {
            if (!dateValidator(sdate, edate, error)) {
                return;
            }
        }

        /* Safe console logging (optional) */
        if (window.console && console.log) {
            console.log(sdate, edate);
        }

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString =
            '?brtSdate=' + encodeURIComponent(sdate) +
            '&brtEdate=' + encodeURIComponent(edate);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });
});