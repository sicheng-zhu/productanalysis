package com.sichengzhu.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AutocompleteController {
    @RequestMapping(method = RequestMethod.POST, value="/api/products/autocomplete")
	
	@ResponseBody	
	public ArrayList<String> getkeywordFrequencyList(@RequestBody Map<String, 
			                                            String> autoCompleteMap) {
    	ArrayList<String> suggesionList = new ArrayList<>();		
		
		// Get JSON object via POST, and assign keywords to keyWordList.
		String typeString = autoCompleteMap.get("type");
		String prefixString = autoCompleteMap.get("prefix");
		
		HashMap<String, Integer> jsonParamCheckMap = new HashMap<>();
		jsonParamCheckMap.put("title", 1);
		jsonParamCheckMap.put("brand", 3);
		jsonParamCheckMap.put("category", 5);
		
		if (typeString == null || !jsonParamCheckMap.containsKey(typeString.toLowerCase()) ||
			prefixString == null || prefixString.length() == 0) {
			suggesionList.add("Please correct the request JSON file.");
			return suggesionList;
		}
		
		int typeNumber = jsonParamCheckMap.get(typeString);
		
		suggesionList = getSuggestion(typeNumber, prefixString);

		return suggesionList;
	}
    
    private ArrayList<String> getSuggestion(int typeNumber, String prefixString) {
    	Set<String> suggestionSet = new HashSet<>();
    	ArrayList<String> suggestionList = new ArrayList<>();
		
		StringTokenizer st;
		int count = 0;		
	    String productDetailString = null;	
	    String lowerCasedProductDetailString = null;
	    String lowerCasedPrefixString = null;
	    
		try {    			
		    InputStream fis = Thread.currentThread().getContextClassLoader()
		    		                .getResourceAsStream("sample_product_data.tsv");
		    
		    BufferedInputStream bis = new BufferedInputStream(fis); 
		    BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		    
		    String productRecordString = br.readLine(); // Read first line.		    	    
		    
		    while (productRecordString != null) {
	            st = new StringTokenizer(productRecordString, "\t");	            
	            
	            while (st.hasMoreElements()) {	            		            	
		            if (count == typeNumber) {		            	
		            	productDetailString = st.nextElement().toString();		            	
	            	} else {
	            		st.nextElement();	            		
	            	}
		            
		            count++;
	            }	            	            	            	            
	             
	            if (count < 6 || prefixString.length() >= productDetailString.length()) {	            	
	            	count = 0;
	            	productDetailString = null;
		            productRecordString = br.readLine();
	            	continue;
	            } else {
	            	lowerCasedProductDetailString = productDetailString.toLowerCase();
	            	lowerCasedPrefixString = prefixString.toLowerCase();
	            	
	            	if (lowerCasedProductDetailString.substring(0, prefixString.length())
	            		.equals(lowerCasedPrefixString) && 
	            		!suggestionSet.contains(productDetailString)) {
	            		
	            		suggestionSet.add(productDetailString);
	            		suggestionList.add(productDetailString);
	            	}
	            }
	            
	            if (suggestionList.size() == 10) {
	            	break;
	            }
	            
	            count = 0;
	            productDetailString = null;
	            productRecordString = br.readLine(); // Read next line of data.
	        }		    
		    
		    bis.close();    
		    fis.close();		    		    
		    		    
		    return suggestionList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return suggestionList;
		}
    }
}