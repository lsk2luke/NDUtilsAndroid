package com.nelepovds.ndutils.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Cache {
	public File cacheDir;

	public Cache(File pCacheDir) {
		this.cacheDir = pCacheDir;
	}

	public File getFileFromCache(String fileName) {
		File retFile = null;
		if (this.isFileExtistInCache(fileName)) {
			retFile = new File(this.cacheDir, fileName);
		}
		return retFile;
	}

	public Boolean isFileExtistInCache(String fileName) {
		Boolean retResult = false;
		File fileInCache = new File(this.cacheDir, fileName);
		retResult = fileInCache.exists();
		return retResult;
	}

	public String loadFile(String fileName) {
		String fileContent = "";
		File jsonLastSaved = new File(this.cacheDir, fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(jsonLastSaved);
			fileContent = this.getFileContent(fis);
		} catch (FileNotFoundException e) {
			this.saveFile(fileName, fileContent);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileContent;
	}

	/**
	 * Save file with name and content
	 * 
	 * @param fileName
	 *            - name of file to be saved
	 * @param content
	 *            - content to be written to file
	 * @return Object of class File if success. Else return Null
	 */
	public File saveFile(String fileName, String content) {
		File fileSaved = null;
		BufferedWriter bufferWriter = null;
		try {
			fileSaved = new File(this.cacheDir, fileName);
			fileSaved.mkdirs();
			fileSaved.delete();
			fileSaved.createNewFile();
			bufferWriter = new BufferedWriter(new FileWriter(fileSaved));
			bufferWriter.write(content);

		} catch (IOException e) {
			e.printStackTrace();
			fileSaved = null;
		} finally {
			if (bufferWriter != null) {
				try {
					bufferWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
					fileSaved = null;
				}
			}
		}
		return fileSaved;
	}

    public File saveFileStreamed(String fileName, InputStream is) {
        File fileSaved = null;
        try {
            fileSaved = new File(this.cacheDir, fileName);
            fileSaved.mkdirs();
            fileSaved.delete();
            fileSaved.createNewFile();
            FileOutputStream fos = new FileOutputStream(fileSaved);

            byte[] buffer = new byte[1024];// In bytes
            int realyReaded;
            while ((realyReaded = is.read(buffer)) > -1) {
                fos.write(buffer, 0, realyReaded);
            }

            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            fileSaved = null;
        } finally {

        }
        return fileSaved;
    }

	public File saveFile(String fileName, ByteArrayOutputStream content) {
		File fileSaved = null;
		try {
			fileSaved = new File(this.cacheDir, fileName);
			fileSaved.mkdirs();
			fileSaved.delete();
			fileSaved.createNewFile();
			FileOutputStream fos = new FileOutputStream(fileSaved);
			content.writeTo(fos);
			fos.flush();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
			fileSaved = null;
		} finally {

		}
		return fileSaved;
	}

	public String getFileContent(InputStream is) throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public File cleanDirectory(String nameDir) {
		File newDir = new File(this.cacheDir, nameDir);
		// Clear dir
		String[] files = newDir.list();
		for (int fIndex = 0; fIndex < files.length; fIndex++) {
			File fileForRemove = new File(newDir, files[fIndex]);
			if (fileForRemove.isDirectory()) {
				this.cleanDirectory(nameDir + "/" + files[fIndex]);
			} else {
				if (!fileForRemove.delete()) {
					newDir = null;
					break;
				}
			}
		}

		return newDir;
	}

	public File createDir(String nameDir) {
		File newDir = new File(this.cacheDir, nameDir);
		if (newDir.exists()) {
			newDir = this.cleanDirectory(nameDir);
		} else {
			if (!newDir.mkdirs()) {
				newDir = null;
			}
		}
		return newDir;
	}

	public boolean moveFiles(String oldDir, String newDir) {
		boolean movingOk = true;
		File oldDirectory = new File(cacheDir, oldDir);
		File newDirectory = new File(cacheDir, newDir);
		newDirectory.mkdirs();
		String[] oldFiles = oldDirectory.list();
		for (int fIndex = 0; fIndex < oldFiles.length; fIndex++) {
			File oldFile = new File(oldDirectory, oldFiles[fIndex]);
			File newFile = new File(newDirectory, oldFiles[fIndex]);
			if (newFile.exists()) {
				newFile.delete();
			}
			if (oldFile.isDirectory()) {
				if (!newFile.exists())
					if(!newFile.mkdirs()) {
					movingOk = false;
					break;
				}
				moveFiles(oldDir+"/"+oldFiles[fIndex],newDir+"/"+oldFiles[fIndex]);
			} else if (!oldFile.renameTo(newFile)) {
				movingOk = false;
				break;

			}
		}
		return movingOk;
	}

}
