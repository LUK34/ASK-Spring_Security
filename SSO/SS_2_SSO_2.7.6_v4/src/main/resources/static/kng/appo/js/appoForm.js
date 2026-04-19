document.addEventListener('DOMContentLoaded', function () {

    /* -----------------------------------
       DYNAMIC FILTERS INIT
    ----------------------------------- */

    if (typeof departmentDynaFilter === "function") 
    {
        departmentDynaFilter('appo_form_oid', 'appo_form_did');
    }

    if (typeof doctorDynaFilter === "function") 
    {
        doctorDynaFilter('appo_form_did', 'appo_form_docid');
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
        var oidEl   = document.getElementById('appo_form_oid');
        var didEl   = document.getElementById('appo_form_did');
        var docEl   = document.getElementById('appo_form_docid');
        var error   = document.getElementById('appo-error-message');

        if (!sdateEl || !oidEl || !didEl || !docEl) 
        {
            return;
        }

        var sdate = sdateEl.value;
        var oid   = oidEl.value;

        /* -----------------------------------
           ALL / SELECT HANDLING
        ----------------------------------- */

        var didInt = didEl.value;
        var did = (didInt === '-- Select Department / ALL --') ? 0 : didInt;

        var docidInt = docEl.value;
        var docid = (docidInt === '-- Select Doctor / ALL --') ? 0 : docidInt;

        /* Safe console logging */
        if (window.console && console.log) 
        {
            console.log(sdate, oid, did, docid);
        }

        /* -----------------------------------
           QUERY STRING CONSTRUCTION
        ----------------------------------- */

        var queryString =
            '?appoStartDate=' + encodeURIComponent(sdate) +
            '&appoOfficeId='   + encodeURIComponent(oid) +
            '&appoDepartmentId='   + encodeURIComponent(did) +
            '&appoDoctorId=' + encodeURIComponent(docid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
