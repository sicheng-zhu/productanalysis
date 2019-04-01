package com.sichengzhu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
		
		// Get json object via POST, and assign keywords to keyWordList.
		String typeString = autoCompleteMap.get("type");
		String prefixString = autoCompleteMap.get("prefix");
		
		HashSet<String> jsonParamCheckSet = new HashSet<>();
		jsonParamCheckSet.add("title");
		jsonParamCheckSet.add("brand");
		jsonParamCheckSet.add("category");
		
		if (typeString == null || !jsonParamCheckSet.contains(typeString.toLowerCase()) ||
			prefixString == null) {
			suggesionList.add("Please correct the request json file.");
			return suggesionList;
		}
		
//		suggesionList = getSuggestion(typeString, prefixString);
		return suggesionList;
	}
    
//    private HashMap<String, Integer> getSuggestion(String[] keyWordList) {
//    	
//    }
}
