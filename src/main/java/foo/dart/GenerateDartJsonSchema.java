package foo.dart;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateDartJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/dart/settings.schema.json";
		String title = "LSP4IJ dart server settings JSON schema";
		String description = "JSON schema for dart server settings.";

		JsonObject schema = generateJsonSchema(GenerateDartJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
