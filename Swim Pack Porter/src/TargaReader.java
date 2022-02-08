import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// code forked from https://stackoverflow.com/a/1514096/14894746
// code also forked from https://stackoverflow.com/a/59992724/14894746
// no point in spending hours on this type of thing when its already been done

public class TargaReader {
	
	// TGA to PNG
	
	public static BufferedImage getImage(String fileName) throws IOException {
		File f = new File(fileName);
		byte[] buf = new byte[(int) f.length()];
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
		bis.read(buf);
		bis.close();
		return decode(buf);
	}

	private static int offset;

	private static int btoi(byte b) {
		int a = b;
		return (a < 0 ? 256 + a : a);
	}

	private static int read(byte[] buf) {
		return btoi(buf[offset++]);
	}

	public static BufferedImage decode(byte[] buf) throws IOException {
		offset = 0;
		// Reading header
		for (int i = 0; i < 12; i++)
		read(buf);
		int width = read(buf) + (read(buf) << 8);
		int height = read(buf) + (read(buf) << 8);
		read(buf);
		read(buf);
		// Reading data
		int n = width * height;
		int[] pixels = new int[n];
		int idx = 0;
		while (n > 0) {
			int nb = read(buf);
			if ((nb & 0x80) == 0) {
				for (int i = 0; i <= nb; i++) {
					int b = read(buf);
					int g = read(buf);
					int r = read(buf);
					pixels[idx++] = 0xff000000 | (r << 16) | (g << 8) | b;
				}
			} else {
				nb &= 0x7f;
				int b = read(buf);
				int g = read(buf);
				int r = read(buf);
				int v = 0xff000000 | (r << 16) | (g << 8) | b;
				for (int i = 0; i <= nb; i++)
					pixels[idx++] = v;
			}
			n -= nb + 1;
		}
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bimg.setRGB(0, 0, width, height, pixels, 0, width);
		return bimg;
	}

	// PNG to TGA
	
	public static void writeTargaImageTo(Path file, BufferedImage image) throws IOException {
		try (WritableByteChannel channel = Files.newByteChannel(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			writeTargaImageTo(channel, image);
		}
	}

	public static void writeTargaImageTo(WritableByteChannel out, BufferedImage image) throws IOException {
		short originX = (short) image.getMinX();
		short originY = (short) image.getMinY();
		short width = (short) image.getWidth();
		short height = (short) image.getHeight();
		ByteBuffer header = ByteBuffer.allocate(18);
		header.order(ByteOrder.LITTLE_ENDIAN);
		header.put((byte) 0); // Length of string Identification
		header.put((byte) 0); // No colormap
		header.put((byte) 10); // Image type: RLE compressed RGB
		header.putShort((short) 0); // Colormap type (irrelevant, no colormap)
		header.putShort((short) 0); // Colormap first index (irrelevant)
		header.put((byte) 0); // Colormap bits per entry (irrelevant)
		header.putShort(originX); 
		header.putShort(originY); 
		header.putShort(width); 
		header.putShort(height); 
		header.put((byte) 32); // Bits per pixel
		header.put((byte) (8 | (1 << 5))); // 6 is Number of alpha bits, 1 << 5 = Origin which is upper left
		header.flip();
		out.write(header);
		int lastPixel = 0;
		int runLength = 0;
		ByteBuffer rlePacket = ByteBuffer.allocate(1 + 4);
		ByteBuffer rawPacket = ByteBuffer.allocate(1 + 128 * 4);
		rlePacket.order(ByteOrder.LITTLE_ENDIAN);
		rawPacket.order(ByteOrder.LITTLE_ENDIAN);
		rawPacket.put((byte) 0); // placeholder for header
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = image.getRGB(originX + x, originY + y);
				if (x == 0 && y == 0) {
					lastPixel = pixel;
					runLength = 1;
					rawPacket.putInt(pixel);
					continue;
				}
				
				boolean finalPixel = (x == width - 1 && y == height - 1);
				
				if (finalPixel) {
					if (pixel == lastPixel) {
						runLength++;
					} else {
						rawPacket.putInt(pixel);
					}
				}
				
				if (runLength >= 128 || (runLength > 1 && (finalPixel || pixel != lastPixel))) {
					rlePacket.clear();
					rlePacket.put((byte) (0x80 | (runLength - 1)));
					rlePacket.putInt(lastPixel);
					rlePacket.flip();
					out.write(rlePacket);
					runLength = 0;
					rawPacket.clear();
					rawPacket.put((byte) 0); // placeholder for header
				}

				if (!rawPacket.hasRemaining() || (rawPacket.position() > 1 && (finalPixel || pixel == lastPixel))) {
					// "Forget" duplicated pixel, since it will be in RLE
					if (!finalPixel) {
						rawPacket.position(rawPacket.position() - 4);
					}
					int rawPixelCount = (rawPacket.position() - 1) / 4;
					if (rawPixelCount > 0) {
						rawPacket.put(0, (byte) (rawPixelCount - 1));
						rawPacket.flip();
						out.write(rawPacket);
					}
					runLength = 1;
					rawPacket.clear();
					rawPacket.put((byte) 0); // placeholder for header
				}

				if (!finalPixel) {
					if (pixel == lastPixel) {
						runLength++;
					} else {
						rawPacket.putInt(pixel);
						lastPixel = pixel;
					}
				}
			}
		}
	}
}