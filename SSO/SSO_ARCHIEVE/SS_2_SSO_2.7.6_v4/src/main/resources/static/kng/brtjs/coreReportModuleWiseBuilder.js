function coreReportModuleWiseBuilder(moduleId, EngTitleNameToAccess, ArbTitleNameToAccess, lang) 
{
    var container = document.getElementById(moduleId);

    if (!container) 
    {
        console.error('Container with ID "' + moduleId + '" not found.');
        return;
    }

    console.log("=========== Lab report builder here ===========");

    for (var s = 0; s < reportSections.length; s++) 
    {
        var section = reportSections[s];

        // Main title status + language title check
        if (section.mainTitleStatus !== 'Y') {
            continue;
        }

        if (section.mainTitle !== EngTitleNameToAccess &&
            section.mainArabicTitle !== ArbTitleNameToAccess) {
            continue;
        }

        var card = '';
        card += '<div class="row">';
        card += '  <div class="col-md-12">';
        card += '    <div class="table-responsive">';
        card += '      <table class="table table-bordered text-center">';

        // Filter active reports
        var filteredReports = [];
        for (var r = 0; r < section.reports.length; r++) 
        {
            if (section.reports[r].reportStatus === 'Y') {
                filteredReports.push(section.reports[r]);
            }
        }

        // 2 reports per row
        for (var i = 0; i < filteredReports.length; i += 2) 
        {
            card += '<tr>';

            for (var j = i; j < i + 2; j++) 
            {
                if (j < filteredReports.length) 
                {
                    var report = filteredReports[j];
                    var buttonTitle = (lang === 'en')
                        ? report.buttonTitle
                        : report.buttonArabicTitle;

                    card += '<td class="col-md-6 ' + report.buttonClass + '">';
                    card += '  <button type="button" ';
                    card += '          class="btn btn-default medas-linklist" ';
                    card += '          data-toggle="modal" ';
                    card += '          data-target="' + report.modalTarget + '">';
                    card +=        report.buttonTitleNo + '. ' + buttonTitle;
                    card += '  </button>';
                    card += '</td>';
                }
                else 
                {
                    // Empty cell if odd number of reports
                    card += '<td class="col-md-6"></td>';
                }
            }

            card += '</tr>';
        }

        card += '      </table>';
        card += '    </div>';
        card += '  </div>';
        card += '</div>';

        container.innerHTML += card;
    }
}
