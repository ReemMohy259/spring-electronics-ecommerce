<#--  VoltEdge — Custom Login Theme  -->
<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        <#-- Empty header — brand is rendered in the card itself -->
    <#elseif section = "form">
        <div class="ve-card">
            <div class="ve-card__brand">
                <div class="ve-logo">
                    <svg class="ve-logo__icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M20 2L4 24h12l-4 14 20-22H20l4-14L20 2z" fill="#0d9488"/>
                    </svg>
                    <span class="ve-logo__text">VoltEdge</span>
                </div>
                <p class="ve-card__tagline">Powering Your Digital Edge</p>
            </div>

            <#if message?has_content && message.type != "">
                <div class="ve-alert ve-alert--${message.type}">
                    <svg class="ve-alert__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                        <#if message.type = "error">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                        <#elseif message.type = "success">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                        <#else>
                            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                        </#if>
                    </svg>
                    <span class="ve-alert__text">${kcSanitize(message.summary)?no_esc}</span>
                    <button class="ve-alert__close" onclick="this.parentElement.remove()" type="button">&times;</button>
                </div>
            </#if>

            <form id="kc-form-login" class="ve-form" action="${url.loginAction}" method="post" novalidate>
                <div class="ve-field">
                    <label class="ve-field__label" for="username">
                        <#if !realm.loginWithEmailAllowed>${msg("username")}
                        <#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}
                        <#else>${msg("email")}
                        </#if>
                    </label>
                    <div class="ve-field__input-wrap">
                        <svg class="ve-field__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                            <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                        </svg>
                        <input id="username" class="ve-field__input" type="text" name="username" autofocus autocomplete="username"
                               value="${login.username!''}" placeholder="${msg('username')}" aria-label="${msg('username')}"/>
                    </div>
                </div>

                <div class="ve-field">
                    <label class="ve-field__label" for="password">${msg("password")}</label>
                    <div class="ve-field__input-wrap">
                        <svg class="ve-field__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                            <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
                        </svg>
                        <input id="password" class="ve-field__input" type="password" name="password" autocomplete="current-password"
                               placeholder="&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;" aria-label="${msg('password')}"/>
                        <button type="button" class="ve-field__toggle" onclick="togglePassword()" aria-label="${msg('showPassword')}">
                            <svg class="ve-field__toggle-eye" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                                <path d="M10 12a2 2 0 100-4 2 2 0 000 4z"/>
                                <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd"/>
                            </svg>
                        </button>
                    </div>
                </div>

                <div class="ve-form__actions">
                    <#if realm.rememberMe && !usernameEditDisabled??>
                        <label class="ve-checkbox">
                            <input id="rememberMe" name="rememberMe" type="checkbox" class="ve-checkbox__input"
                            <#if login.rememberMe??>checked</#if>/>
                            <span class="ve-checkbox__checkmark"></span>
                            <span class="ve-checkbox__label">${msg("rememberMe")}</span>
                        </label>
                    </#if>

                    <#if realm.resetPasswordAllowed>
                        <a href="${url.loginResetCredentialsUrl}" class="ve-link ve-link--sm">${msg("doForgotPassword")}</a>
                    </#if>
                </div>

                <button id="kc-login" class="ve-btn ve-btn--primary" type="submit">
                    <span>${msg("doLogIn")}</span>
                    <svg class="ve-btn__arrow" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                        <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
                    </svg>
                </button>

                <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
            </form>

            <#if social.providers?? && social.providers?has_content>
                <div class="ve-divider">
                    <span class="ve-divider__text">${msg("or Continue With")}</span>
                </div>
                <div class="ve-social">
                    <#list social.providers as provider>
                        <a href="${provider.loginUrl}" class="ve-btn ve-btn--social ve-btn--social-${provider.alias}">
                            <#if provider.alias = "google">
                                <svg viewBox="0 0 24 24" width="20" height="20" xmlns="http://www.w3.org/2000/svg">
                                    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 01-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"/>
                                    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                                </svg>
                            <#else>
                                <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z"/>
                                </svg>
                            </#if>
                            <span>${provider.displayName!}</span>
                        </a>
                    </#list>
                </div>
            </#if>

            <#if realm.registrationAllowed && !usernameEditDisabled??>
                <p class="ve-card__footer-text">
                    ${msg("noAccount")}
                    <a href="${url.registrationUrl}" class="ve-link">${msg("doRegister")}</a>
                </p>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
