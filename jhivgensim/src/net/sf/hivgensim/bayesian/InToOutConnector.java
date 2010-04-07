package net.sf.hivgensim.bayesian;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InToOutConnector {

	private boolean stop;
	private InputStream in;
	private OutputStream out;
	private Thread feed;

	public InToOutConnector(InputStream inputStream, OutputStream outPutStream) throws IOException {
		in = new BufferedInputStream(inputStream);
		out = outPutStream;
		feed = new Thread(){
			public void run() {
				while(true){
					byte[] content = new byte[256];
					try {
						int read = -1;
						do{
							synchronized (this) {
								if(stop) break;
								read = in.read(content, 0, 256);
							}
							Thread.yield();
						} while(read==-1);
						synchronized (this) {
							if(stop) break;
							out.write(content, 0, read);
						}
					} catch (IOException e) {
						System.err.println("InOutConnector failed to feed.");
						e.printStackTrace();
					}
				}
			};
		};
	}

	public void stop(){
		this.stop = true;
	}

	public void start() {
		this.stop = false;
		feed.start();
	}

}
