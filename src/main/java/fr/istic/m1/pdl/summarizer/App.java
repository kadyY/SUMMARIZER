package fr.istic.m1.pdl.summarizer;

import java.lang.Object;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.opencompare.api.java.Feature;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.PCMLoader;

import fr.istic.m1.pdl.summarizer.model.DataModel;
import fr.istic.m1.pdl.summarizer.visitor.PCMDataModelConstructor;

public class App {
	public static void main(String[] args) {
		
		StringBuilder builder = new StringBuilder("");
		
		// Load a PCM
		File pcmFile = new File("pcms/example.pcm");
		PCMLoader loader = new KMFJSONLoader();
		try {
			PCM pcm = loader.load(pcmFile).get(0).getPcm();

			// looding a dataModel from pcm
			PCMDataModelConstructor dataConstructor = new PCMDataModelConstructor();
			DataModel model = dataConstructor.createDataModel(pcm);

			// retourner la liste des features
			System.out.println("La liste des features");
			HashMap<Integer, Feature> listeFeatures = new HashMap<>();
			int Pfeature = 0;
			for (Feature feature : pcm.getConcreteFeatures()) {
				Pfeature++;
				listeFeatures.put(Pfeature, feature);

				System.out.println(Pfeature + ": " + feature.getName());
			}

			// utiliser le model pour les calculs statistiques ici
			Map<String, List<Number>> list = model.getAllQuantitativeValues();
			ApiCalculStat b = new ApiCalculStat(model);
			
	    	builder.append("var summarizerData = ");
			builder.append("{ \"resume\" :[ ");
			
			builder.append("{ \"QuantitativesFeatures\" :{");
			for (String columnName : list.keySet()) {
				List<Number> item = list.get(columnName);
				double min = b.getMinimum(item);
				double max = b.getMaximum(item);
				double moy = b.getAverage(item);
				
				System.out.println("Col "+ columnName + " : ");
				System.out.println(" \tle minimum est "+ min);
				System.out.println(" \tle max est "+ max);
				System.out.println(" \tla moy est "+ moy);
				
				builder.append(" \" " + columnName + " \" : {");
				builder.append(" \" min \": " + min + ",");
				builder.append(" \" max \": " + max + ",");
				builder.append(" \" moy \" :" + moy+ ",");
				builder.append(" \" correlation \" : {" );
				
				for (String name2 : list.keySet()) {
					if (!columnName.equals(name2)) {
						double coef = b.CoefficentCorrelation(list.get(columnName), list.get(name2));
						builder.append(" \" " + name2 + " \": " + coef + ",");
					}
				}
		        builder.deleteCharAt(builder.lastIndexOf(","));
				builder.append("}");
				
				builder.append("},");
			}
	        //builder.deleteCharAt(builder.lastIndexOf(","));
			builder.append("} ");
	        
			builder.append(",\"QualitativesFeatures\" : { ");
	        HashMap<String, List<String>> listQ = model.getAllQualitativeValues2();
	        for(String currentFeat: listQ.keySet()){
				HashMap<String, Integer> occurences = b.getOccurrence(listQ.get(currentFeat));
	        	
				System.out.println("Nombre d'occurences "+ occurences) ;
				builder.append(" \" " + currentFeat + " \" : {");
	        	for(String key: occurences.keySet()){
	        		builder.append(" \" " + key + " \": " + occurences.get(key) + ",");
	        	}
		        builder.deleteCharAt(builder.lastIndexOf(","));
	        	builder.append("},");
	        }
	        builder.deleteCharAt(builder.lastIndexOf(","));
			builder.append("} ");
			builder.append("} ");
			builder.append("]} ");
			
			System.out.println(builder.toString());
			
			//Ecriture du fichier
	        File resumeFile = new File("src/main/java/fr/istic/m1/pdl/summarizer/view/json/summarizer.js"); //fichier cible
	        createFile(builder.toString(), resumeFile);
			System.out.println("Terminé !");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Sauvegarde les donn�es au format string dans un fichier sp�cifi�
     * 
     * @param data Chaines de caract�re � sauvegarder
     * @param outputFile Fichier de sauvegarde
     * @throws IOException 
     */
    public static void createFile(String data, File outputFile) throws IOException{
        FileWriter fw;
        fw = new FileWriter(outputFile);
        fw.write(data);
        fw.close();
    }
	

}
