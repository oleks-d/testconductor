<#import "parts/page.ftlh" as p>

<@p.page>
<h2> Error </h2>
<#if errorStatus?hasContent>
Status: ${errorStatus}
</#if>
<br>
<#if errorMessage?hasContent>
Error Message: <br> ${errorMessage}
</#if>
<br>

</@p.page>