<#import "parts/page.ftlh" as p>

<@p.page>

    <div class="row">
    <h2> Create Theme (load questions) </h2>
    </div>

    <div class="row">
    <form action="?add_questions" method = "post">
    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <div class="form-group">
                    <label> Theme Title </label>
                    <input class="form-control" type="text" name="themeName"/>
                    </div>
                    <div class="form-group">
                    <label> Questions </label>
                    <textarea class="form-control" style="min-width: 100%" name="questionsText"/></textarea>
                    </div>

            <button class="btn btn-primary" type='submit'>Load</button>
    </form>

       </div>
           <hr>
       <div class="row">
        <h2>View Questions</h2>
    </div>
           <div class="row">

           <form action="/questions?show" method = "post">
           <input type="hidden" name="_csrf" value="${_csrf.token}">

                <div class="form-group">
                <label> Theme </label>
               <select id="themeName" name="themeName" onchange="this.form.submit()">
                     <#list themes as theme>
                         <option value="${theme.name}" <#if selectedTheme?has_content><#if theme.name == selectedTheme> selected </#if></#if> >${theme.name}</option>
                     </#list>
                 </select>
                 </div>
            </form>
       </div>
       <div class="row">
        <#if questions?has_content>

        <table class="table">
            <tr>
                <th>Question</th>
                <th>Answers</th>
                <th>Correct Answer</th>
                <th>Theme</th>
            </tr>

            <tr>
    <form action="?add_questions" method = "post">
    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <div class="form-group">
                    <input class="form-control" type="hidden" name="themeName" value = "${selectedTheme}""/>
                    </div>
                    <div class="form-group">
                    <label> New questions </label>
                    <textarea class="form-control" style="min-width: 100%" name="questionsText"/></textarea>
                    </div>

            <button class="btn btn-primary" type='submit'>Add questions</button>
    </form>
            </tr>

            <#list questions as question>
                <tr>


                    <form action="/questions?edit" method = "post">
                    <td><textarea name = "questionText">${question.text}</textarea></td>
                    <td><textarea name = "questionAnswers">${question.answers}</textarea></td>
                    <td><textarea name = "correctAnswer">${question.correctAnswer}</textarea></td>
                    <td>${question.theme}</td>

                        <input type="hidden" name="questionID" value="${question.id?c}">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                        <td><button class="btn btn-primary" type='submit'>Save</button></td>
                        </form>

                    <td>
                        <form action="/questions?delete" method = "post">
                        <input type="hidden" name="questionToDeleteID" value="${question.id?c}">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                        <button class="btn btn-danger" type='submit'>Delete</button>
                        </form>
                    </td>

                </tr>
            </#list>

        </table>
        </#if>
    </div>


</@p.page>