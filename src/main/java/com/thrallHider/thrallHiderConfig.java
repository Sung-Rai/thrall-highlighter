package com.thrallHider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;

@ConfigGroup(thrallHiderConfig.GROUP)
public interface thrallHiderConfig extends Config
{
	String GROUP = "thrall-hider";

	@ConfigItem(
		position = 1,
		keyName = "hideThralls",
		name = "Hide Thralls",
		description = "Hide thralls from view"
	)
	default boolean hideThralls()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "outlineThralls",
		name = "Outline Thralls",
		description = "Outline thralls"
	)
	default boolean outlineThralls()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "outlineColor",
		name = "Outline Color",
		description = "Color of the thrall outline"
	)
	default Color outlineColor()
	{
		return Color.BLACK;
	}

	@ConfigItem(
		position = 4,
		keyName = "outlineWidth",
		name = "Outline Width",
		description = "Width of the thrall outline"
	)
	default int outlineWidth()
	{
		return 1;
	}

	@ConfigItem(
		position = 5,
		keyName = "enableThrallTypeOverride",
		name = "Enable Thrall Type Override",
		description = "Use unique colors for different thrall types (ghost, skeleton, zombie)"
	)
	default boolean enableThrallTypeOverride()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "ghostThrallColor",
		name = "Ghost Thrall Color",
		description = "Color for ghost (mage) thralls when type override is enabled"
	)
	default Color ghostThrallColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		position = 7,
		keyName = "skeletonThrallColor",
		name = "Skeleton Thrall Color",
		description = "Color for skeleton (range) thralls when type override is enabled"
	)
	default Color skeletonThrallColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 8,
		keyName = "zombieThrallColor",
		name = "Zombie Thrall Color",
		description = "Color for zombie (melee) thralls when type override is enabled"
	)
	default Color zombieThrallColor()
	{
		return Color.RED;
	}
}
