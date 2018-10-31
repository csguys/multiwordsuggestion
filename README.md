# multiwordsuggestion
 a simple adapter class which will provide prefix based search suggestion from provided suggestions list with syntax highlighting
 
## Getting Started
 just add the util package into your project and start using the adapter class for **AutoCompleteTextView** and **RecyclerView**

# Demo
![demo](https://raw.githubusercontent.com/csguys/prefixSearch/dev/ezgif.com-video-to-gif.gif)
 # usage
 ## For AutoCompleteTextView
 ```
  List<String> list = // your suggestion list data
  PrefixSearchAutoCompleteAdapter adapter = new PrefixSearchAutoCompleteAdapter(context, list, Color.YELLOW);
  autoCompleteTextViewObject.setAdapter(adapter);
 ```
 ## For RecyclerView
 ```
  List<String> list = // your suggestion list data
  PRecyclerPrefixSearchAdapter adapter = new RecyclerPrefixSearchAdapter(context, list, Color.YELLOW);
  recyclerviewObject.setAdapter(adapter);
 ```
