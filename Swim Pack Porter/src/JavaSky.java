import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

public class JavaSky extends SwingWorker<Void, String> {
	public static void cubemapToSky(String cubemapPath, String exportPath, Boolean direct) throws IOException {
		if (direct) {
			Window.textArea.setText(" Currently Porting Sky..");
			Window.textArea.setForeground(Color.WHITE);
			Window.textArea.setSelectedTextColor(Color.WHITE);
		}
		try {
			BufferedImage cubemap0 = ImageIO.read(new File(cubemapPath + "\\cubemap_0.png")); // top right corner
			BufferedImage cubemap1 = ImageIO.read(new File(cubemapPath + "\\cubemap_1.png")); // bottom left corner
			BufferedImage cubemap2 = ImageIO.read(new File(cubemapPath + "\\cubemap_2.png")); // bottom middle
			BufferedImage cubemap3 = ImageIO.read(new File(cubemapPath + "\\cubemap_3.png")); // bottom right corner
			BufferedImage cubemap4 = ImageIO.read(new File(cubemapPath + "\\cubemap_4.png")); // flipped 180 top middle corner
			BufferedImage cubemap5 = ImageIO.read(new File(cubemapPath + "\\cubemap_5.png")); // flipped 180 top left corner
			int width = cubemap0.getWidth() * 3;
			int height = cubemap0.getHeight() * 2;
			// now for graphics work
			BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = canvas.getGraphics();
			g.setColor(Color.LIGHT_GRAY); // background color
			g.fillRect(0, 0, width, height); // draw background

			// rotate cubemap4
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			tx.translate(cubemap4.getWidth(null) * -1, cubemap4.getHeight(null) * -1);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			BufferedImage cubemap4Rotated = op.filter(cubemap4, null);

			// rotate cubemap5
			AffineTransform tx2 = AffineTransform.getScaleInstance(-1, -1);
			AffineTransformOp op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			tx2.translate(cubemap5.getWidth(null) * -1, cubemap5.getHeight(null) * -1);
			op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			BufferedImage cubemap5Rotated = op2.filter(cubemap5, null);

			// place cubemaps accordingly
			g.drawImage(cubemap0, cubemap0.getWidth() * 2, 0, cubemap0.getWidth(), cubemap0.getHeight(), null);
			g.drawImage(cubemap1, 0, cubemap1.getHeight(), cubemap1.getWidth(), cubemap1.getHeight(), null);
			g.drawImage(cubemap2, cubemap2.getWidth(), cubemap2.getHeight(), cubemap2.getWidth(), cubemap2.getHeight(), null);
			g.drawImage(cubemap3, cubemap3.getWidth() * 2, cubemap3.getHeight(), cubemap3.getWidth(), cubemap3.getHeight(), null);
			g.drawImage(cubemap4Rotated, cubemap4.getWidth(), 0, cubemap4.getWidth(), cubemap4.getHeight(), null); // rotated 180
			g.drawImage(cubemap5Rotated, 0, 0, cubemap5.getWidth(), cubemap5.getHeight(), null); // rotated 180
			g.dispose(); // when done
			ImageIO.write(canvas, "png", new File(exportPath + "\\sky.png"));
			// out put text
			if (direct) {
				Window.textArea.setText("");
				Window.updateConsoleText("Ported sky:\n " + exportPath, false);
			}
			// enable file actions again
			Window.busy = false;
			System.out.println("no longer busy");
		} catch (Exception e) {
			if (direct) {
				Window.textArea.setText("");
				Window.updateConsoleText("Failed to Port Sky\n Possibly missing cubemap images or wrong directory was chosen!", true);
				Window.busy = false;
				File ep = new File(exportPath);
				if (ep.exists()) {
					FileUtils.forceDelete(ep);
				}
				System.out.println("no longer busy");
			}
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		try {
			System.out.println("JAVA SKY PORTER THREAD RUNNING");
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
			LocalDateTime now = LocalDateTime.now();
			String skyLocation = Window.configs.get(2) + "\\java sky " + dtf.format(now) + "\\";
			JavaSky.cubemapToSky(Window.workerSkyPath, skyLocation, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
