document.addEventListener('DOMContentLoaded', function () 
{

    var form = document.getElementById('pvoscf');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate16');
        var edateEl = document.getElementById('edate16');
        var error   = document.getElementById('pvoscf-error-message');

        if (!sdateEl || !edateEl) 
        {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;

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
            '&brtEdate=' + encodeURIComponent(edate);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });
});