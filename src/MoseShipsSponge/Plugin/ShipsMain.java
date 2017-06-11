package MoseShipsSponge.Plugin;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import MoseShipsSponge.VersionChecking;
import MoseShipsSponge.Commands.DebugCMD;
import MoseShipsSponge.Commands.InfoCMD;
import MoseShipsSponge.Commands.ShipsCMD;
import MoseShipsSponge.Commands.SignCMD;
import MoseShipsSponge.Configs.BlockList;
import MoseShipsSponge.Listeners.ShipsListeners;
import MoseShipsSponge.ShipBlock.Signs.ShipAltitudeSign;
import MoseShipsSponge.ShipBlock.Signs.ShipSign;
import MoseShipsSponge.Ships.VesselTypes.DefaultTypes.AirTypes.OpShip.StaticOPShip;

@Plugin(id = ShipsMain.ID, name = ShipsMain.NAME, version = ShipsMain.VERSION)
public class ShipsMain {

	public static final String ID = "ships";
	public static final String NAME = "Ships";
	public static final String VERSION = "6.0.0.0|PreAlpha-Sponge(Alpha 1,0,1)";
	public static final String TESTED_API = "6.0.0.0";
	public static final String[] TESTED_MC = { "1.11.2" };

	static ShipsMain PLUGIN;

	@Inject
	Game GAME;

	@Inject
	PluginContainer CONTAINER;
	
	private void registerSigns(){
		ShipSign.SHIP_SIGNS.add(new ShipLicenceSign());
		ShipSign.SHIP_SIGNS.add(new ShipAltitudeSign());
		ShipSign.SHIP_SIGNS.add(new ShipEngineSign());
		ShipSign.SHIP_SIGNS.add(new ShipWheelSign());
		ShipSign.SHIP_SIGNS.add(new ShipAltitudeSign());
		ShipSign.SHIP_SIGNS.add(new ShipEOTSign());
	}

	private void registerCMDs() {
		new InfoCMD();
		new DebugCMD();
		new SignCMD();
		new HelpCMD();
		new BlockListCMD();
		new AutoPilotCMD();
	}

	private void registerShipTypes() {
		new StaticOPShip();
	}

	private void displayVersionChecking() {
		VersionChecking.VersionOutcome previous = null;
		for (String tested : TESTED_MC) {
			int[] test = VersionChecking.convert(tested);
			VersionChecking.VersionOutcome outcome = VersionChecking.isGreater(VersionChecking.MINECRAFT_VERSION,
					test);
			if (outcome.equals(VersionChecking.VersionOutcome.EQUAL)) {
				previous = outcome;
				break;
			} else if ((previous != null) && (!outcome.equals(previous))) {
				previous = null;
				break;
			} else {
				previous = outcome;
			}
		}
		ConsoleSource console = Sponge.getServer().getConsole();
		if (previous != null) {
			switch (previous) {
				case EQUAL:
					Text text = Text.builder("Your MC version has been tested with Ships").color(TextColors.GREEN)
							.build();
					console.sendMessage(text);
					break;
				case GREATER:
					Text text2 = Text
							.builder(
									"Your MC version is greater then the tested versions. Ships should be uneffected however please be keep in mind that you should look for updates as your MC version is unsupported")
							.color(TextColors.YELLOW).build();
					console.sendMessage(text2);
					break;
				case LOWER:
					Text text3 = Text
							.builder(
									"Your MC version is lower then the tested versions. Ships is not supported at all, Please note that Ships may still work")
							.color(TextColors.RED).build();
					console.sendMessage(text3);
					break;
			}
		} else {
			Text text = Text
					.builder(
							"Your MC version is between tested versions. It is unlikely there will be a upgrade for ships to your MC version. Please keep in mind that Ships should still work fine.")
					.color(TextColors.RED).build();
			console.sendMessage(text);
		}
	}

	@Listener
	public void onEnable(GameStartingServerEvent event) {
		PLUGIN = this;
		GAME.getEventManager().registerListeners(this, new ShipsListeners());
		GAME.getCommandManager().register(this, new ShipsCMD.Executer(), "Ships", "Vessels");
		BlockList.BLOCK_LIST.reload();
		registerShipTypes();
		registerCMDs();
		displayVersionChecking();
	}
	
	@Listener
	public void onLoad(GameStartedServerEvent event){
		//load the ship
	}

	@Listener
	public void onDisable(GameStoppingServerEvent event) {

	}

	public Game getGame() {
		return GAME;
	}

	public PluginContainer getContainer() {
		return CONTAINER;
	}

	public static Optional<User> getUser(UUID uuid) {
		UserStorageService service = PLUGIN.GAME.getServiceManager().getRegistration(UserStorageService.class).get()
				.getProvider();
		return service.get(uuid);
	}

	public static LiteralText formatCMDHelp(String message) {
		return Text.builder(message).color(TextColors.AQUA).build();
	}

	public static LiteralText format(String message, boolean error) {
		if (error) {
			LiteralText ret = Text.builder("[Ships] ").color(TextColors.GOLD)
					.append(Text.builder(message).color(TextColors.RED).build()).build();
			return ret;
		} else {
			LiteralText ret = Text.builder("[Ships] ").color(TextColors.GOLD)
					.append(Text.builder(message).color(TextColors.AQUA).build()).build();
			return ret;
		}
	}

	public static LiteralText format(Text text, boolean error) {
		if (error) {
			LiteralText ret = Text.builder("[Ships] ").color(TextColors.GOLD)
					.append(Text.builder(text.toPlain()).color(TextColors.RED).build()).build();
			return ret;
		} else {
			LiteralText ret = Text.builder("[Ships] ").color(TextColors.GOLD)
					.append(Text.builder(text.toPlain()).color(TextColors.AQUA).build()).build();
			return ret;
		}
	}

	public static ShipsMain getPlugin() {
		return PLUGIN;
	}

}