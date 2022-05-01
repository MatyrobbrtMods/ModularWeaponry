package com.matyrobbrt.modularweaponry.modulebench;

import com.matyrobbrt.modularweaponry.init.BETypeInit;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModuleBenchBE extends BlockEntity {

    public ModuleBenchBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BETypeInit.MODULE_BENCH.get(), pWorldPosition, pBlockState);
    }

}
