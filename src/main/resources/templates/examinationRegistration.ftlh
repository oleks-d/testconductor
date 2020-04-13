<#import "parts/page.ftlh" as p>

<@p.page>
<h2> Register for exam</h2>

You will get a code to start test in email <br>
<form action="/register_for_test" method="post">
    <div class="form-group">
    <label> Student Name </label><input class="form-control" name="studentName"/>
    </div>
    <div class="form-group">
    <label> Email </label><input class="form-control" name="studentEmail"/>
    </div>
    <div class="form-group">
    <label> Group name </label>
                   <select id="groupID" name="groupID">
                         <#list groups as group>
                             <option value="${group.id?c}">${group.groupName}</option>
                         </#list>
                     </select>
     </div>
     <div class="form-group">
         <#if selectedExam?has_content>
         <label> Exam: "${selectedExamLabel}" </label>
          <input hidden id="examID" name="examID" value="${selectedExam}"/>
          <#else>
                      <select id="examID" name="examID">
                           <#list exams as exam>
                               <option value="${exam.examID}">${exam.examName} ${exam.theme} ${exam.startDateTime}</option>
                           </#list>
                       </select>
          </#if>
     </div>

                    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                    <input class="btn btn-primary" type="submit" value="Register"/>
</form>
<hr>

</@p.page>