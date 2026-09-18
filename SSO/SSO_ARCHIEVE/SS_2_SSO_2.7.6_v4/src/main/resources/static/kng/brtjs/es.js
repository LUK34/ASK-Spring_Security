document.addEventListener('DOMContentLoaded', function ()
{
			
		 var form = document.getElementById('es'); // Get the form by its ID

    	form.addEventListener('submit', function (e) 
    	{
   			e.preventDefault(); 
        window.open(form.getAttribute('action'), '_blank');

    	});
});