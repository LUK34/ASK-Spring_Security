window.onload = function () 
{
        var tabs = document.getElementsByClassName("tab-item");

        for (var i = 0; i < tabs.length; i++) {
            tabs[i].onclick = function () {

                // Remove active class from all tabs
                for (var j = 0; j < tabs.length; j++) 
                {
                    tabs[j].classList.remove("tab-active");
                }

                // Add active class to clicked tab
                this.classList.add("tab-active");

                // OPTIONAL: Get selected value for backend
                console.log("Selected Tab: " + this.getAttribute("data-value"));
            };
        }
 };