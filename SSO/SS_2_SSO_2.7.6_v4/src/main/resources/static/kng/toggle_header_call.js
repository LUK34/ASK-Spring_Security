(function ()
{
	jQuery(document).ready(function () 
	{
		 // General section (fixed IDs)
        //For static related data
       // toggleHeaderFnc("General_Data_Header");
       // bindOfficeHeaderToggle_idonly();
        
       bindHeaderToggleByPanels("General_Data_Header",["panel_APPO","panel_SICK","panel_DISPENSE","panel_CIVIL"]);

		//For dynamic office id
        console.log("Initializing dynamic office headers");
        bindDynamicOfficeHeaders();
	});
	
})();