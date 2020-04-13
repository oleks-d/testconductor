<#import "parts/page.ftlh" as p>

<@p.page>

<div class="row">
<h2>Register</h2>
</div>
<div class="row">
<form method="post">

                <div class="form-group">
                <label>Email</label>
                <input class="form-control" type="text" id="email" name="email" required/>
                      <div class="invalid-feedback" for="email">
                        Введіть електронну пошту / Please specify Email
                      </div>
                </div>
                <div class="form-group">
                <label>Пароль / Password</label>
                <input class="form-control" type="password" id="password" name="password" required/>
                      <div class="invalid-feedback" for="password">
                        Введіть пароль / Please specify Password
                      </div>
                </div>
                <div class="form-group">
                <label>Повне Ім'я / Full Name</label>
                <input class="form-control" type="text" id="studentName" name="studentName" required/>
                      <div class="invalid-feedback" for="studentName">
                        Вкажіть ім'я / Please specify Student Name
                      </div>
                </div>
                <div class="form-group">
                <label> Номер групи / Group number </label>
               <select id="groupID" name="groupID" required>
                     <option value=""></option>
                     <#list groups as group>
                         <option value="${group.id?c}">${group.groupName}</option>
                     </#list>
                 </select>
                </div>
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <div><input class="btn btn-primary" type="submit" value="Зареєструватися / Register"/></div>
</form>
</div>


</@p.page>