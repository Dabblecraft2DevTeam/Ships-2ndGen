package MoseShipsBukkit.ShipBlock.Signs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import MoseShipsBukkit.Utils.SOptional;
import MoseShipsBukkit.Vessel.Common.RootTypes.LiveShip;

public interface ShipSign {

	public static final List<ShipSign> SHIP_SIGNS = new ArrayList<ShipSign>();

	public void onCreation(SignChangeEvent event);

	public void onShiftRightClick(Player player, Sign sign, LiveShip ship);

	public void onRightClick(Player player, Sign sign, LiveShip ship);

	public void onLeftClick(Player player, Sign sign, LiveShip ship);

	public void onRemove(Player player, Sign sign);

	public List<String> getFirstLine();

	public boolean isSign(Sign sign);
	
	public void apply(Sign sign);

	public SOptional<LiveShip> getAttachedShip(Sign sign);

}
