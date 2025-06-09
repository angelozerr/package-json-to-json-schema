package foo.ruby;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateRubyJsonSchema {

	public static void main(String[] args) {

		// https://github.com/swiftlang/vscode-swift

		String id = "LSP4IJ/sourcekit-lsp/settings.schema.json";
		String title = "LSP4IJ Swift language server settings JSON schema";
		String description = "JSON schema for Swift language server settings.";

		JsonObject schema = generateJsonSchema(GenerateRubyJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);

		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
