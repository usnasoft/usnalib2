package it.usna.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import it.usna.swing.UsnaSwingUtils;
import it.usna.swing.dialog.FindReplaceDialog;
import it.usna.swing.texteditor.SyntaxEditor;
import it.usna.swing.texteditor.TextLineNumber;

/**
 * Usage example for: <br>
 * <code>
 * it.usna.swing.texteditor.SyntaxEditor
 * it.usna.swing.texteditor.TextLineNumber
 * it.usna.swing.dialog.FindReplaceDialog
 * </code>
 * @author usna
 */
public class SyntacticTextEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("deprecation")
	public static final int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // getMenuShortcutKeyMaskEx() from java 10

	public SyntacticTextEditor() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SyntacticTextEditor.class.getResource("/img/usna16.gif")));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("USNA syntactic editor");
		
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, Font.MONOSPACED);
		SyntaxEditor editor = new SyntaxEditor(style);
		
		//Java
		Style styleComment = editor.addStyle("usna_red", null);
		StyleConstants.setForeground(styleComment, Color.RED);
		editor.addSyntaxRule(new SyntaxEditor.BlockSimpleSyntax("//", "\n", styleComment));
		editor.addSyntaxRule(new SyntaxEditor.BlockSimpleSyntax("/*", "*/", styleComment));
		
		Style styleStr = editor.addStyle("usna_green", null);
		StyleConstants.setForeground(styleStr, new Color(0, 120, 0));
		editor.addSyntaxRule(new SyntaxEditor.BlockSimpleSyntax("\"", "\"", "\\", styleStr));
		editor.addSyntaxRule(new SyntaxEditor.BlockSimpleSyntax("'", "'", "\\", styleStr));
		
		Style styleBrachets = editor.addStyle("usna_brachets", null);
		StyleConstants.setBold(styleBrachets, true);
		editor.addSyntaxRule(new SyntaxEditor.Keywords(new String[] {"{", "}", "[", "]"}, styleBrachets));
		
		Style styleOperators = editor.addStyle("usna_brachets", null);
		StyleConstants.setForeground(styleOperators, new Color(150, 0, 0));
		editor.addSyntaxRule(new SyntaxEditor.Keywords(new String[] {"=", "+", "-", "*", "/", "%", "<", ">", "&", "|", "!"}, styleOperators));
		
		Style styleReserved = editor.addStyle("usna_styleReserved", null);
		StyleConstants.setBold(styleReserved, true);
		StyleConstants.setForeground(styleReserved, Color.blue);
		editor.addSyntaxRule(new SyntaxEditor.DelimitedKeywords(new String[] {
				"abstract",	"continue",	"for", "new", "switch",
				"assert",	"default",	"goto",	"package", "synchronized",
				"boolean", "do", "if", "private", "this",
				"break", "double", "implements", "protected", "throw",
				"byte", "else", "import", "public", "throws",
				"case", "enum", "instanceof", "return", "transient",
				"catch", "extends", "int", "short", "try",
				"char", "final", "interface", "static", "void",
				"class", "finally", "long", "strictfp", "volatile",
				"const", "float", "native", "super", "while"}, styleReserved));
		
		// Json
//		Style styleBrachets = editor.addStyle("usna_brachets", null);
//		StyleConstants.setBold(styleBrachets, true);
//		editor.addSyntaxRule(new SyntaxEditor.Keywords(new String[] {"{", "}", "[", "]"}, styleBrachets));
//
//		Style styleStr = editor.addStyle("usna_red", null);
//		StyleConstants.setForeground(styleStr, Color.red);
//		editor.addSyntaxRule(new SyntaxEditor.BlockRegExpSyntax(":\\s*(\")", "(\")", "\\", styleStr));
//		
//		Style styleAll = editor.addStyle("usna_green", null);
//		StyleConstants.setForeground(styleAll, /*new Color(0, 120, 0)*/Color.green);
//		editor.addSyntaxRule(new SyntaxEditor.BlockRegExpSyntax(":\\s*([\\w-])", "[,\\n]", null, styleAll));
//		
//		Style styleBlue = editor.addStyle("usna_blue", null);
//		StyleConstants.setForeground(styleBlue, Color.blue);
//		editor.addSyntaxRule(new SyntaxEditor.BlockRegExpSyntax("(\")", "(\")", "\\", styleBlue));

		editor.activateUndo();
		editor.setTabSize(4);
				
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
		
		editor.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, SHORTCUT_KEY), "find_usna");
		editor.getActionMap().put("find_usna", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				FindReplaceDialog f = new FindReplaceDialog(SyntacticTextEditor.this, editor, true);
				f.setLocationRelativeTo(SyntacticTextEditor.this);
				f.setVisible(true);
			}
		});
		
		setContentPane(jContentPane);
		setSize(600, 400);

		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] arg) throws Exception {
		UsnaSwingUtils.setLookAndFeel(UsnaSwingUtils.LF_NIMBUS);
		new SyntacticTextEditor();
	}
}