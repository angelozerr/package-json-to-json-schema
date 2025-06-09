package foo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PackageJsonToJsonSchema {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static JsonObject convertConfigurationsToSchemas(Reader content, JsonObject localization)
			throws IOException {

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

		// Créer le schéma global
		JsonObject jsonSchema = new JsonObject();
		jsonSchema.addProperty("$schema", "http://json-schema.org/draft-07/schema#");

		// Fusionner toutes les propriétés
		JsonObject mergedProperties = new JsonObject();

		for (JsonElement element : configArray) {
			JsonObject configItem = element.getAsJsonObject();

			// Fusionner les propriétés de chaque configuration
			JsonObject properties = configItem.getAsJsonObject("properties");
			for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
				String propertyKey = entry.getKey();
				JsonObject propertyValue = entry.getValue().getAsJsonObject();

				// Remplacer les descriptions avec les valeurs de localisation
				if (propertyValue.has("description")) {
					String description = propertyValue.get("description").getAsString();
					propertyValue.addProperty("description", replacePlaceholders(description, localization));
				} else if (propertyValue.has("markdownDescription")) {
					String description = propertyValue.get("markdownDescription").getAsString();
					propertyValue.addProperty("description", replacePlaceholders(description, localization));
					propertyValue.remove("markdownDescription");
				}

				propertyValue.remove("scope");

				// Transformer la clé en structure imbriquée avec "properties"
				// JsonObject nestedProperty = splitPropertyIntoNestedObject(propertyKey,
				// propertyValue);

				// Fusionner les propriétés imbriquées
				mergeNestedProperties(mergedProperties, propertyKey, propertyValue);
			}
		}

		// Ajouter les propriétés fusionnées au schéma
		jsonSchema.add("properties", mergedProperties);

		return jsonSchema;
	}

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

	public static void main(String[] args) {
		try {

			JsonObject localization = loadLocalization(null);

			InputStream inputStream = PackageJsonToJsonSchema.class.getResourceAsStream("package.json");
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
		InputStream inputStream = PackageJsonToJsonSchema.class.getResourceAsStream("package.nls.json");
		if (inputStream == null) {
			return null;
		}
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		JsonObject packageJson = JsonParser.parseReader(reader).getAsJsonObject();
		return packageJson;

	}

	private static String replacePlaceholders(String property, JsonObject localization) {
		if (localization != null && property.startsWith("%") && property.endsWith("%")) {
			JsonElement s = localization.get(property.substring(1, property.length() - 1));
			if (s != null) {
				return s.getAsString();
			}
		}
		return property;
	}

	private static void mergeNestedProperties(JsonObject mergedProperties, String propertyKey,
			JsonObject nestedProperty) {
		String[] keys = propertyKey.split("\\."); // Découpe la clé en parties
		JsonObject currentLevel = mergedProperties; // Commence à la racine de mergedProperties

		// Pour chaque partie de la clé, on crée une nouvelle structure imbriquée ou on
		// descend dans une structure existante
		for (int i = 0; i < keys.length - 1; i++) {
			String key = keys[i];
			JsonObject nextLevel = currentLevel.has(key) ? currentLevel.getAsJsonObject(key) : new JsonObject();
			if (!nextLevel.has("type")) {
				nextLevel.addProperty("type", "object"); // Ajouter le type "object" à chaque niveau
			}
			if (!nextLevel.has("properties")) {
				nextLevel.add("properties", new JsonObject()); // Créer un objet "properties"
				currentLevel.add(key, nextLevel); // Ajouter ou mettre à jour l'objet au niveau courant
			} else {
				//nextLevel = nextLevel.getAsJsonObject("properties");
			}
			currentLevel = nextLevel; // Descendre dans l'objet imbriqué
		}

		// Ajoute la dernière partie de la clé avec les propriétés de l'objet
		String lastKey = keys[keys.length - 1];
		JsonObject properties = currentLevel.getAsJsonObject("properties");
		properties.add(lastKey, nestedProperty); // Ajout du dernier niveau de propriété
	}

	private static JsonObject splitPropertyIntoNestedObject(String key, JsonElement value) {
		String[] parts = key.split("\\.");
		JsonObject currentObject = new JsonObject();
		JsonObject finalObject = currentObject;

		// Itérer sur les parties de la clé et créer des objets imbriqués
		for (int i = 0; i < parts.length - 1; i++) {
			JsonObject nestedObject = currentObject.getAsJsonObject(parts[i]);

			// Si l'objet imbriqué n'existe pas, on le crée
			if (nestedObject == null) {
				nestedObject = new JsonObject();
				currentObject.add(parts[i], nestedObject);
			}

			currentObject = nestedObject; // Passer à l'objet imbriqué suivant
		}

		// Ajouter la dernière propriété sans ajouter de redondance pour les objets
		// existants
		String lastKey = parts[parts.length - 1];
		if (currentObject.has("properties")) {
			currentObject.getAsJsonObject("properties").add(lastKey, value);
		} else {
			JsonObject properties = new JsonObject();
			properties.add(lastKey, value);
			currentObject.add("properties", properties);
		}

		return finalObject;
	}

}
