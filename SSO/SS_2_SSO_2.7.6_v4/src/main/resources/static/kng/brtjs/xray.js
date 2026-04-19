document.addEventListener('DOMContentLoaded', function () {

    /* -----------------------------------
       KWTI - NKWTI FILTER INIT
    ----------------------------------- */

    if (typeof kwtNkwtFilter === "function") {
        kwtNkwtFilter('kwtid17');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('xray');
    if (!form) {
        return;
    }

    form.addEventListener('submit', function (e) {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate17');
        var edateEl = document.getElementById('edate17');
        var oidEl   = document.getElementById('oid17');
        var kwtEl   = document.getElementById('kwtid17');
        var error   = document.getElementById('xray-error-message');

        if (!sdateEl || !edateEl || !oidEl || !kwtEl) {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;
        var oid   = oidEl.value;
        var kwtid = kwtEl.value;

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
            '?brtSdate='       + encodeURIComponent(sdate) +
            '&brtEdate='       + encodeURIComponent(edate) +
            '&brtOid='         + encodeURIComponent(oid) +
            '&brtKwtNkwtid='   + encodeURIComponent(kwtid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
