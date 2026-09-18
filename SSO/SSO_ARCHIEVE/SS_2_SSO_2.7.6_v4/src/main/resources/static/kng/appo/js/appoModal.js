$(document).on("click",".open-slot-modal",function()
{

	var slot = $(this).data("slot");
	
	/* Read context values from hidden inputs */
	
	var appoDate = $("#CMN_DTLS_AppoDate").val();
	var officeName = $("#CMN_DTLS_OfficeName").val();
	var departmentName = $("#CMN_DTLS_DepartmentName").val();
	var doctorName = $("#CMN_DTLS_DoctorName").val();
	
	var officeId = $("#CMN_DTLS_OfficeId").val();
	var departmentId = $("#CMN_DTLS_DepartmentId").val();
	var doctorId = $("#CMN_DTLS_DoctorId").val();
	
	/* Fill visible readonly inputs */
	
	$("#selectedTimeSlot").text(slot);
	
	$("#appo_display_AppoDate").val(appoDate);
	$("#appo_display_OfficeName").val(officeName);
	$("#appo_display_DepartmentName").val(departmentName);
	$("#appo_display_DoctorName").val(doctorName);
	
	/* Fill hidden submission fields */
	
	$("#appo_display_OfficeId").val(officeId);
	$("#appo_display_DepartmentId").val(departmentId);
	$("#appo_display_DoctorId").val(doctorId);
	$("#appo_display_AppoDate").val(appoDate);
	$("#appo_display_TimeSlot").val(slot);
	
	/* Open modal */
	
	$("#timeslotModal").modal("show");

});