package foo.metals;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateMetalsJsonSchema {

	public static void main(String[] args) {

		// https://github.com/scalameta/metals-vscode

		String id = "LSP4IJ/metals/settings.schema.json";
		String title = "LSP4IJ Metals language server settings JSON schema";
		String description = "JSON schema for Metals language server settings.";

		JsonObject schema = generateJsonSchema(GenerateMetalsJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);

		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
