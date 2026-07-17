<#--  VoltEdge — Update Password Theme  -->
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
                <h1 class="ve-card__title">${msg("updatePasswordTitle")}</h1>
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

            <form id="kc-passwd-update-form" class="ve-form" action="${url.loginAction}" method="post" novalidate>
                <div class="ve-field">
                    <label class="ve-field__label" for="password-new">${msg("passwordNew")}</label>
                    <div class="ve-field__input-wrap">
                        <svg class="ve-field__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                            <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
                        </svg>
                        <input id="password-new" class="ve-field__input" type="password" name="password-new" autofocus autocomplete="new-password"
                               placeholder="&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;" aria-label="${msg('passwordNew')}"/>
                        <button type="button" class="ve-field__toggle" onclick="togglePassword()" aria-label="${msg('showPassword')}">
                            <svg class="ve-field__toggle-eye" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                                <path d="M10 12a2 2 0 100-4 2 2 0 000 4z"/>
                                <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd"/>
                            </svg>
                        </button>
                    </div>
                </div>
                <div class="ve-field">
                    <label class="ve-field__label" for="password-confirm">${msg("passwordConfirm")}</label>
                    <div class="ve-field__input-wrap">
                        <svg class="ve-field__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                            <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
                        </svg>
                        <input id="password-confirm" class="ve-field__input" type="password" name="password-confirm" autocomplete="new-password"
                               placeholder="&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;" aria-label="${msg('passwordConfirm')}"/>
                    </div>
                </div>

                <button class="ve-btn ve-btn--primary" type="submit">
                    <span>${msg("doSubmit")}</span>
                    <svg class="ve-btn__arrow" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                        <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
                    </svg>
                </button>

                <input type="hidden" id="username" name="username" value="${username?has_content?then(username, login.username)}"/>
            </form>
        </div>
    </#if>
</@layout.registrationLayout>
