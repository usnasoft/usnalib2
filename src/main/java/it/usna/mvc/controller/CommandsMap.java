package it.usna.mvc.controller;

import java.awt.event.ActionEvent;
import java.util.EnumMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


public class CommandsMap<K extends Enum<K>> extends EnumMap<K, Action> {
	private static final long serialVersionUID = 1L;
	private final Controller controller;

	public CommandsMap(final Class<K> keyType, final Controller controller) {
		super(keyType);
		this.controller = controller;
	}

	public void put(final K command, final String name) {
		put(command, name, null, null, null, false);
	}
	
	public void put(final K command, final String name, final String tooltip) {
		put(command, name, null, tooltip, null, false);
	}
	
	public void put(final K command, final String name, final KeyStroke accelerator, final String tooltip, final String icon) {
		put(command, name, accelerator, tooltip, icon, false);
	}
	
	public void put(final K command, final String name, final KeyStroke accelerator, final String tooltip, final String icon, final boolean active) {
		super.put(command, new AbstractAction(name) {
			private static final long serialVersionUID = 1L;
			{
				if(accelerator != null) {
					putValue(AbstractAction.ACCELERATOR_KEY, accelerator);
				}
				if(tooltip != null) {
					putValue(AbstractAction.SHORT_DESCRIPTION, tooltip);
				}
				if(icon != null) {
					putValue(AbstractAction.LARGE_ICON_KEY, new ImageIcon(getClass().getResource(icon)));
				}
				setEnabled(active);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.signalTopmostView(command);
			}
		});
	}
}
