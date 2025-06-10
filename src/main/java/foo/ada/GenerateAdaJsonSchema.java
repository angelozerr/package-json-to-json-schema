package foo.ada;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;

import com.google.gson.JsonObject;
import static foo.PackageJsonToJsonSchema3.print;

public class GenerateAdaJsonSchema {

	public static void main(String[] args) {

		// https://github.com/AdaCore/ada_language_server/blob/master/integration/vscode/ada/package.json
		
		String id = "LSP4IJ/ada_language_server/settings.schema.json";
		String title = "LSP4IJ ada_language_server server settings JSON schema";
		String description = "JSON schema for ada_language_server server settings.";

		JsonObject schema = generateJsonSchema(GenerateAdaJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
