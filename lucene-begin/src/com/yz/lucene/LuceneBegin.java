package com.yz.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;

public class LuceneBegin {

    @Test
    public void createIndex() throws Exception{
        //    创建一个indexwriter对象。
        //            1）指定索引库的存放位置Directory对象
        Directory directory = FSDirectory.open(new File("/Users/zhuyu/Desktop/Java/lucene-resource/index").toPath());
        //            2）指定一个IndexWriterConfig对象。
        IndexWriterConfig config = new IndexWriterConfig();
        // 创建indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory,config);
        //   原始文档的路径
        File dir = new File("/Users/zhuyu/Desktop/Java/lucene-resource/searchsource");
        for(File f : dir.listFiles()) {
            // 文件名
            String fileName = f.getName();
            // 文件内容
            String fileContent = FileUtils.readFileToString(f,"utf-8");
            // 文件路径
            String filePath = f.getPath();
            // 文件大小
            Long fileSize = FileUtils.sizeOf(f);
            /**
             * 创建文件名域 param1：域的名称 param2：域的内容 param3：是否存储
             */
            Field fileNameField = new TextField("filename",fileName,Field.Store.YES);
            /**
             * 文件内容域（不分析，不索引、只存储）
             */
            Field fileContentField = new TextField("content",fileContent,Field.Store.YES);

            /**
             * 文件路径域（不分析，不索引、只存储）
             */
            Field filePathField = new TextField("path",filePath,Field.Store.YES);

            /**
             * 文件大小域
             */
            Field fileSizeField = new TextField("size",fileSize+"",Field.Store.YES);

            // 创建doucument对象
            Document document = new Document();

            document.add(fileNameField);

            document.add(fileContentField);

            document.add(filePathField);

            document.add(fileSizeField);

            // 创建索引并写进索引库
            indexWriter.addDocument(document);
        }
        //   关闭IndexWriter对象。
        indexWriter.close();
    }
    // 查询索引
    @Test
    public void searchIndex() throws Exception {
        // 指定存放索引库的路径
        Directory directory = FSDirectory.open(new File("/Users/zhuyu/Desktop/Java/lucene-resource/index").toPath());

        // 创建indexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);

        // 创建indexSearch对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // 创建查询
        Query query = new TermQuery(new Term("filename","apache"));

        /**
         * 执行查询 param1：查询对象 param2：查询结果返回的最大值
         */
        TopDocs topDocs = indexSearcher.search(query,10);

        // 查询结果的总条数
        System.out.println("查询结果的总条数：" + topDocs.totalHits);

        // 便利查询结果
        //topDocs.scoreDocs存储了document对象的id
        for(ScoreDoc scoreDoc:topDocs.scoreDocs) {
            // scoreDoc的doc属性就是document对象的id
            // 根据doucument对象的id 找到document对象
            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println(document.get("filename"));

            System.out.println(document.get("content"));

            System.out.println(document.get("path"));

            System.out.println(document.get("size"));
        }
        // 关闭indexReader对象
        indexReader.close();
    }
}
