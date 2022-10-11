package it.usna.mvc.controller;

import javax.swing.Action;

/**
 * Implement this interface in the Controller based class to easily control
 * the status of any action from views/models without the knowledge of the
 * controller implementation class.<p>
 * Typical use is: define an enum to identify actions; instantiate a EnumMap (comMap)
 * in the controller to associate enum members to the actions; implement this interface
 * like this:<p><code>
 *	public void enableCommand(final Enum<?> command, final boolean enable) {<br>
		comMap.get(command).setEnabled(enable);<br>
	}<br>

	public Action getCommand(final Enum<?> command) {<br>
		return comMap.get(command);<br>
	}</code><p>
 * CommandsMap could be used instead of EnumMap to simplify this task.
 * @author antonio
 *
 */
public interface CommandsManager {

	public void enableCommand(final Enum<?> command, final boolean enable);
	
	public Action getCommand(final Enum<?> command);
}
