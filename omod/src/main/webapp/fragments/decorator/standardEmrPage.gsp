<%
    sessionContext.requireAuthentication()

	ui.includeFragment("appui", "standardEmrIncludes")

	def title = config.title ?: ui.message("emr.title")
%>

${ ui.includeFragment("appui", "header") }

<ul id="breadcrumbs"></ul>

<div id="body-wrapper">

    ${ ui.includeFragment("uicommons", "infoAndErrorMessage") }

    <div id="content" class="container">
        <%= config.content %>
    </div>

</div>

<script id="breadcrumb-template" type="text/template">
    <li>
        {{ if (!first) { }}
        <i class="icon-chevron-right link"></i>
        {{ } }}
        {{ if (!last && breadcrumb.link) { }}
        <a href="{{= breadcrumb.link }}">
        {{ } }}
        {{ if (breadcrumb.icon) { }}
        <i class="{{= breadcrumb.icon }} small"></i>
        {{ } }}
        {{ if (breadcrumb.label) { }}
        {{= breadcrumb.label }}
        {{ } }}
        {{ if (!last && breadcrumb.link) { }}
        </a>
        {{ } }}
    </li>
</script>

<script type="text/javascript">
    jq(function() {
        emr.updateBreadcrumbs();
    });

    // global error handler
    jq(document).ajaxError(function(event, jqxhr) {
        emr.redirectOnAuthenticationFailure(jqxhr);
    });

    var featureToggles = {};

    <% featureToggles.getToggleMap().each { %>
        featureToggles["${it.key}"] = ${ Boolean.parseBoolean(it.value)};
    <% } %>

</script>