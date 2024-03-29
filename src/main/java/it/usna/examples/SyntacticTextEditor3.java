package it.usna.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;

import it.usna.swing.SyntaxEditor3;
import it.usna.swing.UsnaSwingUtils;

public class SyntacticTextEditor3 extends JFrame {
	private static final long serialVersionUID = 1L;
	public final static int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	public SyntacticTextEditor3() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SyntacticTextEditor.class.getResource("/img/usna16.gif")));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("USNA syntactic editor");
		
		SyntaxEditor3 editor = new SyntaxEditor3();
		Style styleComment = editor.addStyle("usna_red", null);
		StyleConstants.setForeground(styleComment, Color.RED);
		editor.addSyntax(new SyntaxEditor3.BlockSyntax("//", "\n", styleComment));
		editor.addSyntax(new SyntaxEditor3.BlockSyntax("/*", "*/", styleComment));
		Style styleStr = editor.addStyle("usna_green", null);
		StyleConstants.setForeground(styleStr, Color.GREEN);
		editor.addSyntax(new SyntaxEditor3.BlockSyntax("\"", "\"", "\\", styleStr));
		Style styleBrachets = editor.addStyle("usna_brachets", null);
		StyleConstants.setBold(styleBrachets, true);
		editor.addKeywords(new SyntaxEditor3.Keywords(new String[] {"{", "}"}, styleBrachets));

		UndoManager manager = editor.activateUndo();
		
		JPanel jContentPane = new JPanel();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(editor);
		jContentPane.setLayout(new BorderLayout());
		
		jContentPane.add(scrollPane, BorderLayout.CENTER);
		
		AbstractAction undoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(manager.canUndo()) {
					manager.undo();
				}
			}
		};
		editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_KEY), "undo_usna");
		editor.getActionMap().put("undo_usna", undoAction);
		
		AbstractAction redoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(manager.canRedo()) {
					manager.redo();
				}
			}
		};
		editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, SHORTCUT_KEY), "redo_usna");
		editor.getActionMap().put("redo_usna", redoAction);
		
		setContentPane(jContentPane);
		setSize(600, 400);
//		center();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] arg) throws Exception {
		UsnaSwingUtils.setLookAndFeel(UsnaSwingUtils.LF_NIMBUS);
		new SyntacticTextEditor3();
	}
}