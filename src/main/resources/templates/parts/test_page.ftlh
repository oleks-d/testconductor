<#macro page>
<!DOCTYPE html>
<html>
    <head>
        <title>Questions</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.1/css/bootstrap.min.css" integrity="sha256-Md8eaeo67OiouuXAi8t/Xpd8t2+IaJezATVTWbZqSOw=" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

    <#if timeout?has_content>
<script>
        var time = ${timeout};

setInterval(function() {
time = time - 1
hr = Math.floor(time/60)
min = time - hr * 60
document.getElementById("timer").innerHTML = hr +":"+ min
if (time == 0){
document.getElementById("testForm").submit();
}
}, 1000);

</script>

    </#if>

    </head>
    <body style="padding-top: 70px;">
    <div class="container">

    <#if timeout?has_content>

    <div class='row'><h2>Time left/Часу лишилось: <div id = 'timer'>${timeout} seconds</div></h2></div>
        </#if>

<#nested>
    </div>
    </body>
    </html>
</#macro>