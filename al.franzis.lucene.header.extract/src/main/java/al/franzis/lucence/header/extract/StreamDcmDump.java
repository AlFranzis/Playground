package al.franzis.lucence.header.extract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;

public class StreamDcmDump {


    public static void dumpStream(InputStream inputStream, File target, String name)
    {
        File df = new File(target, name+".txt"); //$NON-NLS-1$
        if (df.exists() && df.length() > 0)
            return;
        try
        {
            
            DicomInputStream dis=new DicomInputStream(inputStream);
            dis.setHandler(new StopTagInputHandler(Tag.PixelData));
            DicomObject dcm = dis.readDicomObject();
            dis.close();
            
            dumpDicomElement(dcm);
            
            
            if (!df.getParentFile().exists())
                df.getParentFile().mkdirs();
            FileOutputStream fos=new FileOutputStream(df);
            fos.write(dcm.toString().getBytes());
            fos.close();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private static void dumpDicomElement( DicomObject dicomObject )
    {
//    	PatientModule pModule = new PatientModule( dicomObject );
    	dicomObject.accept(new DicomVisitor(dicomObject, new ArrayList<Integer>()));
    }

}
