package vazkii.quark.api.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IConfigElement extends Comparable<IConfigElement> {

	public String getName();
	public String getGuiDisplayName();
	public List<String> getTooltip();
	public String getSubtitle();
	public @Nullable IConfigCategory getParent();
	public boolean isDirty();
	public void clean();
	public void save();
	
	public void refresh();
	public void reset(boolean hard);
	public void print(String pad, PrintStream out);
	
}
