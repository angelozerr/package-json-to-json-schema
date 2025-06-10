package foo.camel;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateCamelJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/camel/settings.schema.json";
		String title = "LSP4IJ camel server settings JSON schema";
		String description = "JSON schema for camel server settings.";

		JsonObject schema = generateJsonSchema(GenerateCamelJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");
 
		print(generateJsonWithDefault(schema));
	}

}
