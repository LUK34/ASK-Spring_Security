function dataTableCreator(tableId, pageLength, buttons) {
    try {
        if (!jQuery || !jQuery.fn.DataTable) {
            console.log("DataTables not loaded for table: " + tableId);
            return;
        }

        var selector = "#" + tableId;
        if (jQuery(selector).length === 0) {
            console.log("Table not found: " + tableId);
            return;
        }

        jQuery(selector).DataTable({
            pageLength: pageLength || 10,
            dom: 'Bfrtip',
            buttons: buttons || [],
            destroy: true, // allow reinit
            ordering: true,
            responsive: false
        });

        console.log("DataTable initialized for: " + tableId);
    } catch (e) {
        console.log("Error initializing DataTable for " + tableId + ": " + e);
    }
}