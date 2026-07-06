<#--  VoltEdge — Forgot Password Theme  -->
<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
    <#elseif section = "form">
        <div class="ve-card">
            <div class="ve-card__brand">
                <div class="ve-logo">
                    <svg class="ve-logo__icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M20 2L4 24h12l-4 14 20-22H20l4-14L20 2z" fill="#0d9488"/>
                    </svg>
                    <span class="ve-logo__text">VoltEdge</span>
                </div>
                <h1 class="ve-card__title">${msg("emailForgotTitle")}</h1>
                <p class="ve-card__desc">${msg("emailInstruction")}</p>
            </div>

            <#if message?has_content && message.type != "">
                <div class="ve-alert ve-alert--${message.type}">
                    <svg class="ve-alert__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                        <#if message.type = "error">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                        <#else>
                            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                        </#if>
                    </svg>
                    <span class="ve-alert__text">${kcSanitize(message.summary)?no_esc}</span>
                    <button class="ve-alert__close" onclick="this.parentElement.remove()" type="button">&times;</button>
                </div>
            </#if>

            <form id="kc-reset-password-form" class="ve-form" action="${url.loginResetCredentialsUrl}" method="post" novalidate>
                <div class="ve-field">
                    <label class="ve-field__label" for="username">
                        <#if !realm.loginWithEmailAllowed>${msg("username")}
                        <#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}
                        <#else>${msg("email")}
                        </#if>
                    </label>
                    <div class="ve-field__input-wrap">
                        <svg class="ve-field__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                            <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                            <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                        </svg>
                        <input id="username" class="ve-field__input" type="text" name="username" autofocus autocomplete="username"
                               value="${login.username!''}" placeholder="${msg('email')}" aria-label="${msg('email')}"/>
                    </div>
                </div>

                <button class="ve-btn ve-btn--primary" type="submit">
                    <span>${msg("doSubmit")}</span>
                    <svg class="ve-btn__arrow" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                        <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
                    </svg>
                </button>
            </form>

            <p class="ve-card__footer-text">
                <a href="${url.loginUrl}" class="ve-link">${msg("backToLogin")}</a>
            </p>
        </div>
    </#if>
</@layout.registrationLayout>
