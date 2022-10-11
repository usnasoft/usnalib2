package it.usna.swing;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * TransferableImage simple Image Transferable.<br>
 * Usage:
 * <pre>
 * Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
 * TransferableImage trans = new TransferableImage(image);
 * c.setContents(trans, null);
 * </pre>  
 * @author a.flaccomio
 */
public class TransferableImage implements Transferable {
	private Image img;

	public TransferableImage(Image i) {
		this.img = i;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.equals(DataFlavor.imageFlavor) && img != null) {
			return img;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {DataFlavor.imageFlavor};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(DataFlavor.imageFlavor);
	}
}