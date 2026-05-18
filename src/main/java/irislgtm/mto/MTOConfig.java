package irislgtm.mto;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MTOConfig {
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec.DoubleValue OUTLINE_OPACITY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        OUTLINE_OPACITY = builder.defineInRange("outlineOpacity", 0.4D, 0.0D, 1.0D);
        CLIENT_SPEC = builder.build();
    }

    private MTOConfig() {
    }
}