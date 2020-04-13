<#import "parts/page.ftlh" as p>

<@p.page>

<form action="/signin" method="post">
                <div class="form-group">
                <label>Email</label>
                <input class="form-control" type="text" name="username" required/>
                </div>
                <div class="form-group">
                <label>Пароль / Password</label>
                <input class="form-control" type="password" name="password" required/>
                </div>
                <div class="form-group.">
                <a href = "/registration"> Зареєструватися / Register </a>
                </div>
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <div><input class="btn btn-primary" type="submit" value="Увійти / Sign In"/></div>
</form>
</@p.page>