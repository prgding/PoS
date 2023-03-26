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
	private static final String ARTICLE_A = "a";
	private static final String ARTICLE_AN = "an";
	private static final String ARTICLE_THE = "the";

	public static void main(String[] args) throws IOException {
		IDictionary dict = initDictionary();
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		StanfordCoreNLP pipeline = initPipeline();

		String text = "You're the worst";
		String cleanText = text.replaceAll("[^\\w\\s']+", "");

		processText(cleanText, pipeline, stemmer);
	}

	private static IDictionary initDictionary() throws IOException {
		String path = "/Users/dingshuai/Downloads/dict";
		URL url = new URL("file", null, path);
		IDictionary dict = new Dictionary(url);
		dict.open();
		return dict;
	}

	private static StanfordCoreNLP initPipeline() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		return new StanfordCoreNLP(props);
	}

	private static void processText(String cleanText, StanfordCoreNLP pipeline, WordnetStemmer stemmer) {
		Annotation document = new Annotation(cleanText);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			processSentence(sentence, stemmer);
		}
	}

	private static void processSentence(CoreMap sentence, WordnetStemmer stemmer) {
		for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			String word = token.get(CoreAnnotations.TextAnnotation.class);
			String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
			String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);

			String targetWord = processTargetWord(pos, word, lemma, stemmer);
			String newPos = processPos(pos);

			System.out.println(word + ", " + newPos + ", target === " + targetWord);
		}
	}

	private static String processTargetWord(String pos, String word, String lemma, WordnetStemmer stemmer) {
		if (pos.contains("VB") || pos.contains("NNS")) {
			return lemma;
		} else if (pos.equals("JJR") || pos.equals("JJS")) {
			List<String> stems = stemmer.findStems(word, POS.ADJECTIVE);
			return stems.get(0);
		} else if (pos.contains("NN")) {
			List<String> stems = stemmer.findStems(word, POS.NOUN);
			return stems.get(0);
		} else {
			return word;
		}
	}

	private static String processPos(String pos) {
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
//		posMap.put("WP", "Pronoun");
		posMap.put("DT", "Determiner");
		posMap.put("RB", "Adverb");
		posMap.put("CD", "Number");
		posMap.put("NN", "Noun");
		posMap.put("EX", "Existential");
		posMap.put("JJ", "Adjective");
		posMap.put("UH", "Interjection");
		for (String key : posMap.keySet()) {
			if (pos.contains(key)) {
				return posMap.get(key);
			}
		}
		return pos;
	}
}
