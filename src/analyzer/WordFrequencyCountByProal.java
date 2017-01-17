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
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;





import constant.DefaultConstant;

import dataio.DataReader;
import dataio.PolarWriter;
import dataio.VectorWriter;
/**
 * �ִʵõ��ִ�Ƶ��
 *
 * 
 */
public class WordFrequencyCountByProal {
//   ���ڴ洢����ʵĸ�����map
	private Map<String, WordPolarFrequency> wordsIndexMap = new HashMap<String, WordPolarFrequency>();//�洢���д�
	Map<String, WordPolarFrequency> nWordsIndexMap = new HashMap<String, WordPolarFrequency>();//�洢����
	Map<String, WordPolarFrequency> vWordsIndexMap = new HashMap<String, WordPolarFrequency>();//�洢����
	Map<String, WordPolarFrequency> adjWordsIndexMap = new HashMap<String, WordPolarFrequency>();//�洢���ݴ�
	private EmotionDictionary emoDic = new EmotionDictionary();
	 OutputStreamWriter write;
	 BufferedWriter writer;
	private WordSegmentor wordSeg = new WordSegmentor();//��ʼ���ִ���
	private DataReader reader;//��ȡ�ļ���reader
	
	public WordFrequencyCountByProal() {
		reader = new DataReader(DefaultConstant.DEFAULT_DATA_FILE);
	}
	public void StatisticalFrequency(){
		WordFrequencyCountByProal wProal = new WordFrequencyCountByProal();

		emoDic.addDic(DefaultConstant.DEFAULT_POSITIVE_DIC, true);
		emoDic.addDic(DefaultConstant.DEFAULT_NEGATIVE_DIC, false);
		String text = null;
		System.out.println("��Ƶͳ�������");
		while((text = reader.getWeibo()) != null) {
			List<String> words = wordSeg.getWords(text);//���зִʴ���
			if(words == null)
				break;
			//����ȡ�Ĵʺʹ���wordsIndexMap��
			int flag;
			for(String word : words) {
				
				String data=word.split("/")[0];
				String tempNominal =word.split("/")[1];
				String nominal = "";
				if(!tempNominal.isEmpty()){
					nominal = tempNominal.substring(0,1);//ȡ�ָ�����ĵ�һ����ĸ��Ϊ�ʵĴ���
				}
				flag = emoDic.testWord(data);
				boolean stopword= Stopwords.isStopword(data);//�ж��Ƿ���ֹͣ��
				if(flag!=0&&!stopword) {
					if(nominal.equals("n")){//����
						isExist(nWordsIndexMap, data, flag);
					}else if(nominal.equals("v")){//����
						isExist(vWordsIndexMap, data, flag);
					}else if(nominal.equals("a")){//���ݴ�
						isExist(adjWordsIndexMap, data, flag);
					}
				}	
			}
		}
		PrintBySort(adjWordsIndexMap,"./DataFiles/adjFrequency.txt");
		PrintBySort(nWordsIndexMap,"./DataFiles/nFrequency.txt");
		PrintBySort(vWordsIndexMap,"./DataFiles/vFrequency.txt");
		closeIOStream();//�ر���
	}
	//��������
	public void PrintBySort(Map map,String dic) {
		List<Map.Entry<String,WordPolarFrequency>> fwlistList= new ArrayList<Map.Entry<String,WordPolarFrequency>>(map.entrySet());  
	    //Ȼ��ͨ���Ƚ�����ʵ������  
	    Collections.sort(fwlistList,new Comparator<Map.Entry<String,WordPolarFrequency>>() {  
	        //��������

			@Override
			public int compare(Entry<String, WordPolarFrequency> o1,
					Entry<String, WordPolarFrequency> o2) {
				// TODO Auto-generated method stub
				return o2.getValue().compareTo(o1.getValue());
			}
	    });  
	    WriteListToFile(fwlistList,dic);
		
	}
	//�жϹ�ϣ�����Ƿ��Ѿ����������
	public Map<String, WordPolarFrequency> isExist(Map<String, WordPolarFrequency> map,String word,int flag) {
		if(map.containsKey(word)) {
			WordPolarFrequency w = map.get(word);
			w.frequency++;
			map.replace(word, w);
		} else {
			WordPolarFrequency w = new WordPolarFrequency(word, flag, 1);
			map.put(word, w);
		}
		return map;
	}
	//��ӡ���ִ���
		public void printbyCount() {
			for (String key : wordsIndexMap.keySet()) { 
				
			    System.out.println(wordsIndexMap.get(key).toString());   
			}  
		}

	//�������ַ�����
	public static String transListToString(List<Map.Entry<String,WordPolarFrequency>> list){  
		  
		  StringBuffer sb = new StringBuffer();  
		  for(int i = 0; i < list.size(); i++) {
			  if (i == list.size() - 1) {
				  
				  sb.append(list.get(i).toString());
			} else {
				sb.append(list.get(i).toString());
				System.out.println(list.get(i));
				sb.append("\r\n");
				}
			  }
		  return sb.toString();
		 }
		  
//������д���ļ�
 public void  WriteListToFile(List<Map.Entry<String,WordPolarFrequency>> list,String filedec) {
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
//	        System.out.println(content);
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
	public EmotionDictionary getEmoDic() {
		return emoDic;
	}
	 private class WordPolarFrequency implements Comparable<WordPolarFrequency>{
			int polar;
			int frequency;
			String word;
			public WordPolarFrequency(String word,int polar,int frequency){
				this.word =word;
				this.frequency = frequency;
				this.polar = polar;
			}
			
			public int compareTo(WordPolarFrequency wpf) {
				
			    if (frequency < wpf.frequency)
		            return -1;
			    else if (frequency > wpf.frequency)
		            return 1;
			    else
			        return word.compareTo(wpf.word);  
			}
			public String toString() {
				return "polar:"+polar+", frequency:"+frequency;
				
			}
		}
	 

}

