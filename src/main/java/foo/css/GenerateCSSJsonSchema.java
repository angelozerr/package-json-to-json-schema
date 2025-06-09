package foo.css;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateCSSJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/vscode-css-language-server/settings.schema.json";
		String title = "LSP4IJ VSCode CSS language server settings JSON schema";
		String description = "JSON schema for VSCode CSS language server settings.";

		JsonObject schema = generateJsonSchema(GenerateCSSJsonSchema.class.getResourceAsStream("package.json"),
				GenerateCSSJsonSchema.class.getResourceAsStream("package.nls.json"), id, title, description);

		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
