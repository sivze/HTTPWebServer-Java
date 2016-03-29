package Handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import AbstractTypes.BaseHandler;
import AbstractTypes.Constants;
import Utilities.Logger;
/**
 * Reference - https://github.com/jrudolph/Pooling-web-server
 * A FileHandler which interprets URIs as files with paths relative to a root directory.
 * This class extends BaseHandler and contains functionality specific to handling files.
 * Reads all types of files and sends back to the client using multi-threads.
 */
public class FileHandler extends BaseHandler {
	private static final int BUFFERSIZE = 65536;
	private String root;

	public FileHandler(){
		
	}
	public FileHandler(String root) {
		this.root = root;
	}

	private File fileByPath(String path) {
		/*
		 * FIXME: This is a potential security risk. In a real application, you
		 * would have to make sure that you effectively restrict the scope of
		 * accessible files to the ones intended by excluding the parent path
		 * (..) and symbolic links etc.
		 */

		// strip off leading slashes
		while (path.startsWith("/"))
			path = path.substring(1);

		return new File(root, path);
	}
/**
 * return the file type of the incoming file
 */
	private String anyFileType() {
		return "application/octet-stream"; // represents all file type i.e any file as a binary file
	}

	@Override
	protected Response serve(String uri) {
		try{
		final File f = fileByPath(uri);

		if (f.exists() && f.isFile() && !f.isHidden()) {
			Logger.log("Serving '%s'", f);

			return new Response(Constants.HTTP_OK) {
				// implements abstract addHeaders() method of Response Class nested inside BaseHandler Class
				@Override
				public void addHeaders() {
					addResponseHeader("Content-Type", anyFileType());
					addResponseHeader("Content-Length", Long.toString(f.length()));
				}
				// implements abstract addBody() method of Response Class nested inside BaseHandler Class
				// send the file content to the client by writing it to OutputStream
				@Override
				public String addBody(OutputStream out) throws IOException {
					InputStream is = new FileInputStream(f);

                    try {
                        byte[] buffer = new byte[BUFFERSIZE];

                        while (is.available() > 0) {
                            int read = is.read(buffer);
                            out.write(buffer, 0, read);
                        }
                    } finally {
                        is.close();
                    }
                    return null;
				}
			};
		} else {
			return new Response(Constants.HTTP_NOT_FOUND) {
				String mesg ="<html><body>"+Constants.HTTP_NOT_FOUND+"\n"+ f + " not found.</body></html>";

				@Override
				public void addHeaders() {
					addResponseHeader("Content-Type", Constants.CONTENT_HTML);
					addResponseHeader("Content-Length", Long.toString(mesg.getBytes().length));
				}

				@Override
				public String addBody(OutputStream out) throws IOException{
					return mesg;
				}
			};
		}
		}catch(Exception e){
			//executor closes connection on returning null i.e due to null exception
			Logger.log("Erorr in processing File response: closing client...", e.getMessage());
			return null;
		}
	}
}
