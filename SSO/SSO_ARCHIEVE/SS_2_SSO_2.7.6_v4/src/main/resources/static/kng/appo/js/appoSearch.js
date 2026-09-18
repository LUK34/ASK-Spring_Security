$(function(){

    $("#appo_militarySearchInput").autocomplete({

        minLength: 3,
        delay: 300,

        source: function(request, response){

            $.ajax({
                url: "/medas_api/ptntsRecord/search",
                method: "GET",
                dataType: "json",

                data: {
                    kngId: request.term
                },

                success: function(data){

                    response($.map(data, function(item){

                        return {

                            label:
                             "Name: " +
                             item.patientNameEn +
                            " Military No: "+
                            item.militaryNo + 
                            " Civil No: " +
                            item.patientCivilId +    
                            " Medical Record: " +
                             item.medicalRecordNo,

                            value: item.militaryNo ? item.militaryNo : item.civilId,

                            medicalRecordNo: item.medicalRecordNo,
                            patientNameEn: item.patientNameEn,
                            patientNameAr: item.patientNameAr,
                            mobileNo: item.mobileNo,
                            militaryNo: item.militaryNo,
                            patientCivilId: item.patientCivilId,
                            employeeCivilId: item.employeeCivilId,
                            relationship: item.relationship,
                            gender: item.gender,
                            designation: item.designation,
                            patientType: item.patientType
                            
                        };

                    }));

                },

                error: function(xhr, status, error){
                    console.log("Search error:", error);
                }

            });

        },

        select: function(event, ui){

            $("#appo_militarySearchInput").val(ui.item.value);

			/* Populate Modal Fields -> START */
			$("#display_MedicalRecordNo").val(ui.item.medicalRecordNo);
			$("#display_PatientNameEn").val(ui.item.patientNameEn);
    		$("#display_PatientNameAr").val(ui.item.patientNameAr);
    		$("#display_mobileNo").val(ui.item.mobileNo);
    		$("#display_militaryNo").val(ui.item.militaryNo);
    		$("#display_patientCivilId").val(ui.item.patientCivilId);
    		$("#display_employeeCivilId").val(ui.item.employeeCivilId);
    		$("#display_relationship").val(ui.item.relationship);
    		$("#display_gender").val(ui.item.gender);
    		$("#display_designation").val(ui.item.designation);
    		$("#display_patientType").val(ui.item.patientType);
 		   /* Populate Modal Fields -> END */


			/* Console DEBUG -> START */
            console.log("Medical Record No: ", ui.item.medicalRecordNo);
            console.log("Patient Name En: ", ui.item.patientNameEn);
            console.log("Patient Name Ar: ", ui.item.patientNameAr);
            console.log("Mobile No: ", ui.item.mobileNo);
            console.log("Military No: ", ui.item.militaryNo);
            console.log("Patient Civil Id: ", ui.item.patientCivilId);
            console.log("Employee Civil Id: ", ui.item.employeeCivilId);
            console.log("Gender: ", ui.item.gender);
            console.log("Designation: ", ui.item.designation);
            console.log("Patient Type:", ui.item.patientType);
			/* Console DEBUG -> END */

            return false;
        }

    });

});