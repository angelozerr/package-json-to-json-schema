package foo.julia;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateJuliaJsonSchema {

	public static void main(String[] args) {

		// https://github.com/julia-vscode/julia-vscode

		String id = "LSP4IJ/julia/settings.schema.json";
		String title = "LSP4IJ Julia language server settings JSON schema";
		String description = "JSON schema for Julia language server settings.";

		JsonObject schema = generateJsonSchema(GenerateJuliaJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);

		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
