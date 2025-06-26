package foo.terraform;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateTerraformJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/terraform/settings.schema.json";
		String title = "LSP4IJ terraform server settings JSON schema";
		String description = "JSON schema for terraform server settings.";

		JsonObject schema = generateJsonSchema(GenerateTerraformJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
