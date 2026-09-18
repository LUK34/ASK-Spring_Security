document.addEventListener("DOMContentLoaded", function () 
{
    var container = document.getElementById("dynamicReportContainer");

    if (!container || !reportSections) {
        return;
    }

    for (var s = 0; s < reportSections.length; s++) 
    {
        var section = reportSections[s];

        // Only Active Headings
        if (section.mainTitleStatus !== 'Y') {
            continue;
        }

        var card = '';
        card += '<div class="row">';
        card += '  <div class="col-md-12">';
        card += '    <div class="panel panel-success">';
        card += '      <div class="panel-heading medas-fheading33">';
        card += '        <h3 class="panel-title medas-linklist">';
        card +=              section.mainTitleNo + '. ' + section.mainTitle;
        card += '        </h3>';
        card += '      </div>';
        card += '      <div class="panel-body table-responsive">';
        //card += '        <table class="table report-table">';
		 card += '        <table class="table table-bordered report-table">';

        // Filter active reports
        var reports = [];
        for (var r = 0; r < section.reports.length; r++) {
            if (section.reports[r].reportStatus === 'Y') {
                reports.push(section.reports[r]);
            }
        }

        // 2 reports per row (Bootstrap 3 → col-md-6)
        for (var i = 0; i < reports.length; i += 2) 
        {
            card += '<tr>';

            for (var j = i; j < i + 2; j++) 
            {
                if (j < reports.length) 
                {
                    card += '<td class="col-md-6 ' + reports[j].buttonClass + '">';
                    card += '  <div class="report-btn-wrapper">';
                    card += '  <button type="button" ';
                    card += '          class="btn btn-default medas-linklist" ';
                    card += '          data-toggle="modal" ';
                    card += '          data-target="' + reports[j].modalTarget + '">';
                    card +=        reports[j].buttonTitleNo + '. ' + reports[j].buttonTitle;
                    card += '  </button>';
                    card += '  </div>';
                    card += '</td>';
                } 
                else 
                {
                    // Empty column if odd number of reports
                    card += '<td class="col-md-6"></td>';
                }
            }

            card += '</tr>';
        }

        card += '        </table>';
        card += '      </div>';
        card += '    </div>';
        card += '  </div>';
        card += '</div>';

        container.innerHTML += card;
    }
});
