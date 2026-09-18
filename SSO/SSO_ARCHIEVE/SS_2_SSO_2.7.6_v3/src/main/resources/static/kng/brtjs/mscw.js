document.addEventListener('DOMContentLoaded', function () 
{

    var form = document.getElementById('mscw');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var oidEl = document.getElementById('oid19');
        if (!oidEl) 
        {
            return;
        }

        var oid = oidEl.value;

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString = '?brtOid=' + encodeURIComponent(oid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });
});
