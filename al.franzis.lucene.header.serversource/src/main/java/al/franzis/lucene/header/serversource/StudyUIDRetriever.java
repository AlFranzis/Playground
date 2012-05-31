package al.franzis.lucene.header.serversource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.tool.dcmqr.DcmQR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyUIDRetriever {
	private static final Logger LOG = LoggerFactory.getLogger(StudyUIDRetriever.class);
	
	private String remoteAE;
	private String[] matchingKeys;
	
	public void fetch() {
		DcmQR dcmqr = new DcmQR("DCMQR");
		String[] calledAETAddress = split(remoteAE, '@');
		dcmqr.setCalledAET(calledAETAddress[0], false);
		if (calledAETAddress[1] == null) {
			dcmqr.setRemoteHost("127.0.0.1");
			dcmqr.setRemotePort(104);
		} else {
			String[] hostPort = split(calledAETAddress[1], ':');
			dcmqr.setRemoteHost(hostPort[0]);
			dcmqr.setRemotePort(toPort(hostPort[1]));
		}
		
		dcmqr.setPackPDV(true);
		dcmqr.setTcpNoDelay(true);
		dcmqr.setMaxOpsInvoked(1);
		dcmqr.setMaxOpsPerformed(0);
		
		dcmqr.setCFind(true);
	    
		for (int i = 1; i < matchingKeys.length; i++, i++)
			dcmqr.addMatchingKey(Tag.toTagPath(matchingKeys[i - 1]), matchingKeys[i]);
		
		 dcmqr.configureTransferCapability(false);
		 
		 int repeat = 0;
		 int interval = 0;
		 boolean closeAssoc = false;
		 
		try {
			dcmqr.start();
		} catch (Exception e) {
			System.err.println("ERROR: Failed to start server for receiving "
					+ "requested objects:" + e.getMessage());
			System.exit(2);
		}
		try {
			long t1 = System.currentTimeMillis();
			try {
				dcmqr.open();
			} catch (Exception e) {
				LOG.error("Failed to establish association:", e);
				System.exit(2);
			}
			long t2 = System.currentTimeMillis();
			LOG.info("Connected to {} in {} s", remoteAE,
					Float.valueOf((t2 - t1) / 1000f));

			for (;;) {
				List<DicomObject> result;
				if (dcmqr.isCFind()) {
					result = dcmqr.query();
					long t3 = System.currentTimeMillis();
					LOG.info("Received {} matching entries in {} s",
							Integer.valueOf(result.size()),
							Float.valueOf((t3 - t2) / 1000f));
					t2 = t3;
				} else {
					result = Collections.singletonList(dcmqr.getKeys());
				}
				if (dcmqr.isCMove() || dcmqr.isCGet()) {
					if (dcmqr.isCMove())
						dcmqr.move(result);
					else
						dcmqr.get(result);
					long t4 = System.currentTimeMillis();
					LOG.info(
							"Retrieved {} objects (warning: {}, failed: {}) in {}s",
							new Object[] {
									Integer.valueOf(dcmqr.getTotalRetrieved()),
									Integer.valueOf(dcmqr.getWarning()),
									Integer.valueOf(dcmqr.getFailed()),
									Float.valueOf((t4 - t2) / 1000f) });
				}
				if (repeat == 0 || closeAssoc) {
					try {
						dcmqr.close();
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
					LOG.info("Released connection to {}", remoteAE);
				}
				if (repeat-- == 0)
					break;
				Thread.sleep(interval);
				long t4 = System.currentTimeMillis();
				dcmqr.open();
				t2 = System.currentTimeMillis();
				LOG.info("Reconnect or reuse connection to {} in {} s",
						remoteAE, Float.valueOf((t2 - t4) / 1000f));
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			dcmqr.stop();
		}
		 
	}
	
	private static String[] split(String s, char delim) {
		String[] s2 = { s, null };
		int pos = s.indexOf(delim);
		if (pos != -1) {
			s2[0] = s.substring(0, pos);
			s2[1] = s.substring(pos + 1);
		}
		return s2;
	}
	
	private static int toPort(String port) {
		return port != null ? parseInt(port, "illegal port number", 1, 0xffff)
				: 104;
	}

	private static int parseInt(String s, String errPrompt, int min, int max) {
		try {
			int i = Integer.parseInt(s);
			if (i >= min && i <= max)
				return i;
		} catch (NumberFormatException e) {
			// parameter is not a valid integer; fall through to exit
		}

		throw new RuntimeException();
	}
	
}
