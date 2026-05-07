/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, Sung-Rai
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.thrallHighlighter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;

@ConfigGroup(thrallHighlighterConfig.GROUP)
public interface thrallHighlighterConfig extends Config
{
	String GROUP = "thrall-highlighter";

	@ConfigItem(
		position = 1,
		keyName = "hideThralls",
		name = "Hide Thralls",
		description = "Hide thralls from view"
	)
	default boolean hideThralls()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "outlineThralls",
		name = "Outline Thralls",
		description = "Outline thralls"
	)
	default boolean outlineThralls()
	{
		return true;
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
