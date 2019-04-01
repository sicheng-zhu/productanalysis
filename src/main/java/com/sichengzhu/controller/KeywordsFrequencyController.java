package com.sichengzhu.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This class implements endpoint 3.
 * 
 * @author Sicheng Zhu
 *
 */
@Controller
public class KeywordsFrequencyController {
	@RequestMapping(method = RequestMethod.POST, value="/api/products/keywords")
	
	@ResponseBody	
	public Map<String, Integer> getkeywordFrequencyList(@RequestBody Map<String, 
			                                            String[]> keyWordMap) {
		Map<String, Integer> frequencyList = new HashMap<>();		
		
		// Get json object via POST, and assign keywords to keyWordList.
		String[] keyWordList = keyWordMap.get("keywords");	
		
		frequencyList = getFrequency(keyWordList);
		
		return frequencyList;
	}
	
	
	/**
	 * This getFrequency method get frequency of each requested word. This method only search
	 * titles in sample_product_data.tsv. The frequencies are exact match of keyword.
	 * 
	 * 
	 * @param  keyWordList a list of keyword to lookup frequency.
	 * @return a hashmap store keywords and frequencies.
	 */
	public HashMap<String, Integer> getFrequency(String[] keyWordList) {	
		Map<String, Integer> fileWordFrequencyMap = new HashMap<>();
		HashMap<String, Integer> responseFrequencyMap = new HashMap<>();
		
		StringTokenizer st;
		int count = 0;
	    final int TITLEPOSITION = 1;
	    String[] productDetailArray = null;	
	    
		try {    			
		    FileInputStream fis = new FileInputStream(
		    		                  ResourceUtils.getFile("classpath:sample_product_data.tsv"));
		    BufferedInputStream bis = new BufferedInputStream(fis); 
		    BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		    
		    String productRecordString = br.readLine(); // Read first line.		    	    
		    
		    while (productRecordString != null) {
	            st = new StringTokenizer(productRecordString, "\t");	            
	            
	            while (st.hasMoreElements()) {	            		            	
		            if (count == TITLEPOSITION) {
		            	productDetailArray = st.nextElement().toString().split("\\s+");			            	
	            	} else {
	            		st.nextElement();	            		
	            	}
		            
		            count++;
	            }	            	            
	            
	            if (count < 6) {
	            	count = 0;
		            productDetailArray = null;
		            productRecordString = br.readLine();
	            	continue;
	            }
	             
	            for (String str : productDetailArray) {
	            	String lowerCasedString = str.toLowerCase();
	            	
	                if (!fileWordFrequencyMap.containsKey(lowerCasedString)) {
	                	fileWordFrequencyMap.put(lowerCasedString, 1);
	                } else {
	                	fileWordFrequencyMap.put(lowerCasedString, fileWordFrequencyMap.get(lowerCasedString) + 1);
	                }
	            }	            	           
	            
	            count = 0;
	            productDetailArray = null;
	            productRecordString = br.readLine(); // Read next line of data.
	        }		    
		    
		    bis.close();    
		    fis.close();
		    
		    for (String word : keyWordList) {
		    	if (fileWordFrequencyMap.get(word) == null) {
		    		responseFrequencyMap.put(word, 0);
		    	} else {
		    		responseFrequencyMap.put(word, fileWordFrequencyMap.get(word));
		    	}
		    	
		    }
		    
		    return responseFrequencyMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return responseFrequencyMap;
		}
	}
}