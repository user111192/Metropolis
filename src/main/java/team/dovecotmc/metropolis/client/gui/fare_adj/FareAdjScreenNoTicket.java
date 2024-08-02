package team.dovecotmc.metropolis.client.gui.fare_adj;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import team.dovecotmc.metropolis.Metropolis;

/**
 * @author Arrokoth
 * @project Metropolis
 * @copyright Copyright © 2024 Arrokoth All Rights Reserved.
 */
public class FareAdjScreenNoTicket extends Screen {
    private static final Identifier BG_TEXTURE_ID = new Identifier(Metropolis.MOD_ID, "textures/gui/fare_adj_main/base.png");
    protected static final int BG_TEXTURE_WIDTH = 256;
    protected static final int BG_TEXTURE_HEIGHT = 196;

    protected final BlockPos pos;
    protected final FareAdjData data;
    protected final Screen parent;

    protected double mouseX = 0;
    protected double mouseY = 0;
    protected boolean pressing = false;
    private boolean lastPressing = false;
    protected boolean pressed = false;

    public FareAdjScreenNoTicket(BlockPos pos, FareAdjData data, Screen parent) {
        super(Text.translatable("gui.metropolis.fare_adj_no_ticket.title"));
        this.pos = pos;
        this.data = data;
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.pressed = false;
        this.pressing = false;
        this.lastPressing = false;
        this.mouseX = 0;
        this.mouseY = 0;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);

        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.push();
        RenderSystem.setShaderTexture(0, BG_TEXTURE_ID);
        drawTexture(
                matrices,
                this.width / 2 - BG_TEXTURE_WIDTH / 2,
                this.height / 2 - BG_TEXTURE_HEIGHT / 2,
                0,
                0,
                BG_TEXTURE_WIDTH, BG_TEXTURE_HEIGHT,
                BG_TEXTURE_WIDTH, BG_TEXTURE_HEIGHT
        );

        // Title
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        this.textRenderer.drawWithOutline(
                title.asOrderedText(),
                intoTexturePosX(36),
                intoTexturePosY(12),
                0xFFFFFF,
                0x16161B,
                matrices.peek().getPositionMatrix(),
                immediate,
                15728880
        );
        immediate.draw();
        matrices.pop();

        // Subtitle
        matrices.push();
        float scaleFactor = 1.5f;
        matrices.scale(scaleFactor, scaleFactor, scaleFactor);
        this.textRenderer.draw(
                matrices,
                Text.translatable("gui.metropolis.fare_adj_no_ticket.subtitle"),
                intoTexturePosX(24) / scaleFactor,
                intoTexturePosY(34) / scaleFactor,
                0x3F3F3F
        );
        matrices.pop();

        super.render(matrices, mouseX, mouseY, delta);

        // Handle inputs
        if (client != null) {
            if (pressed) {
            }
        }

        if (pressing) {
            pressed = !lastPressing;
        } else {
            pressed = false;
        }
        lastPressing = pressing;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.pressing = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.pressing = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    private int intoTexturePosX(double x) {
        return (int) (this.width / 2 - BG_TEXTURE_WIDTH / 2 + x);
    }

    private int intoTexturePosY(double y) {
        return (int) (this.height / 2 - BG_TEXTURE_HEIGHT / 2 + y);
    }

    public void playButtonDownSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}