package irislgtm.mto;

<<<<<<< Updated upstream
import net.minecraftforge.common.ForgeConfigSpec;

public class MTOConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.DoubleValue OUTLINE_OPACITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
=======
import net.neoforged.neoforge.common.ModConfigSpec;

public class MTOConfig {
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec.DoubleValue OUTLINE_OPACITY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
>>>>>>> Stashed changes
        OUTLINE_OPACITY = builder.defineInRange("outlineOpacity", 0.4D, 0.0D, 1.0D);
        CLIENT_SPEC = builder.build();
    }

    private MTOConfig() {
    }
}