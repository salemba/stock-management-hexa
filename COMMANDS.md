# Commandes Utiles - Projet Gestion de Stock Hexagonal

Ce fichier rassemble l'ensemble des commandes nécessaires pour la compilation, le test, le déploiement sur OpenShift et les tests HTTP via `curl`.

---

## 1. Compilation & Tests Locaux (Maven)

```bash
# Compilation et génération du code à partir du contrat OpenAPI (stock-api.yaml)
mvn compile

# Exécution des tests unitaires du Domaine pur Java
mvn test -Dtest=StockManagementServiceTest

# Packaging complet de l'application (génère le fichier JAR exécutable)
mvn clean package -DskipTests
```

> **Note sous Windows :** Si `mvn` n'est pas dans le `PATH` Bash, utilisez PowerShell :
> `powershell -Command "mvn clean package -DskipTests"`

---

## 2. Déploiement & Gestion OpenShift (`oc`)

### A. Connexion et Projets
```bash
# Se connecter à OpenShift Sandbox (récupérer le token depuis la console Web OpenShift)
oc login --token=sha256~XXXXX --server=https://api.sandbox-xxxxx.openshiftapps.com:6443

# Sélectionner son namespace
oc project <nom-de-votre-namespace>
```

### B. Déploiement des Composants Infrastructure
```bash
# Déployer PostgreSQL (PVC + Pod + Service)
oc apply -f deploy/openshift/postgres-pod.yaml

# Déployer HashiCorp Vault (Pod + Config v2 KV)
oc apply -f deploy/openshift/vault-pod.yaml

# Déployer la ConfigMap de l'application
oc apply -f deploy/openshift/configmap.yaml

# Appliquer le Service et la Route de l'application
oc apply -f deploy/openshift/service.yaml
oc apply -f deploy/openshift/route.yaml
```

### C. Construction et Publication de l'Image Applicative (S2I)
```bash
# 1. Créer le BuildConfig binaire (à exécuter une seule fois)
oc new-build registry.access.redhat.com/ubi9/openjdk-21:latest --binary=true --name=stock-management-hexa

# 2. Envoyer le fichier JAR compilé pour construire l'image conteneur
oc start-build stock-management-hexa --from-file=target/stock-management-hexa-1.0.0-SNAPSHOT.jar --follow

# 3. Associer le déploiement à la nouvelle image
oc set image deployment/stock-management-hexa stock-management=stock-management-hexa:latest
```

### D. Commandes Utiles de Supervision & Diagnostic
```bash
# Lister l'état de tous les pods
oc get pods -w

# Consulter les logs d'un déploiement
oc logs deployment/stock-vault
oc logs deployment/stock-management-hexa

# Redémarrer un déploiement (Rollout Restart)
oc rollout restart deployment/stock-vault
oc rollout restart deployment/stock-management-hexa

# Consulter les secrets directement dans Vault
oc exec deployment/stock-vault -- sh -c "VAULT_ADDR=http://127.0.0.1:8200 VAULT_TOKEN=root vault kv get secret/stock-management"
```

### E. Nettoyage Complet
```bash
# Supprimer tous les composants déployés
oc delete -f deploy/openshift/

# Vérifier qu'il ne reste aucun composant
oc get all
```

---

## 3. Tests des Endpoints REST (`curl`)

### Récupération de l'URL publique de la Route :
* **Bash / Git Bash :**
  ```bash
  export APP_URL="https://$(oc get route stock-management-route -o jsonpath='{.spec.host}')"
  ```
* **PowerShell :**
  ```powershell
  $APP_URL = "https://" + (oc get route stock-management-route -o jsonpath='{.spec.host}')
  ```

### Recommandations de tests :

```bash
# 1. Santé de l'application (Actuator)
curl -i -k $APP_URL/actuator/health

# 2. Lister tous les produits
curl -i -k $APP_URL/api/v1/stocks

# 3. Créer un nouveau produit
curl -i -k -X POST $APP_URL/api/v1/stocks \
  -H "Content-Type: application/json" \
  -d '{
        "sku": "LAPTOP-01",
        "name": "MacBook Pro 16",
        "category": "ELECTRONICS",
        "unitPrice": 2499.99,
        "quantity": 10,
        "reorderThreshold": 3
      }'

# 4. Consulter un produit par SKU
curl -i -k $APP_URL/api/v1/stocks/LAPTOP-01

# 5. Entrée de stock (Approvisionnement)
curl -i -k -X POST $APP_URL/api/v1/stocks/LAPTOP-01/add \
  -H "Content-Type: application/json" \
  -d '{
        "quantity": 5,
        "reason": "Livraison fournisseur"
      }'

# 6. Sortie de stock (Vente)
curl -i -k -X POST $APP_URL/api/v1/stocks/LAPTOP-01/deduct \
  -H "Content-Type: application/json" \
  -d '{
        "quantity": 13,
        "reason": "Vente client"
      }'

# 7. Test de validation métier (Tentative de stock négatif - Doit renvoyer HTTP 400)
curl -i -k -X POST $APP_URL/api/v1/stocks/LAPTOP-01/deduct \
  -H "Content-Type: application/json" \
  -d '{
        "quantity": 10,
        "reason": "Vente excessive"
      }'
```
