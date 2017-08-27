package MoseShipsBukkit.ShipBlock.Snapshot.MaterialSnapshot;

import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.NoteBlock;

import MoseShipsBukkit.ShipBlock.Snapshot.BlockSnapshot;
import MoseShipsBukkit.ShipBlock.Snapshot.SpecialSnapshot;

public class NoteBlockSnapshot extends BlockSnapshot implements SpecialSnapshot {

	Note g_note;

	public NoteBlockSnapshot(BlockState state) {
		super(state);
	}

	@Override
	public void onRemove(Block block) {
		if (block.getState() instanceof NoteBlock) {
			NoteBlock note = (NoteBlock) block.getState();
			g_note = note.getNote();
		}
	}

	@Override
	public void onPlace(Block block) {
		if (block.getState() instanceof NoteBlock) {
			NoteBlock note = (NoteBlock) block.getState();
			note.setNote(g_note);
			note.update();
		}
	}

}