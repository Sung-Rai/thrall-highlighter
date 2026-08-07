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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Renderable;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.ColorUtil;

@Slf4j
@PluginDescriptor(
	name = "Thrall Highlighter"
)
public class thrallHighlighterPlugin extends Plugin implements RenderCallback
{
	private static final Set<Integer> THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_GHOST_LESSER, NpcID.ARCEUUS_THRALL_SKELETON_LESSER, NpcID.ARCEUUS_THRALL_ZOMBIE_LESSER,  // Lesser Thrall (ghost, skeleton, zombie)
		NpcID.ARCEUUS_THRALL_GHOST_SUPERIOR, NpcID.ARCEUUS_THRALL_SKELETON_SUPERIOR, NpcID.ARCEUUS_THRALL_ZOMBIE_SUPERIOR,  // Superior Thrall (ghost, skeleton, zombie)
		NpcID.ARCEUUS_THRALL_GHOST_GREATER, NpcID.ARCEUUS_THRALL_SKELETON_GREATER, NpcID.ARCEUUS_THRALL_ZOMBIE_GREATER,   // Greater Thrall (ghost, skeleton, zombie)

		NpcID.THRALL_IMP_MAGIC_LESSER, NpcID.THRALL_IMP_MELEE_LESSER, NpcID.THRALL_IMP_RANGED_LESSER,  // Lesser Imp Thrall (magic, melee, ranged)
		NpcID.THRALL_IMP_MAGIC_SUPERIOR, NpcID.THRALL_IMP_MELEE_SUPERIOR, NpcID.THRALL_IMP_RANGED_SUPERIOR,  // Superior Imp Thrall (magic, melee, ranged)
		NpcID.THRALL_IMP_MAGIC_GREATER, NpcID.THRALL_IMP_MELEE_GREATER, NpcID.THRALL_IMP_RANGED_GREATER   // Greater Imp Thrall (magic, melee, ranged)
	);

	private static final Set<Integer> GHOST_THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_GHOST_LESSER, NpcID.ARCEUUS_THRALL_GHOST_SUPERIOR, NpcID.ARCEUUS_THRALL_GHOST_GREATER,
		NpcID.THRALL_IMP_MAGIC_LESSER, NpcID.THRALL_IMP_MAGIC_SUPERIOR, NpcID.THRALL_IMP_MAGIC_GREATER
	);

	private static final Set<Integer> SKELETON_THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_SKELETON_LESSER, NpcID.ARCEUUS_THRALL_SKELETON_SUPERIOR, NpcID.ARCEUUS_THRALL_SKELETON_GREATER,
		NpcID.THRALL_IMP_RANGED_LESSER, NpcID.THRALL_IMP_RANGED_SUPERIOR, NpcID.THRALL_IMP_RANGED_GREATER
	);

	private static final Set<Integer> ZOMBIE_THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_ZOMBIE_LESSER, NpcID.ARCEUUS_THRALL_ZOMBIE_SUPERIOR, NpcID.ARCEUUS_THRALL_ZOMBIE_GREATER,
		NpcID.THRALL_IMP_MELEE_LESSER, NpcID.THRALL_IMP_MELEE_SUPERIOR, NpcID.THRALL_IMP_MELEE_GREATER
	);



	@Inject
	private Client client;

	@Inject
	private thrallHighlighterConfig config;

	@Inject
	private RenderCallbackManager renderCallbackManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ModelOutlineRenderer modelOutlineRenderer;

	private final Set<NPC> activeThralls = Collections.newSetFromMap(new IdentityHashMap<>());

	private boolean hideThralls;
	private boolean outlineThralls;
	private boolean enableThrallTypeOverride;
	private int outlineWidth;
	private Color outlineColor;
	private Color ghostThrallColor;
	private Color skeletonThrallColor;
	private Color zombieThrallColor;

	private final Overlay thrallOutlineOverlay = new Overlay()
	{
		{
			setPosition(OverlayPosition.DYNAMIC);
			setLayer(OverlayLayer.ABOVE_SCENE);
			setPriority(0.0f);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			if (!outlineThralls)
			{
				return null;
			}

			for (NPC npc : activeThralls)
			{
				if (!npc.isDead())
				{
					Color color = enableThrallTypeOverride ? getThrallColor(npc) : outlineColor;
					modelOutlineRenderer.drawOutline(npc, outlineWidth, color, 0);
				}
			}

			return null;
		}
	};

	@Override
	protected void startUp() throws Exception
	{
		updateConfig();
		initializeActiveThralls();
		renderCallbackManager.register(this);
		overlayManager.add(thrallOutlineOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		renderCallbackManager.unregister(this);
		overlayManager.remove(thrallOutlineOverlay);
		activeThralls.clear();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		if (e.getGroup().equals(thrallHighlighterConfig.GROUP))
		{
			updateConfig();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned e)
	{
		NPC npc = e.getNpc();
		if (THRALL_IDS.contains(npc.getId()))
		{
			activeThralls.add(npc);
		}
	}

	@Subscribe
	public void onNpcChanged(NpcChanged e)
	{
		NPC npc = e.getNpc();
		if (THRALL_IDS.contains(npc.getId()))
		{
			activeThralls.add(npc);
		}
		else
		{
			activeThralls.remove(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned e)
	{
		activeThralls.remove(e.getNpc());
	}

	private void updateConfig()
	{
		hideThralls = config.hideThralls();
		outlineThralls = config.outlineThralls();
		enableThrallTypeOverride = config.enableThrallTypeOverride();
		outlineWidth = config.outlineWidth();

		int outlineOpacity = config.outlineOpacity();
		outlineColor = applyOpacity(config.outlineColor(), outlineOpacity);
		ghostThrallColor = applyOpacity(config.ghostThrallColor(), outlineOpacity);
		skeletonThrallColor = applyOpacity(config.skeletonThrallColor(), outlineOpacity);
		zombieThrallColor = applyOpacity(config.zombieThrallColor(), outlineOpacity);
	}

	private void initializeActiveThralls()
	{
		activeThralls.clear();
		for (NPC npc : client.getWorldView(-1).npcs())
		{
			if (THRALL_IDS.contains(npc.getId()))
			{
				activeThralls.add(npc);
			}
		}
	}

	@Provides
	thrallHighlighterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(thrallHighlighterConfig.class);
	}

	private Color getThrallColor(NPC npc)
	{
		int npcId = npc.getId();

		if (GHOST_THRALL_IDS.contains(npcId))
		{
			return ghostThrallColor;
		}
		else if (SKELETON_THRALL_IDS.contains(npcId))
		{
			return skeletonThrallColor;
		}
		else if (ZOMBIE_THRALL_IDS.contains(npcId))
		{
			return zombieThrallColor;
		}

		return outlineColor;
	}

	private static Color applyOpacity(Color configuredColor, int opacityPercent)
	{
		int clampedOpacity = Math.max(0, Math.min(100, opacityPercent));
		int pickerAlpha = configuredColor.getAlpha();
		int globalAlpha = Math.round(255 * clampedOpacity / 100.0f);
		int combinedAlpha = Math.round(pickerAlpha * globalAlpha / 255.0f);

		return ColorUtil.colorWithAlpha(configuredColor, combinedAlpha);
	}

	@Override
	public boolean addEntity(Renderable renderable, boolean ui)
	{
		if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;

			if (THRALL_IDS.contains(npc.getId()))
			{
				return !hideThralls;
			}
		}

		return true;
	}
}
