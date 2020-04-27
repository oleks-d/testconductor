<#import "parts/page.ftlh" as p>

<@p.page>
<h2> Результати </h2>
<div class='row'>
<div class='col'>
<form action="/results" method="get">
                <div class="form-group">
                    <label> Заняття: </label>
                      <select id="examID" name="examID" onchange="this.form.submit()">
                            <option value="">-</option>
                           <#list exams as exam>
                                <option value="${exam.id?c}" <#if selectedExamID?has_content><#if exam.id == selectedExamID> selected </#if></#if> >${exam.examName} ${exam.theme} ${exam.startDateTime}</option>
                           </#list>
                       </select>
                </div>
                </form>
</div>
<div class='col'>
                       <form action="/results" method="get"  >
                <div class="form-group">
                <label> Група: </label><br>
               <select id="groupID" name="groupID" onchange="this.form.submit()">
                     <option value="">-</option>
                     <#list groups as group>
                         <option value="${group.id?c}" <#if selectedGroup?has_content><#if group.groupName == selectedGroup> selected </#if></#if> >${group.groupName}</option>
                     </#list>
                 </select>
                 </div>
</form>
</div>
<div class='col'>
    <form action="/print_results" method="post">
    <#if results?has_content>
    <#list results as result>
     <input type="hidden" name="${result.id}" value="${result.name} -- ${result.groupName} -- ${result.exam.theme} -- ${result.time} -- ${result.result}/10" />
     </#list>
     <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <div><input class="btn btn-primary" type="submit" value="Друк"/></div>
    </#if>
</form>
</div>
</div>
<#if results?has_content>
            <table class="table">
                <tr>
                    <th>Ім'я</th>
                    <th>Група</th>
                    <th>Оцінка</th>
                    <th>Email</th>
                    <th>Почав</ht>
                    <th>Здав</th>
                    <th>Відповіді</th>
                </tr>

                <#list results as result>

<tr>
                        <td><a href='/results?userEmail=${result.email}'> ${result.name}</a></td>
                        <td>${result.groupName}</td>

                                            <#if result.resultString?contains("TIMEOUT") >
                                                <td class='text-danger'>
                                            <#else>
                                                <td>
                                            </#if>

                         ${result.result}/${result.exam.numberOfQuestions}</td>
                         <td>${result.email}</td>
                         <td>${result.time}</td>
                         <td><#if result.endTime?has_content>${result.endTime}</#if></td>
                        <td>${result.exam.examName} - ${result.exam.theme}<br><button onClick="hideShow('${result}')">Деталі</button><div style="display:none" id='${result}'>
                        <#list result.resultString?split("\n") as x>
                            ${x}<br>
                        </#list>
                        </div>
                        </td>

                    </tr>
                </#list>
            </table>
</#if>
</@p.page>