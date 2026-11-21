# Block Sound Changer (Fabric, Minecraft 1.21.4)

Öffne mit **B** (änderbar unter Optionen > Steuerung > Tastenbelegung > "Block Sound Changer")
ein Menü mit allen Blöcken. Klicke einen Block an – ab sofort klingt jedes Platzieren
(auch das anderer Spieler) wie das Platzieren dieses Blocks. "Zurücksetzen" stellt die normalen Sounds wieder her.
Die Auswahl wird in `config/blocksound.properties` gespeichert.

## Bauen
Voraussetzung: JDK 21.
1. Im Projektordner `gradle wrapper --gradle-version 8.12` ausführen (oder das Projekt in IntelliJ importieren).
2. `./gradlew build`
3. Die Mod liegt danach in `build/libs/blocksound-1.0.0.jar` -> in den `mods`-Ordner legen
   (benötigt Fabric Loader + Fabric API für 1.21.4).
