<#import "parts/page.ftlh" as p>

<@p.page>

<div class="row">
<h2>Add new Teacher</h2>
</div>
<div class="row">
<form action="/add_teacher" method="post">
                <div class="form-group">
                <label>Username</label>
                <input class="form-control" type="text" name="username" required/>
                </div>
                <div class="form-group">
                <label>Email</label>
                <input class="form-control" type="text" name="email" required/>
                </div>
                <div class="form-group">
                <label>Password</label>
                <input class="form-control" type="password" name="password" required/>
                </div>
                <div class="form-group">
                <label>Teacher Name</label>
                <input class="form-control" type="text" name="teacherName" required/>
                </div>

    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <div><input class="btn btn-primary" type="submit" value="Register"/></div>
</form>
</div>


<div class="row">
<h2>Add new Group</h2>
</div>
<div class="row">

<form action="/add_groups" method="post">
                <div class="form-group">
                <label>Group Name</label>
                <input class="form-control" type="text" name="groupNames"/>
                </div>
                <div class="form-group">
                <label>Language</label>
                <select class="form-control" type="text" name="lang"/>
                <option value = "UKR">UKR</option>
                <option value = "ENG">ENG</option>
                </select>
                </div>
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <div><input class="btn btn-primary" type="submit" value="Add"/></div>
</form>
</div>

</@p.page>