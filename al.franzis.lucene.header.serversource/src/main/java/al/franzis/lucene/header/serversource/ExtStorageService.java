package al.franzis.lucene.header.serversource;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.Status;
import org.dcm4che2.net.service.StorageService;

public class ExtStorageService extends StorageService {

	private OutputStreamFactory<OutputStream> outputStreamFactory;
	private int fileBufferSize = 256;
	
	protected ExtStorageService(String sopClass) {
		super(sopClass);
	}

	protected ExtStorageService(String[] sopClasses, OutputStreamFactory<OutputStream> outputStreamFactory) {
		super(sopClasses);
		this.outputStreamFactory = outputStreamFactory;
	}
	
	@Override
    protected void onCStoreRQ(Association as, int pcid, DicomObject rq, PDVInputStream dataStream, String tsuid, DicomObject rsp)
            throws IOException, DicomServiceException {
		
			if ( outputStreamFactory == null )
			{
				dataStream.skipAll();
				return;
			}
      
            try {
                String cuid = rq.getString(Tag.AffectedSOPClassUID);
                String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
                BasicDicomObject fmi = new BasicDicomObject();
                fmi.initFileMetaInformation(cuid, iuid, tsuid);
               
                OutputStream outputStream = outputStreamFactory.getOutputStream(iuid);
                BufferedOutputStream bos = new BufferedOutputStream(outputStream, fileBufferSize);
                DicomOutputStream dos = new DicomOutputStream(bos);
                dos.writeFileMetaInformation(fmi);
                dataStream.copyTo(dos);
                dos.close();
                outputStreamFactory.close(iuid, outputStream);
            } catch (IOException e) {
                throw new DicomServiceException(rq, Status.ProcessingFailure, e.getMessage());
            }
        
    }

}
