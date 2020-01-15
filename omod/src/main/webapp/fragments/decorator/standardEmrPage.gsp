<%
    sessionContext.requireAuthentication()

    def title = config.title ?: ui.message("emr.title")
    def timezoneOffset = -Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis()) / 60000
    def jsTimezone = new java.text.SimpleDateFormat("ZZ").format(new Date());
    def includeBootstrap = config.containsKey('includeBootstrap') ? config.includeBootstrap : true;  // include bootstrap unless specifically excluded

	ui.includeFragment("appui", "standardEmrIncludes", [ includeBootstrap: includeBootstrap ])

    if (includeBootstrap) {
        ui.includeJavascript("appui", "popper.min.js")
        ui.includeJavascript("appui", "bootstrap.min.js")
        ui.includeCss("appui", "bootstrap.min.css")
    }
    else {
        ui.includeCss("appui", "no-bootstrap.css")
    }

    ui.includeCss("appui", "header.css")

%>

<!DOCTYPE html>
<html>
    <head>
        <title>${ title ?: "OpenMRS" }</title>
        <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
        <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
        <!-- Latest compiled and minified CSS -->
        <meta name="viewport" content="width=device-width, initial-scale=1">
        ${ ui.resourceLinks() }
    </head>
    <body>
        <script type="text/javascript">
            var OPENMRS_CONTEXT_PATH = '${ ui.contextPath() }';
            var openmrsContextPath = '/' + OPENMRS_CONTEXT_PATH;
            window.sessionContext = window.sessionContext || {
                locale: "${ ui.escapeJs(sessionContext.locale.toString()) }"
            };
            window.translations = window.translations || {};
            var openmrs = {
                server: {
                    timezone: "${ jsTimezone }",
                    timezoneOffset: ${ timezoneOffset }
                }
            }
        </script>

        ${ ui.includeFragment("appui", "header", [ useBootstrap: includeBootstrap ]) }

        <ul id="breadcrumbs"></ul>

        <div id="body-wrapper">

            ${ ui.includeFragment("uicommons", "infoAndErrorMessage") }

            <div id="content" class="container-fluid">
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
    </body>
</html>
