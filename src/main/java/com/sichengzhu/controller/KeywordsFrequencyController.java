package com.sichengzhu.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.stereotype.Controller;
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
	
	/**
	 * This method is the entry point of get keyword's frequency.
	 * 
	 * @param keyWordMap The request JSON values.
	 * @return           A hashmap contains keywords and frequency.
	 */
	@ResponseBody	
	public Map<String, Integer> getkeywordFrequencyList(@RequestBody Map<String, 
			                                            String[]> keyWordMap) {
		Map<String, Integer> frequencyList = new HashMap<>();
		
		// If input JSON has no key named "keywords", return JSON file with error message.
		if (keyWordMap == null || !keyWordMap.containsKey("keywords")) {
			frequencyList.put("Please correct the request JSON file.", 0);
			return frequencyList;
		}		
		
		// Get JSON object via POST, and assign keywords to keyWordList.
		String[] keyWordList = keyWordMap.get("keywords");	
		
		// If input JSON contains no keywords, return JSON file with error message.
		if (keyWordList == null || keyWordList.length == 0) {
			frequencyList.put("Please correct the request json file.", 0);
			return frequencyList;
		}
		
		// Call getFrequency method, and get frequency.
		frequencyList = getFrequency(keyWordList);
		
		return frequencyList;
	}	
	
	/**
	 * This getFrequency method get frequency of each requested word. This method only search
	 * titles in sample_product_data.tsv. The frequencies are exact match of keyword.
	 * 
	 * 
	 * @param  keyWordList a list of keyword to lookup frequency.
	 * @return             a hashmap stores keywords and frequencies.
	 */
	private HashMap<String, Integer> getFrequency(String[] keyWordList) {
		// fileWordFrequencyMap stores all title words and frequencies.
		Map<String, Integer> fileWordFrequencyMap = new HashMap<>();
		
		// fileWordFrequencyMap stores all keywords and frequencies in .tsv file.
		HashMap<String, Integer> responseFrequencyMap = new HashMap<>();
		
		StringTokenizer st;
		int count = 0;
	    final int TITLEPOSITION = 1;
	    String[] productDetailArray = null;	
	    
		try {
			// The following three statements read .tsv file.			
		    InputStream fis = Thread.currentThread().getContextClassLoader()
		    		                .getResourceAsStream("sample_product_data.tsv");
		    
		    BufferedInputStream bis = new BufferedInputStream(fis); 
		    BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		    
		    String productRecordString = br.readLine(); // Read first line in .tsv.		    	    
		    
		    while (productRecordString != null) {
		    	// Split each field in each line by tab, and store to StringTokenizer object.
	            st = new StringTokenizer(productRecordString, "\t");	            
	            
	            // Visit each field in one line.
	            while (st.hasMoreElements()) {	
	            	/**
	            	 * Title field is the second field in each line. Only title field is needed
	            	 * in this controller, so ignore the rest fields.
	            	 * 
	            	 * Split title string by whitespace and store in productDetailArray.
	            	 */
		            if (count == TITLEPOSITION) {
		            	productDetailArray = st.nextElement().toString().split("\\s+");			            	
	            	} else {
	            		st.nextElement();	            		
	            	}
		            
		            count++;
	            }	            	            
	            
	            /**
	             *  If count is less than 6, this means field(s) in each line is empty, 
	             *  so skip this line.	             
	             */
	            if (count < 6) {
	            	count = 0;
		            productDetailArray = null;
		            productRecordString = br.readLine();
	            	continue;
	            }
	            
	            // For each word in title in each line, add to hashmap or increment its frequency.
	            for (String str : productDetailArray) {
	            	String lowerCasedString = str.toLowerCase();
	            	
	                if (!fileWordFrequencyMap.containsKey(lowerCasedString)) {
	                	fileWordFrequencyMap.put(lowerCasedString, 1);
	                } else {
	                	fileWordFrequencyMap
	                	.put(lowerCasedString, fileWordFrequencyMap.get(lowerCasedString) + 1);
	                }
	            }	            	           
	            
	            count = 0;
	            productDetailArray = null;
	            productRecordString = br.readLine(); // Read next line of data.
	        }		    
		    
		    // Close input file reader objects.
		    bis.close();    
		    fis.close();
		    
		    // Get frequency for each keyword in request JSON, and add to responseFrequencyMap.
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