
package SkipList;

public class SkipListSetTester{ //this class is just to test what I am implementing with my actual skiplist
  public static void main(String []args){

    SkipListSet<Integer> skippy = new SkipListSet<>();

    skippy.add(13);
    skippy.add(12);
    skippy.add(4);
    skippy.add(5);
    skippy.add(1);
    skippy.add(16);
    
    for(Integer item : skippy){
      System.out.println(item);
    }

    // skippy.detailedPrint();

    if(skippy.contains(3))
      System.out.println("Value was found");
    else
      System.out.println("Not found");

    if(skippy.isEmpty())
      System.out.println("its empty");
    else
      System.out.println("not empty");

    System.out.println("Total values in skip list is " +skippy.size());


  }


  
}
