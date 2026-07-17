<#--  VoltEdge — Base Template (replaces PatternFly wrapper)  -->
<#macro registrationLayout>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="robots" content="noindex, nofollow">

    <#if properties.meta?has_content>
        <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}">
        </#list>
    </#if>

    <title>${msg("loginTitle",(realm.displayName!''))}</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" type="image/x-icon">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet">
        </#list>
    </#if>

    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>

    <#if authenticationSession??>
        <script type="module">
            import { checkCookiesAndBypassLogin } from "${url.resourcesCommonPath}/node_modules/@keycloak/keycloak-account-ui/app/keycloak.js";
            checkCookiesAndBypassLogin();
        </script>
    </#if>

    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>
<body>
    <div class="ve-page">
        <div class="ve-page__bg">
            <div class="ve-page__gradient"></div>
            <div class="ve-page__pattern"></div>
        </div>

        <main class="ve-page__main">
            <#nested "header">
            <#nested "form">

            <#if social?? && social.providers?? && social.providers?has_content>
                <#nested "socialProviders">
            </#if>

            <#nested "footer">
        </main>

        <footer class="ve-page__footer">
            <p>&copy; ${.now?string('yyyy')} VoltEdge. All rights reserved.</p>
        </footer>
    </div>

    <script>
        function togglePassword() {
            const pw = document.getElementById('password') || document.getElementById('password-new');
            if (!pw) return;
            if (pw.type === 'password') { pw.type = 'text'; } else { pw.type = 'password'; }
        }

        document.querySelectorAll('.ve-alert__close').forEach(btn => {
            btn.addEventListener('click', function() { this.parentElement.remove(); });
        });

        document.querySelectorAll('.ve-field__input').forEach(input => {
            input.addEventListener('focus', function() {
                this.closest('.ve-field__input-wrap')?.classList.add('ve-field__input-wrap--focused');
            });
            input.addEventListener('blur', function() {
                this.closest('.ve-field__input-wrap')?.classList.remove('ve-field__input-wrap--focused');
            });
        });
    </script>
</body>
</html>
</#macro>
