package al.franzis.lucene.header.index;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class QueueDICOMIndexer {
	private static final Logger LOGGER = Logger.getLogger(QueueDICOMIndexer.class);
	
	private final Queue<Document> docsQueue;
	private final File indexDir;
	private final boolean create;
	private Directory dir; 
	
	public QueueDICOMIndexer( Queue<Document> docsQueue, String indexDir, boolean create) {
		this.docsQueue = docsQueue;
		this.indexDir = checkIndexDir(indexDir);
		this.create = create;
		
	}
	
	public QueueDICOMIndexer( Queue<Document> docsQueue, Directory indexDir, boolean create) {
		this.docsQueue = docsQueue;
		this.indexDir = null;
		this.create = create;
		this.dir = indexDir;
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
			indexDocs(writer, docsQueue);

			// call optimize to maximize search performance
			// writer.optimize();

			writer.close();

			Date end = new Date();
			LOGGER.info(end.getTime() - start.getTime() + " total milliseconds");
		} catch (IOException e) {
			LOGGER.error("Error while indexing", e);
		}
	}
	
	private static void indexDocs(IndexWriter writer, Queue<Document> docQueue) throws CorruptIndexException, IOException {
		Document document = null;
		while ((document = docQueue.poll()) != null) {
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// new index
				LOGGER.info("adding " + document);
				writer.addDocument(document);
			} else {
				// existing index -> an old copy of this document may
				// exist
				LOGGER.info("updating " + document);
				writer.updateDocument(new Term("path", null), document);
			}
		}
	}
}
