# StandModelLib
Lightweight and Optimized ArmorStand Command parser and model spawner/manager for SpigotMC Minecraft server software.

<p align="center">
  <img src="stands.gif">
</p>

---
## Website Model Creator
Supports models generated from https://mrgarretto.com/armorstand/ <b>version 1.12</b> only

**Important Note:** When using the website - The following limitations apply:
* You may and only use "Medium" or "Large" pieces. That means NO part can be "Small" or "Solid" otherwise it will break.
* The command must be generated using the "Single" option.
* When loading from a config, ensure the command is on the same line and encapsulated in ' '
* Models must be centered on the website before being generated. Otherwise they will not spawn in centered.

## Features
* Ability to parse model creation commands (that normally go into command blocks) from v1.12 https://mrgarretto.com/armorstand/
* Model rotation around central position
* Highly Optimised:
  *  Render distance to remove un-needed ArmorStands
  *  Animation distance to only display animations when a player is near-by
  *  Player being within Render or Animation distance is updated asynchronous
* Custom Model Interaction event

## Example Usage
https://github.com/lewdev-uk/StandModelLib-Example

## Maven
Add this repository to you pom.xml:
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
Finally add this dependency:
```
<dependency>
    <groupId>com.github.lewysDavies</groupId>
    <artifactId>StandModelLib</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
## With Thanks
Massive thanks to Spigot user [RandomHashTags](https://www.spigotmc.org/members/randomhashtags.76364/) for the amazing resource [Universal Material](https://www.spigotmc.org/threads/universal-material-names.349115/)
