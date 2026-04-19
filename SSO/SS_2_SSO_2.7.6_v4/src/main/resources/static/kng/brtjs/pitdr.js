document.addEventListener('DOMContentLoaded', function () 
{

    /* -----------------------------------
       INIT SELECT2 (IE11 SAFE)
    ----------------------------------- */

    if (window.jQuery && jQuery.fn.select2) 
    {
        $('#mid14').select2(
		{
            placeholder: "Select a medicine",
            allowClear: true
        });
    }

    /* -----------------------------------
       MEDICINE DYNAMIC FILTER
    ----------------------------------- */

    if (typeof medicineDynaFilter === "function") 
    {
        medicineDynaFilter('oid14', 'mid14');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('pitdr');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var sdateEl = document.getElementById('sdate14');
        var edateEl = document.getElementById('edate14');
        var oidEl   = document.getElementById('oid14');
        var error   = document.getElementById('pitdr-error-message');

        if (!sdateEl || !edateEl || !oidEl) 
        {
            return;
        }

        var sdate = sdateEl.value;
        var edate = edateEl.value;
        var oid   = oidEl.value;

        /* -----------------------------------
           SELECT2 VALUES (IE11 SAFE)
        ----------------------------------- */

        var medicineIds = [];

        if (window.jQuery && $('#mid14').length) 
        {
            var selectedMedicines = $('#mid14').select2('data');

            for (var i = 0; i < selectedMedicines.length; i++) 
            {
                medicineIds.push(selectedMedicines[i].id);
            }
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
            '?brtSdate='      + encodeURIComponent(sdate) +
            '&brtEdate='      + encodeURIComponent(edate) +
            '&brtOid='        + encodeURIComponent(oid) +
            '&brtMedicineid=' + encodeURIComponent(medicineIds.join(','));

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */
        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
