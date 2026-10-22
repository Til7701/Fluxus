package de.til7701.fluxus.common.environment;

import de.til7701.fluxus.ast.statement.Import;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class Imports {

    /// Maps from the local names to the real type names.
    private final Map<String, String> nameMapping = new HashMap<>();

    public Imports(List<Import> imports) {
        imports.forEach(this::addImport);
    }

    public void addImport(Import imp) {
        String localName = imp.asName();
        if (localName == null)
            localName = imp.typeToImport().substring(imp.typeToImport().lastIndexOf('.') + 1);
        nameMapping.put(localName, imp.typeToImport());
    }

    private void addImport(String localName, String typeName) {
        nameMapping.put(localName, typeName);
    }

    public String map(String name) {
        return nameMapping.getOrDefault(name, name);
    }

    public Imports snapshot() {
        Imports snapshot = new Imports();
        nameMapping.forEach(snapshot::addImport);
        return snapshot;
    }
}
