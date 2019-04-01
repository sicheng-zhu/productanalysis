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

/**
 * This class implements endpoint 1.
 * 
 * @author Sicheng Zhu
 *
 */
@Controller
public class AutocompleteController {
    @RequestMapping(method = RequestMethod.POST, value="/api/products/autocomplete")
	
    /**
	 * This method is the entry point of get suggestion words.
	 * 
	 * @param autoCompleteMap The request JSON values.
	 * @return                An arraylist contains suggested words.
	 */
	@ResponseBody	
	public ArrayList<String> getSuggestionList(@RequestBody Map<String, 
			                                            String> autoCompleteMap) {
    	ArrayList<String> suggesionList = new ArrayList<>();		
		
		// Get the type's and prefix's value in JSON object.
		String typeString = autoCompleteMap.get("type");
		String prefixString = autoCompleteMap.get("prefix");
		
		/**
		 * Only "title", "brand", and "category" are valid values for type in request JSON.
		 * Create a hashmap to do validation check.
		 */
		HashMap<String, Integer> jsonParamCheckMap = new HashMap<>();
		jsonParamCheckMap.put("title", 1);
		jsonParamCheckMap.put("brand", 3);
		jsonParamCheckMap.put("category", 5);
		
		// Validate values of type and prefix in request JSON file.
		if (typeString == null || !jsonParamCheckMap.containsKey(typeString.toLowerCase()) ||
			prefixString == null || prefixString.length() == 0) {
			suggesionList.add("Please correct the request JSON file.");
			return suggesionList;
		}
		
		/**
		 *  typeNumber is column index of .tsv file. Since "title", "brand", and "category" 
		 *  are valid values for type in request JSON, so typeNumber can only be 1, 3, 5.
		 */
		int typeNumber = jsonParamCheckMap.get(typeString);
		
		// Call getSuggestion method, and get suggestion words.
		suggesionList = getSuggestion(typeNumber, prefixString);

		return suggesionList;
	}
    
    /**
     * This getSuggestion method visits "title", "brand", or "category" column of each line
     * and find the first 10 prefix matches.
     * 
     * @param typeNumber   The corresponding index of "title", "brand", or "category" column.
     * @param prefixString Prefix word in JSON file.
     * @return             A list of prefix matching strings.
     */
    private ArrayList<String> getSuggestion(int typeNumber, String prefixString) {
    	// Use suggestionSet to avoid duplicate prefix matches.
    	Set<String> suggestionSet = new HashSet<>();
    	// Store the first 10 prefix matching.
    	ArrayList<String> suggestionList = new ArrayList<>();
		
		StringTokenizer st;
		int count = 0;		
	    String productDetailString = null;	
	    String lowerCasedProductDetailString = null;
	    String lowerCasedPrefixString = null;
	    
		try {
			// The following three statements read .tsv file.
		    InputStream is = Thread.currentThread().getContextClassLoader()
		    		                .getResourceAsStream("sample_product_data.tsv");
		    
		    BufferedInputStream bis = new BufferedInputStream(is); 
		    BufferedReader br = new BufferedReader(new InputStreamReader(bis));

		    String productRecordString = br.readLine(); // Read first line in .tsv.	    	    
		    
		    while (productRecordString != null) {
		    	// Split each field in each line by tab, and store to StringTokenizer object.
	            st = new StringTokenizer(productRecordString, "\t");	            
	            
	            // Visit each field in one line.
	            while (st.hasMoreElements()) {
	            	/**
	            	 * "title", "brand", or "category" fields are 1st, 3rd and 5th fields 
	            	 * in each line. Ignore the rest fields.
	            	 * 
	            	 * Split the expected string by whitespace and store in productDetailString.
	            	 */
		            if (count == typeNumber) {		            	
		            	productDetailString = st.nextElement().toString();		            	
	            	} else {
	            		st.nextElement();	            		
	            	}
		            
		            count++;
	            }	            	            	            	            
	            
	            /**
	             *  If count is less than 6, this means field(s) in each line is empty, 
	             *  so skip this line.
	             *  
	             *  If the prefix string in JSON is longer than a word in .tsv, skip this case.
	             */
	            if (count < 6 || prefixString.length() >= productDetailString.length()) {	            	
	            	count = 0;
	            	productDetailString = null;
		            productRecordString = br.readLine();
	            	continue;
	            } else {
	            	/**
	            	 *  Convert prefix string in JSON, and a word in .tsv into lower case, and
	            	 *  compare these two words to check if prefix matching. If so, add to 	
	            	 *  suggestionSet and suggestionList.       
	            	 */
	            	lowerCasedProductDetailString = productDetailString.toLowerCase();
	            	lowerCasedPrefixString = prefixString.toLowerCase();
	            	
	            	if (lowerCasedProductDetailString.substring(0, prefixString.length())
	            		.equals(lowerCasedPrefixString) && 
	            		!suggestionSet.contains(productDetailString)) {
	            		
	            		suggestionSet.add(productDetailString);
	            		suggestionList.add(productDetailString);
	            	}
	            }
	            
	            // Only store the first 10 prefix matching words.
	            if (suggestionList.size() == 10) {
	            	break;
	            }
	            
	            count = 0;
	            productDetailString = null;
	            productRecordString = br.readLine(); // Read next line of data.
	        }		    
		    
		    // Close input file reader objects.
		    bis.close();    
		    is.close();		    		    
		    		    
		    return suggestionList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return suggestionList;
		}
    }
}