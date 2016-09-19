package MoseShipsBukkit.Ships.VesselTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import MoseShipsBukkit.Causes.MovementResult;
import MoseShipsBukkit.Ships.ShipsData;
import MoseShipsBukkit.Ships.Movement.Movement;
import MoseShipsBukkit.Ships.Movement.StoredMovement;
import MoseShipsBukkit.Ships.Movement.Movement.Rotate;
import MoseShipsBukkit.Ships.Movement.MovingBlock.MovingBlock;
import MoseShipsBukkit.Ships.VesselTypes.DataTypes.LiveData;
import MoseShipsBukkit.Ships.VesselTypes.Loading.ShipLoader;
import MoseShipsBukkit.Ships.VesselTypes.Loading.ShipsLocalDatabase;
import MoseShipsBukkit.Ships.VesselTypes.Satic.StaticShipType;
import MoseShipsBukkit.Signs.ShipsSigns.SignType;

public abstract class LoadableShip extends ShipsData {

	public abstract Optional<MovementResult> hasRequirements(List<MovingBlock> blocks);

	public abstract boolean shouldFall();

	public abstract Map<String, Object> getInfo();

	public abstract StaticShipType getStatic();
	
	protected boolean g_moving = false;
	protected int g_max_blocks = 4000;
	protected int g_min_blocks = 200;

	static List<LoadableShip> SHIPS = new ArrayList<LoadableShip>();

	public LoadableShip(String name, Block sign, Location teleport) {
		super(name, sign, teleport);
	}

	public LoadableShip(ShipsData data) {
		super(data);
	}
	
	public int getMaxBlocks(){
		return g_max_blocks;
	}
	
	public LoadableShip setMaxBlocks(int A){
		g_max_blocks = A;
		return this;
	}

	public int getMinBlocks(){
		return g_min_blocks;
	}
	
	public LoadableShip setMinBlocks(int A){
		g_min_blocks = A;
		return this;
	}
	
	public boolean isMoving(){
		return g_moving;
	}
	
	public void setMoving(boolean check){
		g_moving = check;
	}

	public ShipsLocalDatabase getLocalDatabase() {
		return new ShipsLocalDatabase(this);
	}

	public Optional<MovementResult> move(BlockFace dir, int speed) {
		Block block = new Location(getWorld(), 0, 0, 0).getBlock().getRelative(dir, speed);
		return Movement.move(this, block.getX(), block.getY(), block.getZ());
	}

	public Optional<MovementResult> move(int X, int Y, int Z) {
		return Movement.move(this, X, Y, Z);
	}

	public Optional<MovementResult> rotateLeft() {
		return Movement.rotateLeft(this);
	}

	public Optional<MovementResult> rotateRight() {
		return Movement.rotateRight(this);
	}

	public Optional<MovementResult> rotate(Rotate type) {
		return Movement.rotate(this, type);
	}

	public Optional<MovementResult> teleport(StoredMovement move) {
		return Movement.teleport(this, move);
	}

	public Optional<MovementResult> teleport(Location loc) {
		return Movement.teleport(this, loc);
	}

	public Optional<MovementResult> teleport(Location loc, int X, int Y, int Z) {
		return Movement.teleport(this, loc, X, Y, Z);
	}
	
	@Override
	public List<Block> updateBasicStructure() {
		List<Block> structure = super.updateBasicStructure();
		getLocalDatabase().saveBasicShip(this);
		return structure;
	}

	@Override
	public List<Block> setBasicStructure(List<Block> locs, Block licence) {
		List<Block> structure = super.setBasicStructure(locs, licence);
		getLocalDatabase().saveBasicShip(this);
		return structure;
	}

	@Override
	public LoadableShip setTeleportToLocation(Location loc) {
		super.setTeleportToLocation(loc);
		getLocalDatabase().saveBasicShip(this);
		return this;
	}

	@Override
	public LoadableShip setOwner(OfflinePlayer user) {
		super.setOwner(user);
		getLocalDatabase().saveBasicShip(this);
		return this;
	}

	public static boolean addToRam(LoadableShip type) {
		for(LoadableShip ship : SHIPS){
			if(ship.getName().equalsIgnoreCase(type.getName())){
				return false;
			}
		}
		SHIPS.add(type);
		return true;
	}

	public static Optional<LoadableShip> getShip(String name) {
		for (LoadableShip ship : SHIPS) {
			System.out.println("ship found: " + ship.getName());
			if (ship.getName().equalsIgnoreCase(name)) {
				return Optional.of(ship);
			}
		}
		System.out.println("Attempting to force load " + name);
		return ShipLoader.loadShip(name);
	}

	public static Optional<LoadableShip> getShip(SignType type, Sign sign, boolean refresh) {
		System.out.println("type: " + type.name());
		if (type.equals(SignType.LICENCE)) {
			String text = sign.getLine(2);
			System.out.println("Attempting to find " + text);
			return getShip(ChatColor.stripColor(text));
		} else {
			return getShip(sign.getBlock(), refresh);
		}
	}

	public static Optional<LoadableShip> getShip(Block loc, boolean updateStructure) {
		for (LoadableShip ship : SHIPS) {
			if (updateStructure) {
				ship.updateBasicStructure();
			}
			if (ship.getBasicStructure().contains(loc)) {
				return Optional.of(ship);
			}
		}
		for (LoadableShip ship : getShips()) {
			System.out.println("loaded Ship");
			if (updateStructure) {
				ship.updateBasicStructure();
			}
			if (ship.getBasicStructure().contains(loc)) {
				return Optional.of(ship);
			}
		}
		return Optional.empty();
	}

	public static List<LoadableShip> getReasentlyUsedShips() {
		return SHIPS;
	}

	public static List<LoadableShip> getShips() {
		List<LoadableShip> ships = new ArrayList<LoadableShip>();
		ships.addAll(SHIPS);
		for (StaticShipType type : StaticShipType.TYPES) {
			File[] files = new File("plugins/Ships/VesselData/" + type.getName()).listFiles();
			if (files != null) {
				for (File file : files) {
					String name = file.getName().replace(".yml", "");
					boolean check = false;
					for (LoadableShip ship : ships) {
						if (ship.equals(name)) {
							check = true;
						}
					}
					if(!check){
						Optional<LoadableShip> opShip = ShipLoader.loadShip(file);
						if (opShip.isPresent()) {
							ships.add(opShip.get());
						}
					}
				}
			}
		}
		return ships;
	}

	public static <T extends StaticShipType> List<LoadableShip> getShips(StaticShipType type) {
		List<LoadableShip> ships = new ArrayList<LoadableShip>();
		for (LoadableShip ship : ships) {
			if (type.equals(ship.getStatic())) {
				ships.add(ship);
			}
		}
		File[] files = new File("plugins/Ships/VesselData/" + type.getName()).listFiles();
		if (files != null) {
			for (File file : files) {
				String name = file.getName().replace(".yml", "");
				for (LoadableShip ship : ships) {
					if (!ship.getName().equals(name)) {
						Optional<LoadableShip> opShip = ShipLoader.loadShip(file);
						if (opShip.isPresent()) {
							ships.add(opShip.get());
						}
					}
				}
			}
		}
		return ships;
	}

	public static List<LoadableShip> getShipsByRequirements(Class<? extends LiveData> type) {
		List<LoadableShip> ships = new ArrayList<LoadableShip>();
		for (LoadableShip ship : getShips()) {
			if (type.isInstance(ship)) {
				ships.add(ship);
			}
		}
		return ships;
	}

}