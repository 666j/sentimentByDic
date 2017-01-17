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
 * 分词得到分词频率
 *
 * 
 */
public class WordFrequencyCountByProal {
//   用于存储词与词的个数的map
	private Map<String, WordPolarFrequency> wordsIndexMap = new HashMap<String, WordPolarFrequency>();//存储所有词
	Map<String, WordPolarFrequency> nWordsIndexMap = new HashMap<String, WordPolarFrequency>();//存储名词
	Map<String, WordPolarFrequency> vWordsIndexMap = new HashMap<String, WordPolarFrequency>();//存储动词
	Map<String, WordPolarFrequency> adjWordsIndexMap = new HashMap<String, WordPolarFrequency>();//存储形容词
	private EmotionDictionary emoDic = new EmotionDictionary();
	 OutputStreamWriter write;
	 BufferedWriter writer;
	private WordSegmentor wordSeg = new WordSegmentor();//初始化分词器
	private DataReader reader;//读取文件的reader
	
	public WordFrequencyCountByProal() {
		reader = new DataReader(DefaultConstant.DEFAULT_DATA_FILE);
	}
	public void StatisticalFrequency(){
		WordFrequencyCountByProal wProal = new WordFrequencyCountByProal();

		emoDic.addDic(DefaultConstant.DEFAULT_POSITIVE_DIC, true);
		emoDic.addDic(DefaultConstant.DEFAULT_NEGATIVE_DIC, false);
		String text = null;
		System.out.println("词频统计输出：");
		while((text = reader.getWeibo()) != null) {
			List<String> words = wordSeg.getWords(text);//进行分词处理
			if(words == null)
				break;
			//将读取的词和存入wordsIndexMap中
			int flag;
			for(String word : words) {
				
				String data=word.split("/")[0];
				String tempNominal =word.split("/")[1];
				String nominal = "";
				if(!tempNominal.isEmpty()){
					nominal = tempNominal.substring(0,1);//取分隔符后的第一个字母作为词的词性
				}
				flag = emoDic.testWord(data);
				boolean stopword= Stopwords.isStopword(data);//判断是否是停止词
				if(flag!=0&&!stopword) {
					if(nominal.equals("n")){//名词
						isExist(nWordsIndexMap, data, flag);
					}else if(nominal.equals("v")){//动词
						isExist(vWordsIndexMap, data, flag);
					}else if(nominal.equals("a")){//形容词
						isExist(adjWordsIndexMap, data, flag);
					}
				}	
			}
		}
		PrintBySort(adjWordsIndexMap,"./DataFiles/adjFrequency.txt");
		PrintBySort(nWordsIndexMap,"./DataFiles/nFrequency.txt");
		PrintBySort(vWordsIndexMap,"./DataFiles/vFrequency.txt");
		closeIOStream();//关闭流
	}
	//进行排序
	public void PrintBySort(Map map,String dic) {
		List<Map.Entry<String,WordPolarFrequency>> fwlistList= new ArrayList<Map.Entry<String,WordPolarFrequency>>(map.entrySet());  
	    //然后通过比较器来实现排序  
	    Collections.sort(fwlistList,new Comparator<Map.Entry<String,WordPolarFrequency>>() {  
	        //降序排列

			@Override
			public int compare(Entry<String, WordPolarFrequency> o1,
					Entry<String, WordPolarFrequency> o2) {
				// TODO Auto-generated method stub
				return o2.getValue().compareTo(o1.getValue());
			}
	    });  
	    WriteListToFile(fwlistList,dic);
		
	}
	//判断哈希表中是否已经有这个词了
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
	//打印出现次数
		public void printbyCount() {
			for (String key : wordsIndexMap.keySet()) { 
				
			    System.out.println(wordsIndexMap.get(key).toString());   
			}  
		}

	//将链表字符串化
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
		  
//将链表写入文件
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

