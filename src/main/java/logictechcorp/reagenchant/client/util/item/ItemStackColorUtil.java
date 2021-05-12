/*
 * Reagenchant
 * Copyright (c) 2019-2021 by LogicTechCorp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package logictechcorp.reagenchant.client.util.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemStackColorUtil {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static final BlockModelShapes BLOCK_MODEL_SHAPES = MINECRAFT.getModelManager().getBlockModelShapes();
    private static final ItemRenderer ITEM_RENDERER = MINECRAFT.getItemRenderer();
    private static final Map<ResourceLocation, float[]> ITEM_COLORS = new HashMap<>();

    public static float[] getAverageColorComponents(ItemStack stack, Random random) {
        Item item = stack.getItem();

        if(!ITEM_COLORS.containsKey(item.getRegistryName())) {
            TextureAtlasSprite sprite;

            if(item instanceof BlockItem) {
                sprite = BLOCK_MODEL_SHAPES.getModel(((BlockItem) item).getBlock().getDefaultState()).getParticleTexture(EmptyModelData.INSTANCE);
            }
            else {
                sprite = ITEM_RENDERER.getItemModelWithOverrides(stack, null, null).getQuads(null, null, random, EmptyModelData.INSTANCE).get(0).getSprite();
            }

            int maxU = sprite.getWidth();
            int maxV = sprite.getHeight();
            float rComponent = 0;
            float gComponent = 0;
            float bComponent = 0;
            int pixelCount = 0;

            for(int v = 0; v < maxV; v++) {
                for(int u = 0; u < maxU; u++) {
                    if(!sprite.isPixelTransparent(0, u, v)) {
                        int pixelColor = sprite.getPixelRGBA(0, u, v);
                        rComponent += (pixelColor & 0xFF) / 255.0F;
                        gComponent += ((pixelColor >> 8) & 0xFF) / 255.0F;
                        bComponent += ((pixelColor >> 16) & 0xFF) / 255.0F;
                        pixelCount++;
                    }
                }
            }

            float[] colorComponents = new float[]{ rComponent / pixelCount, gComponent / pixelCount, bComponent / pixelCount };
            ITEM_COLORS.put(item.getRegistryName(), colorComponents);
        }

        return ITEM_COLORS.get(item.getRegistryName());
    }
}
