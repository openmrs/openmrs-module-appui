<%
    config.require("codes")
%>

<script type="text/javascript">
    window.messages = window.messages || { };
    <% config.codes.each { %>
        window.messages['${ ui.escapeJs(it) }'] = '${ ui.escapeJs(ui.message(it)) }';
    <% } %>
</script>