package foo.rust;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;

import com.google.gson.JsonObject;
import static foo.PackageJsonToJsonSchema3.print;

public class GenerateRustJsonSchema {

	public static void main(String[] args) {

		String id = "LSP4IJ/rust-analyzer/settings.schema.json";
		String title = "LSP4IJ rust-analyzer server settings JSON schema";
		String description = "JSON schema for rust-analyzer server settings.";

		
		JsonObject schema = generateJsonSchema(GenerateRustJsonSchema.class.getResourceAsStream("package.json"),null, id, title, description);
		
		System.out.println("");
		
		print(generateJsonWithDefault(schema));
	}

}
