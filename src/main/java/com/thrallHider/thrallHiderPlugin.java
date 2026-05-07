package com.thrallHider;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Renderable;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Slf4j
@PluginDescriptor(
	name = "Thrall Hider"
)
public class thrallHiderPlugin extends Plugin implements RenderCallback
{
	private static final Set<Integer> THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_GHOST_LESSER, NpcID.ARCEUUS_THRALL_SKELETON_LESSER, NpcID.ARCEUUS_THRALL_ZOMBIE_LESSER,  // Lesser Thrall (ghost, skeleton, zombie)
		NpcID.ARCEUUS_THRALL_GHOST_SUPERIOR, NpcID.ARCEUUS_THRALL_SKELETON_SUPERIOR, NpcID.ARCEUUS_THRALL_ZOMBIE_SUPERIOR,  // Superior Thrall (ghost, skeleton, zombie)
		NpcID.ARCEUUS_THRALL_GHOST_GREATER, NpcID.ARCEUUS_THRALL_SKELETON_GREATER, NpcID.ARCEUUS_THRALL_ZOMBIE_GREATER   // Greater Thrall (ghost, skeleton, zombie)
	);

	private static final Set<Integer> GHOST_THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_GHOST_LESSER, NpcID.ARCEUUS_THRALL_GHOST_SUPERIOR, NpcID.ARCEUUS_THRALL_GHOST_GREATER
	);

	private static final Set<Integer> SKELETON_THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_SKELETON_LESSER, NpcID.ARCEUUS_THRALL_SKELETON_SUPERIOR, NpcID.ARCEUUS_THRALL_SKELETON_GREATER
	);

	private static final Set<Integer> ZOMBIE_THRALL_IDS = ImmutableSet.of(
		NpcID.ARCEUUS_THRALL_ZOMBIE_LESSER, NpcID.ARCEUUS_THRALL_ZOMBIE_SUPERIOR, NpcID.ARCEUUS_THRALL_ZOMBIE_GREATER
	);

	@Inject
	private Client client;

	@Inject
	private thrallHiderConfig config;

	@Inject
	private RenderCallbackManager renderCallbackManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ModelOutlineRenderer modelOutlineRenderer;

	private boolean hideThralls;

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
			if (!config.outlineThralls())
			{
				return null;
			}
			for (NPC npc : client.getWorldView(-1).npcs())
			{
				if (THRALL_IDS.contains(npc.getId()) && !npc.isDead())
				{
					Color outlineColor = config.outlineThralls() && config.enableThrallTypeOverride() ? getThrallColor(npc) : config.outlineColor();
					modelOutlineRenderer.drawOutline(npc, config.outlineWidth(), outlineColor, 0);
				}
			}

			return null;
		}
	};

	@Override
	protected void startUp() throws Exception
	{
		updateConfig();
		renderCallbackManager.register(this);
		overlayManager.add(thrallOutlineOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		renderCallbackManager.unregister(this);
		overlayManager.remove(thrallOutlineOverlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		if (e.getGroup().equals(thrallHiderConfig.GROUP))
		{
			updateConfig();
		}
	}

	private void updateConfig()
	{
		hideThralls = config.hideThralls();
	}

	@Provides
	thrallHiderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(thrallHiderConfig.class);
	}

	private Color getThrallColor(NPC npc)
	{
		int npcId = npc.getId();

		if (GHOST_THRALL_IDS.contains(npcId))
		{
			return config.ghostThrallColor();
		}
		else if (SKELETON_THRALL_IDS.contains(npcId))
		{
			return config.skeletonThrallColor();
		}
		else if (ZOMBIE_THRALL_IDS.contains(npcId))
		{
			return config.zombieThrallColor();
		}

		return config.outlineColor();
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
