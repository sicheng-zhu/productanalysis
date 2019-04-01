# Simple API
## Environment
- Java 8
- Tomcat
- Spring Boot
- Maven
- PostMan
- Heroku
## Links
- [Heroku](https://productanalysis.herokuapp.com/) (Do not click, see links below)
## Autocomplete
### How to test
Paste this link in Postman: https://productanalysis.herokuapp.com/api/products/autocomplete, and see screenshot in screenshot folder for Postman configuration.
### Restrictions
- In JSON request file, "type" can only be "title", "brand", or "category".
- When doing search in sample_product_data.tsv, if any field's prefix value matches the prefix value in JSON request, then this is a suggestion.
- Only return first 10 suggestions. (Open to discussion.)
### Solution Description
1. The entry point is getSuggestionList method in AutocompleteController.java. Do validation check, and error message will be returned in JSON file. Then call getSuggestion method to get suggestions.
2. Use BufferedReader, BufferedInputStream, InputStream, and StringTokenizer to read and parse .tsv file line by line.
3. Store the expected field value in productDetailString variable.
4. For each line in .tsv, if there are less than 6 fields, this means field(s) missing. Then skip this line.
5. If the prefix string from request JSON file matches the prefix of productDetailString variable, and productDetailString hasn't been seen previously (use HashSet here), add this productDetailString to an arraylist.
6. Only collect the first 10 suggestions.
7. Return the arraylist.
###  Memory And Runtime Analysis
Prefix string size in JSON file: average (preSize) characters.
Each line length in .tsv file: average (lineSize) characters.
Target field value length: average (tarSize) characters.

Time: O(50000 * (lineSize + preSize))
Space: O(2 * 10 * tarSize)

## Keywords Frequency
### How to test
Paste this link in Postman: https://productanalysis.herokuapp.com/api/products/keywords, and see screenshot in screenshot folder for Postman configuration.
### Restrictions
- Only search title in sample_product_data.tsv.
- When doing search in sample_product_data.tsv, each title string is splitted by whitespace. So a word after split may contains punctuations.
- Case insensitive when doing search.
### Solution Description
1. The entry point is getkeywordFrequencyList method in KeywordsFrequencyController.java. Do validation check, and error message will be returned in JSON file. Then call getFrequency method to get frequencies.
2. Use BufferedReader, BufferedInputStream, InputStream, and StringTokenizer to read and parse .tsv file line by line.
3. Store product title string in productDetailString variable.
4. For each line in .tsv, if there are less than 6 fields, this means field(s) missing. Then skip this line.
5. If one word in product title exactly match a keyword in JSON file, increment this word's frequency in HashMap.
7. Return the HashMap.
###  Memory And Runtime Analysis
Number of keywords in JSON: (keywords.length).
Each line length in .tsv file: average (lineSize) characters.
Product title length: average (titleSize) characters.
Number of words in each title: (count).
Each word length in title: average (wordSize) characters.
Time: O(50000 * (lineSize + titleSize))
Space: O(50000 * wordSize * count + keywords.length)
