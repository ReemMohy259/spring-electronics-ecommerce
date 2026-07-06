<#--  VoltEdge — Error Theme  -->
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
                <h1 class="ve-card__title">${msg("errorTitle")}</h1>
            </div>

            <div class="ve-alert ve-alert--error">
                <svg class="ve-alert__icon" viewBox="0 0 20 20" fill="currentColor" width="18" height="18">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                </svg>
                <span class="ve-alert__text">${message.summary?has_content?then(message.summary, msg("errorTitle"))}</span>
            </div>

            <#if client?? && client.baseUrl?has_content>
                <p class="ve-card__footer-text">
                    <a href="${client.baseUrl}" class="ve-link">${msg("backToApplication")}</a>
                </p>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
