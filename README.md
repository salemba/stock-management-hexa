# Projet Gestion de Stock - Architecture Hexagonale

## Présentation

Ce projet est un microservice Java Spring Boot de **Gestion de Stock** conçu selon les principes de l'**Architecture Hexagonale (Ports et Adaptateurs)**, ciblant un déploiement sur **OpenShift Developer Sandbox** avec persistance **PostgreSQL** et gestion des secrets par **HashiCorp Vault**.

---

## Documentation d'Architecture (ADR)

Pour retrouver le détail de la conception, le schéma d'architecture complet ainsi que le suivi des choix techniques, veuillez consulter le registre d'architecture :

* 📄 [ADR 0001 : Mise en œuvre de l'Architecture Hexagonale](docs/adr/0001-hexagonal-architecture-domain-isolation.md)

---

## Structure des Packages

```text
com.company.stock
├── domain/                  # Cœur Métier Pur Java (Modèles, Services, Exceptions)
├── ports/                   # Interfaces (Inbound UseCases, Outbound Repositories/Notifiers)
├── adapters/                # Adaptateurs Infrastructure (REST OpenAPI, JPA PostgreSQL, Vault)
└── infrastructure/          # Configuration Spring, Sécurité & Beans Domaine
```

---

## Démarrage Rapide

### Exécution des Tests Métier (Domaine Pur)
```bash
mvn test
```

### Compilation et Déploiement OpenShift
```bash
# Compilation et génération des contrats OpenAPI
mvn compile

# Déploiement des manifestes sur OpenShift
oc apply -f deploy/openshift/postgres-pod.yaml
oc apply -f deploy/openshift/configmap.yaml
oc apply -f deploy/openshift/deployment.yaml
oc apply -f deploy/openshift/service.yaml
oc apply -f deploy/openshift/route.yaml
```
