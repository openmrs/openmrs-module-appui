<%
    def addContextPath = {
        if (!it)
            return null
        if (it.startsWith("/")) {
            it = "/" + org.openmrs.ui.framework.WebConstants.CONTEXT_PATH + it
        }
        return it
    }
    def logoIconUrl = addContextPath(configSettings?."logo-icon-url") ?: ui.resourceLink("uicommons", "images/logo/openmrs-with-title-small.png")
    def logoLinkUrl = addContextPath(configSettings?."logo-link-url") ?: "/${ org.openmrs.ui.framework.WebConstants.CONTEXT_PATH }/"
    def useBootstrap = config.containsKey('useBootstrap') ? config.useBootstrap : true;  // use bootstrap unless specifically excluded

    def enableUserAccountExt = userAccountMenuItems.size > 0;

%>
<script type="text/javascript">

    var sessionLocationModel = {
        id: ko.observable(),
        text: ko.observable()
    };

    jq(function () {

        ko.applyBindings(sessionLocationModel, jq('.change-location').get(0));
        sessionLocationModel.id(${ sessionContext.sessionLocationId });
        sessionLocationModel.text("${ ui.escapeJs(ui.encodeHtmlContent(ui.format(sessionContext.sessionLocation))) }");

        var locationsList = jq('div#session-location').find('ul.select');
        var loginLocationsUrl = emr.fragmentActionLink("appui", "session", "getLoginLocations");
        var sessionLocation = {};
        sessionLocation.name = "${ sessionContext.sessionLocation ? sessionContext.sessionLocation.name : '' }";
        var multipleLoginLocations = false;

        jq.getJSON(loginLocationsUrl).done(function(locations) {
            if (jq(locations).size() > 1) {
                // we only want to activate the functionality to change location if there are actually multiple login locations
                multipleLoginLocations = true;
            }

            locations.sort(function(locationA, locationB) {
                return locationA.name.localeCompare(locationB.name);
            });
            jq.each(locations, function(index, location) {
                jq('<li>').addClass(sessionLocation.name == location.name ? 'selected' : '')
                            .attr('locationUuid', location.uuid)
                            .attr('locationId', location.id)
                            .attr('locationName', location.name)
                            .text(location.name)
                            .appendTo(locationsList);
            });
        }).always(function() {
            if (multipleLoginLocations == true) {
                enableLoginLocations();
            }

            <% if (enableUserAccountExt) { %>
            var event = ('ontouchstart' in window) ? 'click' : 'mouseenter mouseleave';

            jq('.identifier').on(event,function(){
                jq('.appui-toggle').toggle();
                jq('.appui-icon-caret-down').toggle();
            });

            jq('.identifier').css('cursor', 'pointer');
            <% } %>
        });
    });

    function enableLoginLocations() {
        jq('.change-location a i:nth-child(3)').show();

        jq(".change-location a").click(function () {
            jq('#session-location').show();
            jq(this).addClass('focus');
            jq(".change-location a i:nth-child(3)").removeClass("icon-caret-down");
            jq(".change-location a i:nth-child(3)").addClass("icon-caret-up");
        });

        jq('#session-location').mouseleave(function () {
            jq('#session-location').hide();
            jq(".change-location a").removeClass('focus');
            jq(".change-location a i:nth-child(3)").addClass("icon-caret-down");
            jq(".change-location a i:nth-child(3)").removeClass("icon-caret-up");
        });

        jq("#session-location ul.select").on('click', 'li', function (event) {
            var element = jq(event.target);
            var locationId = element.attr("locationId");
            var locationUuid = element.attr("locationUuid");
            var locationName = element.attr("locationName");

            <% if (ui.convertTimezones()) { %>
                var clientCurrentTimezone = jq("#clientTimezone").val();
                data = { locationId: locationId , clientTimezone: clientCurrentTimezone };
            <% } else { %>
                data = { locationId: locationId };
            <% } %>
            jq("#spinner").show();

            jq.post(emr.fragmentActionLink("appui", "session", "setLocation", data), function (data) {
                sessionLocationModel.id(locationId);
                sessionLocationModel.text(locationName);
                jq('#selected-location').attr("location-uuid", locationUuid);
                jq('#session-location li').removeClass('selected');
                element.addClass('selected');
                jq("#spinner").hide();
                jq(document).trigger("sessionLocationChanged");
            })

            jq('#session-location').hide();
            jq(".change-location a").removeClass('focus');
            jq(".change-location a i:nth-child(3)").addClass("icon-caret-down");
            jq(".change-location a i:nth-child(3)").removeClass("icon-caret-up");
        });
    }
    jq(document).ready(function () {
        if (jq("#clientTimezone").length) {
            jq("#clientTimezone").val(Intl.DateTimeFormat().resolvedOptions().timeZone)
        }
    });
</script>
<header>

    <% if (context.authenticated) { %>

        <% if (useBootstrap) { %>
            <nav class="navbar navbar-expand-lg navbar-dark navigation">
                <div class="logo">
                    <a href="${ logoLinkUrl }">
                        <img src="${ logoIconUrl }"/>
                    </a>
                </div>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav ml-auto user-options">
                    <li class="nav-item identifier">
        <% } else { %>
                    <div class="logo">
                        <a href="${ logoLinkUrl }">
                            <img src="${ logoIconUrl }"/>
                        </a>
                    </div>
                    <ul class="user-options">
                    <li class="identifier">
        <% } %>

                    <i class="icon-user small"></i>
                    ${context.authenticatedUser.username ?: context.authenticatedUser.systemId}
                    <% if (enableUserAccountExt) { %>
                        <i class="icon-caret-down appui-icon-caret-down link"></i><i class="icon-caret-up link appui-toggle" style="display: none;"></i>
                        <ul id="user-account-menu" class="appui-toggle">
                            <% userAccountMenuItems.each{ menuItem  -> %>
                            <li>
                                <a id="" href="/${ contextPath }/${ menuItem.url }">
                                    ${ ui.message(menuItem.label) }
                                </a>
                            </li>
                            <% } %>
                        </ul>
                    <% } %>
                </li>
            <li class="change-location">
                    <a href="javascript:void(0);">
                        <i class="icon-map-marker small"></i>
                        <span id="selected-location" data-bind="text: text" location-uuid="${ sessionContext.sessionLocation ? sessionContext.sessionLocation.uuid : "" }"></span>
                        <i class="icon-caret-down link" style="display:none"></i>
                    </a>
                </li>
                <li class="nav-item logout">
                    <a href="${ ui.actionLink("logout", ["successUrl": contextPath]) }">
                        ${ui.message("emr.logout")}
                        <i class="icon-signout small"></i>
                    </a>
                </li>
                </ul>

        <% if (useBootstrap) { %>
            </div>
        </nav>
        <% } %>

        <div id="session-location">
                <div id="spinner" style="position:absolute; display:none">
                    <img src="${ui.resourceLink("uicommons", "images/spinner.gif")}">
                </div>
                <ul class="select"></ul>
    <% if (ui.convertTimezones()) { %>
            <input type="hidden" id="clientTimezone" name="clientTimezone">
    <% } %>
</div>
    <% } else { %>
        <div class="logo">
            <a href="${ logoLinkUrl }">
                <img src="${ logoIconUrl }"/>
            </a>
        </div>
    <% } %>
</header>
