<#import "security.ftlh" as s>

  <#if s.name != 'GUEST'>

<nav class="fixed-top navbar navbar-expand-lg navbar-light bg-light">
  <a class="navbar-brand" href="#">
    <img alt="" src="https://remote.vnmu.edu.ua/img/logo.png" width="30" height="30" alt="">
  </a>
  <a class="navbar-brand" href="#">Tests</a>

  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">
      <li class="nav-item">
        <a class="nav-link" href="/"> Додому/Home <span class="sr-only">(current)</span></a>
      </li>

<#if s.isTeacher >
      <li class="nav-item">
        <a class="nav-link" href="results">Результати/Results</a>
      </li>
</#if>
<#if s.isAdmin >
      <li class="nav-item">
        <a class="nav-link" href="questions">Questions</a>
      </li>
</#if>
<#if s.isAdmin >
      <li class="nav-item">
        <a class="nav-link" href="settings">Settings</a>
      </li>
</#if>
<#if s.isAdmin || s.isTeacher>
      <li class="nav-item">
        <a class="nav-link" href="users">Студенти/Students</a>
      </li>
</#if>

  </div>
  <div class='navbar-text'>${currentUserName}</div>
</nav>

</#if>