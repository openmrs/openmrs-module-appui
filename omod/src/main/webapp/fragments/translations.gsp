<%
    config.require("codes")
%>

<script type="text/javascript">
    window.translations = window.translations || { };
    <% config.codes.each { %>
        window.translations['${ ui.escapeJs(it) }'] = '${ ui.escapeJs(ui.message(it)) }';
    <% } %>
</script>