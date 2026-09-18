document.addEventListener('DOMContentLoaded', function () {

    /* -----------------------------------
       REPORT TYPE FILTER INIT
    ----------------------------------- */

    if (typeof reportTypeFilterV1 === "function") 
    {
        reportTypeFilterV1('reporttype_lpcpr');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('lpcpr');
    if (!form) {
        return;
    }

    form.addEventListener('submit', function (e) {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate_lpcpr');
        var edateEl = document.getElementById('edate_lpcpr');
        var oidEl   = document.getElementById('oid_lpcpr');
        var rptEl   = document.getElementById('reporttype_lpcpr');
        var error   = document.getElementById('lpcpr-error-message');

        if (!sdateEl || !edateEl || !oidEl || !rptEl) {
            return;
        }

        var sdate      = sdateEl.value;
        var edate      = edateEl.value;
        var oid        = oidEl.value;
        var rprttypeid = rptEl.value;

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
            '?brtSdate='        + encodeURIComponent(sdate) +
            '&brtEdate='        + encodeURIComponent(edate) +
            '&brtOid='          + encodeURIComponent(oid) +
            '&brtReportTypeId=' + encodeURIComponent(rprttypeid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
