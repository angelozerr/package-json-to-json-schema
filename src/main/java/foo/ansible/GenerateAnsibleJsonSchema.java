package foo.ansible;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateAnsibleJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/ansible/settings.schema.json";
		String title = "LSP4IJ ansible server settings JSON schema";
		String description = "JSON schema for ansible server settings.";

		JsonObject schema = generateJsonSchema(GenerateAnsibleJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
