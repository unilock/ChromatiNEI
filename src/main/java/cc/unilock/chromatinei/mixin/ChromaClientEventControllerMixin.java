package cc.unilock.chromatinei.mixin;

import Reika.ChromatiCraft.ChromaClientEventController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChromaClientEventController.class, remap = false)
public class ChromaClientEventControllerMixin {
    @WrapOperation(method = "interceptNEI", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z"))
    private boolean isKeyDown(int key, Operation<Boolean> original) {
        return !original.call(key);
    }
}
