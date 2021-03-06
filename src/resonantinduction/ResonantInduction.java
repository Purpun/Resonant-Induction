package resonantinduction;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import resonantinduction.entangler.ItemQuantumEntangler;
import resonantinduction.tesla.BlockTesla;
import resonantinduction.tesla.TileEntityTesla;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * @author Calclavia
 * 
 */
@Mod(modid = ResonantInduction.ID, name = ResonantInduction.NAME, version = ResonantInduction.VERSION)
@NetworkMod(channels = ResonantInduction.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class ResonantInduction
{
	/**
	 * Mod Information
	 */
	public static final String ID = "resonantinduction";
	public static final String NAME = "Resonant Induction";
	public static final String CHANNEL = "R_INDUC";

	public static final String MAJOR_VERSION = "@MAJOR@";
	public static final String MINOR_VERSION = "@MINOR@";
	public static final String REVISION_VERSION = "@REVIS@";
	public static final String BUILD_VERSION = "@BUILD@";
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;

	@Instance(ID)
	public static ResonantInduction INSTNACE;

	@SidedProxy(clientSide = ID + ".ClientProxy", serverSide = ID + ".CommonProxy")
	public static CommonProxy proxy;

	@Mod.Metadata(ID)
	public static ModMetadata metadata;

	public static final Logger LOGGER = Logger.getLogger(NAME);

	/**
	 * Directory Information
	 */
	public static final String DOMAIN = "resonantinduction";
	public static final String PREFIX = DOMAIN + ":";
	public static final String DIRECTORY = "/assets/" + DOMAIN + "/";
	public static final String TEXTURE_DIRECTORY = "textures/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "/gui";
	public static final String BLOCK_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "models/";

	public static final String LANGUAGE_DIRECTORY = DIRECTORY + "languages/";
	public static final String[] LANGUAGES = new String[] { "en_US" };

	/**
	 * Settings
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), NAME + ".cfg"));
	public static float POWER_PER_COAL = 5;

	/** Block ID by Jyzarc */
	private static final int BLOCK_ID_PREFIX = 3200;
	/** Item ID by Horfius */
	private static final int ITEM_ID_PREFIX = 20150;

	private static int NEXT_BLOCK_ID = BLOCK_ID_PREFIX;
	private static int NEXT_ITEM_ID = ITEM_ID_PREFIX;

	public static int getNextBlockID()
	{
		return NEXT_BLOCK_ID++;
	}

	public static int getNextItemID()
	{
		return NEXT_ITEM_ID++;
	}
	
	//Items
	public static Item itemQuantumEntangler;

	//Blocks
	public static Block blockTesla;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt)
	{
		LOGGER.setParent(FMLLog.getLogger());

		CONFIGURATION.load();
		
		//config
		POWER_PER_COAL = (float) CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Coal Wattage", POWER_PER_COAL).getDouble(POWER_PER_COAL);

		//items
		itemQuantumEntangler = new ItemQuantumEntangler(getNextItemID());
		GameRegistry.registerItem(itemQuantumEntangler, itemQuantumEntangler.getUnlocalizedName());
		
		//blocks
		blockTesla = new BlockTesla(getNextBlockID());
		GameRegistry.registerBlock(blockTesla, blockTesla.getUnlocalizedName());
		
		CONFIGURATION.save();
		
		//tiles
		GameRegistry.registerTileEntity(TileEntityTesla.class, blockTesla.getUnlocalizedName());

		ResonantInduction.proxy.registerRenderers();

		TabRI.ITEMSTACK = new ItemStack(blockTesla);
	}

	@EventHandler
	public void init(FMLInitializationEvent evt)
	{
		LOGGER.fine("Languages Loaded:" + loadLanguages(LANGUAGE_DIRECTORY, LANGUAGES));

		metadata.modId = ID;
		metadata.name = NAME;
		metadata.description = "Resonant Induction is a Minecraft mod focusing on the manipulation of electricity and wireless technology. Ever wanted blazing electrical shocks flying off your evil lairs? You've came to the right place!";
		metadata.url = "http://universalelectricity.com";
		metadata.version = VERSION + BUILD_VERSION;
		metadata.authorList = Arrays.asList(new String[] { "Calclavia", "Aidancbrady" });
		metadata.logoFile = "/";
		metadata.credits = "Thanks to Archadia for the assets.";
		metadata.autogenerated = true;
	}

	@EventHandler
	public void preInit(FMLPostInitializationEvent evt)
	{
		/**
		 * Recipes
		 */
		/** by Jyzarc */
		GameRegistry.addRecipe(new ShapedOreRecipe(blockTesla, "EEE", " C ", " I ", 'E', Item.eyeOfEnder, 'C', Item.redstone, 'I', Block.blockIron));

	}

	public static int loadLanguages(String languagePath, String[] languageSupported)
	{
		int loaded = 0;

		for (String language : languageSupported)
		{
			LanguageRegistry.instance().loadLocalization(languagePath + language + ".properties", language, false);
			loaded++;
		}

		return loaded;
	}
}
