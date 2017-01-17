package analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



import constant.DefaultConstant;

import dataio.DataReader;
import dataio.PolarWriter;
import dataio.VectorWriter;
/**
 * �ִʵõ��ִ�Ƶ��
 *
 * 
 */
public class WordFrequencyCount {
//   ���ڴ洢����ʵĸ�����map
	private Map<String, Double> wordsIndexMap = new HashMap<String, Double>();//�洢���д�
	Map<String, Double> nWordsIndexMap = new HashMap<String, Double>();//�洢����
	Map<String, Double> vWordsIndexMap = new HashMap<String, Double>();//�洢����
	Map<String, Double> adjWordsIndexMap = new HashMap<String, Double>();//�洢���ݴ�
	private EmotionDictionary emoDic = new EmotionDictionary();
	 OutputStreamWriter write;
	 BufferedWriter writer;
	private WordSegmentor wordSeg = new WordSegmentor();//��ʼ���ִ���
	private DataReader reader;//��ȡ�ļ���reader
	private static int wCount=0;//�ʵ��ܸ���
	
	public WordFrequencyCount() {
		reader = new DataReader(DefaultConstant.DEFAULT_DATA_FILE);
	}
	
	public void  getfWord() {
		String text = null;
		System.out.println("��Ƶͳ�������");
		while((text = reader.getWeibo()) != null) {
			List<String> words = wordSeg.getWords(text);//���зִʴ���
			if(words == null)
				break;
			//����ȡ�Ĵʺʹ���wordsIndexMap��
			int flag;
			for(String word : words) {
				wCount++;
				String data=word.split("/")[0];
				
				if((flag = emoDic.testWord(data)) != 0&& !Stopwords.isStopword(data)) {
					System.out.println(word+"	"+flag);
				}
				if(wordsIndexMap.containsKey(word)) {
					double num = wordsIndexMap.get(word);
					num++;
					wordsIndexMap.replace(word, num);
				} else {
					wordsIndexMap.put(word, (double)1);
				}
			}
		}
	
		
		closeIOStream();//�ر���
//		printByNominal();
//		printbyCount();
//		printbyFrenquency();
		PrintBySort(wordsIndexMap);
	}
	

	//��ӡ��Ƶ
	public void printbyFrenquency() {
		for (String key : wordsIndexMap.keySet()) { 
			System.out.print("wcount:"+wCount);
			DecimalFormat df = new DecimalFormat("#.#######");
			double d = 3.14159;
			System.out.println(df.format(d));
			double f=  wordsIndexMap.get(key)/wCount;
		    System.out.println(key+" ��Ƶ�ʣ� "+df.format(f));   
		}  
	}
	//��ӡ���ִ���
	public void printbyCount() {
		for (String key : wordsIndexMap.keySet()) { 
		    System.out.println(key+"�ĸ����� "+wordsIndexMap.get(key));   
		}  
	}
	
	//�Դʵĳ��ִ�����������
	public void PrintBySort(Map map) {
		List<Map.Entry<String,Double>> fwlistList= new ArrayList<Map.Entry<String,Double>>(map.entrySet());  
	    //Ȼ��ͨ���Ƚ�����ʵ������  
	    Collections.sort(fwlistList,new Comparator<Map.Entry<String,Double>>() {  
	        //��������  
	        public int compare(Entry<String, Double> o1,  
	                Entry<String, Double> o2) {  
	            return o1.getValue().compareTo(o2.getValue());  
	        }  
	    });  
	    WriteFile(map,"./DataFiles/frenquencyBysort.txt");
		
	}
	//���������
	public void printByNominal() {
		
		for(String word :wordsIndexMap.keySet()) {
			String data=word.split("/")[0];
			String tempNominal =word.split("/")[1];
			
			String nominal = "";
			if(!tempNominal.isEmpty()){
				nominal = tempNominal.substring(0,1);
			}
			
			
			if(nominal.equals("n")){
				nWordsIndexMap.put(data, wordsIndexMap.get(word));
			}else if(nominal.equals("v")){
				vWordsIndexMap.put(data, wordsIndexMap.get(word));
			}else if(nominal.equals("a")){
				adjWordsIndexMap.put(data, wordsIndexMap.get(word));
			}
				
		}
		 WriteFile(wordsIndexMap,"./DataFiles/allFrequency.txt");
		System.out.println("--------------���ݴʣ�------------------------");
		 StringBuffer sb = new StringBuffer(); 
		for(String key :adjWordsIndexMap.keySet()) {
//			System.out.println("���ݴʣ�"+key+"�ĸ����� "+adjWordsIndexMap.get(key));
			 WriteFile(adjWordsIndexMap,"./DataFiles/adjFrequency.txt");
			
		}
		
		System.out.println("-----------------���ʣ�--------------------");
		for(String key :vWordsIndexMap.keySet()) {
//			System.out.println("���ʣ�"+key+"�ĸ����� "+vWordsIndexMap.get(key));
			 WriteFile(vWordsIndexMap,"./DataFiles/vFrequency.txt");
		}
		System.out.println("----------------���ʣ�--------------");
		for(String key :nWordsIndexMap.keySet()) {
//			System.out.println("���ʣ�"+key+"�ĸ����� "+nWordsIndexMap.get(key));
			 WriteFile(nWordsIndexMap,"./DataFiles/nFrequency.txt");
		}
	
	}
	public static String transListToString(List<Map.Entry<String,Double>> list){  
		  
		  StringBuffer sb = new StringBuffer();  
		  for(int i = 0; i < list.size(); i++) {
			  if (i == list.size() - 1) {
				  sb.append(list.get(i));
			} else {
				sb.append(list.get(i));
				sb.append("\r\n");
				}
			  }
		  return sb.toString();
		 }
		  
	public static String transMapToString(Map map){  
		  Map.Entry entry;  
		  StringBuffer sb = new StringBuffer();  
		  for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)  
		  {
		    entry = (java.util.Map.Entry)iterator.next();  
		      sb.append(entry.getKey().toString()).append( " : " ).append(null==entry.getValue()?"":  
		      entry.getValue().toString()).append (iterator.hasNext() ? "\r\n" : "");  
		  }  
		  return sb.toString();  
		} 


 public void  WriteFile(Map map,String filedec) {
	 try   
	    {      
	        File f = new File(filedec);      
	        if (!f.exists())   
	        {       
	            f.createNewFile();      
	        }      
	        write = new OutputStreamWriter(new FileOutputStream(f),"gbk");      
	        writer =new BufferedWriter(write);          
	        String content = transMapToString(map);
	        writer.write(content);      
	        writer.close();     
	    } catch (Exception e)   
	    {      
	        e.printStackTrace();     
	    }  
	 
	 
}
 public void  WriteListToFile(List<Map.Entry<String,Double>> list,String filedec) {
	 try   
	    {      
	        File f = new File(filedec);      
	        if (!f.exists())   
	        {       
	            f.createNewFile();      
	        }      
	        write = new OutputStreamWriter(new FileOutputStream(f),"gbk");      
	        writer =new BufferedWriter(write);          
	        String content = transListToString(list);
	        System.out.println(content);
	        writer.write(content);      
	        writer.close();     
	    } catch (Exception e)   
	    {      
	        e.printStackTrace();     
	    }  
	 
	 
}
 
 
	public void closeIOStream() {
		
		reader.closeReader();
	}

}

