package it.usna.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import it.usna.swing.SyntaxEditor2;
import it.usna.swing.TextLineNumber;
import it.usna.swing.UsnaSwingUtils;

public class SyntacticTextEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	public final static int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	public SyntacticTextEditor() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SyntacticTextEditor.class.getResource("/img/usna16.gif")));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("USNA syntactic editor");
		
		SyntaxEditor2 editor = new SyntaxEditor2();
		Style styleComment = editor.addStyle("usna_red", null);
		StyleConstants.setForeground(styleComment, Color.RED);
		editor.addSyntax(new SyntaxEditor2.BlockSyntax("//", "\n", styleComment));
		editor.addSyntax(new SyntaxEditor2.BlockSyntax("/*", "*/", styleComment));
		Style styleStr = editor.addStyle("usna_green", null);
		StyleConstants.setForeground(styleStr, Color.GREEN);
		editor.addSyntax(new SyntaxEditor2.BlockSyntax("\"", "\"", "\\", styleStr));
		Style styleBrachets = editor.addStyle("usna_brachets", null);
		StyleConstants.setBold(styleBrachets, true);
		editor.addKeywords(new SyntaxEditor2.Keywords(new String[] {"{", "}"}, styleBrachets));

		editor.activateUndo();
		
		JPanel jContentPane = new JPanel();
		JScrollPane scrollPane = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		TextLineNumber lineNum = new TextLineNumber(editor);
		lineNum.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		scrollPane.setRowHeaderView(lineNum);
		
		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(scrollPane, BorderLayout.CENTER);
		
		editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_KEY), "undo_usna");
		editor.getActionMap().put("undo_usna", editor.getUndoAction());

		editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, SHORTCUT_KEY), "redo_usna");
		editor.getActionMap().put("redo_usna", editor.getRedoAction());
		
		AbstractAction font_plus = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				UsnaSwingUtils.initializeFontSize(2.2f);
				SwingUtilities.updateComponentTreeUI(getContentPane());
			}
		};
		editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, SHORTCUT_KEY), "font_plus");
		editor.getActionMap().put("font_plus", font_plus);
		
		setContentPane(jContentPane);
		setSize(600, 400);
//		center();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] arg) throws Exception {
		UsnaSwingUtils.setLookAndFeel(UsnaSwingUtils.LF_NIMBUS);
		
		new SyntacticTextEditor();
	}
}