package foo.clangd;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateClangdJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/clangd/settings.schema.json";
		String title = "LSP4IJ clangd server settings JSON schema";
		String description = "JSON schema for clangd server settings.";

		JsonObject schema = generateJsonSchema(GenerateClangdJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
