package foo.astro;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateAstroJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/astro/settings.schema.json";
		String title = "LSP4IJ astro server settings JSON schema";
		String description = "JSON schema for astro server settings.";

		JsonObject schema = generateJsonSchema(GenerateAstroJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");
 
		print(generateJsonWithDefault(schema));
	}

}
