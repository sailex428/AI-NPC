# AI-NPC Launcher

AI-NPC Launcher is a Minecraft mod that launches and controls the AI-NPC client, enabling you to create and interact with NPCs that can move, mine, and chat with players in the game. This mod serves as the launcher for the [AI-NPC](https://github.com/sailex428/AI-NPC) client, which powers the actual NPC functionality. Currently, the AI-NPC's capabilities include basic movement, mining actions, and chatting with players.

> **Note**: This project is under active development. Future updates will expand the NPCs capabilities.

## Requirements

- **Minecraft Version**: 1.20.4 (support for newer minecraft versions coming soon)
- **Dependencies**: [FabricAPI](https://github.com/FabricMC/fabric) and java 17

## Installation

1. **Download the Mod**:
    - Get the latest version of the `AI-NPC Launcher` mod from the [Releases](https://github.com/sailex428/AI-NPC-Launcher/releases) or [Modrinth](https://modrinth.com/project/ai-npc) page.

2. **Install the Mod**:
    - Place `AI-NPC Launcher` in your `mods` folder.

3. **Launch Minecraft**:
    - Start Minecraft client with the 1.20.4 version to load the mod.

## Usage
**(at the moment is an openai API key needed to use the mod)**

1. **Set Configuration**: 
    - Use the `/setconfig <propertyKey> <propertyValue>` command to set the properties. (Example: `/setconfig npc.llm.openai.api_key sk-proj-XYZ...`)

2. **Spawn NPCs**:
    - Use the `/npc add <npcname> <isOffline> <openai|ollama> <llm model>` command to create an NPC. (Example: `/npc add sailex428 true openai gpt-4o-mini`) (at this point only openai is supported)
    - (v1.0.1-alpha) The client will auto connect to localhost:25565

3. **Remove NPCs**:
    - Use the `/npc remove <npcname>` command to remove an NPC from the game world.

4. **Interact with NPCs**:
    - Just write in the chat to interact with the NPCs.

## Development Status

This project is under development, and additional features will be released over time. 
Upcoming features may include combat skills and more interactions like crafting.

## Contributing

Contributions are welcome! Please check the Issues tab for open tasks or create a new issue if you encounter any bugs. 
Feel free to submit pull requests with improvements, bug fixes, or feature additions.

## License

This project is licensed under the [LGPL-3.0](LICENSE.md).
