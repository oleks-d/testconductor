<#import "parts/page.ftlh" as p>

<@p.page>

       <div class="row">
        <h2>View Students</h2>
    </div>
           <div class="row">

           <form action="/users?show" method = "post">
           <input type="hidden" name="_csrf" value="${_csrf.token}">

                <div class="form-group">
                <label> Theme </label>
               <select id="groupID" name="groupID">
                     <#list groups as group>
                         <option value="${group.id?c}" <#if selectedGroup?has_content><#if group.groupName == selectedGroup> selected </#if></#if> >${group.groupName}</option>
                     </#list>
                 </select>
                 </div>
                 <div><input class="btn btn-primary" type="submit" value="Show Students"/></div>
            </form>
       </div>
       <div class="row">
<#if users?has_content>

        <table class="table">
            <tr>
                <th>Email</th>
                <th>Name</th>
                <th>Group</th>
                <th>Is Active</th>
            </tr>

<#if  p.s.isTeacher && !p.s.isAdmin>
            <#list users as user>
                <tr>
                    <td>${user.email}</td>
                    <td>${user.name}</td>
                    <td>${user.groupName}</td>
                    <td>${user.active?string("yes", "no")}</td>

                    <td>
                        <form action="/results" method = "get">
                        <input type="hidden" name="userEmail" value="${user.email}">
                        <button class="btn btn-primary" type='submit'>Show results</button>
                        </form>
                    </td>

                </tr>
            </#list>
</#if>

<#if  p.s.isAdmin>

            <#list users as user>
                <tr>
                     <form action="/users?edit" method = "post">
                    <td><input name = "userEmail" value = "${user.email}"></input></td>
                    <td><input name = "userName" value = "${user.name}"></input></td>
                    <td><input name = "userGroup" value = "${user.groupName}"></input></td>
                    <td>${user.active?string("Active", "Inactive")}</td>


                        <input type="hidden" name="userID" value="${user.id?c}">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                        <td><button class="btn btn-primary" type='submit'>Save</button></td>
                        </form>

                    <td>
                        <form action="/results" method = "get">
                        <input type="hidden" name="userEmail" value="${user.email}">
                        <button class="btn btn-primary" type='submit'>Show results</button>
                        </form>
                    </td>

                    <td>
                        <form action="/users?delete" method = "post">
                        <input type="hidden" name="userToDeleteID" value="${user.id?c}">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                        <button class="btn btn-danger" type='submit'>Delete</button>
                        </form>
                    </td>

                </tr>
            </#list>
</#if>
        </table>
</#if>
    </div>



</@p.page>