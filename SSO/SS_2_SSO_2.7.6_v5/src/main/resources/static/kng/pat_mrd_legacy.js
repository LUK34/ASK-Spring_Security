// pat_mrd_legacy.js  --  IE11 compatible
(function () 
{

    // Wait for DOM ready
    jQuery(document).ready(function () 
    {
        console.log("Initializing DataTables (Legacy IE11 mode)");

		// --------------------------------------------------------------------------------------
							// Patient Medical Record -> DATA Tables
							
        dataTableCreator("r_appnmt_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_clinic_refer_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_lab_refer_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_xray_refer_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_proc_refer_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_medi_refer_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_nn_refer_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_sick_ref_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_exemp_ref_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_pain_rate_ref_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_conf_stmt_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_tplan_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_vs_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_pat_exam_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_dent_notes_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_int_ref_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_ext_ref_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_dent_trmt_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_dent_exam_list", 10, ["excel", "colvis"]);

        dataTableCreator("r_diag_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_exemp_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_eye_exam_list", 10, ["excel", "colvis"]);
        dataTableCreator("r_glass_pres_list", 10, ["excel", "colvis"]);
        // --------------------------------------------------------------------------------------
							// KNG_Medas Appointment Module -> DATA Tables
		
		dataTableCreator("appo_booked_data", 10, ["excel", "colvis"]);				
        
        // --------------------------------------------------------------------------------------
        
    });

})();