package io.sailex.aiNpcLauncher.launcher;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ClientProcessManager {

	private final Map<String, Process> npcClientProcesses;

	public ClientProcessManager() {
		this.npcClientProcesses = new HashMap<>();
	}

	public void addProcess(String npcName, Process process) {
		npcClientProcesses.put(npcName, process);
	}

	public void endProcess(String npcName) {
		Process process = npcClientProcesses.get(npcName);
		if (process != null) {
			process.destroy();
			npcClientProcesses.remove(npcName);
		}
	}
}
