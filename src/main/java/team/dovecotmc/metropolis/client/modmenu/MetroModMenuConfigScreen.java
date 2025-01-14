package team.dovecotmc.metropolis.client.modmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import team.dovecotmc.metropolis.Metropolis;
import team.dovecotmc.metropolis.client.MetropolisClient;
import team.dovecotmc.metropolis.client.config.MetroClientConfig;
import team.dovecotmc.metropolis.config.MetroConfig;

import java.util.Optional;

/**
 * @author Arrokoth
 * @project Metropolis
 * @copyright Copyright © 2024 Arrokoth All Rights Reserved.
 */
public class MetroModMenuConfigScreen extends Screen {
    private final Screen parent;
    public static final Identifier SWITCH_ON_TEXTURE_ID = new Identifier(Metropolis.MOD_ID, "textures/gui/config/switch_on.png");
    public static final Identifier SWITCH_OFF_TEXTURE_ID = new Identifier(Metropolis.MOD_ID, "textures/gui/config/switch_off.png");
    public static final int SWITCH_TEXTURE_WIDTH = 32;
    public static final int SWITCH_TEXTURE_HEIGHT = 16;

    public MetroModMenuConfigScreen(Screen parent) {
        super(Text.translatable("metropolis.modmenu.config.title"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        this.renderBackground(matrices);
        this.renderBackgroundTexture(0);

        super.render(matrices, mouseX, mouseY, delta);

        // Mod container
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Metropolis.MOD_ID);

        if (modContainer.isEmpty()) {
            return;
        }

        ModContainer mod = modContainer.get();
        int button_offset = (SWITCH_TEXTURE_HEIGHT - textRenderer.fontHeight) / 2;

        matrices.push();
        matrices.scale(2f, 2f, 2f);
        Text name = Text.literal(mod.getMetadata().getName());
        textRenderer.drawWithShadow(
                matrices,
                name,
                (width / 2f - textRenderer.getWidth(name) * 2f / 2f) / 2f, 16 / 2f,
                0xFFFFFF
        );
        matrices.pop();

        matrices.push();
        Text text = Text.translatable("config.metropolis.client.enable_glowing_texture");
        textRenderer.draw(
                matrices,
                text,
                width / 2f - 32 - textRenderer.getWidth(text),
                64,
                0xFFFFFF
        );

        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, SWITCH_ON_TEXTURE_ID);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(
                matrices,
                width / 2 + 32,
                64 - button_offset,
                0,
                0,
                SWITCH_TEXTURE_WIDTH,
                SWITCH_TEXTURE_HEIGHT,
                SWITCH_TEXTURE_WIDTH,
                SWITCH_TEXTURE_HEIGHT
        );

        text = Text.translatable("config.metropolis.client.enable_station_info_overlay");
        textRenderer.draw(
                matrices,
                text,
                width / 2f - 32 - textRenderer.getWidth(text),
                64 + (16 + textRenderer.fontHeight),
                0xFFFFFF
        );

        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, SWITCH_ON_TEXTURE_ID);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(
                matrices,
                width / 2 + 32,
                64 + (16 + textRenderer.fontHeight) - button_offset,
                0,
                0,
                SWITCH_TEXTURE_WIDTH,
                SWITCH_TEXTURE_HEIGHT,
                SWITCH_TEXTURE_WIDTH,
                SWITCH_TEXTURE_HEIGHT
        );
        matrices.pop();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
            MetroConfig.save(Metropolis.config);
            MetroClientConfig.save(MetropolisClient.config);
        }
    }
}
