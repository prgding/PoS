import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.morph.WordnetStemmer;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class test {
	@Test
	public void testApp() throws IOException {
		// 建立 WordNet 数据库的路径
		String path = "/Users/dingshuai/Downloads/dict";
		URL url = new URL("file", null, path);

		// 建立 WordNet 数据库
		IDictionary dict = new Dictionary(url);
		dict.open();

		// 查询 "pretty" 的所有名词
		IIndexWord idxWord = dict.getIndexWord("prettier", POS.ADJECTIVE);
		if (idxWord != null) {
			for (IWordID wordID : idxWord.getWordIDs()) {
				IWord word = dict.getWord(wordID);
				ISynset synset = word.getSynset();
				System.out.println("Meaning: " + synset.getGloss());
				System.out.println("Examples:");
				for (IWord w : synset.getWords()) {
					System.out.println(" - " + w.getLemma());
				}
			}
		} else {
			System.out.println("Not found");
		}

		// 关闭数据库连接
		dict.close();
	}

	@Test
	public void testApp2() throws IOException {
		// 建立 WordNet 数据库的路径
		String path = "/Users/dingshuai/Downloads/dict";
		URL url = new URL("file", null, path);

		// 建立 WordNet 数据库
		IDictionary dict = new Dictionary(url);
		dict.open();

		// 查询 "pretty" 的比较级
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		List<String> stems = stemmer.findStems("Apples", POS.NOUN);
		System.out.println(stems);

		// 关闭数据库连接
		dict.close();
	}
}