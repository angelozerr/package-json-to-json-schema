package foo.perllsp;

import static foo.PackageJsonToJsonSchema3.generateJsonSchema;
import static foo.PackageJsonToJsonSchema3.generateJsonWithDefault;

import com.google.gson.JsonObject;
import static foo.PackageJsonToJsonSchema3.print;

public class GeneratePerlspJsonSchema {

	public static void main(String[] args) {

		// https://github.com/EffortlessMetrics/perl-lsp/blob/master/vscode-extension/package.json
		
		String id = "LSP4IJ/perlsp/settings.schema.json";
		String title = "LSP4IJ Perl LSP server settings JSON schema";
		String description = "JSON schema for perlsp server settings.";

		JsonObject schema = generateJsonSchema(GeneratePerlspJsonSchema.class.getResourceAsStream("package.json"), null,
				id, title, description);
		System.out.println("");

		print(generateJsonWithDefault(schema));
	}

}
