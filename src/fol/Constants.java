package fol;

import java.util.UUID;
import java.util.WeakHashMap;

public class Constants {


	private WeakHashMap<UUID, Constant> constants = new WeakHashMap<>();
	
	public Constants() {
	}
	

	
	public Constant get(UUID key) {
		return constants.computeIfAbsent(key, (k)->{return new Constant(k);});
	}

	public static class Constant {
		private final UUID uuid;
		String label;
		

		private Constant(UUID uuid) {
			this.uuid = uuid;
		}

		public UUID getUUID() {
			return uuid;
		}
		
		public String getLabel() {
			return label;
		}
		public Constant setLabel(String l) {
			this.label = l;
			return this;
		}
	}

}
