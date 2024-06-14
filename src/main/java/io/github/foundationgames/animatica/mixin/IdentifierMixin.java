package io.github.foundationgames.animatica.mixin;

import io.github.foundationgames.animatica.Animatica;
import io.github.foundationgames.animatica.util.Flags;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Allows invalid characters in paths to support packs with extremely outdated formatting (because OptiFine does too)
@Mixin(Identifier.class)
public class IdentifierMixin {
    @Inject(method = "of(Ljava/lang/String;)Lnet/minecraft/util/Identifier;", at = @At("TAIL"))
    private static void animatica$reportInvalidIdentifierCharacters(String id, CallbackInfoReturnable<Identifier> ci) {
        if (Flags.ALLOW_INVALID_ID_CHARS && !animatica$isPathAllowed(Identifier.splitOn(id, ':').getPath()) && !Identifier.splitOn(id, ':').getPath().startsWith("~/")) {
            Animatica.LOG.warn("Legacy resource pack is using an invalid namespaced identifier '{}'! DO NOT use non [a-z0-9_.-] characters for resource pack files and file names!", id);
        }
    }

    @Inject(method = "isPathCharacterValid", at = @At("RETURN"), cancellable = true)
    private static void animatica$allowInvalidCharacters(char character, CallbackInfoReturnable<Boolean> cir) {
        if (Flags.ALLOW_INVALID_ID_CHARS) {
            cir.setReturnValue(true);
        }
    }

    private static boolean animatica$isPathAllowed(String path) {
        if (path == null) return true;
        for (char c : path.toCharArray()) {
            if (!(c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.')) {
                return false;
            }
        }
        return true;
    }
}
