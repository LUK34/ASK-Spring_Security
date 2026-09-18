document.addEventListener('DOMContentLoaded', function () 
{

    /* -----------------------------------
       KWTI / NKWTI FILTER INIT
    ----------------------------------- */

    if (typeof kwtNkwtFilter2 === "function") 
    {
        kwtNkwtFilter2('kwtid19');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('mbkp');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate19');
        var edateEl = document.getElementById('edate19');
        var oidEl   = document.getElementById('oid20');
        var pidEl   = document.getElementById('pid19');
        var kwtEl   = document.getElementById('kwtid19');
        var error   = document.getElementById('mbkp-error-message');

        if (!sdateEl || !edateEl || !oidEl || !kwtEl) {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;
        var oid   = oidEl.value;
        var pid   = pidEl ? pidEl.value : '';
        var kwtid = kwtEl.value;

        var queryString = '';

        /* Safe console logging */
        if (window.console && console.log) {
            console.log(sdate, edate, oid, kwtid);
        }

        /* -----------------------------------
           VALIDATIONS
        ----------------------------------- */

        if (typeof dateValidator === "function") 
        {
            if (!dateValidator(sdate, edate, error)) 
            {
                return;
            }
        }

        if (typeof militaryIdValidator === "function") 
        {
            if (!militaryIdValidator(pid, error)) 
            {
                return;
            }
        }

        /*
        if (typeof fillerIdValidator === "function") {
            if (!fillerIdValidator(fid, error)) {
                return;
            }
        }
        */

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        if (pid === '' || pid === null) 
        {
            queryString =
                '?brtSdate='     + encodeURIComponent(sdate) +
                '&brtEdate='     + encodeURIComponent(edate) +
                '&brtOid='       + encodeURIComponent(oid) +
                '&brtKwtNkwtid=' + encodeURIComponent(kwtid);
        } 
        else 
        {
            queryString =
                '?brtSdate='     + encodeURIComponent(sdate) +
                '&brtEdate='     + encodeURIComponent(edate) +
                '&brtOid='       + encodeURIComponent(oid) +
                '&brtKwtNkwtid=' + encodeURIComponent(kwtid) +
                '&brtPid='       + encodeURIComponent(pid);
        }

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });
});