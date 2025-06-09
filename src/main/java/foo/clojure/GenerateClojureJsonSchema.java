package foo.clojure;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;

import com.google.gson.JsonObject;
import static foo.PackageJsonToJsonSchema3.print;

public class GenerateClojureJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/clojure-lsp/settings.schema.json";
		String title = "LSP4IJ clojure-lsp server settings JSON schema";
		String description = "JSON schema for clojure-lsp server settings.";

		JsonObject schema = generateJsonSchema(GenerateClojureJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
