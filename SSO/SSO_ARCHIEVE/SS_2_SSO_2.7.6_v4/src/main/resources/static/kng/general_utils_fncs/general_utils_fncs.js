// ########################################################################################################################
								// DATE VALIDATOR FUNCTION 
		
function dateValidator(sdate, edate, errorElement) {

    var startDate;
    var endDate;
    var currentDate = new Date();

    /* SAFETY CHECK */
    if (!errorElement) {
        return false;
    }

    /* Start Date must not be empty */
    if (!sdate) {
        errorElement.innerHTML = 'From date must not be empty.';
        return false;
    }

    /* End Date must not be empty */
    if (!edate) {
        errorElement.innerHTML = 'To date must not be empty.';
        return false;
    }

    /* Parse yyyy-MM-dd (IE11 safe) */
    startDate = new Date(sdate);
    endDate   = new Date(edate);

    /* Invalid date check (extra safety) */
    if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
        errorElement.innerHTML = 'Invalid date format.';
        return false;
    }

    /* Start date must be less than end date */
    if (startDate.getTime() > endDate.getTime()) {
        errorElement.innerHTML = 'From date must be less than To date.';
        return false;
    }

    /* End date must not exceed current date */
    if (endDate.getTime() > currentDate.getTime()) {
        errorElement.innerHTML = 'To date should not exceed the current date.';
        return false;
    }

    /* Clear error */
    errorElement.innerHTML = '';
    return true;
}

// ########################################################################################################################
							//CLINIC VALIDATOR function

function clinicValidator(oid, errorElement) 
{
    if (oid == '0') 
    {
        errorElement.innerText = 'Please select Clinic';
        return false; // validation failed
    }

    // Clear error message
    errorElement.innerText = '';
    return true; // validation success
}

// ########################################################################################################################

								// DEPERTMENT DYNAMIC FILTER based on OFFICE_ID FUNCTION
								
function departmentDynaFilter(officeId, departmentDropdownId)
 {

	console.log("Hello from departmentDynaFilter function");
    var officeEl = document.getElementById(officeId);

    if (!officeEl) 
    {
		console.log("No office id detected.");
        return;
    }

    officeEl.attachEvent? officeEl.attachEvent('onchange', onOfficeChange): officeEl.addEventListener('change', onOfficeChange);

    function onOfficeChange() 
    {
        var clinicId = officeEl.value;
		console.log("OFFICE_ID:",clinicId);
        if (!clinicId) 
        {
			console.log("No office id detected.");
            return;
        }

        var xhr = new XMLHttpRequest();
        xhr.open('GET', './medas_api/departmentList/' + clinicId, true);

        xhr.onreadystatechange = function () 
        {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {

                    var data = JSON.parse(xhr.responseText);
                    var departmentDropdown = document.getElementById(departmentDropdownId);

                    // Clear existing options
                    departmentDropdown.options.length = 0;

                    // Default option
                    var defaultOption = document.createElement('option');
                    defaultOption.value = "0";
                    defaultOption.text = "-- Select Department / ALL --";
                    departmentDropdown.appendChild(defaultOption);

                    for (var i = 0; i < data.length; i++) {
                        var option = document.createElement('option');
                        option.value = data[i].departmentId;
                      	console.log("OFFICE -> DEPARTMENT_ID: "+option.value);
                        option.text = data[i].departmentNameEn;
                        console.log("OFFICE -> DEPARTMENT_NAME_EN: "+option.text);
                        departmentDropdown.appendChild(option);
                    }

                } else {
                    if (window.console) {
                        console.log('Error loading departments');
                    }
                }
            }
        };

        xhr.send(null);
    }
}

// ########################################################################################################################

						//PATIENT MILITARY ID/CIVIL ID/OP_NUMBER FUNCTION

function patientValidator(pid, errorElement)
{
    // IE11-safe trim
    pid = pid.replace(/^\s+|\s+$/g, '');

    if (!pid) 
    {
        errorElement.innerText = 'Civil Id/Military Id/MR.No should not be empty. Please enter Data.';
        return false;
    }

    // Whitespace check (IE11 safe)
    if (/\s/.test(pid)) 
    {
        errorElement.innerText = 'Civil Id/Military Id/MR.No should not contain whitespaces.';
        return false;
    }

    // Military ID length check
    if (pid.length > 6 && pid.length <= 8)
    {
        errorElement.innerText =
            'Military id length should be less than 7 characters. Please check the military ID entered.';
        return false;
    }

    // Civil ID < 12
    if (pid.length > 8 && pid.length < 12)
    {
        errorElement.innerText =
            'Civil id length is less than 12 characters. Please check the civil ID entered.';
        return false;
    }

    // Civil ID > 12
    if (pid.length > 12 && pid.length < 13)
    {
        errorElement.innerText =
            'Civil id length should not exceed 12 characters. Please check the civil ID entered.';
        return false;
    }

    // MR No = 13 but wrong prefix
    if (pid.length === 13 && pid.indexOf('KNG-') !== 0)
    {
        errorElement.innerText =
            'MR. No should start with the pattern `KNG-`. `KNG-` should be in Capital letters.';
        return false;
    }

    // MR No > 13
    if (pid.length > 13 && pid.indexOf('KNG-') !== 0)
    {
        errorElement.innerText =
            'MR. No length should not exceed 13 characters. Please check the MR. No entered.';
        return false;
    }

    // Clear error message
    errorElement.innerText = '';
    return true;
}

// ########################################################################################################################

									// MILITARY ID VALIDATOR  FUNCTION

function militaryIdValidator(pid, errorElement)
{
    // IE11-safe trim
    pid = pid.replace(/^\s+|\s+$/g, '');

    // Whitespace check
    if (/\s/.test(pid))
    {
        errorElement.innerText = 'Military Id should not contain whitespaces.';
        return false;
    }

    // Alphabet check
    if (/[A-Za-z]/.test(pid))
    {
        errorElement.innerText = 'Military Id should not contain any alphabets';
        return false;
    }

    // Length validation
    if (pid.length > 6 && pid.length <= 8)
    {
        errorElement.innerText =
            'Military id length should be less than 7 characters. Please check the military ID entered.';
        return false;
    }

    // Clear error message
    errorElement.innerText = '';
    return true;
}

// ########################################################################################################################
							// MEDICINE FILLER VALIDATOR  FUNCTION

function fillerIdValidator(fid, errorElement)
{
    // IE11-safe trim
    fid = fid.replace(/^\s+|\s+$/g, '');

    // Allow empty
    if (fid === '')
    {
        errorElement.innerText = '';
        return true;
    }

    // Length should not exceed 4
    if (fid.length > 4)
    {
        errorElement.innerText =
            'Medicines Dispensed field should either be `All` or empty.';
        return false;
    }

    // Allow only 'All'
    if (fid !== 'All')
    {
        errorElement.innerText =
            'Medicines Dispensed field should either be `All` or empty.';
        return false;
    }

    // Clear error message
    errorElement.innerText = '';
    return true;
}

// ########################################################################################################################
							//DOCTOR DYNAMIC FILTER

function doctorDynaFilter(departmentDropdownId, doctorDropdownId)
{
    var departmentDropdown = document.getElementById(departmentDropdownId);

    if (!departmentDropdown)
    {
        return;
    }

    // IE11-safe event binding
    if (departmentDropdown.addEventListener)
    {
        departmentDropdown.addEventListener('change', onDepartmentChange);
    }
    else if (departmentDropdown.attachEvent)
    {
        departmentDropdown.attachEvent('onchange', onDepartmentChange);
    }

    function onDepartmentChange()
    {
        var departmentId = departmentDropdown.value;
        console.log("DEPARTMENT_ID: ",departmentId);
        var xhr = new XMLHttpRequest();

        xhr.open('GET', './medas_api/doctorList/' + departmentId, true);

        xhr.onreadystatechange = function ()
        {
            if (xhr.readyState === 4)
            {
                if (xhr.status === 200)
                {
                    var data = JSON.parse(xhr.responseText);
                    var doctorDropdown = document.getElementById(doctorDropdownId);

                    // Clear dropdown
                    doctorDropdown.options.length = 0;

                    // Default option
                    var defaultOption = document.createElement('option');
                    defaultOption.value = '0';
                    defaultOption.innerText = '-- Select Doctor / ALL --';
                    doctorDropdown.appendChild(defaultOption);

                    // Populate doctors
                    for (var i = 0; i < data.length; i++)
                    {
                        var option = document.createElement('option');
                        option.value = data[i].doctorId;
                        console.log("DEPARTMENT-> DOCTOR-> DOCTOR_ID: ",option.value);
                       // option.innerText = data[i].doctorName;
                       option.innerText = data[i].doctorNameEn;
                       console.log("DEPARTMENT-> DOCTOR -> DOCTOR_NAME_EN: ",option.innerText);
                       doctorDropdown.appendChild(option);
                    }
                }
                else
                {
                    // Optional: silent fail (legacy UX friendly)
                    console.log('Doctor list fetch failed. Status:', xhr.status);
                }
            }
        };

        xhr.send(null);
    }
}

// ########################################################################################################################
									// MONTH FILTER FUNCTION
									 
function monthFilter(monthdropdownId)
{
    var monthNames = [
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    ];

    var monthDropdown = document.getElementById(monthdropdownId);

    if (!monthDropdown)
    {
        return;
    }

    for (var i = 0; i < monthNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;          // Month id starts from 1
        option.innerText = monthNames[i];
        monthDropdown.appendChild(option);
    }
}

// ########################################################################################################################
									// YEAR FILTER FUNCTION
					
function yearFilter(yeardropdownId)
{
    var yearDropdown = document.getElementById(yeardropdownId);

    if (!yearDropdown)
    {
        return;
    }

    var currentYear = new Date().getFullYear();
    var startYear = 2019;

    for (var year = startYear; year <= currentYear; year++)
    {
        var option = document.createElement("option");
        option.value = year;
        option.innerText = year;
        yearDropdown.appendChild(option);
    }
}

// ########################################################################################################################
								
								// QUARTER FILTER FUNCTION
								
function quarterFilter(quarterdropdownId)
{
    var quarterNames = [
        "First Quarter",
        "Second Quarter",
        "Third Quarter",
        "Fourth Quarter"
    ];

    var quarterDropdown = document.getElementById(quarterdropdownId);

    if (!quarterDropdown)
    {
        return;
    }

    for (var i = 0; i < quarterNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;          // Quarter id starts from 1
        option.innerText = quarterNames[i];
        quarterDropdown.appendChild(option);
    }
}


// ########################################################################################################################

						// KUWAITI NON KUWAITI FILTER FUNCTION

function kwtNkwtFilter(kwtNkwtdropdownId)
{
    var kwtNkwtNames = [
        "All",
        "Kuwaiti",
        "Non-Kuwaiti"
    ];

    var kwtNkwtDropdown = document.getElementById(kwtNkwtdropdownId);

    if (!kwtNkwtDropdown)
    {
        return;
    }

    for (var i = 0; i < kwtNkwtNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;          // All=1, Kuwaiti=2, Non-Kuwaiti=3
        option.innerText = kwtNkwtNames[i];
        kwtNkwtDropdown.appendChild(option);
    }
}

// ########################################################################################################################

					// KUWAITI- NON KUWAITI FILTER version 2 OPTIONS START

function kwtNkwtFilter2(kwtNkwtdropdownId2)
{
    var kwtNkwtNames2 = [
        "Kuwaiti",
        "Non-Kuwaiti"
    ];

    var kwtNkwtDropdown2 = document.getElementById(kwtNkwtdropdownId2);

    if (!kwtNkwtDropdown2)
    {
        return;
    }

    for (var i = 0; i < kwtNkwtNames2.length; i++)
    {
        var option2 = document.createElement("option");
        option2.value = i + 1;        // Kuwaiti=1, Non-Kuwaiti=2
        option2.innerText = kwtNkwtNames2[i];
        kwtNkwtDropdown2.appendChild(option2);
    }
}

// ########################################################################################################################

						// DYNAMIC MEDICINE FILTER OPTIONS  based on OFFICE ID START
function medicineDynaFilter(officeId, medicineDropdownId)
{
    var officeDropdown = document.getElementById(officeId);

    if (!officeDropdown)
    {
        return;
    }

    // IE11-safe event binding
    if (officeDropdown.addEventListener)
    {
        officeDropdown.addEventListener('change', onOfficeChange);
    }
    else if (officeDropdown.attachEvent)
    {
        officeDropdown.attachEvent('onchange', onOfficeChange);
    }

    function onOfficeChange()
    {
        var clinicId = officeDropdown.value;

        var xhr = new XMLHttpRequest();
        xhr.open('GET', './medas_api/medicineList/' + clinicId, true);

        xhr.onreadystatechange = function ()
        {
            if (xhr.readyState === 4)
            {
                if (xhr.status === 200)
                {
                    var data = JSON.parse(xhr.responseText);

                    var medicineDropdown = $('#' + medicineDropdownId);

                    // Destroy select2 if already initialized
                    if (medicineDropdown.hasClass('select2-hidden-accessible'))
                    {
                        medicineDropdown.select2('destroy');
                    }

                    // Clear existing options
                    medicineDropdown.empty();

                    // Default empty option
                    medicineDropdown.append('<option value="0"></option>');

                    // Populate medicines
                    for (var i = 0; i < data.length; i++)
                    {
                        medicineDropdown.append(
                            $('<option></option>')
                                .val(data[i].itemId)
                                .text(data[i].itemName)
                        );
                    }

                    // Re-initialize select2 (IE11 safe)
                    medicineDropdown.select2({
                        placeholder: "Search and Select a medicine",
                        allowClear: true,
                        multiple: true,
                        tags: true
                    });
                }
                else
                {
                    console.log('Medicine list fetch failed. Status:', xhr.status);
                }
            }
        };

        xhr.send(null);
    }
}

// ########################################################################################################################
							
							// DYNAMIC MEDICINE FILTER OPTIONS  based on OFFICE ID START

function medicineDynaSingleFilter(officeId, medicineDropdownId)
{
    var officeDropdown = document.getElementById(officeId);

    if (!officeDropdown)
    {
        return;
    }

    // IE11-safe event binding
    if (officeDropdown.addEventListener)
    {
        officeDropdown.addEventListener('change', onOfficeChange);
    }
    else if (officeDropdown.attachEvent)
    {
        officeDropdown.attachEvent('onchange', onOfficeChange);
    }

    function onOfficeChange()
    {
        var clinicId = officeDropdown.value;

        var xhr = new XMLHttpRequest();
        xhr.open('GET', './medas_api/medicineList/' + clinicId, true);

        xhr.onreadystatechange = function ()
        {
            if (xhr.readyState === 4)
            {
                if (xhr.status === 200)
                {
                    var data = JSON.parse(xhr.responseText);

                    var medicineDropdown = $('#' + medicineDropdownId);

                    // Destroy select2 if already initialized
                    if (medicineDropdown.hasClass('select2-hidden-accessible'))
                    {
                        medicineDropdown.select2('destroy');
                    }

                    // Clear existing options
                    medicineDropdown.empty();

                    // Default empty option
                    medicineDropdown.append('<option value="0"></option>');

                    // Populate medicines
                    for (var i = 0; i < data.length; i++)
                    {
                        medicineDropdown.append(
                            $('<option></option>')
                                .val(data[i].itemId)
                                .text(data[i].itemName)
                        );
                    }

                    // Re-initialize select2 (single select)
                    medicineDropdown.select2({
                        placeholder: "Search and Select a medicine",
                        allowClear: true,
                        multiple: false,
                        tags: true
                    });
                }
                else
                {
                    console.log('Medicine list fetch failed. Status:', xhr.status);
                }
            }
        };

        xhr.send(null);
    }
}

// ########################################################################################################################

							// OFFICE FILTER (WITHOUT ALL) 

function officeFilterWithoutAllCondition(officedropdownId)
{
    var officeNames = [
        "HeadQuarters-MultiSpeciality",
        "Sheikh Salem Al Ali Camp",
        "Summod Camp-MultiSpeciality",
        "Tahreer Camp-Dental",
        "Tahreer Camp-MultiSpeciality"
    ];

    var officeDropdown = document.getElementById(officedropdownId);

    if (!officeDropdown)
    {
        return;
    }

    for (var i = 0; i < officeNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;          // ID starts from 1
        option.innerText = officeNames[i];
        officeDropdown.appendChild(option);
    }
}

// ########################################################################################################################

							// REPORT TYPE FILTER V1
							
function reportTypeFilterV1(reportTypedropdownId)
{
    var ReportTypeNames = [
        "Summary",
        "Detailed"
    ];

    var ReportTypeDropdown = document.getElementById(reportTypedropdownId);

    if (!ReportTypeDropdown)
    {
        return;
    }

    for (var i = 0; i < ReportTypeNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;        // Summary=1, Detailed=2
        option.innerText = ReportTypeNames[i];
        ReportTypeDropdown.appendChild(option);
    }
}

// ########################################################################################################################

							// PHA TRANSACTION  FUNCTION
							
function phaTransacTypeFilter(phaTransacTypedropdownId)
{
    var phaTransacTypeNames = [
        "Material Transfer",
        "Purchase",
        "Sale"
    ];

    var phaTransacTypeDropdown = document.getElementById(phaTransacTypedropdownId);

    if (!phaTransacTypeDropdown)
    {
        return;
    }

    for (var i = 0; i < phaTransacTypeNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;          // Material Transfer=1, Purchase=2, Sale=3
        option.innerText = phaTransacTypeNames[i];
        phaTransacTypeDropdown.appendChild(option);
    }
}

// ########################################################################################################################

					// UserType Options FUNCTION
					
function userTypeFilter(UserTypedropdownId)
{
    var UserTypeNames = [
        "All",
        "Admin",
        "Doctor",
        "Nurse",
        "Other",
        "Radiographer Technologist",
        "Radiographer Technician"
    ];

    var UserTypeDropdown = document.getElementById(UserTypedropdownId);

    if (!UserTypeDropdown)
    {
        return;
    }

    for (var i = 0; i < UserTypeNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;   // All=1, Admin=2, Doctor=3, Nurse=4, Other=5, RT=6, RTec=7
        option.innerText = UserTypeNames[i];
        UserTypeDropdown.appendChild(option);
    }
}

// ########################################################################################################################
							// KNG / MOH FILTER FUNCTION
							
function kngMohFilter(kngMohdropdownId)
{
    var kngMohNames = [
        "All",
        "KNG",
        "MOH"
    ];

    var kngMohDropdown = document.getElementById(kngMohdropdownId);

    if (!kngMohDropdown)
    {
        return;
    }

    for (var i = 0; i < kngMohNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;        // All=1, KNG=2, MOH=3
        option.innerText = kngMohNames[i];
        kngMohDropdown.appendChild(option);
    }
}

// ########################################################################################################################

					// STATUS FILTER FUNCTION

function statusFilter(statusdropdownId)
{
    var statusNames = [
        "All",
        "Active",
        "Inactive"
    ];

    var statusDropdown = document.getElementById(statusdropdownId);

    if (!statusDropdown)
    {
        return;
    }

    for (var i = 0; i < statusNames.length; i++)
    {
        var option = document.createElement("option");
        option.value = i + 1;      // All=1, Active=2, Inactive=3
        option.innerText = statusNames[i];
        statusDropdown.appendChild(option);
    }
}



