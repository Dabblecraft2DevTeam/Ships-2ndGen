package MoseShipsBukkit.Utils;

import java.util.ArrayList;
import java.util.List;

import MoseShipsBukkit.Algorthum.Movement.MovementAlgorithm;
import MoseShipsBukkit.Configs.ShipsConfig;

public class MovementAlgorithmUtil {

	public static MovementAlgorithm getConfig() {
		String name = ShipsConfig.CONFIG.get(String.class, ShipsConfig.PATH_ALGORITHMS_MOVEMENT);
		SOptional<MovementAlgorithm> opMove = get(name);
		if (opMove.isPresent()) {
			return opMove.get();
		} else {
			// DEFAULTS TO SHIPS 5 AT THE MOMENT, WILL DEFAULT TO SHIPS 6 LATER
			return MovementAlgorithm.SHIPS5;
		}
	}

	public static List<MovementAlgorithm> get() {
		List<MovementAlgorithm> list = new ArrayList<MovementAlgorithm>(MovementAlgorithm.MOVEMENT_ALGORITHMS);
		list.add(MovementAlgorithm.SHIPS5);
		list.add(MovementAlgorithm.SHIPS6);
		return list;
	}

	public static SOptional<MovementAlgorithm> get(String name) {
		for (MovementAlgorithm alg : get()) {
			if (alg.getName().equalsIgnoreCase(name)) {
				return new SOptional<MovementAlgorithm>(alg);
			}
		}
		return new SOptional<MovementAlgorithm>();
	}

}