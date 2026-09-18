/* --- allow clicking the same panel heading to close it (Bootstrap 3, IE11 safe) --- */
if (window.jQuery) {
  jQuery(function ($) {
    $('#dataAccordion').on('click', '.panel-title a[data-toggle="collapse"]', function (e) {
      var target = $(this).attr('href');
      if (!target) return;

      var $panel = $(target);

      // If this panel is already open (.in), close it manually before Bootstrap handles the click
      if ($panel.hasClass('in')) {
        e.preventDefault();      // stop Bootstrap's default toggle
        e.stopPropagation();     // stop event bubbling (ensures no duplicate toggle)
        $panel.collapse('hide'); // manually close
        return false;            // explicit return for IE 11
      }
      // otherwise, allow Bootstrap to open normally
    });
  });
}
