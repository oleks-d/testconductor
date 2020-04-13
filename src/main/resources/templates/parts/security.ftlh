<#assign
known = Session.SPRING_SECURITY_CONTEXT??
>
<#if known>
<#assign
 user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
 name = user.getUsername()
 isTeacher = user.getAuthorities()?seq_contains('1')
 isAdmin = user.getAuthorities()?seq_contains('2')
 isStudent = user.getAuthorities()?seq_contains('3')

>
<#else>
<#assign
 name = "GUEST"
 isTeacher = false
 isAdmin = false

 >
</#if>