# AI NPCs

AI-NPC Launcher is a Minecraft mod that launches and controls the AI-NPC client, enabling you to create and interact with NPCs that can move, mine, and chat with players in the game. This mod serves as the launcher for the [AI-NPC](https://github.com/sailex428/AI-NPC) client, which powers the actual NPC functionality. Currently, the AI-NPC's capabilities include basic movement, mining actions, and chatting with players.

![](https://cdn.modrinth.com/data/cached_images/9a70948639591c9d03b9f7695ec09d336572b522.png)
![](https://cdn.modrinth.com/data/cached_images/a126513c98bbc01e289307466e5d065acfb21e59.png)

> **Note**: This project is under active development. Future updates will expand the NPCs capabilities.

## Requirements

- **Minecraft Version**: 1.20.4 (support for newer minecraft versions coming soon)
- **Dependencies**: [FabricAPI](https://github.com/FabricMC/fabric) and java 17
- **Running Ollama server or an openAi api-key** (instructions are below)

## Installation

1. **Download the Mod**:
   - Get the latest version of the `AI-NPC Launcher` mod from the [Releases](https://github.com/sailex428/AI-NPC-Launcher/releases) or [Modrinth](https://modrinth.com/project/ai-npc) page.

2. **Install the Mod**:
   - Place `AI-NPC Launcher` in your `mods` folder on your minecraft fabric server (from version 1.0.4).
   - If you wanna use this in a singleplayer world, you should use the open to lan option and set the port to the default, 25565.(use the [custom-lan](https://modrinth.com/mod/custom-lan)) to set the integrated server to offline

3. **Launch Minecraft**:
   - Start a Minecraft client and connect to the server and youre ready!

## Usage
(Player must be operator to execute these commands)

1. **Set Configuration**:
   - Use the `/setconfig <propertyKey> <propertyValue>` command to set the properties. (Example: `/setconfig npc.llm.openai.api_key sk-proj-XYZ...`)

2. **Spawn NPCs**:
   - Use the `/npc add <npcname> <isOffline> <openai|ollama> <llm model>` command to create an NPC. (Example: `/npc add sailex428 true openai gpt-4o-mini`) (at this point only openai is supported)

3. **Remove NPCs**:
   - Use the `/npc remove <npcname>` command to remove an NPC from the game world.

4. **Interact with NPCs**:
   - Just write in the chat to interact with the NPCs.

## Setting Up LLM Integration

### **Option 1: OpenAI API Key (Paid)**

1. **Create an Account**:
   - Sign up or log in to [OpenAi](https://platform.openai.com/signup)

2. **Purchase Credits**:
   - Navigate to [Billing](https://platform.openai.com/settings/organization/billing/overview)
   - Add a payment method and add credits to your balance

3. **Generate an API Key**:
   - Navigate to [api-keys](https://platform.openai.com/settings/organization/api-keys)
   - Click on the top right on "Create new secret key"
   - Copy the api-key

5. **Add the API Key to the Mod**:
   - Use `/setconfig npc.llm.openai.api_key <your_api_key>` in-game to set the key.

### **Option 2: Ollama (Local LLM)**

#### What is Ollama?
Ollama is a local LLM platform for running AI models directly on your machine, reducing reliance on external APIs.

#### Installation Steps
1. **Download Ollama**:
   - Visit [Ollamas Website](https://ollama.com/) and download the installer for your operating system.

2. **Install and Run Ollama**:
   - Follow the setup instructions to install.
   - Download a model (i recommend the gemma2 model)
   - Start Ollama and ensure it's running in the background.

3. **Connect to the Mod**:
   - Use the `/npc add` command with `ollama` as the model type.
   - Example: `/npc add npcname true ollama gemma2`
   - if your server runs on a diffrent address you can set that via `/setconfig npc.llm.ollama.url <youre server address>`.

## Development Status

This project is under development, and additional features will be released over time.
Upcoming features may include combat skills and more interactions like crafting.

## License

This project is licensed under the [LGPL-3.0](LICENSE.md).

## Disclaimer
[DISCLAIMER](https://github.com/sailex428/AI-NPC-Launcher/blob/main/DISCLAIMER.md)
