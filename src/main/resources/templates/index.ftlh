<#import "parts/page.ftlh" as p>

<@p.page>

<#if p.s.isAdmin || p.s.isTeacher>

        <div class="row">
        <h2> Додати заняття </h2>
        </div>
            <div class="row">
            <form action="/add_exam" method = "post">
            <input type="hidden" name="_csrf" value="${_csrf.token}">
                <div class="form-group">
                <label> Номер групи (або Відпрацювання/REWORK): </label>
               <select id="examName" name="examName">
                     <#list groups as group>
                         <option value="${group.id?c}">${group.groupName}</option>
                     </#list>
                      <option value="REWORK">REWORK</option>
                 </select>
                </div>

                <div class="form-group">
                <label> Тема заняття (або Екзамен/FinalExam): </label>
               <select id="theme" name="theme">
                     <#list themes as theme>
                         <option value="${theme.name}">${theme.name}</option>
                     </#list>
                     <option value="FINAL_ENG">FinalExam</option>
                     <option value="FINAL_UKR">Екзамен</option>
                 </select>
                 </div>

            <div class="form-group">
            <label> Час початку заняття </label>
                <div class="input-group date" id="datetimepicker2" data-target-input="nearest">
                    <input type="text" name="courseStartTime" class="form-control datetimepicker-input" data-target="#datetimepicker2" required/>
                    <div class="input-group-append" data-target="#datetimepicker2" data-toggle="datetimepicker">
                        <div class="input-group-text"><i class="fa fa-calendar"></i></div>
                    </div>
                </div>
            </div>
        <script type="text/javascript">
            $(function () {
                $('#datetimepicker2').datetimepicker({
                            format: 'YYYY-MM-DD HH:mm'
                });
            });
        </script>

                <div class="form-group">
                <label>Тривалість заняття (хвилин) </label>
                               <select id="courseDurationInMinutes" name="courseDurationInMinutes">
                                        <option value="30">30</option>
                                        <option value="60">60</option>
                                        <option value="90">90</option>
                                 </select>
                </div>
                                <div class="form-group">
                                <label> Час на завдання (хвилин) </label>
                                               <select id="timeForCompletionInMinutes" name="timeForCompletionInMinutes">
                                                        <option value="10">10</option>
                                                        <option value="15">15</option>
                                                        <option value="20">20</option>
                                                 </select>
                                </div>
                <input type="hidden" name="teacher" value="${p.s.name}"/>
            <button class="btn btn-primary" type='submit'>Додати</button>

            </form>
    </div>
</#if>
<hr>
<div class = "row">
<h2> Розклад / Schedule  </h2>
</div>
<#if p.s.isAdmin || p.s.isTeacher>
<div class = "row">
                <form action="/search" method = "get">
                <div class="form-group">
                <label>Фільтр по номеру групи:</label>
                <select id="examName" name="examName" onchange="this.form.submit()">
                    <option value="">-</option>
                     <#list groups as group>
                        <#if examName?has_content>
                            <#if examName == group.groupName>
                             <option selected value="${group.id?c}">${group.groupName}</option>
                             <#else>
                             <option value="${group.id?c}">${group.groupName}</option>
                             </#if>
                         <#else>
                            <option value="${group.id?c}">${group.groupName}</option>
                         </#if>

                     </#list>
                      <option value="REWORK">REWORK</option>
                 </select>

                <label> Лише мої групи</label><input type="checkbox" onchange="this.form.submit()" id="onlyMine" <#if showOnlyMine?has_content> checked value="true" <#else> value="false" </#if> onClick="teacher = document.getElementById('teacherName'); if (teacher.value=='' ) teacher.value ='${p.s.name}'; else teacher.value ='' "/>
                <input type="hidden" id="teacherName" name="teacherName" <#if showOnlyMine?has_content>value="${p.s.name}<#else>value=""</#if>"/>
                </div>
                </form>
</div>
</#if>


<script>

    function checkTime(element){
         var endTime = element.getAttribute('enddatetime').replace(" ", "T")
         console.log(endTime)
         var startTime = element.getAttribute('startdatetime').replace(" ", "T")
         console.log(startTime)

         var currentTime = new Date()

         var expectedTimeEnd = new Date(endTime)
         console.log(expectedTimeEnd)

         var expectedTimeStart = new Date(startTime)
         console.log(expectedTimeStart)

         console.log(currentTime  + " - - " + expectedTimeEnd)
         if (currentTime > expectedTimeEnd){
            element.style.display = "none";
            return
         } else {
            element.style.display = "block"
          }

         if (currentTime < expectedTimeStart){
            element.style.display = "none";
            return
         }  else {
             element.style.display = "block"
         }

    }

/* window.onload = function (){
        setInterval(function() {
        var elements = document.getElementsByClassName('registrationLink');
            for(var i = 0; i < elements.length ;i++ ){
                checkTime(elements[i])
            }
        }, 1000);
} */
</script>

<div class = "row">

        <table class="table">
            <tr>
                <th>Група/Group</th>
                <th>Тема/Theme</th>
                <th>Початок/Start</th>
                <th>Кінець/End</th>
                <th>Час/Timе </th>
                <th>Вчитель/Teacher</th>
                <#if  p.s.isTeacher || p.s.isAdmin >
                <th>Зареєструвалось / Здало</th>
                <#else>
                <th>Результат/Result</th>
                </#if>
            </tr>

            <#list exams as exam>

                <tr>
                     <td>${exam.examName}</td>
                    <td>${exam.theme}</td>
                    <td>${exam.startDateTime}</td>
                    <td>${exam.endDateTime}</td>
                    <td>${exam.timeForCompletionInMinutes}</td>
                    <td><#if exam.teacher??>${exam.teacher}</#if> </td>
<#if p.s.isStudent>
                    <#if exam.result == -3 >
                        <td>N/A</td>
                    <#else>
                    <#if exam.result != -2 && exam.result != -1>
                        <td>${exam.result}</td>
                    <#else>
                        <td>
                           <form action="/start_test" method = "post" enddatetime="${exam.endDateTime}" startdatetime="${exam.startDateTime}" class="registrationLink">
                           <input type="hidden" name="examID" value="${exam.id?c}">
                           <input type="hidden" name="userName" value="${p.s.name}">
                           <input type="hidden" name="_csrf" value="${_csrf.token}">
                           <button class="btn btn-primary" type='submit'>Почати тест/Start Test</button>
                           </form>
                        </td>
                    </#if>
                    </#if>
</#if>

<#if  p.s.isTeacher>
                    <td>
                    ${exam.numberOfSessions} / ${exam.numberOfFinishedSessions}
                    </td>

                    <td>
                        <form action="/results" method = "get">
                        <input type="hidden" name="examID" value="${exam.id?c}">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <#if exam.result == -1>
                        <button class="btn btn-primary" type='submit'>Результати</button>
                    </#if>
                   <#if exam.result == 0>
                         <button class="btn btn-success" type='submit'>В процесі</button>
                   </#if>
                    <#if exam.result == 1>
                        <div></div>
                    </#if>
                         </form>



                    </td>
</#if>
<#if p.s.isAdmin >
                    <td>
                        <form action="/delete_exam" method = "post">
                        <input type="hidden" name="examID" value="${exam.id?c}">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                        <button class="btn btn-danger" type="button" onclick = 'var result = confirm("Want to delete?"); if (result){ this.form.submit()};'  >Видалити</button>
                        </form>
                    </td>
</#if>

                </tr>
            </#list>
        </table>
</div>

</@p.page>