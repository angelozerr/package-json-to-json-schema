package foo.svelte;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateSvelteJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/svelte/settings.schema.json";
		String title = "LSP4IJ svelte server settings JSON schema";
		String description = "JSON schema for svelte server settings.";

		JsonObject schema = generateJsonSchema(GenerateSvelteJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
