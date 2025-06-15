# ☕ Coffee Shop - Application Android

Cette application Android permet de gérer un coffee shop avec un système complet : gestion des produits, authentification, commandes clients, et même une roue de la chance pour fidéliser les utilisateurs.

## 🔐 Authentification

- Inscription avec vérification par e-mail
- Connexion sécurisée avec mémorisation des identifiants
- Réinitialisation de mot de passe
- Gestion des rôles : **administrateur** et **client**

## 🧑‍💼 Espace administrateur

- Ajout, modification et suppression de cafés
- Gestion des stocks : sucre, gobelets
- Téléversement d’images pour chaque produit
- Visualisation de tous les articles disponibles

## 🛍️ Expérience client

- Parcourir les produits disponibles
- Personnaliser la commande avec les options
- Ajout au panier et confirmation de commande
- Choix du moyen de paiement
- Jeu bonus : roue de la chance pour gagner des récompenses

## 🔧 Configuration requise

- Android Studio à jour
- Appareil ou émulateur Android (API 24 minimum)
- Projet Firebase configuré

## ▶️ Installation

```bash
git clone https://github.com/ton-utilisateur/ApplicationAndroid.git

1.Ouvrir le projet dans Android Studio

2.Ajouter le fichier google-services.json dans le dossier app/

3.Connecter l'app à Firebase

4.Lancer l'application

🗂️ Structure du projet
| Activité / Classe                            | Rôle                 |
| -------------------------------------------- | -------------------- |
| `WelcomeActivity`                            | Page d’accueil       |
| `LoginActivity` / `RegisterActivity`         | Authentification     |
| `AdminActivity`                              | Interface admin      |
| `CustomerActivity`                           | Parcours client      |
| `CartActivity`                               | Panier               |
| `CoffeeManageActivity`                       | Gestion des produits |
| `SugarManageActivity` / `CupsManageActivity` | Gestion des options  |

Modèles
UserModel, CoffeeModel, SugarModel, CupsModel

Firebase
Auth : pour la connexion

Firestore : pour les données

Storage : pour les images

🧪 Collections Firebase
À créer dans Firestore :

coffeeTable

sugarOptions

CupsTable

users
📚 Librairies utilisées
Firebase Auth

Firebase Firestore & Storage

Glide

AndroidX

📄 Licence
Ce projet est sous licence MIT.
Voir le fichier LICENSE pour plus de détails.


