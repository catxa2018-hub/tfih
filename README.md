# tfih


A hypixel <span style="font-size:7px">~~macro~~</span> improvement mod for Hypixel Skyblock.

### Feature list
- **Fishing**: Automatically reel and throw bobber; periodically checks for a missing bobber near water or lava and casts when a fishing rod is already selected
- **Umberella**: Periodically jump, use a hotbar item whose name or lore contains "Umberella", then cast the fishing rod

### Configuration

Use `/tfih` or `/tfih view` in chat to view the current fishing settings. Change settings with:

- `/tfih set enabled <true|false>`
- `/tfih set jump <true|false>`
- `/tfih set bobber-check <seconds>`
- `/tfih set distribution <0.0-1.0>`
- `/tfih set water <milliseconds>`
- `/tfih set lava <milliseconds>`
- `/tfih set cast <milliseconds>`
- `/tfih set umberella <true|false>`
- `/tfih testumb` to run the Umberella action once immediately

Delay values can be from 0 to 1000 milliseconds. Changes are saved to the mod configuration file automatically.
The bobber check interval accepts 5 to 3600 seconds and varies randomly by up to 5 seconds around the configured average.
When jump mode is enabled, the player jumps just before reeling in, with a short randomized pause before the reel.
When Umberella is enabled, the first jump is delayed by a random 4:45-4:55 interval. The feature requires a fishing rod in the selected hotbar slot and searches the hotbar for the Umberella item.

### Building it yourself

Build the mod locally to produce the JAR for your own use.

1. Either:
   1. Download this repository and extract the folder.
   2. Clone this repository using Git.
2. Open your command line inside the tfih directory.
3. Run `gradlew.bat :<version>:build` for Windows or `./gradlew :<version>:build` for Linux (if you get a permission missing error, run `chmod +x gradlew;`)
   - Replace `<version>` with a configured target such as `1.21.11`, `26.1`, or `26.2`.
4. After it's done running, the JARs will appear in `build/libs/`, which you can then copy to your mods directory
5. FOR NON DEVS ONLY: Delete any remenants after you're done. <br>
   Your home folder will contain a `.gradle`, most likely hidden, folder which should/could contain multiple hundreds of Megabytes. <br>
   You can safely delete it, but if you want to build this project again, it will have to download everything again. <br>
