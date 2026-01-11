# CampusScheduler – TimeEdit till Canvas

CampusScheduler är en Spring Boot–baserad webbapplikation som möjliggör import av scheman från TimeEdit och export av dessa som kalenderhändelser till Canvas LMS. Applikationen är framtagen som en proof-of-concept inom kursen **Enterprise Architecture och SOA** och fokuserar på integration mellan externa system via REST-API:er.

## Funktionalitet

- Import av scheman från TimeEdit via publik URL
- Automatisk omvandling från TimeEdit HTML-länk till JSON-endpoint
- Server-side rendering (SSR) med Spring MVC och Thymeleaf
- Granskning och redigering av importerade händelser innan export
- Export av kalenderhändelser till Canvas:
  - Kurskalender (course_<id>)
  - Personlig kalender (user_<id>)
- Robust felhantering vid:
  - Ogiltiga TimeEdit-länkar
  - HTML istället för JSON
  - Canvas API-fel (401/403/400)
- Stöd för tidszon (Europe/Stockholm)

## Teknisk stack

- Java 17
- Spring Boot
- Spring MVC
- Thymeleaf
- Spring WebClient
- Jackson (ObjectMapper)
- Maven

## Förutsättningar

- Java 17 installerat
- Maven (eller Maven Wrapper)
- Ett giltigt Canvas API-token

## Konfiguration

Applikationen använder **environment variables** för känslig konfiguration.

### Obligatoriska variabler

```bash
CANVAS_BASE_URL=https://canvas.se
CANVAS_TOKEN=<ditt-canvas-token>
```

**Alternativt skap temporära variabler i terminalen med:**
```bash
$env:CANVAS_BASE_URL="https://canvas.ltu.se"
$env:CANVAS_TOKEN="DITT_TOKEN_HÄR"
```

## Starta applikationen

```bash
./mvnw spring-boot:run
```
Applikationen startar på:
```bash
http://localhost:8080
```

## Användning

### Importera schema från TimeEdit
1. Klistra in en TimeEdit-schema-URL (HTML-länk)
2. Ange Canvas context code (t.ex. course_1234) om du vill exportera till en specifik kurs
3. Klicka på Importera

Applikationen hämtar JSON-versionen av schemat och visar en förhandsgranskning.

### Granska och redigera
- Kontrollera titel, tider, plats och beskrivning
- Alla fält är redigerbara innan export

### Exportera till Canvas

Välj om export ska ske till kurskalender eller din personliga Canvas-kalender (rekommenderas om du saknar lärarbehörighet)

**Klicka på Exportera**

Resultatsidan visar status för varje händelse:

- SUCCESS: Händelsen skapades i Canvas
- FAIL: Ett tydligt felmeddelande visas