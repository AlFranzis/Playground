package al.franzis.lucence.header.extract;

import java.io.File;
import java.io.FileOutputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.DicomObjectToStringParam;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;

//
/////////////////////////////////////////////////////////////////
//                 C O P Y R I G H T  (c) 2008                 //
//    A G F A   H E A L T H C A R E   C O R P O R A T I O N    //
//                    All Rights Reserved                      //
/////////////////////////////////////////////////////////////////
//
//        THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF
//                      AGFA CORPORATION
//       The copyright notice above does not evidence any
//      actual or intended publication of such source code.
//
/////////////////////////////////////////////////////////////////
//

/**
 * do a bulk header dump of all dicom files in the specified path
 * @author lmroz
 *
 */
public class BulkDcmDump
{

    /**
     * @param args
     */
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
            deleteOrphans(source, target, target.getAbsolutePath());
        }
    }

    /**
     * @param source
     * @param target
     * @param absolutePath
     */
    private static void deleteOrphans(File source, File target,
            String leadIn)
    {
        if (1==1)
            return;
        File[] files = target.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
                deleteOrphans(source, file, leadIn);
            else
            {
                String name=file.getAbsolutePath().substring(leadIn.length()+1);
                name=name.substring(0, name.length()-4);
                File orig = new File(source, name);
                if (!orig.exists())
                    file.delete();
            }
        }
        if (target.isDirectory() && target.listFiles().length==0)
            target.delete();
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

}
