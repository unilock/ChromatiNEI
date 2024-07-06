package cc.unilock.chromatinei.mixin;

import Reika.ChromatiCraft.ChromaClientEventController;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChromaClientEventController.class, remap = false)
public class ChromaClientEventControllerMixin {
    @Redirect(method = "interceptNEI", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z"))
    private boolean isKeyDown(int key) {
        return !Keyboard.isKeyDown(key);
    }
}
