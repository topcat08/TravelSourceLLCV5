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

     public static boolean wordSituationMatchingMechanism(int index, String value, String subString) {
         switch (index) {
             case 0:
                 // text contains
                 if (subString.toLowerCase().compareTo(subString.toLowerCase())==0) {
//                     System.out.println("TEXT OF VALUE " + value + " CONTAINS "+subString );
                     return true;
                 }
                 break;
              case 1:
                 //text does not contain
                 int pos = value.toLowerCase().indexOf(subString.toLowerCase());
              {
//                  System.out.println("the contains " + pos);
//                  System.out.println("TEXT OF VALUE " + value + " DOES CONTAINS "+subString );
                  if (pos != 0) return true;
              }
                 break;
             case 2:
                 if (value.startsWith(subString))  {
//                     System.out.println("TEXT OF VALUE TRUE " + value + " STARTS WITH  "+subString );
                     return true;
                 }
                 break;
             case 3:
                 // text ends with
                 boolean endsWith = value.endsWith(subString);
//                 System.out.println("TEXT OF VALUE " + endsWith + " DOES END WITH "+subString );
                 return endsWith;
             case 4:
                 //text is exactly
//                 System.out.println("TEXT OF VALUE " + value + " EXACTLY LIKE "+subString );
                 if (value.compareTo(subString)==0) return true;
                 break;
             default:
                 return false;
         }
        return false;
     }



}
