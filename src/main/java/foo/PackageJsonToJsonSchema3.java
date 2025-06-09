package foo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PackageJsonToJsonSchema3 {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static JsonObject convertConfigurationsToSchemas(Reader content, JsonObject localization, String id,
			String title, String description2, boolean split) throws IOException {

		// Parser le contenu en JsonObject
		JsonObject packageJson = JsonParser.parseReader(content).getAsJsonObject();

		// Accéder à "contributes" -> "configuration"
		JsonObject contributes = packageJson.getAsJsonObject("contributes");
		if (contributes == null) {
			throw new IllegalStateException("Le champ 'contributes' est absent du package.json");
		}

		JsonElement configuration = contributes.get("configuration");
		if (configuration == null || !(configuration.isJsonArray() || configuration.isJsonObject())) {
			throw new IllegalStateException(
					"Le champ 'configuration' est absent ou n'est pas un tableau dans 'contributes'");
		}

		JsonArray configArray = getConfigs(configuration);

		// Créer le schéma global
		JsonObject jsonSchema = new JsonObject();
		jsonSchema.addProperty("$schema", "http://json-schema.org/draft-07/schema#");

		jsonSchema.addProperty("$id", id);
		jsonSchema.addProperty("title", title);
		jsonSchema.addProperty("description", description2);

		jsonSchema.addProperty("type", "object");
		jsonSchema.addProperty("additionalProperties", false);

		// Fusionner toutes les propriétés
		JsonObject mergedProperties = new JsonObject();
		Map<String, JsonObject> cache = new HashMap<>();

		for (JsonElement element : configArray) {
			JsonObject configItem = element.getAsJsonObject();

			// Fusionner les propriétés de chaque configuration
			JsonObject properties = configItem.getAsJsonObject("properties");
			if (properties != null) {
				for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
					String propertyKey = entry.getKey(); // ex: css.customData
					String[] keys = split ? propertyKey.split("\\.") : new String[] { propertyKey };

					JsonObject jsonKey = mergedProperties;
					for (int i = 0; i < keys.length; i++) {
						if (i < keys.length - 1) {
							jsonKey = getJsonKey(keys, i, jsonKey, cache);
						} else {
							JsonObject propertyValue = entry.getValue().getAsJsonObject();
							if (propertyValue.has("description")) {
								String description = propertyValue.get("description").getAsString();
								propertyValue.addProperty("description",
										replacePlaceholders(description, localization));
							} else if (propertyValue.has("markdownDescription")) {
								String description = propertyValue.get("markdownDescription").getAsString();
								propertyValue.addProperty("description",
										replacePlaceholders(description, localization));
								propertyValue.remove("markdownDescription");
							}

							propertyValue.remove("scope");

							String key = keys[i];
							jsonKey.add(key, propertyValue);
						}

					}
//				
//				
//				JsonObject propertyValue = entry.getValue().getAsJsonObject();
//
//				// Remplacer les descriptions avec les valeurs de localisation
//				if (propertyValue.has("description")) {
//					String description = propertyValue.get("description").getAsString();
//					propertyValue.addProperty("description", replacePlaceholders(description, localization));
//				} else if (propertyValue.has("markdownDescription")) {
//					String description = propertyValue.get("markdownDescription").getAsString();
//					propertyValue.addProperty("description", replacePlaceholders(description, localization));
//					propertyValue.remove("markdownDescription");
//				}
//
//				propertyValue.remove("scope");
//
//				// Transformer la clé en structure imbriquée avec "properties"
//				// JsonObject nestedProperty = splitPropertyIntoNestedObject(propertyKey,
//				// propertyValue);
//
//				// Fusionner les propriétés imbriquées
//				mergeNestedProperties(mergedProperties, propertyKey, propertyValue);
				}
			}
		}

		// Ajouter les propriétés fusionnées au schéma
		jsonSchema.add("properties", mergedProperties);

		return jsonSchema;
	}

	private static JsonArray getConfigs(JsonElement configuration) {
		if (configuration.isJsonArray()) {
			return configuration.getAsJsonArray();
		}
		JsonObject jsonObject = configuration.getAsJsonObject();
		JsonArray result = new JsonArray();
		result.add(jsonObject);
		return result;

	}

	private static JsonObject getJsonKey(String[] keys, int index, JsonObject parentProperties,
			Map<String, JsonObject> cache) {
		String cacheKey = concatenateWithDot(keys, index);
		JsonObject properties = cache.get(cacheKey);
		if (properties == null) {
			// for (int i = index; i < keys.length; i++) {
			JsonObject key = new JsonObject();
			key.addProperty("type", "object");
			parentProperties.add(keys[index], key);
			properties = new JsonObject();
			key.add("properties", properties);
			// }
			cache.put(cacheKey, properties);
		}
		return properties;
	}

	private static String concatenateWithDot(String[] array, int index) {
		if (array == null || array.length == 0 || index < 0 || index >= array.length) {
			throw new IllegalArgumentException("Index invalide ou tableau vide");
		}

		// Extraire les éléments jusqu'à l'index spécifié (inclus)
		String[] subArray = new String[index + 1];
		System.arraycopy(array, 0, subArray, 0, index + 1);

		// Joindre les éléments avec un point
		return String.join(".", subArray);
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

	public static JsonObject generateJsonSchema(InputStream packageJson, InputStream packageNlsJson, String id,
			String title, String description) {
		return generateJsonSchema(packageJson, packageNlsJson, id, title, description, false, true);
	}

	public static JsonObject generateJsonSchema(InputStream packageJson, InputStream packageNlsJson, String id,
			String title, String description, boolean split, boolean print) {
		try {

			JsonObject localization = loadLocalization(packageNlsJson);

			Reader reader = new InputStreamReader(packageJson, StandardCharsets.UTF_8);

			JsonObject schemas = convertConfigurationsToSchemas(reader, localization, id, title, description, split);

			// Afficher le JSON Schema formaté
			if (print) {
				print(schemas);
			}
			return schemas;
		} catch (IOException e) {
			System.err.println("Erreur de lecture du fichier : " + e.getMessage());
		} catch (IllegalStateException e) {
			System.err.println("Erreur de structure du fichier : " + e.getMessage());
		}
		return null;
	}

	public static void print(JsonObject schemas) {
		System.out.println(gson.toJson(schemas));
	}

	public static JsonObject generateJsonWithDefault(JsonObject schema) {
		JsonObject jsonWithDefault = new JsonObject();
		generateJsonWithDefault(jsonWithDefault, schema);
		return jsonWithDefault;
	}

	private static void generateJsonWithDefault(JsonObject jsonWithDefault, JsonObject root) {
		Set<Entry<String, JsonElement>> entrySet = root.get("properties").getAsJsonObject().entrySet();
		for (Entry<String, JsonElement> entry : entrySet) {
			JsonObject value = entry.getValue().getAsJsonObject();
			if (value.has("deprecationMessage")) {
				continue;
			}
			if (value.has("default")) {
				jsonWithDefault.add(entry.getKey(), value.get("default"));
			} else if (value.has("properties")) {
				JsonObject parent = new JsonObject();
				jsonWithDefault.add(entry.getKey(), parent);
				generateJsonWithDefault(parent, value);
			}
		}
	}
	
	public static JsonObject loadLocalization(InputStream inputStream) throws IOException {
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

}
