Dungeon game

A pixel art, 2D top down game with the main focus on exploration and fighting monsters in dungeons.
The main story will revolve around why the world is the way that it is.

IDE - Intelijay Community Edition
Java version - 17.0.15
JavaFX version - 17.0.15

JavaFX documentation: https://www.oracle.com/java/technologies/javase/javafx-docs.html

run with .\run.bat

PROJECT LAYOUT
src/game/       Java source (package "game"), entry point is game.Main
src/resources/  Sprites and UI images
src/maps/       Tile maps (.txt)
Load assets with Assets.image("resources/Name.png") / Assets.open("maps/name.txt")

RUNNING
Command line: .\run.bat  (expects the JavaFX SDK at %USERPROFILE%\Downloads\javafx-sdk-17.0.15,
              or set JAVAFX_HOME to wherever it is)
VS Code:      "Run Game" launch configuration
IntelliJ:     run game.Main with VM options
              --module-path <path-to-javafx-sdk>/lib --add-modules javafx.controls

Contributors: Evan Guard



FUTURE ADDITIONS
Save/Load
Quests
Quest Board
Gold
Houses
Sleep functionality
Map/Minimap

NPC:
Buy/sell items
Merchants
Apothicary

MOBS:
Slimes
    Blue
    Green
Spiders
Zombies
Golems
    Stone
    Iron
    Mythril
Witch
Evil Witch
Wizard
Evil Wizard
Vampire
Bat
Wearwolf
Wolf
Gargoyles
Salimander
Dragon
Hydra


ITEMS:
Arrow
Sword
    Great
    Long
    Short
Dagger
Bow
    Crossbow
    Recurve
Slingshot
Rock
Rope
Torch
Halberd
Spear
Pike
Potion - grants status effect
    Healing
    Stamina
Food - grants status effect
    Cheese wheel
    Sweet bun