<% extensions.each { ext -> %>
    <%= ui.includeFragment("uicommons", "extension", [
            extension: ext,
            contextModel: contextModel
    ]) %>
<% } %>