package com.theblockheads.clienttweaks.screen;

import com.theblockheads.clienttweaks.ClientTweaksConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

	private static final int ROW_HEIGHT = 28;
	private static final int WIDGET_WIDTH = 200;
	private static final int BUTTON_WIDTH = 98;

	private final Screen parent;
	private final ClientTweaksConfig working;

	private EditBox widthInput;

	public ConfigScreen(Screen parent) {
		super(Component.literal("Blockheads Client Tweaks"));
		this.parent = parent;
		this.working = ClientTweaksConfig.getCurrent().copy();
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int y = height / 4 + 24;

		addRenderableWidget(CycleButton.booleanBuilder(
				CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF, working.narrowSearchBox)
			.create(cx - WIDGET_WIDTH / 2, y, WIDGET_WIDTH, 20,
				Component.literal("Narrow creative search box"),
				(btn, val) -> working.narrowSearchBox = val));

		y += ROW_HEIGHT;

		widthInput = new EditBox(font, cx + 48, y, 50, 20,
			Component.literal("Search box width"));
		widthInput.setMaxLength(3);
		widthInput.setValue(String.valueOf(working.searchBoxWidth));
		widthInput.setResponder(s -> {
			try {
				int v = Integer.parseInt(s);
				if (v >= 40 && v <= 200) working.searchBoxWidth = v;
			} catch (NumberFormatException ignored) { }
		});
		addRenderableWidget(widthInput);

		int bottomY = height - 28;
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> {
			ClientTweaksConfig.save(working);
			onClose();
		}).bounds(cx - BUTTON_WIDTH - 2, bottomY, BUTTON_WIDTH, 20).build());

		addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> onClose())
			.bounds(cx + 2, bottomY, BUTTON_WIDTH, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
		extractMenuBackground(g);
		super.extractRenderState(g, mouseX, mouseY, delta);
		g.centeredText(font, title, width / 2, 15, 0xFFFFFF);
		if (widthInput != null) {
			g.text(font, Component.literal("Search box width (40–200):"),
				width / 2 - WIDGET_WIDTH / 2, widthInput.getY() + 6, 0xA0A0A0);
		}
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}
}
