<#import "parts/page.ftlh" as p>

<@p.page>

<hr>
<h2> Start exam </h2>
Specify code that you got in email:<br>
<form action="/start_test" method="post">
<div class="form-group">
<label> Code from Email: </label>
<input type="text" name="studentCode" value="${code}" />
</div>
<input type="hidden" name="_csrf" value="${_csrf.token}"/>
<input class="btn btn-primary" type="submit" value="Start exam"/>
</form>
</@p.page>