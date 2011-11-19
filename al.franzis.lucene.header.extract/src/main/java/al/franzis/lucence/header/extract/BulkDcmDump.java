package al.franzis.lucence.header.extract;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.DicomObjectToStringParam;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;

public class BulkDcmDump
{

    public static void main(String[] args)
    {
        if (args.length!=2)
        {
            System.out.println("Usage: bulkdcmdump source(dir) dumpdir"); //$NON-NLS-1$
            return;
        }
        File source=new File(args[0]);
        // if source is a file, a dump with this name will be created in target.
        // if source is a directory, all files will be dumped into target, all sub-directories will be dumped recursively.
        File target=new File(args[1]);
        if (!target.exists())
            target.mkdirs();
        if (!source.exists())
        {
            System.out.println("source file "+source+" not found"); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        DicomObjectToStringParam.setDefaultParam(new DicomObjectToStringParam(true, 100000, 100000, 100000, 1000000, "", "\r\n")); //$NON-NLS-1$ //$NON-NLS-2$
        if (!source.isDirectory())// single dump
        {
            dumpFile(source, target, source.getName());
        }
        else
        {
            dumpDir(source, target, source.getAbsolutePath());
        }
    }

    /**
     * @param source
     * @param target
     * @param leadIn
     */
    private static void dumpDir(File source, File target, String leadIn)
    {
        File[] files = source.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
                dumpDir(file, target, leadIn);
            else
                dumpFile(file, target, file.getAbsolutePath().substring(leadIn.length()+1));
        }
    }

    /**
     * @param source
     * @param target
     * @param name
     */
    private static void dumpFile(File source, File target, String name)
    {
        File df = new File(target, name+".txt"); //$NON-NLS-1$
        if (df.exists() && df.length()>0)
            return;
        try
        {
            System.out.println("dump "+source); //$NON-NLS-1$
            DicomInputStream dis=new DicomInputStream(source);
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
    	dicomObject.accept(new DicomVisitor(dicomObject, new ArrayList<Integer>()));
    }

}
