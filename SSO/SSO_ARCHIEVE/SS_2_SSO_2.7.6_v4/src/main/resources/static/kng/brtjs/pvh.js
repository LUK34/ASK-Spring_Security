document.addEventListener('DOMContentLoaded', function () 
{

    var form = document.getElementById('pvh');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {
        e.preventDefault();

        var pidEl  = document.getElementById('pid1');
        var error  = document.getElementById('pvh-error-message');

        if (!pidEl) 
        {
            return;
        }

        var pid = pidEl.value;

        /* -----------------------------------
           PATIENT ID VALIDATION
        ----------------------------------- */

        if (typeof patientValidator === "function") 
        {
            if (!patientValidator(pid, error)) 
            {
                return;
            }
        }

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString = '?brtPid=' + encodeURIComponent(pid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });
});
