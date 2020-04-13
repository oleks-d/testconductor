<#import "parts/test_page.ftlh" as p>

<@p.page>
<#if message?has_content>
<div class="alert alert-primary" role="alert">
    <b>Result: ${message}</b>
    </div>
</#if>

<hr>
<#if questions?has_content>
<h2> Answer questions </h2>

<form action="/test" id = "testForm" method="post">
            <table class="table">
                <tr>
                    <th>Questions</th>
                </tr>

                <#list questions as question>
                    <tr>
                        <td>${question.text}</td>
                        <td>
                        <div class="form-group">
                        <#list question.answers?split("\n") as x>
                            <input type='radio' name='${question.text}' value='${x}'>${x?trim}</input><br>
                        </#list>
                        </div>
                        </td>
                    </tr>
                </#list>
            </table>
            <input type='hidden' name='studentCode' value='${studentCode}'/>
            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <div><input class="btn btn-primary" type="submit" value="Send results"/></div>
</form>
</#if>
</@p.page>