/* ----------- rela_custom.js (IE11 compatible) ----------- */
function showContent(info) {
  var main = document.getElementById("mainContent");
  if (main) {
    var html =
      "<h3>" + info + "</h3>" +
      "<p>Displaying details for <strong>" + info + "</strong>.</p>";
    main.innerHTML = html;
  }
}

// Search filter (use plain loop instead of forEach)
(function () {
  var input = document.getElementById("searchInput");
  if (!input) return;
  input.onkeyup = function () {
    var filter = this.value.toLowerCase();
    var panels = document.getElementsByClassName("panel");
    for (var i = 0; i < panels.length; i++) {
      var text = (panels[i].innerText || "").toLowerCase();
      panels[i].style.display = text.indexOf(filter) !== -1 ? "" : "none";
    }
  };
})();

// Live clock / date
function updateClock() {
  var now = new Date();
  var clock = document.getElementById("kngClock");
  var date = document.getElementById("kngDate");
  if (clock) clock.innerText = now.toLocaleTimeString();
  if (date) date.innerText = now.toLocaleDateString();
}
setInterval(updateClock, 1000);


// date picker js code is here
$(document).ready(function() {
    $('.datepicker-ie').datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayHighlight: true
    });
});
