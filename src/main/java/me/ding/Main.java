package me.ding;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Main {
	public final String ARTICLE_A = "a";
	public final String ARTICLE_AN = "an";
	public final String ARTICLE_THE = "the";

	private static IDictionary initDictionary() throws IOException {
		// 建立 WordNet 数据库的路径
		String path = "/Users/dingshuai/Downloads/dict";
		URL url = new URL("file", null, path);

		// 建立 WordNet 数据库
		IDictionary dict = new Dictionary(url);
		dict.open();
		return dict;
	}

	public static void main(String[] args) throws IOException {


		IDictionary dict = initDictionary();
		WordnetStemmer stemmer = new WordnetStemmer(dict);


		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		String text = "Please do it more slowly";
		// 去掉标点符号
		String cleanText = text.replaceAll("\\p{Punct}", "");
		Annotation document = new Annotation(cleanText);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
//				System.out.println(word + ", " + pos + ", " + lemma);

				// 词性还原 (不能还原形容词级别)
				// 所有的动词还原，所有的名词复数
				String targetWord = pos.contains("VB") || pos.contains("NNS") ? lemma : word;

				// 处理形容词比较级、最高级
				if (pos.equals("JJR") || pos.equals("JJS")) {
					// 查询形容词的原级
					List<String> stems = stemmer.findStems(word, POS.ADJECTIVE);
					targetWord = stems.get(0);
				} else if (pos.contains("NN")) {
					// NNS 查询复数名词原型
					// NNPS 查询复数专有名词原型
					List<String> stems = stemmer.findStems(word, POS.NOUN);
					targetWord = stems.get(0);
				}

				Map<String, String> posMap = new HashMap<>();
				posMap.put(ARTICLE_A, "Article");
				posMap.put(ARTICLE_AN, "Article");
				posMap.put(ARTICLE_THE, "Article");
				posMap.put("VB", "Verb");
				posMap.put("MD", "Verb");
				posMap.put("CC", "Conjunction");
				posMap.put("IN", "Preposition");
				posMap.put("TO", "T");
				posMap.put("PRP", "Pronoun");
				posMap.put("DT", "Determiner");
				posMap.put("RB", "Adverb");
				posMap.put("CD", "Number");
				posMap.put("NN", "Noun");
				posMap.put("EX", "Existential");
				posMap.put("JJ", "Adjective");
				posMap.put("UH", "Interjection");

				// 用遍历实现 contains 操作
				for (String key : posMap.keySet()) {
					if (pos.contains(key)) {
						pos = posMap.get(key);
						break;
					}
				}
				System.out.println(word + ", " + pos + ", target === " + targetWord);
			}
		}
	}
}