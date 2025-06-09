package foo.swift;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateSwiftJsonSchema {

	public static void main(String[] args) {

		// https://github.com/standardrb/vscode-standard-ruby

		String id = "LSP4IJ/ruby-lsp/settings.schema.json";
		String title = "LSP4IJ Ruby language server settings JSON schema";
		String description = "JSON schema for Ruby language server settings.";

		JsonObject schema = generateJsonSchema(GenerateSwiftJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);

		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
