package foo.harper;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;
import static foo.PackageJsonToJsonSchema3.print;

import com.google.gson.JsonObject;

public class GenerateHarperJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/harper/settings.schema.json";
		String title = "LSP4IJ harper server settings JSON schema";
		String description = "JSON schema for harper server settings.";

		JsonObject schema = generateJsonSchema(GenerateHarperJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");
 
		print(generateJsonWithDefault(schema));
	}

}
