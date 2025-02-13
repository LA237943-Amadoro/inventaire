package be.helha.avancee;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MongoDBInsert {
    public static void main(String[] args) {
        // Connexion à MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("jeu");

        // Sélection des collections
        MongoCollection<Document> personnageCollection = database.getCollection("personnages");
        MongoCollection<Document> inventaireCollection = database.getCollection("inventaire");
        MongoCollection<Document> objetsCollection = database.getCollection("objets");

        try {
            // Charger le fichier JSON
            String jsonData = new String(Files.readAllBytes(Paths.get("script.json")));
            JSONObject jsonObject = new JSONObject(jsonData);

            // Insertion de l'inventaire (sans _id, il sera généré automatiquement)
            JSONObject inventaireJson = jsonObject.getJSONObject("inventaire");
            Document inventaire = new Document()
                    .append("emplacements", new ArrayList<>(Collections.nCopies(10, null)));

            inventaireCollection.insertOne(inventaire);
            ObjectId inventaireId = inventaire.getObjectId("_id"); // Récupérer l'ID généré
            System.out.println("✅ Inventaire inséré avec ID: " + inventaireId);

            // Insertion du personnage avec l'ID d'inventaire
            JSONObject personnageJson = jsonObject.getJSONObject("personnage");
            Document personnage = new Document()
                    .append("nom", personnageJson.getString("nom"))
                    .append("niveau", personnageJson.getInt("niveau"))
                    .append("classe", personnageJson.getString("classe"))
                    .append("inventaire_id", inventaireId); // Lier l'inventaire au personnage

            personnageCollection.insertOne(personnage);
            System.out.println("✅ Personnage inséré avec inventaire ID: " + inventaireId);

            // Insertion des objets (sans _id, MongoDB les génère)
            JSONArray objetsArray = jsonObject.getJSONArray("objets");
            List<Document> objetsList = new ArrayList<>();

            for (int i = 0; i < objetsArray.length(); i++) {
                JSONObject objJson = objetsArray.getJSONObject(i);
                Document document = new Document()
                        .append("nom", objJson.getString("nom"))
                        .append("type", objJson.getString("type"));

                if (objJson.has("quantité_max")) {
                    document.append("quantité_max", objJson.getInt("quantité_max"));
                }

                if (objJson.has("contenu_max")) {
                    document.append("contenu_max", objJson.getInt("contenu_max"))
                            .append("contenu", new ArrayList<>());
                }

                objetsList.add(document);
            }

            objetsCollection.insertMany(objetsList);
            System.out.println("✅ Objets insérés !");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }
}
