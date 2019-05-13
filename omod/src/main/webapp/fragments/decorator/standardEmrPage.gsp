<%
    sessionContext.requireAuthentication()

	ui.includeFragment("appui", "standardEmrIncludes")

	def title = config.title ?: ui.message("emr.title")
    def timezoneOffset = -Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis()) / 60000
    def jsTimezone = new java.text.SimpleDateFormat("ZZ").format(new Date());
%>

<!DOCTYPE html>
<html>
    <head>
        <title>${ title ?: "OpenMRS" }</title>
        <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
        <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
          <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
        <% ui.includeCss("appui", "header.css") %>
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

        ${ ui.includeFragment("appui", "header") }

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
        <!-- jQuery library -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>

        <!-- Latest compiled JavaScript -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
    </body>
</html>