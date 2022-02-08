import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

public class SkyPort extends SwingWorker<Void, String> {
	public static final int ROTATE_LEFT = 1;
	public static final int ROTATE_RIGHT = -1;

	public static void sky(File file, String packName) throws IOException {
		Window.textArea.setText(" Currently Porting Sky..");
		Window.textArea.setForeground(Color.WHITE);
		Window.textArea.setSelectedTextColor(Color.WHITE);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		new File(Window.configs.get(2) + "\\overworld_cubemap " + dtf.format(now) + "\\").mkdirs(); // new cubemap folder
		String skyLocation = Window.configs.get(2) + "\\overworld_cubemap " + dtf.format(now) + "\\";
		BufferedImage skyMap = ImageIO.read(new File(file.getAbsolutePath()));
		System.out.println("sky image selected: " + file.getAbsolutePath() + " " + skyMap);
		if (skyMap != null) {
			System.out.println("porting sky : " + skyMap);
			int x = skyMap.getWidth() / 3;
			int y = skyMap.getHeight() / 2;
			File skyCheck = new File(skyLocation);
			if (!skyCheck.exists()) {
				new File(skyLocation).mkdirs();
			}
			// top right (CORRECT)
			ImageIO.write(skyMap.getSubimage(skyMap.getHeight(), 0, x, y), "png", new File(skyLocation + "cubemap_" + 0 + ".png"));
			// bottom left (CORRECT)
			ImageIO.write(skyMap.getSubimage(0, skyMap.getHeight() / 2, x, y), "png", new File(skyLocation + "cubemap_" + 1 + ".png"));
			// bottom middle (CORRECT)
			ImageIO.write(skyMap.getSubimage(skyMap.getWidth() / 3, skyMap.getHeight() / 2, x, y), "png", new File(skyLocation + "cubemap_" + 2 + ".png"));
			// bottom right (CORRECT)
			ImageIO.write(skyMap.getSubimage(skyMap.getHeight(), skyMap.getHeight() / 2, x, y), "png", new File(skyLocation + "cubemap_" + 3 + ".png"));
			// top middle (CORRECT) // need to do 180 degree flip
			ImageIO.write(skyMap.getSubimage(skyMap.getWidth() / 3, 0, x, y), "png", new File(skyLocation + "cubemap_" + 4 + ".png"));
			File input = new File(skyLocation + "cubemap_" + 4 + ".png");
			BufferedImage lol = ImageIO.read(new File(input.getAbsolutePath()));
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			tx.translate(lol.getWidth(null) * -1, lol.getHeight(null) * -1);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			BufferedImage image = op.filter(lol, null);
			File replace = new File(skyLocation + "cubemap_" + 4 + ".png");
			ImageIO.write(image, "png", replace);
			// top left (CORRECT) // need to do 180 degree flip
			ImageIO.write(skyMap.getSubimage(0, 0, x, y), "png", new File(skyLocation + "cubemap_" + 5 + ".png"));
			File input2 = new File(skyLocation + "cubemap_" + 5 + ".png");
			BufferedImage lol2 = ImageIO.read(new File(input2.getAbsolutePath()));
			AffineTransform tx2 = AffineTransform.getScaleInstance(-1, -1);
			AffineTransformOp op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			tx2.translate(lol2.getWidth(null) * -1, lol2.getHeight(null) * -1);
			op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			BufferedImage image2 = op2.filter(lol2, null);
			File replace2 = new File(skyLocation + "cubemap_" + 5 + ".png");
			ImageIO.write(image2, "png", replace2);
			// output text
			Window.textArea.setText("");
			Window.updateConsoleText("Ported Sky To:\n " + skyLocation, false);
			// enable file actions again
			Window.busy = false;
			System.out.println("no longer busy");
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		try {
			System.out.println("BEDROCK SKY PORTER THREAD RUNNING");
			SkyPort.sky(new File(Window.workerSkyPath), "overworld_cubemap");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
