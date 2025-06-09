package foo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PackageJsonToJsonSchema2 {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Convertit le contributes/configuration d'un package.json en JSON Schema.
	 *
	 * @param packageJsonPath Le chemin vers le fichier package.json
	 * @return Un objet JsonObject représentant le JSON Schema
	 * @throws IOException Si le fichier ne peut pas être lu
	 */
	public static JsonObject convertToSchema(Reader content) throws IOException {
		// Lire le contenu du fichier package.json
		// String content = new String(Files.readAllBytes(Paths.get(packageJsonPath)));

		// Parser le contenu en JsonObject
		JsonObject packageJson = JsonParser.parseReader(content).getAsJsonObject();

		// Accéder à "contributes" -> "configuration"
		JsonObject contributes = packageJson.getAsJsonObject("contributes");
		if (contributes == null) {
			throw new IllegalStateException("Le champ 'contributes' est absent du package.json");
		}

		JsonElement configuration = contributes.get("configuration");
		if (configuration == null || !configuration.isJsonArray()) {
			throw new IllegalStateException(
					"Le champ 'configuration' est absent ou n'est pas un tableau dans 'contributes'");
		}

		JsonArray configArray = configuration.getAsJsonArray();

		// Créer un JSON Schema basé sur le tableau de configuration
		JsonObject jsonSchema = new JsonObject();
		jsonSchema.addProperty("$schema", "http://json-schema.org/draft-07/schema#");
		JsonObject properties = new JsonObject();

		for (JsonElement element : configArray) {
			if (element.isJsonObject()) {
				JsonObject setting = element.getAsJsonObject();
				String id = setting.has("id") ? setting.get("id").getAsString() : "unknown";

				// Créer une propriété pour chaque entrée de configuration
				JsonObject property = new JsonObject();
				if (setting.has("type")) {
					property.addProperty("type", setting.get("type").getAsString());
				}
				if (setting.has("default")) {
					property.add("default", setting.get("default"));
				}
				if (setting.has("description")) {
					property.addProperty("description", setting.get("description").getAsString());
				}

				properties.add(id, property);
			}
		}

		jsonSchema.add("properties", properties);
		return jsonSchema;
	}

	
	public static JsonObject convertConfigurationsToSchemas(Reader content, JsonObject localization) throws IOException {

		// Parser le contenu en JsonObject
        JsonObject packageJson = JsonParser.parseReader(content).getAsJsonObject();

       
		// Accéder à "contributes" -> "configuration"
        JsonObject contributes = packageJson.getAsJsonObject("contributes");
        if (contributes == null) {
            throw new IllegalStateException("Le champ 'contributes' est absent du package.json");
        }

        JsonElement configuration = contributes.get("configuration");
        if (configuration == null || !configuration.isJsonArray()) {
            throw new IllegalStateException("Le champ 'configuration' est absent ou n'est pas un tableau dans 'contributes'");
        }

        JsonArray configArray = configuration.getAsJsonArray();

        // Créer le schéma global
        JsonObject jsonSchema = new JsonObject();
        jsonSchema.addProperty("$schema", "http://json-schema.org/draft-07/schema#");

        JsonObject properties = new JsonObject();

        // Itérer sur chaque élément de configuration pour fusionner les propriétés
        for (JsonElement element : configArray) {
            if (element.isJsonObject()) {
                JsonObject config = element.getAsJsonObject();
                
                if (config.has("properties")) {
                    JsonObject configProperties = config.getAsJsonObject("properties");

                    // Fusionner chaque propriété dans le schéma global
                    for (Entry<String, JsonElement> entry : configProperties.entrySet()) {
                        JsonElement property = entry.getValue();
                        
                        // Vérifier si la propriété contient un "markdownDescription" et le convertir en "description"
                        if (property.isJsonObject()) {
                            JsonObject propertyObject = property.getAsJsonObject();

                            // Si markdownDescription existe, on la remplace par description
                            if (propertyObject.has("markdownDescription")) {
                                propertyObject.addProperty("description", replacePlaceholders(propertyObject.get("markdownDescription").getAsString(), localization));
                                propertyObject.remove("markdownDescription");
                            }
                            
                            // Ignorer le champ "scope" s'il existe
                            propertyObject.remove("scope");
                        }

                        // Ajouter la propriété au schéma global
                        properties.add(entry.getKey(), property);
                    }
                }
            }
        }

        jsonSchema.add("properties", properties);
        return jsonSchema;
    }
	
	private static String replacePlaceholders(String property,JsonObject localization) {
		if (localization != null &&property.startsWith("%") && property.endsWith("%")) {
			JsonElement s = localization.get(property.substring(1, property.length() - 1));
			if (s != null) {
				return s.getAsString();
			}
		}
		return property;
	}


	public static void main(String[] args) {
		try {
			
			JsonObject localization= loadLocalization(null);
			
			InputStream inputStream = PackageJsonToJsonSchema2.class.getResourceAsStream("package.json");
			Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

			JsonObject schemas = convertConfigurationsToSchemas(reader, localization);

			// Afficher le JSON Schema formaté
			System.out.println(gson.toJson(schemas));
		} catch (IOException e) {
			System.err.println("Erreur de lecture du fichier : " + e.getMessage());
		} catch (IllegalStateException e) {
			System.err.println("Erreur de structure du fichier : " + e.getMessage());
		}
	}
	
	public static JsonObject loadLocalization(String nlsJsonPath) throws IOException {
		InputStream inputStream = PackageJsonToJsonSchema2.class.getResourceAsStream("package.nls.json");
		if (inputStream == null) {
			return null;
		}
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		JsonObject packageJson = JsonParser.parseReader(reader).getAsJsonObject();
		return packageJson;
		
	}
}
