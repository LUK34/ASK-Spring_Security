document.addEventListener('DOMContentLoaded', function () 
{

    /* -----------------------------------
       UTILITIES INIT
    ----------------------------------- */

    if (typeof departmentDynaFilter === "function") 
    {
        departmentDynaFilter('oid10', 'did10');
    }

    if (typeof doctorDynaFilter === "function") 
    {
        doctorDynaFilter('did10', 'docid10');
    }

    if (typeof quarterFilter === "function") 
    {
        quarterFilter('qid10');
    }

    if (typeof yearFilter === "function") 
    {
        yearFilter('yearid10');
    }

    /* -----------------------------------
       FORM HANDLING
    ----------------------------------- */

    var form = document.getElementById('pvcbpdw');
    if (!form) 
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {

        e.preventDefault();

        var oidEl   = document.getElementById('oid10');
        var didEl   = document.getElementById('did10');
        var docEl   = document.getElementById('docid10');
        var qidEl   = document.getElementById('qid10');
        var yearEl  = document.getElementById('yearid10');
        var error   = document.getElementById('pvcbpdw-error-message');

        if (!oidEl || !didEl || !docEl || !qidEl || !yearEl) 
        {
            return;
        }

        var oid = oidEl.value;

        /* -----------------------------------
           ALL / SELECT HANDLING
        ----------------------------------- */

        var didVal = didEl.value;
        var did = (didVal === '-- Select Department / ALL --') ? 0 : didVal;

        var docVal = docEl.value;
        var docid = (docVal === '-- Select Doctor / ALL --') ? 0 : docVal;

        var qid    = qidEl.value;
        var yearid = yearEl.value;

        /* -----------------------------------
           CLINIC VALIDATION
        ----------------------------------- */

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
            '?brtOid='     + encodeURIComponent(oid) +
            '&brtDid='     + encodeURIComponent(did) +
            '&brtDocid='   + encodeURIComponent(docid) +
            '&brtQrterid=' + encodeURIComponent(qid) +
            '&brtYearid='  + encodeURIComponent(yearid);

        /* -----------------------------------
           OPEN REPORT
        ----------------------------------- */

        window.open(form.getAttribute('action') + queryString, '_blank');
    });

});
