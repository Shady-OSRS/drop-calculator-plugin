package com.github.shadyosrs.dropcalculator;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil; // NOSSO NOVO LEITOR DE IMAGENS
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
		name = "Drop Calculator"
)
public class DropCalculatorPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	private DropCalculatorPanel panel;
	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception {
		panel = new DropCalculatorPanel();

		// 1. Usamos a ferramenta do RuneLite para buscar o arquivo "icon.png" na pasta resources
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

		// 2. Colocamos o ícone no nosso botão
		navButton = NavigationButton.builder()
				.tooltip("Drop Calculator")
				.icon(icon)
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(navButton);
	}

	@Provides
	DropCalculatorConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(DropCalculatorConfig.class);
	}
}