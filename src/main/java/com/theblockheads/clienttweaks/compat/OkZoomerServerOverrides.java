package com.theblockheads.clienttweaks.compat;

import com.theblockheads.clienttweaks.ClientTweaksConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class OkZoomerServerOverrides {
	private OkZoomerServerOverrides() {
	}

	public static void apply(String spyglassMode, String zoomOverlay) {
		try {
			Class<?> configManagerClass = Class.forName("page.langeweile.ok_zoomer.config.OkZoomerConfigManager");
			Class<?> spyglassModesClass = Class.forName("page.langeweile.ok_zoomer.config.ConfigEnums$SpyglassModes");
			Class<?> zoomOverlaysClass = Class.forName("page.langeweile.ok_zoomer.config.ConfigEnums$ZoomOverlays");

			Object config = configManagerClass.getField("CONFIG").get(null);
			setTrackedOverride(config, "controls", "spyglassMode", Enum.valueOf((Class) spyglassModesClass, spyglassMode));
			setTrackedOverride(config, "appearance", "zoomOverlay", Enum.valueOf((Class) zoomOverlaysClass, zoomOverlay));

			Method configureZoomInstance = configManagerClass.getMethod("configureZoomInstance");
			configureZoomInstance.invoke(null);
			ClientTweaksConstants.LOGGER.info("Applied server OK Zoomer overrides: spyglassMode={}, zoomOverlay={}", spyglassMode, zoomOverlay);
		} catch (ReflectiveOperationException | RuntimeException e) {
			ClientTweaksConstants.LOGGER.warn("Could not apply server OK Zoomer overrides", e);
		}
	}

	private static void setTrackedOverride(Object config, String sectionName, String valueName, Object override) throws ReflectiveOperationException {
		Field sectionField = config.getClass().getField(sectionName);
		Object section = sectionField.get(config);
		Field valueField = section.getClass().getField(valueName);
		Object trackedValue = valueField.get(section);
		Method setOverride = trackedValue.getClass().getMethod("setOverride", Object.class);
		setOverride.invoke(trackedValue, override);
	}
}
