<#--  VoltEdge — Terms & Conditions Theme  -->
<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
    <#elseif section = "form">
        <div class="ve-card ve-card--wide">
            <div class="ve-card__brand">
                <div class="ve-logo">
                    <svg class="ve-logo__icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M20 2L4 24h12l-4 14 20-22H20l4-14L20 2z" fill="#0d9488"/>
                    </svg>
                    <span class="ve-logo__text">VoltEdge</span>
                </div>
                <h1 class="ve-card__title">${msg("termsTitle")}</h1>
            </div>

            <div class="ve-terms">
                ${kcSanitize(msg("termsText"))?no_esc}
            </div>

            <form class="ve-form" action="${url.loginAction}" method="post">
                <div class="ve-form__actions ve-form__actions--center">
                    <button class="ve-btn ve-btn--primary" name="accept" type="submit">
                        <span>${msg("doAccept")}</span>
                    </button>
                    <button class="ve-btn ve-btn--secondary" name="cancel" type="submit">
                        <span>${msg("doDecline")}</span>
                    </button>
                </div>
            </form>
        </div>
    </#if>
</@layout.registrationLayout>
