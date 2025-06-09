package foo.go;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GenerateGoJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/gopls/settings.schema.json";
		String title = "LSP4IJ Go language server settings JSON schema";
		String description = "JSON schema for Go language server settings.";

		JsonObject schema = generateJsonSchema(GenerateGoJsonSchema.class.getResourceAsStream("package.json"), null, id,
				title, description, false, false);

		JsonObject hintsProperties = null;
		JsonObject properties = schema.get("properties").getAsJsonObject();
		JsonObject gopls = properties.getAsJsonObject("gopls").getAsJsonObject("properties");
		// Remove all go.* keys
		Set<String> keys = new LinkedHashSet<String>(properties.keySet());
		for (String key : keys) {
			if (key.equals("gopls")) {
				// keep gopls
			} else if (key.startsWith("go.inlayHints.")) {
				// https://github.com/golang/vscode-go/blob/3673270a540086ac93d176350bd6fd8d30fd058e/extension/src/language/goLanguageServer.ts#L908
				if (hintsProperties == null) {
					JsonObject hints = new JsonObject();
					hints.addProperty("type", "object");
					hintsProperties = new JsonObject();
					hints.add("properties", hintsProperties);
					gopls.add("ui.inlayhint.hints", hints);
				}
				// Set inlay hint to true by default
				JsonObject inlayHint = properties.get(key).getAsJsonObject();
				JsonElement defaultValue= inlayHint.get("default");
				if (defaultValue != null && defaultValue.isJsonPrimitive() && defaultValue.getAsJsonPrimitive().isBoolean()) {
					inlayHint.addProperty("default", true);
				}
				hintsProperties.add(key.substring("go.inlayHints.".length()), inlayHint);
				properties.remove(key);
			} else {
				// remove go.*
				properties.remove(key);
			}
		}

		print(schema);

		System.out.println("");

		JsonObject settings = generateJsonWithDefault(schema);
		settings.get("gopls").getAsJsonObject().remove("build.memoryMode");
		print(settings);
	}

}
