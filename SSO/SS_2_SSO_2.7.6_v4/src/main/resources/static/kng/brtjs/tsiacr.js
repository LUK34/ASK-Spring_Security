document.addEventListener('DOMContentLoaded', function () 
{

    var form = document.getElementById('tsiacr');
    if (!form)
    {
        return;
    }

    form.addEventListener('submit', function (e) 
    {
        e.preventDefault();
        window.open(form.getAttribute('action'), '_blank');
    });
});
