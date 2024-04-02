package it.usna.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

//import it.usna.swing.SyntaxEditor;
import it.usna.swing.SyntaxEditor2;
import it.usna.swing.TextLineNumber;
import it.usna.swing.UsnaSwingUtils;

public class SyntacticTextEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("deprecation")
	public final static int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // getMenuShortcutKeyMaskEx() from java 10

	public SyntacticTextEditor() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SyntacticTextEditor.class.getResource("/img/usna16.gif")));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("USNA syntactic editor");
		
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, Font.MONOSPACED);
		SyntaxEditor2 editor = new SyntaxEditor2(style);
		
		//Java
		Style styleComment = editor.addStyle("usna_red", null);
		StyleConstants.setForeground(styleComment, Color.RED);
		editor.addSyntaxRule(new SyntaxEditor2.BlockSimpleSyntax("//", "\n", styleComment));
		editor.addSyntaxRule(new SyntaxEditor2.BlockSimpleSyntax("/*", "*/", styleComment));
		
		Style styleStr = editor.addStyle("usna_green", null);
		StyleConstants.setForeground(styleStr, new Color(0, 120, 0));
		editor.addSyntaxRule(new SyntaxEditor2.BlockSimpleSyntax("\"", "\"", "\\", styleStr));
		editor.addSyntaxRule(new SyntaxEditor2.BlockSimpleSyntax("'", "'", "\\", styleStr));
		
		Style styleBrachets = editor.addStyle("usna_brachets", null);
		StyleConstants.setBold(styleBrachets, true);
		editor.addSyntaxRule(new SyntaxEditor2.Keywords(new String[] {"{", "}", "[", "]"}, styleBrachets));
		
		Style styleOperators = editor.addStyle("usna_brachets", null);
		StyleConstants.setForeground(styleOperators, new Color(150, 0, 0));
		editor.addSyntaxRule(new SyntaxEditor2.Keywords(new String[] {"=", "+", "-", "*", "/", "%", "<", ">", "&", "|", "!"}, styleOperators));
		
		Style styleReserved = editor.addStyle("usna_styleReserved", null);
		StyleConstants.setBold(styleReserved, true);
		StyleConstants.setForeground(styleReserved, Color.blue);
		editor.addSyntaxRule(new SyntaxEditor2.DelimitedKeywords(new String[] {
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
		
		setContentPane(jContentPane);
		setSize(600, 400);

		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] arg) throws Exception {
		UsnaSwingUtils.setLookAndFeel(UsnaSwingUtils.LF_NIMBUS);
//		UsnaSwingUtils.initializeFontSize(2f);
		new SyntacticTextEditor();
	}
}

// https://docs.oracle.com/javase/8/docs/api/javax/swing/undo/CompoundEdit.html