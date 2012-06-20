package al.franzis.lucene.header.index;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class DICOMIndexer {
	private static final Logger LOGGER = Logger.getLogger(DICOMIndexer.class);
	
	private final File docsDir;
	private final File indexDir;
	private final boolean create;
	private Directory dir; 
	
	public DICOMIndexer( String docsDir, String indexDir, boolean create) {
		this.docsDir = checkDocsDir(docsDir);
		this.indexDir = checkIndexDir(indexDir);
		this.create = create;
		
	}
	
	public DICOMIndexer( String docsDir, Directory indexDir, boolean create) {
		this.docsDir = checkDocsDir(docsDir);
		this.indexDir = null;
		this.create = create;
		this.dir = indexDir;
	}
	
	private File checkDocsDir( String docsDir ) {
		final File docDir = new File(docsDir);
		if (!docDir.exists() || !docDir.canRead()) {
			LOGGER.error( format("Document directory '%s' does not exist or is not readable", docDir.getAbsoluteFile()));
			System.exit(1);
		}
		return docDir;
	}
	
	private File checkIndexDir( String indexDir ) {
		final File _indexDir = new File(indexDir);
		if (!_indexDir.exists() )
		{
			if(!_indexDir.mkdirs())
				System.exit(1);
		}
		
		if ( _indexDir.isFile() || !_indexDir.canWrite() ) {
			LOGGER.error( format( "Index directory '%s' is not a directory or is not writeable", _indexDir.getAbsoluteFile()));
			System.exit(1);
		}
		
		return _indexDir;
	}
	
	public boolean open() {
		try {
			if( dir == null )
				dir = FSDirectory.open(indexDir);
			return true;
		} catch (IOException e) {
			LOGGER.error( "I/O Error while opening index dir", e);
		}
		return false;
	}
	
	
	public void index() {
		Date start = new Date();
		try {
			LOGGER.info("Indexing to directory '" + indexDir + "'...");

			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_34,
					analyzer);

			if (create) {
				// create a new index -> remove existing docs
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			// iwc.setRAMBufferSizeMB(256.0);

			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docsDir);

			// call optimize to maximize search performance
			// writer.optimize();

			writer.close();

			Date end = new Date();
			LOGGER.info(end.getTime() - start.getTime() + " total milliseconds");
		} catch (IOException e) {
			LOGGER.error("Error while indexing", e);
		}
	}
	
	private static void indexDocs(IndexWriter writer, File file)
			throws IOException {
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					return;
				}

				try {
					Document doc = new Document();

					Field pathField = new Field("path", file.getPath(),
							Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
					pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
					doc.add(pathField);

					NumericField modifiedField = new NumericField("modified");
					modifiedField.setLongValue(file.lastModified());
					doc.add(modifiedField);
					
					Field sopInstanceUIDField = new Field("uid", file.getName(),
							Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
					doc.add(sopInstanceUIDField);

					doc.add(new Field("contents", new BufferedReader(
							new InputStreamReader(fis, "UTF-8"))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// new index
						LOGGER.info("adding " + file);
						writer.addDocument(doc);
					} else {
						// existing index -> an old copy of this document may
						// exist
						LOGGER.info("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()), doc);
					}
				} finally {
					fis.close();
				}
			}
		}
	}
}
