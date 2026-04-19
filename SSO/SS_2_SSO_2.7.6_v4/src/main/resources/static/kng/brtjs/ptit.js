document.addEventListener('DOMContentLoaded', function () {

    /* -----------------------------------
       INIT SELECT2 (IE11 SAFE)
    ----------------------------------- */

    if (window.jQuery && jQuery.fn.select2) 
    {
        $('#mid_ptit').select2(
		{
            placeholder: "Select a medicine",
            allowClear: true
        });
    }

    /* -----------------------------------
       DYNAMIC FILTERS INIT
    ----------------------------------- */

    if (typeof medicineDynaSingleFilter === "function") 
    {
        medicineDynaSingleFilter('oid_ptit', 'mid_ptit');
    }

    if (typeof phaTransacTypeFilter === "function") 
    {
        phaTransacTypeFilter('phaTransacType_ptit');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('ptit');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate_ptit');
        var edateEl = document.getElementById('edate_ptit');
        var oidEl   = document.getElementById('oid_ptit');
        var midEl   = document.getElementById('mid_ptit');
        var typeEl  = document.getElementById('phaTransacType_ptit');
        var error   = document.getElementById('ptit-error-message');

        if (!sdateEl || !edateEl || !oidEl || !midEl || !typeEl) {
            return;
        }

        var sdate  = sdateEl.value;
        var edate  = edateEl.value;
        var oid    = oidEl.value;
        var mid    = midEl.value;
        var typeid = typeEl.value;

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

        if (typeof clinicValidator === "function") 
        {
            if (!clinicValidator(oid, error)) 
            {
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
            '&brtPharmItemId='  + encodeURIComponent(mid) +
            '&brtTranscTypeId=' + encodeURIComponent(typeid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });
});
