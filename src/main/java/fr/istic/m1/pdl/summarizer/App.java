package fr.istic.m1.pdl.summarizer;

import java.io.File;
import java.io.IOException;

import org.opencompare.api.java.PCM;
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.PCMLoader;

import fr.istic.m1.pdl.summarizer.model.DataModel;
import fr.istic.m1.pdl.summarizer.visitor.PCMDataModelConstructor;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		// Load a PCM
		File pcmFile = new File("pcms/example.pcm");
		PCMLoader loader = new KMFJSONLoader();
		try {
			PCM pcm = loader.load(pcmFile).get(0).getPcm();
			
			//looding a dataModel from pcm
			PCMDataModelConstructor dataConstructor = new PCMDataModelConstructor();
			DataModel model = dataConstructor.createDataModel(pcm);
			
			System.out.println(model);
			//utiliser le model pour les calculs statistiques ici
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
