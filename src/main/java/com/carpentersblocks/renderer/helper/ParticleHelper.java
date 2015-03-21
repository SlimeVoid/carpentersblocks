package com.carpentersblocks.renderer.helper;

//import com.carpentersblocks.data.Torch;
import com.carpentersblocks.renderer.entity.CarpentersDiggingFX;
import com.carpentersblocks.tileentity.TEBase;
//import com.carpentersblocks.tileentity.TECarpentersTorch;
import com.carpentersblocks.util.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleHelper {

    /**
     * Spawns big smoke particle when torch lowers state.
     */
//    public static void spawnTorchBigSmoke(TECarpentersTorch TE)
//    {
//        double[] headCoords = new Torch().getHeadCoordinates(TE);
//        TE.getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, headCoords[0], headCoords[1], headCoords[2], 0.0D, 0.0D, 0.0D);
//    }

    /**
     * Spawns a particle at the base of an entity.
     *
     * @param entity the entity at which feet the particles will spawn
     * @param itemStack the itemstack to use∂
     */
    public static void spawnTileParticleAt(Entity entity, ItemStack itemStack)
    {
        BlockProperties.prepareItemStackForRendering(itemStack);

        entity.worldObj.spawnParticle
        (
                EnumParticleTypes.BLOCK_CRACK,
                entity.posX + (entity.worldObj.rand.nextFloat() - 0.5D) * entity.width, entity.getBoundingBox().minY + 0.1D,
                entity.posZ + (entity.worldObj.rand.nextFloat() - 0.5D) * entity.width,
                -entity.motionX * 4.0D,
                1.5D,
                -entity.motionZ * 4.0D,
                Block.getStateId(Block.getBlockFromItem(itemStack.getItem()).getStateFromMeta(itemStack.getMetadata()))
        );
    }

    /**
     * Produces block destruction particles at coordinates.
     */
    public static void addDestroyEffect(World world, BlockPos pos, ItemStack itemStack, EffectRenderer effectRenderer)
    {
        BlockProperties.prepareItemStackForRendering(itemStack);
        byte factor = 4;

        for (int posX = 0; posX < factor; ++posX)
        {
            for (int posY = 0; posY < factor; ++posY)
            {
                for (int posZ = 0; posZ < factor; ++posZ)
                {
                    double dirX = pos.getX() + (posX + 0.5D) / factor;
                    double dirY = pos.getY() + (posY + 0.5D) / factor;
                    double dirZ = pos.getZ() + (posZ + 0.5D) / factor;

                    EntityDiggingFX particle = new CarpentersDiggingFX(world, dirX, dirY, dirZ, dirX - pos.getX() - 0.5D, dirY - pos.getY() - 0.5D, dirZ - pos.getZ() - 0.5D, BlockProperties.toBlockState(itemStack));
                    effectRenderer.addEffect(particle.func_174846_a/*applyColourMultiplier*/(pos));
                }
            }
        }
    }

    /**
     * Produces block hit particles at coordinates.
     */
    public static void addHitEffect(TEBase TE, MovingObjectPosition target, double x, double y, double z, ItemStack itemStack, EffectRenderer effectRenderer)
    {
        BlockProperties.prepareItemStackForRendering(itemStack);

        EntityDiggingFX particle = new CarpentersDiggingFX(TE.getWorld(), x, y, z, 0.0D, 0.0D, 0.0D, BlockProperties.toBlockState(itemStack));
        effectRenderer.addEffect(particle.func_174846_a/*applyColourMultiplier*/(target.getBlockPos()).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
    }

}
