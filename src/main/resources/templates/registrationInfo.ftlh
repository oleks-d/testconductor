<#import "parts/page.ftlh" as p>

<@p.page>

<#if lang == 'ENG'>
<div class='row'>
<h1> Check your email: ${email} </h1>
</div>
<div class='row'>
<h1> Check Inbox and Spam folders</h1>
</div>
<div class='row'>
<h1> Open link from Email </h1>
</div>
<div class='row'>
<h1 class="text-danger"> If link is in Spam - <b>Copy link</b> from email and <b>Paste in Browser</b></h1>
</div>
<#else>
<div class='row'>
<h1> Перевірте EMail: ${email} </h1>
</div>
<div class='row'>
<h1> Перевірте Вхідні та Спам листи </h1>
</div>
<div class='row'>
<h1> Відкрийте посилання з листа </h1>
</div>
<div class='row'>
<h1 class="text-danger"> Якщо лист прийшов у Спам <b>скопіюйте посилання<b> і <b>Вставте у стрічку броузера</b></h1>
</div>
</#if>

</@p.page>