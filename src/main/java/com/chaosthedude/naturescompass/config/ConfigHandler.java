package com.chaosthedude.naturescompass.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {
	
	private static final ForgeConfigSpec.Builder GENERAL_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
	
    public static final General GENERAL = new General(GENERAL_BUILDER);
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    
    public static final ForgeConfigSpec GENERAL_SPEC = GENERAL_BUILDER.build();
    public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    public static class General {
        public final ForgeConfigSpec.BooleanValue allowTeleport;
        public final ForgeConfigSpec.IntValue distanceModifier;
        public final ForgeConfigSpec.IntValue sampleSpaceModifier;
        public final ForgeConfigSpec.ConfigValue<List<String>> biomeBlacklist;
        public final ForgeConfigSpec.IntValue maxSamples;

        General(ForgeConfigSpec.Builder builder) {
        	String desc;
            builder.push("General");
            
            desc = "Allows a player to teleport to a located biome when in creative mode, opped, or in cheat mode.";
            allowTeleport = builder.comment(desc).define("allowTeleport", true);

            desc = "biomeSize * distanceModifier = maxSearchDistance. Raising this value will increase search accuracy but will potentially make the process more resource intensive.";
    		distanceModifier = builder.comment(desc).defineInRange("distanceModifier", 2500, 0, 1000000);

    		desc = "biomeSize * sampleSpaceModifier = sampleSpace. Lowering this value will increase search accuracy but will make the process more resource intensive.";
    		sampleSpaceModifier = builder.comment(desc).defineInRange("sampleSpaceModifier", 16, 0, 1000000);

    		desc = "A list of biomes that the compass will not be able to search for. Specify by resource location (ex: minecraft:ocean), name (ex: Ocean), or ID (ex: 0)";
    		biomeBlacklist = builder.comment(desc).define("biomeBlacklist", new ArrayList<String>());

    		desc = "The maximum samples to be taken when searching for a biome.";
    		maxSamples = builder.comment(desc).defineInRange("maxSamples", 100000, 0, 1000000);

            builder.pop();
        }
    }
    
    public static class Client {
    	public final ForgeConfigSpec.BooleanValue displayWithChatOpen;
        public final ForgeConfigSpec.BooleanValue fixBiomeNames;
        public final ForgeConfigSpec.IntValue lineOffset;
        
        Client(ForgeConfigSpec.Builder builder) {
        	String desc;
        	builder.push("Client");
        	
        	desc = "Displays Nature's Compass information even while chat is open.";
    		displayWithChatOpen = builder.comment(desc).define("displayWithChatOpen", true);

    		desc = "Fixes biome names by adding missing spaces. Ex: ForestHills becomes Forest Hills";
    		fixBiomeNames = builder.comment(desc).define("fixBiomeNames", true);
    		
    		desc = "The line offset for information rendered on the HUD.";
    		lineOffset = builder.comment(desc).defineInRange("lineOffset", 1, 0, 50);
        
    		builder.pop();
        }
    }

}
