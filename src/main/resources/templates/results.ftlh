<#import "parts/page.ftlh" as p>

<@p.page>
<h2> Результати </h2>
<div class='row'>
<div class='col'>
<form action="/results" method="get">
                <div class="form-group">
                    <label> Заняття: </label>
                      <select id="examTheme" name="examTheme" onchange="this.form.submit()">
                            <option value="">-</option>
                           <#list themes as theme>
                                <option value="${theme.name}" <#if selectedTheme?has_content><#if theme.name == selectedTheme> selected </#if></#if> >${theme.name}</option>
                           </#list>
                           <option value="FINAL_ENG" <#if selectedTheme?has_content><#if 'Exam' == selectedTheme> selected </#if></#if> >Exam</option>
                           <option value="FINAL_UKR" <#if selectedTheme?has_content><#if 'Залік' == selectedTheme> selected </#if></#if> >Залік</option>
                       </select>
                </div>
                <div class="form-group">
                <label> Група: </label><br>
               <select id="groupID" name="groupID" onchange="this.form.submit()">
                     <option value="">-</option>
                     <#list groups as group>
                         <option value="${group.id?c}" <#if selectedGroup?has_content><#if group.groupName == selectedGroup> selected </#if></#if> >${group.groupName}</option>
                     </#list>
                     <option value="0" <#if selectedGroup?has_content><#if selectedGroup == 'REWORK'> selected </#if></#if>>REWORK</option>
                 </select>
                 </div>
                </form>
</div>

<div class='col'>
    <form action="/print_results" method="post">
    <#if results?has_content>
    <#list results as result>
     <input type="hidden" name="${result.id}" value="${result.name} -- ${result.groupName} -- ${result.exam.theme} -- ${result.time} -- ${result.result}/${result.exam.numberOfQuestions}" />
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
                        <td>

                        <form action="/results" method='GET'>
                        <input type='hidden' name='examTheme' value='${result.exam.theme}'/>
                        <input type='hidden' name='examName' value='${result.exam.examName}'/>
                        <button class="btn btn-link" onclick="this.form.submit()">
                        ${result.exam.examName} -
                        <#if result.exam.theme=='FINAL_UKR'>
                        Залік
                        <#else>
                          <#if result.exam.theme=='FINAL_ENG'>
                          Exam
                          <#else>
                          ${result.exam.theme}
                          </#if>
                          </#if>
                         </button>
                        </form>
                        <br><button onClick="hideShow('${result}')">Деталі</button><div style="display:none" id='${result}'>
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