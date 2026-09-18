document.addEventListener('DOMContentLoaded', function () {

    /* -----------------------------------
       DYNAMIC FILTERS INIT
    ----------------------------------- */

    if (typeof departmentDynaFilter === "function") {
        departmentDynaFilter('oid4', 'did4');
    }

    if (typeof doctorDynaFilter === "function") {
        doctorDynaFilter('did4', 'docid4');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('pvbddr');
    if (!form) {
        return;
    }

    form.addEventListener('submit', function (e) {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate4');
        var edateEl = document.getElementById('edate4');
        var oidEl   = document.getElementById('oid4');
        var didEl   = document.getElementById('did4');
        var docEl   = document.getElementById('docid4');
        var error   = document.getElementById('pvbddr-error-message');

        if (!sdateEl || !edateEl || !oidEl || !didEl || !docEl) {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;
        var oid   = oidEl.value;

        /* -----------------------------------
           ALL / SELECT HANDLING
        ----------------------------------- */

        var didInt = didEl.value;
        var did = (didInt === '-- Select Department / ALL --') ? 0 : didInt;

        var docidInt = docEl.value;
        var docid = (docidInt === '-- Select Doctor / ALL --') ? 0 : docidInt;

        /* Safe console logging */
        if (window.console && console.log) {
            console.log(sdate, edate, oid, did, docid);
        }

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
            '&brtOid='   + encodeURIComponent(oid) +
            '&brtDid='   + encodeURIComponent(did) +
            '&brtDocid=' + encodeURIComponent(docid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
