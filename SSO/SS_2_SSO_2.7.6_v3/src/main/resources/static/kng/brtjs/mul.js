document.addEventListener('DOMContentLoaded', function () {

    /* -----------------------------------
       UTILITIES INIT
    ----------------------------------- */

    if (typeof kwtNkwtFilter === "function") {
        kwtNkwtFilter('mulDemog');
    }

    if (typeof kngMohFilter === "function") {
        kngMohFilter('mulKngMoh');
    }

    if (typeof userTypeFilter === "function") {
        userTypeFilter('mulUserType');
    }

    if (typeof statusFilter === "function") {
        statusFilter('mulStatus');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('mul');
    if (!form) {
        return;
    }

    form.addEventListener('submit', function (e) {

        e.preventDefault();

        var mulDemogEl    = document.getElementById('mulDemog');
        var mulKngMohEl   = document.getElementById('mulKngMoh');
        var mulUserTypeEl = document.getElementById('mulUserType');
        var mulStatusEl   = document.getElementById('mulStatus');

        if (!mulDemogEl || !mulKngMohEl || !mulUserTypeEl || !mulStatusEl) {
            return;
        }

        var mulDemogid    = mulDemogEl.value;
        var mulKngMohid   = mulKngMohEl.value;
        var mulUserTypeid = mulUserTypeEl.value;
        var mulStatusid   = mulStatusEl.value;

        /* Safe console logging */
        if (window.console && console.log) {
            console.log(mulDemogid, mulKngMohid, mulUserTypeid, mulStatusid);
        }

        var error = document.getElementById('mul-error-message');

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString =
            '?brtKwtNkwtid=' + encodeURIComponent(mulDemogid) +
            '&brtKngMoh='    + encodeURIComponent(mulKngMohid) +
            '&brtUserType='  + encodeURIComponent(mulUserTypeid) +
            '&brtStatus='    + encodeURIComponent(mulStatusid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
