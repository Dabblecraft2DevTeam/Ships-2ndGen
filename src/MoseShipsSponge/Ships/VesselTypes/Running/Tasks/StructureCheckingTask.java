package MoseShipsSponge.Ships.VesselTypes.Running.Tasks;

import MoseShipsSponge.Configs.ShipsConfig;
import MoseShipsSponge.Ships.VesselTypes.DataTypes.LiveData;
import MoseShipsSponge.Ships.VesselTypes.Running.ShipsTask;

public class StructureCheckingTask implements ShipsTask{

	boolean g_can_update_structure = true;
	
	@Override
	public void onRun(LiveData ship) {
		g_can_update_structure = true;
	}
	
	public void setUpdateStructure(boolean check){
		g_can_update_structure = check;
	}
	
	public boolean canUpdateStructure(){
		return g_can_update_structure;
	}

	@Override
	public long getDelay() {
		return ShipsConfig.CONFIG.get(Integer.class, ShipsConfig.PATH_SCHEDULER_STRUCTURE);
	}

}