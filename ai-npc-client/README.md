# AI-NPC

**AI-NPC** is a Minecraft mod that introduces NPCs (non-player characters) powered by artificial intelligence. These NPCs are designed to interact with the player and their environment, capable of basic movements, mining, and real-time chat.

> **Note**: While the AI-NPC Launcher mod is not required, it simplifies the process of launching and managing NPC instances. You can find the launcher here: ([AI NPC Launcher](https://github.com/sailex428/AI-NPC-Launcher))

## Requirements

- **Minecraft Version**: 1.20.4 (support for newer minecraft versions coming soon)
- **Dependencies**: java 17 (fabric API is included in the mod)

## Installation

1. **Download the Mod**:
    - Download the latest release of the **AI-NPC** mod from the [Releases page](https://github.com/sailex428/AI-NPC/releases).

2. **Install the Mod**:
    - Place both mods AI-NPC into your Minecraft `mods` folder.

3. **Launch Minecraft Client**:
    - Start the client with these jvm args: `-Dnpc.llm.openai.model=gpt-4o-mini` `-Dnpc.llm.type=openai` `-Dnpc.llm.openai.api_key=sk-proj-XYZ...`
    - Set a username by using `--username <youre npc name>`
    - (v1.0.1-alpha) The client will auto connect to localhost:25565

## Development Status

This project is under development, and additional features will be released over time.
Upcoming features may include combat skills and more interactions like crafting.

## Contributing

Contributions are welcome! Please check the Issues tab for open tasks or create a new issue if you encounter any bugs.
Feel free to submit pull requests with improvements, bug fixes, or feature additions.

## License

This project is licensed under the [LGPL-3.0](LICENSE.md).
