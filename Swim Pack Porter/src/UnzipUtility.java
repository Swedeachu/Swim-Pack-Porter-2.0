import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipUtility {
	public static void extractFolder(String zipFile) throws IOException {
		int buffer = 2048;
		File file = new File(zipFile);
		try (ZipFile zip = new ZipFile(file)) {
			String newPath = zipFile.substring(0, zipFile.length() - 4);
			new File(newPath).mkdir();
			Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
			// Process each entry
			while (zipFileEntries.hasMoreElements()) {
				// grab a zip file entry
				ZipEntry entry = zipFileEntries.nextElement();
				String currentEntry = entry.getName();
				File destFile = new File(newPath, currentEntry);
				File destinationParent = destFile.getParentFile();
				// System.out.println("extracting: " +currentEntry);
				if (currentEntry.contains("manifest.json")) {
					String[] packName = currentEntry.split("/", 2);
					System.out.println("pack name:" + packName[0]);
					JavaPort.tempPackName = packName[0];
				}
				// create the parent directory structure if needed
				destinationParent.mkdirs();
				if (!entry.isDirectory()) {
					BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					byte[] data = new byte[buffer];
					// write the current file to disk
					FileOutputStream fos = new FileOutputStream(destFile);
					try (BufferedOutputStream dest = new BufferedOutputStream(fos, buffer)) {
						// read and write until last byte is encountered
						while ((currentByte = is.read(data, 0, buffer)) != -1) {
							dest.write(data, 0, currentByte);
						}
						dest.flush();
						is.close();
					}
				}
				if (currentEntry.endsWith(".zip")) {
					// found a zip file, try to open
					extractFolder(destFile.getAbsolutePath());
				}
			}
		}
	}
}
