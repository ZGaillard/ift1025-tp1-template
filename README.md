# TP1 – Simulation d’un Écosystème

## Objectifs pédagogiques
Ce projet vise à vous familiariser avec :
- Les principes de la **programmation orientée objet** (héritage, polymorphisme, encapsulation).  
- La conception par **interfaces de comportements** pour favoriser la composition.  
- L’architecture en couches selon le modèle **MVC** (*Model – View – Controller*).  
- L’utilisation d’un **générateur aléatoire déterministe** pour garantir la reproductibilité.  
- La gestion d’un projet collaboratif avec **GitHub**.  

---

## Description
Le projet consiste à implémenter une simulation d’écosystème sur une grille 2D (`width × height`).  
Chaque cellule peut contenir :
- une **plante**,  
- un **animal herbivore**,  
- et/ou un **animal carnivore**.  

La simulation évolue **tour par tour** selon un enchaînement strict de phases :  
1. Croissance des plantes  
2. Phase des herbivores (fuite, mouvement, consommation, coût énergétique)  
3. Phase des carnivores (chasse, mouvement, consommation, coût énergétique)  
4. Reproduction (plantes puis animaux)  
5. Nettoyage (suppression des morts)  

Toutes les règles de logique, d’énergie et d’ordre d’exécution doivent être respectées à la lettre.  
L’ensemble des décisions aléatoires (mouvements, choix de proie ou de case de reproduction) doit utiliser exclusivement la classe `RandomGenerator`, afin d’assurer des résultats reproductibles entre les tests publics et cachés.  

Une interface graphique (`GUI`) est fournie pour visualiser l’évolution du monde, mais la logique se trouve entièrement dans le modèle et le contrôleur.

---

## Structure du projet
```
src/
├── Main.java
│
├── prof/
│   ├── test/
│   │   ├── hidden/
│   │   ├── open/
│   │   │   ├── Autograder.java
│   │   │   ├── CarnivoreTest.java
│   │   │   ├── ControllerPhasesTest.java
│   │   │   ├── HerbivoreTest.java
│   │   │   ├── IntegrationTest.java
│   │   │   ├── Phase1Test.java
│   │   │   ├── Phase2Test.java
│   │   │   ├── Phase3Test.java
│   │   │   ├── Phase4Test.java
│   │   │   ├── Phase5Test.java
│   │   │   └── PlantTest.java
│   │   └── RandomGeneratorTest.java
│   │
│   ├── utils/
│   │   ├── RandomGenerator.java
│   │   └── WorldLoader.java
│   │
│   └── view/
│       ├── ControlPanel.java
│       ├── GridPanel.java
│       ├── GUI.java
│       ├── LoggerPanel.java
│       └── StatusBar.java
│
└── student/
    ├── controller/
    │   └── SimulationController.java
    │
    └── model/
        ├── behaviors/
        │   ├── Eater.java
        │   ├── Edible.java
        │   ├── Energetic.java
        │   ├── Fleeing.java
        │   ├── Growable.java
        │   ├── Hunting.java
        │   ├── Movable.java
        │   ├── Perceptive.java
        │   └── Reproducible.java
        │
        ├── core/
        │   ├── Cell.java
        │   ├── Position.java
        │   └── World.java
        │
        └── organisms/
            ├── Animal.java
            ├── Carnivore.java
            ├── Herbivore.java
            ├── Organism.java
            └── Plant.java
```

---

## Règles de la simulation

### Grille et positions
- Le monde est une grille `width × height` de cellules.  
- Chaque position `(x, y)` doit être dans les bornes (x ≥ 0, y ≥ 0).  
- Les déplacements et créations d’organismes hors bornes sont ignorés.  
- Les animaux se déplacent uniquement dans les directions **cardinales** (N, S, E, O).  

### Cycle complet d’un tour
1. Croissance des plantes  
2. Phase des herbivores (fuite, mouvement, consommation, coût énergétique)  
3. Phase des carnivores (chasse, mouvement, consommation, coût énergétique)  
4. Reproduction (plantes puis animaux)  
5. Nettoyage (suppression des morts)  

Aucune autre phase n’est exécutée. Les coûts énergétiques sont intégrés directement dans les phases de mouvement et de chasse.

---

### Plantes
- Énergie minimale 1, maximale 3.  
- Croissance : si vivante (`énergie > 0`) et énergie < 3 → +1 énergie.  
- Mort : une plante morte (énergie ≤ 0) ne repousse pas.  
- Consommation : lorsqu’un herbivore entre sur la case d’une plante, il gagne exactement l’énergie de cette plante, puis la plante disparaît.  
- Reproduction :  
  - Condition : énergie = 3 et au moins une case voisine libre (diagonale ou cardinale).  
  - Placement : dans une case cardinale libre uniquement.  
  - Si réussite : l’enfant a énergie = 1 et le parent revient à 1.  
  - Si échec : le parent reste à 3.  

---

### Herbivores
- Énergie initiale plafonnée à 10.  
- Énergie maximale : 10.  
- Vision : carré 3×3 (portée logique 2, diagonales incluses).  
- À chaque phase : coût énergétique −1, même en cas d’immobilité.  
- Comportement :
  1. **Fuite** : si un carnivore est visible, choisir la case cardinale libre qui maximise la distance Manhattan.  
  2. **Nourriture** : chercher les plantes visibles, choisir celle d’énergie maximale.  
     - Si elle est adjacente : se déplacer dessus et la manger.  
     - Sinon : mouvement aléatoire cardinal.  
  3. **Blocage** : si aucune case libre, rester sur place.  
- Reproduction :  
  - Condition : énergie ≥ 10 et au moins une case cardinale libre.  
  - Spawn : nouvel herbivore énergie = 3.  
  - Coût parent : énergie divisée par 2 (plancher).  
- Mort : énergie ≤ 0 → retiré en phase de nettoyage.  

---

### Carnivores
- Énergie maximale : 20.  
- Vision : carré 5×5 (portée logique 3).  
- Coût énergétique : −1 par tour.  
- Chasse :
  1. Rechercher les herbivores visibles.  
  2. Cibler la proie la plus proche (distance Manhattan minimale).  
  3. Si plusieurs : choix aléatoire via `RandomGenerator`.  
  4. Se déplacer d’une case cardinale vers la proie.  
  5. Si elle est adjacente : se déplacer sur elle et la consommer.  
  6. Sinon, mouvement aléatoire cardinal.  
- Reproduction :  
  - Condition : énergie ≥ 14 et une case cardinale libre.  
  - Spawn : nouvel enfant énergie = 5.  
  - Coût parent : énergie divisée par 2 (plancher).  
- Mort : énergie ≤ 0 → retiré en phase de nettoyage.  

---

### Nettoyage
- Supprime toutes les plantes, herbivores et carnivores morts (`énergie ≤ 0`).  
- Ne modifie aucun autre état.  
- Idempotent : plusieurs nettoyages successifs ne changent rien.  

---

### Aléatoire et déterminisme
- Toutes les décisions aléatoires (déplacement, choix de proie, case de reproduction, etc.) doivent passer par `RandomGenerator`.  
- Les tests utilisent `RandomGenerator.reseed(seed)` pour vérifier la reproductibilité.  
- N’effectuer un tirage que lorsqu’un choix multiple existe réellement.  

---

## Tableau récapitulatif des paramètres

| Catégorie  | Énergie min / max | Seuil de repro | Énergie de l’enfant | Vision (rayon logique) | Mouvement | Coût/tour | Règle de gain | Règle de perte | Notes |
|-------------|------------------|----------------|----------------------|------------------------|------------|------------|----------------|----------------|-------|
| **Plante** | 1–3 | =3 | 1 | — | — | — | Croissance +1 si <3 | Mangée → retirée | Spawn seulement sur case cardinale libre |
| **Herbivore** | 0–10 | ≥10 | 3 | 2 (3×3) | Cardinal | −1 | Gagne énergie de la plante (clamp à 10) | Meurt si énergie ≤0 | Fuit carnivores visibles |
| **Carnivore** | 0–20 | ≥14 | 5 | 3 (5×5) | Cardinal | −1 | Gagne énergie de la proie (clamp à 20) | Meurt si énergie ≤0 | Cible la proie la plus proche |

---

## Évaluation

### 1. Tests automatiques – 70%
- Passage des tests publics : 50 %  
- Passage des tests cachés : 20 %  

### 2. Qualité du code – 15%
- Utilisation adéquate de la POO et des interfaces comportementales.  
- Lisibilité, structure et commentaires pertinents.  

### 3. Rapport écrit – 15%
- Document de 3 à 4 pages maximum, incluant :
  - vos choix de conception,  
  - les difficultés rencontrées et solutions,  
  - la répartition du travail.  

### Bonus +5 %
- Utilisation rigoureuse de Git et GitHub.  

---

## Contraintes
- Travail en équipe de deux personnes.  
- Dépôt privé GitHub, avec ajout du coéquipier et de l’enseignant (`Zgaillard`).  
- Respect strict des signatures de méthodes et des comportements décrits.  
- Toute source d’aléatoire doit passer par `RandomGenerator`.  

---


## Remise
- Date limite : 2 novembre 2024, 23h59.
- Ne pas faire de commit après la date limite.

---

## Auteurs
- Équipe : *à compléter*  
- Encadrant : Z. Gaillard-Duchassin  
