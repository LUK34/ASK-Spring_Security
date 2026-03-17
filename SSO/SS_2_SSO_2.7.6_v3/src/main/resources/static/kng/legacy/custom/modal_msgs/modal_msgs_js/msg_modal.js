$(document).ready(function () 
{

    if (window.MISSING_MESSAGE_MODAL_STATUS === 'Y') 
    {
        $('#missing_msg_modal').modal(
			{
            	backdrop: 'static',
            	keyboard: false
        	});
    }
    else 
    {
        // NEW: If missing is NOT required (N), show welcome by default
        $('#welcome_msg_modal').modal('show');
    }

    // When user clicks OK on ->  MISSING DATA MODAL -> START
    $('#missingModalOkBtn').on('click', function () 
    {

        // Hide missing modal
        $('#missing_msg_modal').modal('hide');

        // After it is fully hidden, open welcome modal
        $('#missing_msg_modal').on('hidden.bs.modal', function () 
        {

            $('#welcome_msg_modal').modal('show');

            // Remove this event so it doesn't stack multiple times
            $(this).off('hidden.bs.modal');
        });

    });
   // When user clicks OK on ->  MISSING DATA MODAL -> END

   // When user clicks OK on ->  WELCOME MODAL -> START
   $('#welcomeModalOkBtn').on('click', function () 
    {

        // Hide missing modal
        $('#welcome_msg_modal').modal('hide');

        // After it is fully hidden, open welcome modal
        $('#welcome_msg_modal').on('hidden.bs.modal', function () 
        {

            $('#summary_msg_modal').modal('show');

            // Remove this event so it doesn't stack multiple times
            $(this).off('hidden.bs.modal');
        });

    });
	// When user clicks OK on ->  WELCOME MODAL -> END

/*
   if (window.WELCOME_MESSAGE_MODAL_STATUS === 'Y') 
	   {
       	 $('#welcome_msg_modal').modal('show');
   	   }
   	   
   	     if (window.SUMMARY_REPORT_MESSAGE_MODAL_STATUS === 'Y') 
	   {
       	 $('#summary_msg_modal').modal('show');
   	   }
*/
	
   	   
   	   
});