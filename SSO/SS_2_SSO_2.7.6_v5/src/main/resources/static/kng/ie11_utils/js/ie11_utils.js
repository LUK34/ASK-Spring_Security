// ###########################################################################################################################################
													/* IE11 -> STATISTICS -> GLOBAL VARIABLES */
// IE11 -> DATE RANGE -> GLOBAL CONFIGURATION 
var IE11_dash_dateRangeConfig =
[
    {
        value  : "ALL",
        textEn : "All Dates",
        textAr : "جميع التواريخ",
        mode   : "NONE"
    },

    {
        value  : "TODAY",
        textEn : "Today",
        textAr : "اليوم",
        mode   : "TODAY"
    },

    {
        value  : "LAST7",
        textEn : "Last 7 Days",
        textAr : "آخر 7 أيام",
        mode   : "ROLLING_DAYS",
        days   : 7
    },

    {
        value  : "LAST30",
        textEn : "Last 30 Days",
        textAr : "آخر 30 يوم",
        mode   : "ROLLING_DAYS",
        days   : 30
    },

    {
        value  : "LAST_MONTH",
        textEn : "Last Month",
        textAr : "الشهر الماضي",
        mode   : "LAST_MONTH"
    },

    {
        value  : "LAST_YEAR",
        textEn : "Last Year",
        textAr : "السنة الماضية",
        mode   : "LAST_YEAR"
    },

    {
        value  : "CUSTOM",
        textEn : "Custom Range",
        textAr : "نطاق مخصص",
        mode   : "CUSTOM"
    }
];

// IE11 -> AGE RANGE -> GLOBAL CONFIGURATION

var IE11_dash_ageRangeConfig =
[
    {
        value : "",
        text  : "All"
    },
    {
        value : "18-25",
        text  : "18 - 25"
    },
    {
        value : "26-35",
        text  : "26 - 35"
    },
    {
        value : "36-40",
        text  : "36 - 40"
    },
    {
        value : "41-45",
        text  : "41 - 45"
    },
    {
        value : "46-50",
        text  : "46 - 50"
    },
    {
        value : "50+",
        text  : "50+"
    }
];


// ###########################################################################################################################################

// ###########################################################################################################################################

											/* IE11 -> STATISTICS ->  Helper Methods */
											
											
											
							//BDS -> STATISTICS -> AGE CALCULATION
function IE11_DashGetAgeRange()
{
    var age = $("#IE11_dash_age").val();

    if(age === "")
    {
        return {
            ageFrom : null,
            ageTo   : null
        };
    }

    if(age === "50+")
    {
        return {
            ageFrom : 50,
            ageTo   : 200
        };
    }

    var split = age.split("-");

    return {
        ageFrom : parseInt(split[0],10),
        ageTo : parseInt(split[1],10)
    };
}



								// STATISTICS -> HELPER METHODS-> VALIDATE DATE RANGE

// Validate Date Range
function IE11_DashValidateDateRange()
{
    var dateRange = $("#IE11_dash_date_range").val();

    if(dateRange === "CUSTOM")
    {
        var fromDate = $.trim($("#IE11_dash_start_date").val());
        var toDate   = $.trim($("#IE11_dash_end_date").val());

        if(fromDate === "")
        {
            alert("Please select Start Date.");
            return false;
        }

        if(toDate === "")
        {
            alert("Please select End Date.");
            return false;
        }

        if(fromDate > toDate)
        {
            alert("Start Date cannot be greater than End Date.");
            return false;
        }
    }

    return true;
}

								// STATISTICS -> DATE RANGE function (Mainly for statistics)

// DATE RANGE -> HELPER FUNCTION -> FORMAT DATE
function IE11_DashFormatDate(date)
{
    var year  = date.getFullYear();
    var month = ("0" + (date.getMonth() + 1)).slice(-2);
    var day   = ("0" + date.getDate()).slice(-2);

    return year + "-" + month + "-" + day;
}

// DATE RANGE -> HELPER FUNCTION -> LAST DAY OF MONTH
function IE11_DashGetLastDayOfMonth(year, month)
{
    return new Date(year, month + 1, 0).getDate();
}

// DATE RANGE -> LOOKUP FUNCTION
function IE11_DashGetDateRange(value)
{
    var result = null;

    $.each(IE11_dash_dateRangeConfig,function(index,item)
    {
        if(item.value === value)
        {
            result = item;
            return false;
        }
    });

    return result;
}

// DATE RANGE -> UPDATE the DATE RANGE FUNCTION
function IE11_DashUpdateDateRange()
{
    var $fromDate = $("#IE11_dash_start_date");
    var $toDate   = $("#IE11_dash_end_date");

    var config = IE11_DashGetDateRange($("#IE11_dash_date_range").val());

    if(config == null)
    {
        $fromDate.val("").prop("disabled", true);
        $toDate.val("").prop("disabled", true);
        return;
    }

    var today = new Date();

    var fromDate = "";
    var toDate   = "";

    $fromDate.prop("disabled", true);
    $toDate.prop("disabled", true);

    switch(config.mode)
    {
        case "NONE":
            fromDate = "";
            toDate   = "";
            break;

        case "TODAY":
            fromDate = IE11_DashFormatDate(today);
            toDate   = IE11_DashFormatDate(today);
            break;

        case "ROLLING_DAYS":
            var start = new Date(today);
            start.setDate(start.getDate() - (config.days - 1));

            fromDate = IE11_DashFormatDate(start);
            toDate   = IE11_DashFormatDate(today);
            break;

        case "LAST_MONTH":
            var year  = today.getFullYear();
            var month = today.getMonth() - 1;

            if(month < 0)
            {
                month = 11;
                year--;
            }

            fromDate = year + "-" +
                       ("0" + (month + 1)).slice(-2) +
                       "-01";

            toDate = year + "-" +
                     ("0" + (month + 1)).slice(-2) +
                     "-" +
                     ("0" + IE11_DashGetLastDayOfMonth(year, month)).slice(-2);

            break;

        case "LAST_YEAR":
            var lastYear = today.getFullYear() - 1;

            fromDate = lastYear + "-01-01";
            toDate   = lastYear + "-12-31";
            break;

        case "CUSTOM":
            $fromDate.prop("disabled", false).focus();
			$toDate.prop("disabled", false);
			
            $fromDate.val("");
			$toDate.val("");
            return;
    }

    $fromDate.val(fromDate);
    $toDate.val(toDate);
}

// ###########################################################################################################################################

// ###########################################################################################################################################

									/* IE11 -> STATISTICS CONTROLS */

// DATE RANGE -> LOADER FUNCTION
function IE11_DashboardDateRangeDropdown()
{
    var $dropdown = $("#IE11_dash_date_range");

    $dropdown.empty();

    $.each(IE11_dash_dateRangeConfig, function(index, item)
    {
        $dropdown.append(

            $("<option>",{

                value : item.value,

                text  : item.textAr + " | " + item.textEn

            })

        );
    });

    //$dropdown.prop("selectedIndex",0);
    $dropdown.val("ALL");

    IE11_DashUpdateDateRange();
}


// DATE RANGE -> EVENT BINDING FUNCTION
function IE11_DashboardDateRangeEvents()
{
    var $dropdown = $("#IE11_dash_date_range");

    $dropdown.off("change").on("change", function()
    {
        IE11_DashUpdateDateRange();
    });
}

// STATISTICS -> AGE RANGE function (Mainly for statistics)
function IE11_DashboardAgeDropdown()
{
    var $dropdown = $("#IE11_dash_age");

    $dropdown.empty();

    $.each(IE11_dash_ageRangeConfig, function(index, item)
    {
        $dropdown.append(
            $("<option>", {
                value : item.value,
                text  : item.text
            })
        );
    });

    $dropdown.prop("selectedIndex", 0);
}

// STATISTICS -> ASSESMENT YEAR function (Mainly for statistics)
function IE11_DashboardRankDropdown()
{
    $.ajax(
    {
        url : IE11_contextPath + "ie_vw_hr_rest/designation",
        type : "GET",
        dataType : "json",

        success : function(response)
        {
            var $dropdown = $("#IE11_dash_rank");

            $dropdown.empty();

            $dropdown.append(
                $("<option>", {
                    value : "",
                    text  : "جميع الرتب | All Ranks"
                })
            );

            $.each(response, function(index, designation)
            {
                $dropdown.append(
                    $("<option>", {
                        value : designation,
                        text  : designation
                    })
                );
            });

            $dropdown.prop("selectedIndex", 0);
        },

        error : function(xhr)
        {
            alert(xhr.responseText);
        }
    });
}


function IE11_DashboardAssessmentYearDropdown(startYear)
{
    var currentYear = new Date().getFullYear();

    var $dropdown = $("#IE11_dash_assessment_year");

    $dropdown.empty();

    $dropdown.append(
        $("<option>",{
            value : "",
            text  : "جميع السنوات | All Assessment Years"
        })
    );

    for(var year = startYear; year <= currentYear; year++)
    {
        var assessmentYear = year + "-" + (year + 1);

        $dropdown.append(
            $("<option>",{
                value : assessmentYear,
                text  : assessmentYear
            })
        );
    }

    $dropdown.val(currentYear + "-" + (currentYear + 1));
}

function IE11_DashGetPatientTypes()
{
    var patientTypes = [];

    $(".IE11_dash_patient_type:checked").each(function()
    {
        var value = $(this).val();

        if(value === "Officer")
        {
            patientTypes.push(3);
        }
        else if(value === "Soldier")
        {
            patientTypes.push(7);
            patientTypes.push(8);
        }
    });

    return patientTypes;
}


// ###########################################################################################################################################

// ###########################################################################################################################################
								/* IE11 -> COMMON UTILITY FUNCTIONS */

/*
SCENARIO where there is white space characters coming from HR system. we have to approach with fallbackValue.
*/
function IE11_getSafeValue(primaryValue, fallbackValue)
{
    return (primaryValue == null ||   $.trim(primaryValue) === "") ? fallbackValue : primaryValue;
}

// Fetch Current year from system
function IE11_getCurrentYear()
{
    return new Date().getFullYear();
}

// ###########################################################################################################################################
								/* IE11 -> YEAR /MONTH LOADERS */

// YEAR LOADER FUNCTION
function IE11_yearLoader(startYear)
{
    var currentYear = new Date().getFullYear();

    $("select.year-loader").each(function()
    {
        var $dropdown = $(this);

        $dropdown.empty();

       /* $dropdown.append(
            $("<option>", {
                value : "",
                text  : "-- Select Year --"
            })
        );*/

        for(var year = startYear; year <= currentYear; year++)
        {
            $dropdown.append(
                $("<option>", {
                    value : year,
                    text  : year
                })
            );
        }

        $dropdown.val(currentYear);
    });
}

// MONTH LOADER FUNCTION
function IE11_monthLoader()
{
    var months =
    [
        { value:1,  text:"January"   },
        { value:2,  text:"February"  },
        { value:3,  text:"March"     },
        { value:4,  text:"April"     },
        { value:5,  text:"May"       },
        { value:6,  text:"June"      },
        { value:7,  text:"July"      },
        { value:8,  text:"August"    },
        { value:9,  text:"September" },
        { value:10, text:"October"   },
        { value:11, text:"November"  },
        { value:12, text:"December"  }
    ];

    $("select.month-loader").each(function()
    {
        var $dropdown = $(this);

        $dropdown.empty();

        $dropdown.append(
            $("<option>", {
                value : "",
                text  : "-- Select Month --"
            })
        );

        $.each(months,function(i,month)
        {
            $dropdown.append(
                $("<option>",{
                    value : month.value,
                    text  : month.text
                })
            );
        });

        $dropdown.val(new Date().getMonth() + 1);
    });
}
// ###########################################################################################################################################




// ###########################################################################################################################################
								/* IE11 -> DATE HELPERS */
								
								
								
function IE11_loadDatePickers()
{
    $('.datepicker-ie').each(function()
    {
        $(this).datepicker({
            dateFormat: 'yy-mm-dd',
            changeMonth: true,
            changeYear: true
        });


       // $(this).datepicker('setDate', new Date());
        if(!$(this).hasClass("IE11_dash_datepicker"))
        {
            $(this).datepicker('setDate', new Date());
        }
       
    });
}

/*
 * Initializes one or more date picker fields without modifying
 * their existing values.
 *
 * Example:
 * loadEditDatePickers("#ed-ban-date", "#ed-expiry-date");
 */
function IE11_loadEditDatePickers()
{
    $.each(arguments, function(index, selector)
    {
        $(selector).datepicker(
        {
            dateFormat  : "yy-mm-dd",
            changeMonth : true,
            changeYear  : true
        });
    });
}								
								
								
// ###########################################################################################################################################


// ###########################################################################################################################################
								/* IE11 -> TIME HELPERS */

/* Function to LOAD the current time. */
function IE11_loadTimeDropdown()
{
    $('.IE11-univ-time').each(function()
    {
        var $time = $(this);

        $time.empty();

        for(var hour = 0; hour < 24; hour++)
        {
            for(var minute = 0; minute < 60; minute += 15)
            {
                var hh = ('0' + hour).slice(-2);
                var mm = ('0' + minute).slice(-2);

                var value = hh + ':' + mm;

                $time.append(
                    $('<option>', {
                        value: value,
                        text: value
                    })
                );
            }
        }
    });
}

/* Function to SET the current time. */
function IE11_setCurrentTime()
{
    var now = new Date();

    var hour = now.getHours();
    var minute = now.getMinutes();

    // Round to nearest 15-minute slot
    minute = Math.round(minute / 15) * 15;

    if(minute === 60)
    {
        minute = 0;
        hour++;

        if(hour === 24)
        {
            hour = 0;
        }
    }

    var hh = ('0' + hour).slice(-2);
    var mm = ('0' + minute).slice(-2);

    var currentTime = hh + ':' + mm;

    $('.IE11-univ-time').val(currentTime);
}

/* After form submission -> Get Todays date */
function IE11_getTodayDate()
{
    var today = new Date();

    var yyyy = today.getFullYear();
    var mm = ('0' + (today.getMonth() + 1)).slice(-2);
    var dd = ('0' + today.getDate()).slice(-2);

    return yyyy + "-" + mm + "-" + dd;
}
								

// ###########################################################################################################################################


// ###########################################################################################################################################
								/* IE11 -> UNIT SEARCH */
								
function IE11_loadUnitSearch(inputSelector,  hiddenSelector,resultSelector)
{
    $(document)
        .off("keyup", inputSelector)
        .on("keyup", inputSelector, function()
    {
        var keyword = $.trim($(this).val());

        if(keyword.length < 2)
        {
            $(resultSelector).hide();
            return;
        }

        $.ajax(
        {
            url : IE11_contextPath +  "ie_vw_hr_rest/unit/" +   encodeURIComponent(keyword),

            type : "GET",

            dataType : "json",

            success : function(response)
            {
				 console.log("UNIT SEARCH RESPONSE:", response);
                var html = "";

                if(response && response.length > 0)
                {
                    $.each(response,function(index,item)
                    {
                        html +=
						    "<div class='unit-item' " + "data-index='" + index + "' " +  "style='padding:7px 10px;cursor:pointer;border-bottom:1px solid #eee;'>" 
						    		+ "<strong>" + item.unitCode + "</strong> - " + item.unit +
						    "</div>";
                    });

                    $(resultSelector)
                        .html(html)
                        .show()
                        .data("unitData", response);
                }
                else
                {
                    $(resultSelector).hide();
                }
            },

            error : function()
            {
                console.log("Unable to search Unit.");
            }
        });

    });

    $(document)
        .off("click", resultSelector + " .unit-item")
        .on("click", resultSelector + " .unit-item", function()
    {
        var index = $(this).attr("data-index");
        var units = $(resultSelector).data("unitData");
        var unit = units[index];

		if(!unit)
		{
		    return;
		}

		$(inputSelector).val( unit.unit);
      // $(inputSelector).val(unit.unitCode + " | " + unit.unit);

        $(hiddenSelector).val(unit.unitCode);

        $(resultSelector).hide();
    });

    $(document)
    .off("input", inputSelector)
    .on("input", inputSelector, function()
	{
	    $(hiddenSelector).val("");
	    $(resultSelector).hide();
	});
	
	
}



// ###########################################################################################################################################








