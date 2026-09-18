(function () {

    function initShiftTabs() {
		
		console.log("shiftTabs exists:", document.getElementById("shiftTabs"));
        var tabs = document.getElementsByClassName("tab-item");
        var hiddenInput = document.getElementById("genShiftTypeHidden");

        if (!tabs || tabs.length === 0 || !hiddenInput) {
            return;
        }

        if (!hiddenInput.value) {
            hiddenInput.value = "All";
        }

        for (var i = 0; i < tabs.length; i++) {

            tabs[i].onclick = function () {

                for (var j = 0; j < tabs.length; j++) {
                    tabs[j].className =
                        tabs[j].className.replace(" tab-active", "");
                }

                this.className += " tab-active";
                hiddenInput.value = this.getAttribute("data-value");
            };
        }
    }

    // IE11 SAFE DOM READY
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initShiftTabs);
    } else {
        initShiftTabs();
    }

})();