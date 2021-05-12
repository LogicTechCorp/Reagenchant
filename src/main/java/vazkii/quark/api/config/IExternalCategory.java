package vazkii.quark.api.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public interface IExternalCategory extends IConfigCategory {

	public void commit();
	public Map<String, IConfigCategory> getTopLevelCategories();
	
	public IExternalCategory addTopLevelCategory(String name, Consumer<IExternalCategory> onChangedCallback);
	
}
