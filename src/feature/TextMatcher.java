package screen;
public class TextMatcher {
    static String keywords="this ix a test with elephantozaurus";
    static String matcher = "elephant";
     public static void main(String[] args) {
         int situation = 0;

        System.out.println("this is 0" + wordSituationMatchingMechanism(0,"this is a romania","man"));
         System.out.println("this is 1" + wordSituationMatchingMechanism(1,"this is a romania","dog"));
         System.out.println("this is 2" + wordSituationMatchingMechanism(2,"romania","ro"));
         System.out.println("this is 3" + wordSituationMatchingMechanism(3,"this is a romania","ia"));
         System.out.println("this is 4" + wordSituationMatchingMechanism(4,"this is a romania","this is a romania"));

     }
    static boolean isWordPresent(String sentence, String word)
    {
        // To break the sentence in words
        String []s = sentence.split(" ");

        // To temporarily store each individual word
        for ( String temp :s)
        {

            // Comparing the current word
            // with the word to be searched
//            if (temp.compareTo(word) == 0)
//            {
//                return true;
//            }
            if (temp.contains(word)) return true;
        }
        return false;
    }

    public static boolean wordSituationMatchingMechanism(int index, String value, String needle) {
         switch (index) {
             case 0:
                 // text contains
//                 System.out.println("tring with "+ value);
                 if (isWordPresent(value,needle)) {
                     return true;
                 }
//                 if ( value.toLowerCase().indexOf(needle.toLowerCase()) != -1 ) {
//                     int i = value.toLowerCase().indexOf(needle.toLowerCase());
////                     try {
////                         if (value.substring(i, i+needle.length()+ 1).equals(" ")) {
////                             System.out.println("TEXT OF VALUE " + value + " CONTAINS " + value.toLowerCase().indexOf(needle.toLowerCase()));
////                             return true;
////                         }
////                     }catch (java.lang.StringIndexOutOfBoundsException e) {
////                         e.printStackTrace();
////                     }
//                    int l = value.lastIndexOf(needle.toLowerCase());
//                        if (value.substring(l+1,l+2).equals(" ")) {
//                            return true;
//                        } else return false;
//                     return true;
//                 }
//                 if(value.toLowerCase().contains(needle.toLowerCase())) return true;
                 break;
              case 1:
                 //text does not contain
                 int pos = value.toLowerCase().indexOf(needle.toLowerCase());
//                  System.out.println("the contains " + pos);
//                  System.out.println("TEXT OF VALUE " + value + " DOES CONTAINS "+subString );
                  if (pos != 0) return true;
                 break;
             case 2:
                 if (value.startsWith(needle))  {
                     System.out.println("TEXT OF VALUE TRUE " + value + " STARTS WITH  "+needle );
                     return true;
                 }
                 break;
             case 3:
                 // text ends with
                 boolean endsWith = value.endsWith(needle);
//                 System.out.println("TEXT OF VALUE " + endsWith + " DOES END WITH "+subString );
                 return endsWith;
             case 4:
                 //text is exactly
//                 System.out.println("TEXT OF VALUE " + value + " EXACTLY LIKE "+subString );
                 if (value.compareTo(needle)==0) return true;
                 break;
             default:
                 return false;
         }
        return false;
     }



}
