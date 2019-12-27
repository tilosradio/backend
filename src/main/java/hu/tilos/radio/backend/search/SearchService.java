package hu.tilos.radio.backend.search;


import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.search.indexer.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.lucene.util.Version.LUCENE_48;


@Service
public class SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

    @Inject
    private DB db;

    private Directory index;

    private BaseIndexer[] indexers = new BaseIndexer[]{new AuthorIndexer(), new ShowIndexer(), new EpisodeIndexer(), new PageIndexer()};

    private Directory getIndex() throws IOException {
        return index;
    }

    private void createIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(LUCENE_48, createAnalyzer());
        IndexWriter indexWriter = new IndexWriter(getIndex(), config);
        for (BaseIndexer indexer : indexers) {
            indexer.index(db, indexWriter);
        }
        indexWriter.close();

    }

    //reindex in every hours
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void doSomething() {
        index();
    }

    @PostConstruct
    public void init() {
        index();
        LOG.info("initial indexing is finished");
    }

    public synchronized String index() {
        try {
            if (index == null) {
                index = new RAMDirectory();
            }
            createIndex();
            return "Indexing is finished";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "ERROR";
        }
    }

    public Analyzer createAnalyzer() {
        return new StopwordAnalyzerBase(LUCENE_48) {

            @Override
            protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
                final StandardTokenizer src = new StandardTokenizer(matchVersion, reader);
                src.setMaxTokenLength(10);

                TokenStream tok = new StandardFilter(matchVersion, src);
                tok = new LowerCaseFilter(matchVersion, tok);
                tok = new ASCIIFoldingFilter(tok, true);
                tok = new StopFilter(matchVersion, tok, stopwords);

                return new TokenStreamComponents(src, tok) {
                    @Override
                    protected void setReader(final Reader reader) throws IOException {
                        src.setMaxTokenLength(10);
                        super.setReader(reader);
                    }
                };
            }
        };

    }


    public SearchResponse search(String search) throws IOException, ParseException {

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                LUCENE_48,
                new String[]{"name", "alias", "definition", "introduction", "description"},
                createAnalyzer());
        queryParser.setAllowLeadingWildcard(true);

        org.apache.lucene.search.Query q = queryParser.parse(search);

        IndexReader reader = IndexReader.open(getIndex());
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        SearchResponse r = new SearchResponse();
        for (int i = 0; i < hits.length; ++i) {
            SearchResponseElement e = new SearchResponseElement();
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            e.setScore(hits[i].score);
            e.setAlias(safe(d.getField("alias")));
            e.setType(safe(d.getField("type")));
            e.setUri(safe(d.getField("uri")));
            e.setTitle(safe(d.getField("name")));
            e.setDescription(safe(d.getField("description")));
            r.addElement(e);
        }
        return r;
    }

    private String safe(IndexableField type) {
        if (type == null) {
            return null;
        }
        return type.stringValue();
    }


}
