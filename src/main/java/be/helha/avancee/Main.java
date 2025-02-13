package be.helha.avancee;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private static final MongoDatabase database = mongoClient.getDatabase("jeu");
    private static final MongoCollection<Document> personnageCollection = database.getCollection("personnages");
    private static final MongoCollection<Document> inventaireCollection = database.getCollection("inventaire");
    private static final MongoCollection<Document> objetsCollection = database.getCollection("objets");

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        while (true) {
            System.out.println("\n=== MENU CRUD ===");
            System.out.println("1️⃣ Ajouter un personnage");
            System.out.println("2️⃣ Ajouter un objet à l'inventaire d'un joueur");
            System.out.println("0️⃣ Quitter");
            System.out.print("Choix : ");

            int choix = scanner.nextInt();
            scanner.nextLine();  // Consommer la ligne restante

            switch (choix) {
                case 1 -> ajouterPersonnage();
                case 2 -> ajouterObjetAInventaire();
                case 0 -> {
                    System.out.println("👋 Au revoir !");
                    mongoClient.close();
                    return;
                }
                default -> System.out.println("❌ Option invalide !");
            }
        }
    }

    public static void ajouterPersonnage() {
        System.out.print("Entrez le nom du personnage : ");
        String nom = scanner.nextLine();

        System.out.print("Entrez le niveau du personnage : ");
        int niveau = scanner.nextInt();
        scanner.nextLine();  // Consommer la ligne restante

        System.out.print("Entrez la classe du personnage : ");
        String classe = scanner.nextLine();

        // Création de l'inventaire dans la collection "inventaire"
        Document inventaire = new Document()
                .append("emplacements", new ArrayList<>(Collections.nCopies(10, null)));

        inventaireCollection.insertOne(inventaire);
        ObjectId inventaireId = inventaire.getObjectId("_id"); // Récupérer l'ObjectId généré

        // Création du personnage avec la référence à l'inventaire
        Document personnage = new Document()
                .append("nom", nom)
                .append("niveau", niveau)
                .append("classe", classe)
                .append("inventaire_id", inventaireId);  // Lien vers l'inventaire

        personnageCollection.insertOne(personnage);
        System.out.println("✅ Personnage ajouté avec inventaire ID : " + inventaireId);
    }
    public static void ajouterObjetAInventaire() {
        // 1️⃣ Sélection du personnage
        System.out.println("\n📌 Liste des personnages :");
        FindIterable<Document> personnages = personnageCollection.find();
        List<ObjectId> personnagesIds = new ArrayList<>();
        int index = 1;

        for (Document personnage : personnages) {
            ObjectId id = personnage.getObjectId("_id");
            String nom = personnage.getString("nom");
            System.out.println(index + ". " + nom + " (ID: " + id + ")");
            personnagesIds.add(id);
            index++;
        }

        if (personnagesIds.isEmpty()) {
            System.out.println("❌ Aucun personnage trouvé !");
            return;
        }

        System.out.print("Sélectionnez un personnage (numéro) : ");
        int choixPerso = scanner.nextInt();
        scanner.nextLine();

        if (choixPerso < 1 || choixPerso > personnagesIds.size()) {
            System.out.println("❌ Sélection invalide !");
            return;
        }

        ObjectId personnageId = personnagesIds.get(choixPerso - 1);
        Document personnage = personnageCollection.find(Filters.eq("_id", personnageId)).first();
        if (personnage == null) {
            System.out.println("❌ Personnage introuvable !");
            return;
        }

        ObjectId inventaireId = personnage.getObjectId("inventaire_id");

        // 2️⃣ Sélection de l'objet
        System.out.println("\n📌 Liste des objets disponibles :");
        FindIterable<Document> objets = objetsCollection.find();
        List<ObjectId> objetsIds = new ArrayList<>();
        index = 1;

        for (Document objet : objets) {
            ObjectId id = objet.getObjectId("_id");
            String nom = objet.getString("nom");
            System.out.println(index + ". " + nom + " (ID: " + id + ")");
            objetsIds.add(id);
            index++;
        }

        if (objetsIds.isEmpty()) {
            System.out.println("❌ Aucun objet disponible !");
            return;
        }

        System.out.print("Sélectionnez un objet à ajouter (numéro) : ");
        int choixObjet = scanner.nextInt();
        scanner.nextLine();

        if (choixObjet < 1 || choixObjet > objetsIds.size()) {
            System.out.println("❌ Sélection invalide !");
            return;
        }

        ObjectId objetId = objetsIds.get(choixObjet - 1);

        // 3️⃣ Ajouter l'objet à un emplacement libre dans l'inventaire
        Document inventaire = inventaireCollection.find(Filters.eq("_id", inventaireId)).first();
        if (inventaire == null) {
            System.out.println("❌ Inventaire introuvable !");
            return;
        }

        List<Object> emplacements = (List<Object>) inventaire.get("emplacements");

        int emplacementLibre = -1;
        for (int i = 0; i < emplacements.size(); i++) {
            if (emplacements.get(i) == null) {
                emplacementLibre = i;
                break;
            }
        }

        if (emplacementLibre == -1) {
            System.out.println("❌ Inventaire plein !");
            return;
        }

        emplacements.set(emplacementLibre, objetId);

        // Mise à jour de l'inventaire dans la base
        inventaireCollection.updateOne(
                Filters.eq("_id", inventaireId),
                new Document("$set", new Document("emplacements", emplacements))
        );

        System.out.println("✅ Objet ajouté à l'inventaire du personnage !");
    }
}
