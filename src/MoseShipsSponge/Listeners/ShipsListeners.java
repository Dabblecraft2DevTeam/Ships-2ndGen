package MoseShipsSponge.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import MoseShipsSponge.ShipsMain;
import MoseShipsSponge.Causes.FailedCause;
import MoseShipsSponge.Causes.ShipsCause;
import MoseShipsSponge.Events.StaticVessel.Create.AboutToCreateShipEvent;
import MoseShipsSponge.Events.Vessel.Create.ShipsCreateEvent;
import MoseShipsSponge.Ships.VesselTypes.ShipType;
import MoseShipsSponge.Ships.VesselTypes.StaticShipType;
import MoseShipsSponge.Signs.ShipsSigns;
import MoseShipsSponge.Signs.ShipsSigns.SignType;
import MoseShipsSponge.Utils.Permissions;

public class ShipsListeners {

	@Listener
	public void signCreate(ChangeSignEvent event) {
		Map<String, Object> causes = new HashMap<>();
		SignData data = event.getText();
		Optional<SignType> opSignType = ShipsSigns.getSignType(data.get(0).get().toPlain());
		if (opSignType.isPresent()) {
			SignType signType = opSignType.get();
			if (signType.equals(SignType.LICENCE)) {
				if (event.getText().asList().size() >= 3) {
					Optional<StaticShipType> opShipType = StaticShipType.getType(data.get(1).get().toPlain());
					if (opShipType.isPresent()) {
						StaticShipType type = opShipType.get();
						
						//PLAYER CAUSE
						Optional<Player> opPlayer = event.getCause().first(Player.class);
						if (opPlayer.isPresent()) {
							Player player = opPlayer.get();
							if (!Permissions.CREATE_VESSEL.hasPermission(player, type)) {
								return;
							}
							causes.put("Player", player);
							
						}
						
						AboutToCreateShipEvent<StaticShipType> ATCSEvent = new AboutToCreateShipEvent<>(type, event.getTargetTile().getLocation(), data, event.getOriginalText(), ShipsCause.SIGN_CREATE.buildCause(causes) );
						if(!ATCSEvent.isCancelled()){
							Optional<ShipType> opShip = type.createVessel(data.get(2).get().toPlain(), event.getTargetTile().getLocation());
							if(opShip.isPresent()){
								ShipType ship = opShip.get();
								ShipsSigns.colourSign(event.getTargetTile());
								ShipsCreateEvent<ShipType> SCEvent = new ShipsCreateEvent<>(ship, ShipsCause.SIGN_CREATE.buildCause(causes));
								if(!SCEvent.isCancelled()){
									
									//PLAYER 
									if (opPlayer.isPresent()) {
										Player player = opPlayer.get();
										player.sendMessage(ShipsMain.format("Ship created", false));
									}
									ShipType.inject(ship);
									List<Text> lines = new ArrayList<>();
									lines.add(Text.builder("[Ships]").color(TextColors.YELLOW).build());
									lines.add(Text.builder(ship.getStatic().getName()).color(TextColors.BLUE).build());
									lines.add(Text.builder(ship.getName()).color(TextColors.GREEN).build());
									data.set(Keys.SIGN_LINES, lines);
								}
							}
						}
					}
				}
			} else {
				event.getText().set(Keys.SIGN_LINES, Arrays.asList(signType.getDefaultLines().get()));
			}
		}
	}

	@Listener
	public void playerInteractEvent(InteractBlockEvent event, @Root Player player) {
		System.out.println("Interact event");
		Optional<Location<World>> opLoc = event.getTargetBlock().getLocation();
		if (opLoc.isPresent()) {
			System.out.println("block click");
			Location<World> loc = opLoc.get();
			Optional<TileEntity> opEntity = loc.getTileEntity();
			if (opEntity.isPresent()) {
				System.out.println("is tile entity");
				TileEntity tile = opEntity.get();
				if (tile instanceof Sign) {
					System.out.println("tile entity is sign");
					Sign sign = (Sign) tile;
					Optional<SignType> opSignType = ShipsSigns.getSignType(sign);
					if (opSignType.isPresent()) {
						System.out.println("sign type found");
						if (event instanceof InteractBlockEvent.Secondary) {
							System.out.println("is right click");
							Optional<ShipType> opShipType = ShipType.getShip(opSignType.get(), sign, true);
							if (opShipType.isPresent()) {
								System.out.println("gets the ship");
								ShipType ship = opShipType.get();
								switch (opSignType.get()) {
									case EOT:

										break;
									case LICENCE:
										Map<Text, Object> info = ship.getInfo();
										player.sendMessage(ShipsMain.format("Information about " + ship.getName(), false));
										info.entrySet().forEach(e -> player.sendMessage(e.getKey()));
										break;
									case MOVE:
										System.out.println("is move sign");
										Direction direction = loc.get(Keys.DIRECTION).get();
										if (sign.lines().get(2).toPlain().equals("{Boost}")) {
											Optional<FailedCause> cause = ship.move(direction, ship.getStatic().getBoostSpeed(), ShipsCause.SIGN_CLICK.buildCause());
											if(cause.isPresent()){
												FailedCause failed = cause.get();
												failed.getCauses().get(FailedCause.FailedKeys.COLLIDE);
											}
										} else {
											ship.move(direction, ship.getStatic().getDefaultSpeed(), ShipsCause.SIGN_CLICK.buildCause());
										}
										break;
									case WHEEL:
										break;
									case ALTITUDE:
										ship.move(new Vector3i(0, -ship.getStatic().getAltitudeSpeed(), 0), ShipsCause.SIGN_CLICK.buildCause());
										break;
								}
							}
						}
					}
				}
			}
		}
	}

}
