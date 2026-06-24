package irislgtm.mto;

import net.minecraftforge.common.ForgeConfigSpec;

public class MTOConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.BooleanValue MOD_ENABLED;
    public static final ForgeConfigSpec.DoubleValue OUTLINE_OPACITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MOD_ENABLED = builder.define("modEnabled", true);
        OUTLINE_OPACITY = builder.defineInRange("outlineOpacity", 0.4D, 0.0D, 1.0D);
        CLIENT_SPEC = builder.build();
    }

    private MTOConfig() {
    }
}